package com.dcart185.actors

import java.util.concurrent.{ExecutorService, Executors}

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.dcart185.actors.TradePriceActor.NewAverageRate
import com.dcart185.helpers.TradeHelper
import com.dcart185.models.CryptoCurrency
import com.dcart185.networkservices.tradehistory.TradeHistoryRepository
import com.typesafe.config.ConfigFactory
import org.scalatest._
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

import scala.concurrent.{ExecutionContext, Future}

class TradePriceActorSpec extends TestKit(ActorSystem("TradePriceActorSpec"))
  with MustMatchers
  with WordSpecLike
  with ImplicitSender
  with BeforeAndAfterAll
  with TradeHelper
  with MockitoSugar
{

  val executorService : ExecutorService = Executors.newFixedThreadPool(4)
  implicit val executionContext : ExecutionContext = ExecutionContext.fromExecutorService(executorService)

  implicit def toAnswerWithArgs[T](f: InvocationOnMock => T) = new Answer[T] {
    override def answer(i: InvocationOnMock): T = f(i)
  }

  "A TradePriceActor" must {
    "be able to calculate average" in {
      val testConfig = ConfigFactory.load("test.conf")

      val cur1 = CryptoCurrency.BTC
      val cur2 = CryptoCurrency.ETC

      val tradeHistoryMockRepository = mock[TradeHistoryRepository]
      when(tradeHistoryMockRepository.getTradeHistory(cur1,cur2)).thenReturn(Future(Right(tradeList1)))

      val testProbe  = TestProbe()
      system.eventStream.subscribe(testProbe.ref,classOf[NewAverageRate])

      val testTradePriceActorRef : ActorRef = testProbe.childActorOf(TradePriceActor
        .props(testConfig,tradeHistoryMockRepository,cur1,cur2))

      val expectAverage : Double = (tradeList1.head.rate + tradeList1(1).rate)/2
      val expectMsg : NewAverageRate = NewAverageRate(cur1,cur2,expectAverage)
      testProbe.expectMsg(expectMsg)
      system.stop(testTradePriceActorRef)

    }

    "be able to update the current average" in {
      val testConfig = ConfigFactory.load("test.conf")

      val cur1 = CryptoCurrency.BTC
      val cur2 = CryptoCurrency.ETC

      var count = 0
      val tradeHistoryMockRepository = mock[TradeHistoryRepository]
      when(tradeHistoryMockRepository.getTradeHistory(cur1,cur2)).thenAnswer((t)=>{
        if(count==0){
          count = count + 1
          Future(Right(tradeList1))
        }
        else {
          Future(Right(tradeList2))
        }
      })

      val testProbe  = TestProbe()
      system.eventStream.subscribe(testProbe.ref,classOf[NewAverageRate])

      val testTradePriceActorRef : ActorRef = testProbe.childActorOf(TradePriceActor
        .props(testConfig,tradeHistoryMockRepository,cur1,cur2))

      val expectAverage1 : Double = (tradeList1.head.rate + tradeList1(1).rate)/2
      val expectAverage2 : Double = (tradeList2.head.rate + tradeList2(1).rate + tradeList2(2).rate)/3
      val expectMsg1 : NewAverageRate = NewAverageRate(cur1,cur2,expectAverage1)
      val expectMsg2 : NewAverageRate = NewAverageRate(cur1,cur2,expectAverage2)
      testProbe.expectMsg(expectMsg1)
      testProbe.expectMsg(expectMsg2)
      system.stop(testTradePriceActorRef)

    }
  }
}
