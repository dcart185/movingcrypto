package com.dcart185

import java.util.concurrent.{ExecutorService, Executors}

import akka.actor.{ActorRef, ActorSystem}
import com.dcart185.actors.{DisplayActor, TradePriceActor}
import com.dcart185.models.CryptoCurrency
import com.dcart185.modules.RepositoryModule
import com.dcart185.networkservices.tradehistory.TradeHistoryRepository
import com.google.inject.{Guice, Injector}
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext

//#main-class
object MovingAverage extends App {


  def startAverageHistory(cryptoCurrency1: CryptoCurrency,cryptoCurrency2: CryptoCurrency):Unit = {
    val config = ConfigFactory.load("application.conf")
    val executorService : ExecutorService = Executors.newFixedThreadPool(4)
    implicit val executionContext : ExecutionContext = ExecutionContext.fromExecutorService(executorService)

    // Create the 'movingAverage' actor system
    implicit val system: ActorSystem = ActorSystem("movingAverage")

    val injector : Injector = Guice.createInjector(new RepositoryModule(config));
    val tradeHistoryRepository = injector.getInstance(classOf[TradeHistoryRepository])

    val displayActor:ActorRef = system.actorOf(DisplayActor.props)
    system.actorOf(TradePriceActor.props(config,tradeHistoryRepository,cryptoCurrency1,cryptoCurrency2))
  }


  override def main(args: Array[String]): Unit = {
    val usage =
      """
        Usage: [--cur1 currency1] [--cur2 currency2]
      """

    if (args.length == 0) println(usage)
    val arglist = args.toList
    type OptionMap = Map[String, String]

    def nextOption(map : OptionMap, list: List[String]) : OptionMap = {
      def isSwitch(s : String) = (s(0) == '-')
      list match {
        case Nil => map
        case "--cur1" :: value :: tail =>
          nextOption(map ++ Map("cur1" -> value), tail)
        case "--cur2" :: value :: tail =>
          nextOption(map ++ Map("cur2" -> value), tail)
        case option :: tail => {
          println("Unknown option "+option)
          map
        }
      }
    }
    val options = nextOption(Map(),arglist)
    //println(options)

    if(options.nonEmpty){
      val cur1Opt = options.get("cur1")
      val cur2Opt = options.get("cur2")

      val currencyOpts : Option[(Option[CryptoCurrency],Option[CryptoCurrency])] = (cur1Opt,cur2Opt) match {
        case(Some(cur1),Some(cur2)) =>{
          Some((CryptoCurrency.getCryptoCurrentyFromTicket(cur1),CryptoCurrency.getCryptoCurrentyFromTicket(cur2)))
        }
        case _ => None
      }

      currencyOpts match {
        case Some(currencyValueOpts) =>{
          (currencyValueOpts._1,currencyValueOpts._2) match {
            case(Some(cryptoCur1),Some(cryptoCur2))=> startAverageHistory(cryptoCur1,cryptoCur2)
            case _ => println("Invalid cryptocurrency input")
          }
        }
        case None =>{
          println("You didn't enter all the required arguments.")
        }
      }
    }
  }
}