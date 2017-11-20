package com.dcart185.models

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Trade(amount:Double,rate:Double,total:Double,globalTradeID:Long,tradeId:Long,date:DateTime,typeType:String)

object Trade {
  import Trade._

  //{"globalTradeID":259025097,"tradeID":14422180,"date":"2017-11-13 02:30:20","type":"buy","rate":"0.00003303",
  // "amount":"0.00001946","total":"0.00000000"}

  val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

  implicit val tradeReads : Reads[Trade] = (
    (JsPath \ "amount").read[String].map(a => a.toDouble) and
    (JsPath \ "rate").read[String].map(rateString => rateString.toDouble) and
    (JsPath \ "total").read[String].map(totalString => totalString.toDouble) and
    (JsPath \ "globalTradeID").read[Long] and
    (JsPath \ "tradeID").read[Long] and
    (JsPath \ "date").read[String].map(dateTimeString => {formatter.parseDateTime(dateTimeString)} ) and
    (JsPath \ "type").read[String]
    )(Trade.apply _)



}
