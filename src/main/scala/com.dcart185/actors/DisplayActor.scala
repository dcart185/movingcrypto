package com.dcart185.actors

import akka.actor.{Actor, Props}
import com.dcart185.actors.TradePriceActor.NewAverageRate

object DisplayActor{

  def props = Props(new DisplayActor)
}

class DisplayActor extends Actor{


  override def preStart(): Unit = {
    context.system.eventStream.subscribe(self,classOf[NewAverageRate])
    super.preStart()
  }

  override def receive : Receive= {
    case NewAverageRate(cur1,cur2,price)=> {
      println(s"The average token pair price for ${cur1.ticket} & ${cur2.ticket} is $price")
    }
  }

  override def postStop(): Unit = {
    context.system.eventStream.unsubscribe(self,classOf[NewAverageRate])
    super.postStop()
  }
}
