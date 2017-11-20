package com.dcart185.networkservices.tradehistory

import com.dcart185.models.{CryptoCurrency, Trade}
import com.dcart185.networkservices.networkservicesutil.FailResult

import scala.concurrent.Future

class TradeHistoryService(tradeHistory: TradeHistoryRepository) {

  def getTradeHistory(cryptoCurrency1:CryptoCurrency,cryptoCurrency2:CryptoCurrency):
  Future[Either[FailResult,List[Trade]]]={
    tradeHistory.getTradeHistory(cryptoCurrency1,cryptoCurrency2)
  }

}
