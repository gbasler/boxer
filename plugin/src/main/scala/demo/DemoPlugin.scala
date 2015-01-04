package demo

import scala.collection.immutable.HashSet
import scala.collection.mutable
import scala.tools.nsc.Global
import scala.tools.nsc.plugins.{Plugin, PluginComponent}
import scala.tools.nsc.transform.{Transform, TypingTransformers}

class DemoPlugin(val global: Global) extends Plugin {

  val name = "demo-plugin"
  val description = "rewrites sets"

  val rewriteSetComponent = new {
    val global = DemoPlugin.this.global
  } with DemoErasureComponent {
//    val runsAfter = List("pickler")
    val runsAfter = List("erasure")
  }

  val components = List[PluginComponent](rewriteSetComponent)

}
