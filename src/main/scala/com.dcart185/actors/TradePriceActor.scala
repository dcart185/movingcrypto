package com.dcart185.actors

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, Cancellable, Props}
import com.dcart185.actors.TradePriceActor.{GetLatestTrade, NewAverageRate, NewTradeHistory}
import com.dcart185.models.{CryptoCurrency, Trade}
import com.dcart185.networkservices.networkservicesutil.FailResult
import com.dcart185.networkservices.tradehistory.{TradeHistoryRepository, TradeHistoryService}
import com.typesafe.config.Config

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import akka.pattern.pipe

object TradePriceActor {
  case object GetLatestTrade
  final case class NewTradeHistory(eitherTradeHistory:Either[FailResult,List[Trade]])
  final case class NewAverageRate(cur1:CryptoCurrency, cur2:CryptoCurrency, price:Double)


  def props(config: Config,tradeHistoryRepository: TradeHistoryRepository,cur1:CryptoCurrency,cur2:CryptoCurrency)
           (implicit ec:ExecutionContext) = Props(new TradePriceActor(config,tradeHistoryRepository,cur1,cur2))
}

class TradePriceActor(config:Config, tradeHistoryRepository: TradeHistoryRepository, currency1:CryptoCurrency,
                      currency2:CryptoCurrency)(implicit ec:ExecutionContext)
  extends Actor with ActorLogging{

  val tradeHistoryService : TradeHistoryService = new TradeHistoryService(tradeHistoryRepository)

  val startDelay : Long = config.getLong("startDelay")
  val refreshTime : Long = config.getLong("refreshTime")

  def currentStatus(trades:Set[Trade]) : Receive = {
    case GetLatestTrade => fetchLatestTrade()
    case NewTradeHistory(eitherTradeHistory) => handleNewTradeHistory(eitherTradeHistory,trades)
    case _ => log.warning("unrecognized statement")
  }

  override def receive= {
    case GetLatestTrade => fetchLatestTrade()
    case NewTradeHistory(eitherTradeHistory) => handleNewTradeHistory(eitherTradeHistory,Set[Trade]())
    case _ => log.warning("unrecognized")
  }

  def handleNewTradeHistory(eitherTradeHistory:Either[FailResult,List[Trade]],tradeHistory:Set[Trade]):Unit ={
    eitherTradeHistory match {
      case Left(failResult)=>{
        log.error(s"Unable to get trade history ${failResult.msg}")
      }
      case Right(tradeHistory)=>{
        val newTradeHistorySet : Set[Trade] = tradeHistory.toSet ++ tradeHistory
        //calculate new average price
        val average : Double = newTradeHistorySet.map(trade => trade.rate).sum / newTradeHistorySet.size
        val newAverageRate : NewAverageRate = NewAverageRate(currency1,currency2,average)
        context.system.eventStream.publish(newAverageRate)
        context.become(currentStatus(newTradeHistorySet))
      }
    }
  }


  def fetchLatestTrade():Unit = {
    val tradeHistoryFuture : Future[Either[FailResult,List[Trade]]]= tradeHistoryService
      .getTradeHistory(currency1,currency2)
    tradeHistoryFuture.map(tradeHistory => NewTradeHistory(tradeHistory)) pipeTo self
  }

  val cancelable : Cancellable = context.system.scheduler.schedule(FiniteDuration(startDelay,TimeUnit.MILLISECONDS),
    FiniteDuration(refreshTime,TimeUnit.MILLISECONDS),self,GetLatestTrade)

  override def postStop(): Unit = {
    cancelable.cancel()
    super.postStop()
  }
}
