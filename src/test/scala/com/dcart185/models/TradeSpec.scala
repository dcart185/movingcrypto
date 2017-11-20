package com.dcart185.models

import com.dcart185.helpers.TradeHelper
import org.scalatest.{BeforeAndAfter, Matchers, MustMatchers, WordSpec}
import play.api.libs.json._

class TradeSpec extends WordSpec with TradeHelper with MustMatchers with BeforeAndAfter {

  "A Trade" must  {
    "be able to convert json into case class" in {
      val json : JsValue = Json.parse(trade1Json)
      val tradeResult: JsResult[Trade] = json.validate[Trade]

      tradeResult match {
        case JsSuccess(actualTrade,path)=>{
          trade1 mustBe(actualTrade)
        }
        case e: JsError => fail("failed to parse json")
      }
    }
    "be able to convert another json into case class" in {
      val json : JsValue = Json.parse(trade2Json)
      val tradeResult: JsResult[Trade] = json.validate[Trade]

      tradeResult match {
        case JsSuccess(actualTrade,path)=>{
          trade2 mustBe(actualTrade)
        }
        case e: JsError => fail("failed to parse json")
      }
    }
    "be able to convert another json list into case class list" in {
      val json : JsValue = Json.parse(tradeArrayJson1)
      val tradeResult: JsResult[List[Trade]] = json.validate[List[Trade]]

      tradeResult match {
        case JsSuccess(actualTrade,path)=>{
          tradeList1 mustBe(actualTrade)
        }
        case e: JsError => fail("failed to parse json")
      }
    }
  }
}



