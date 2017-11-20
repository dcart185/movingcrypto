package com.dcart185.models

sealed case class CryptoCurrency(name:String,ticket:String)

object CryptoCurrency {
  object BTC extends CryptoCurrency("Bitcoin Cash Exchange","BTC")
  object ETH extends CryptoCurrency("Ethereum Exchange","ETH")
  object DASH extends CryptoCurrency("Dash Exchange","DASH")
  object XRP extends CryptoCurrency("Ripple Exchange","XRP")
  object ETC extends CryptoCurrency("Ethereum Classic Exchange","ETC")
  object XMR extends CryptoCurrency("Monero Exchange","XMR")
  object LTC extends CryptoCurrency("LiteCoin Exchange","LTC")

  private val cryptoMap : Map[String,CryptoCurrency] = Map("BTC"->CryptoCurrency.this.BTC,"ETH"->ETH,"DASH"->DASH,
    "ETC"->ETC, "XMR"->XMR,"LTC"->LTC,"XRP"->XRP)

  def getCryptoCurrentyFromTicket(ticket:String):Option[CryptoCurrency] = cryptoMap.get(ticket)

}
