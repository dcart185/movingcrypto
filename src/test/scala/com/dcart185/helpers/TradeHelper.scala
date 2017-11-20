package com.dcart185.helpers

import com.dcart185.models.Trade
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

trait TradeHelper {
  val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

  val trade1Json : String = "{\"globalTradeID\":259025097,\"tradeID\":14422180,\"date\":\"2017-11-13 02:30:20\"," +
    "\"type\":\"buy\",\"rate\":\"0.00003303\",\"amount\":\"0.00001946\",\"total\":\"0.00000000\"}"

  val dateTime1 : DateTime = formatter.parseDateTime("2017-11-13 02:30:20")
  val trade1 : Trade = Trade(0.00001946,0.00003303,0.00000000,259025097,14422180,dateTime1,"buy")

  val trade2Json : String = "{\"globalTradeID\":259025038,\"tradeID\":14422179,\"date\":\"2017-11-13 02:30:16\"," +
    "\"type\":\"buy\",\"rate\":\"0.00003299\",\"amount\":\"388.93341342\",\"total\":\"0.01283091\"}"
  val dateTime2 : DateTime = formatter.parseDateTime("2017-11-13 02:30:16")
  val trade2 : Trade = Trade(388.93341342,0.00003299,0.01283091,259025038,14422179,dateTime2,"buy")


  val trade3Json : String = "{\"globalTradeID\":264432910,\"tradeID\":12052425,\"date\":\"2017-11-19 21:08:59\"," +
    "\"type\":\"buy\",\"rate\":\"0.00879981\",\"amount\":\"27.50864572\",\"total\":\"0.24207085\"}"
  val dateTime3 : DateTime = formatter.parseDateTime("2017-11-19 21:08:59")
  val trade3 : Trade = Trade(27.50864572,0.00879981,0.24207085,264432910,12052425,dateTime3,"buy")

  val tradeArrayJson1 = s"[$trade1Json,$trade2Json]"
  val tradeList1 : List[Trade] = List(trade1,trade2)

  val tradeArrayJson2 = s"[$trade1Json,$trade2Json,$trade3Json]"
  val tradeList2 : List[Trade] = List(trade1,trade2,trade3)
}