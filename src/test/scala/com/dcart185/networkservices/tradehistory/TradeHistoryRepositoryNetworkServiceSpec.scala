package com.dcart185.networkservices.tradehistory

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{BeforeAndAfter, MustMatchers, WordSpec}
import akka.http.scaladsl.server._
import Directives._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.dcart185.helpers.TradeHelper
import com.dcart185.models.{CryptoCurrency, Trade}
import com.dcart185.networkservices.networkservicesutil.FailResult
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class TradeHistoryRepositoryNetworkServiceSpec extends WordSpec
  with MustMatchers
  with ScalatestRouteTest
  with BeforeAndAfter
  with TradeHelper {

  val smallRoute =
    get {
      pathSingleSlash {
        complete {
          tradeArrayJson1
        }
      }
    }

  var bindingFuture : Future[Http.ServerBinding] = null


  before {
    bindingFuture = Http().bindAndHandle(smallRoute, "localhost", 6000)
  }

  after {
    bindingFuture.map(binder => binder.unbind())
  }



  "The service" must {
    "be able to fetch data" in {
      val testConfig = ConfigFactory.load("test.conf")

      implicit val system = ActorSystem("SensorSystem")
      implicit val materializer = ActorMaterializer()

      val tradeHistoryNetworkService : TradeHistoryRepositoryNetworkService = new TradeHistoryRepositoryNetworkService(testConfig)

      val c1 = CryptoCurrency.BTC
      val c2 = CryptoCurrency.ETC
      val resultFuture : Future[Either[FailResult,List[Trade]]] = tradeHistoryNetworkService.getTradeHistory(c1,c2)
      val result : Either[FailResult,List[Trade]] = Await.result(resultFuture,3 seconds)

      result match {
        case Right(tradeHistory) =>{
          tradeHistory mustBe tradeList1
        }
        case Left(failResult)=>{
          fail("did not work")
        }
      }

    }
  }


}
