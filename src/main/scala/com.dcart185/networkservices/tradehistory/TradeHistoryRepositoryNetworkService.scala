package com.dcart185.networkservices.tradehistory

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.util.ByteString
import com.dcart185.models.{CryptoCurrency, Trade}
import com.dcart185.networkservices.networkservicesutil.FailResult
import com.typesafe.config.Config
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

class TradeHistoryRepositoryNetworkService @Inject()(config: Config)(implicit ec:ExecutionContext, actorSystem:ActorSystem)
  extends TradeHistoryRepository {

  implicit val actorMaterializer :ActorMaterializer = ActorMaterializer()

  override def getTradeHistory(cryptoCurrency1: CryptoCurrency, cryptoCurrency2: CryptoCurrency) :
    Future[Either[FailResult,List[Trade]]] = {
    val url = config.getString("poloniex.url")
    val fullUrl = url +s"?command=returnTradeHistory&currencyPair=${cryptoCurrency1.ticket}_${cryptoCurrency2.ticket}"

    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = fullUrl))

    val tradeListResult : Future[Either[FailResult,List[Trade]]]= responseFuture.flatMap((httpResponse:HttpResponse)=>{
      if(httpResponse.status.isSuccess()){
        val entity = httpResponse.entity
        val content : Future[String] = entity.dataBytes.runWith(Sink.fold(ByteString.empty)(_ ++ _)).map(_.utf8String)
        val parsedResultFuture : Future[Either[FailResult,List[Trade]]] =content.map(contentString => {

          val json : JsValue = Json.parse(contentString)
          val tradeResult: JsResult[List[Trade]] = json.validate[List[Trade]]

          tradeResult match {
            case JsSuccess(tradeList,path)=>{
              Right(tradeList)
            }
            case e: JsError => Left(FailResult("Unable to parse json",Some(httpResponse.status.intValue())))
          }
        })
        parsedResultFuture
      }
      else{
        Future(Left(FailResult(httpResponse.status.reason(),None)))
      }
    })

    tradeListResult
  }
}
