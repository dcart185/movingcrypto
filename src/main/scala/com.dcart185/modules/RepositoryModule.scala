package com.dcart185.modules

import akka.actor.ActorSystem
import com.dcart185.networkservices.tradehistory.{TradeHistoryRepository, TradeHistoryRepositoryNetworkService}
import com.google.inject.AbstractModule
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext

class RepositoryModule(config : Config)(implicit actorSystem:ActorSystem,ec:ExecutionContext) extends AbstractModule{
  override def configure() = {
    bind(classOf[Config]).toInstance(config)
    bind(classOf[ActorSystem]).toInstance(actorSystem)
    bind(classOf[ExecutionContext]).toInstance(ec)
    bind(classOf[TradeHistoryRepository]).to(classOf[TradeHistoryRepositoryNetworkService])
  }
}
