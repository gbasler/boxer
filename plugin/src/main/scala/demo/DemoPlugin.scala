package demo

import scala.collection.immutable.HashSet
import scala.tools.nsc.{Global, Phase}
import scala.tools.nsc.plugins.{Plugin, PluginComponent}

class DemoPlugin(val global: Global) extends Plugin {

  import global._

  val name = "demo-plugin"
  val description = "saves original types"
  val components = List[PluginComponent](DemoTyperComponent, DemoErasureComponent)

  final case class OriginalTypeAttachment(tp: Type)

  private object DemoTyperComponent extends PluginComponent {
    val global: DemoPlugin.this.global.type = DemoPlugin.this.global

    import global._

    override val runsAfter = List("typer")

    val phaseName = "demo-plugin-typer"

    override def newPhase(prev: Phase): StdPhase = new StdPhase(prev) {
      override def apply(unit: CompilationUnit) {
        new DemoTraverser(unit) traverse unit.body
      }
    }

    class DemoTraverser(unit: CompilationUnit) extends Traverser {
      override def traverse(tree: Tree): Unit = {
        tree.updateAttachment(OriginalTypeAttachment(tree.tpe))
        super.traverse(tree)
      }
    }

  }

  private object DemoErasureComponent extends PluginComponent {
    val global: DemoPlugin.this.global.type = DemoPlugin.this.global

    import global._

    override val runsAfter = List("erasure")

    val phaseName = "demo-plugin-erasure"

    override def newPhase(prev: Phase): StdPhase = new StdPhase(prev) {
      override def apply(unit: CompilationUnit) {
        new DemoTraverser(unit) traverse unit.body
      }
    }

    class DemoTransformer(unit: CompilationUnit) extends Transformer {

      override def transform(tree: Tree): Tree = tree match {
        case Apply(r@Select(rcvr@Select(predef, set), name), args) if name.toString == "Set" =>
        Apply(Select(Select(Select(Select(Ident($line30.$read), newTermName("$iw")), newTermName("$iw")), newTermName("LinkedHashSet")), newTermName("apply")), args)
      }
    }

    class DemoTraverser(unit: CompilationUnit) extends Traverser {

      //      val result = new global.Transformer {
      //        override def transform(tree: global.Tree) = {
      //          tree match {
      //            case global.Block(_, _) => block
      //            case t => super.transform(t)
      //          }
      //        }
      //      }.transform(dd)

      private def isSet(tpe: Type) = tpe.typeSymbol.isNonBottomSubClass(typeOf[Set[Any]].typeSymbol)

      private def isHashSet(tpe: Type) = tpe.typeSymbol.isNonBottomSubClass(typeOf[HashSet[Any]].typeSymbol)

      override def traverse(tree: Tree): Unit = {
        tree match {
          case New(tpt) if afterTyper(isSet(tpt.tpe)) =>
            unit.warning(tree.pos, s"Instantiated set!")
          //          case Apply(Select(Select(predef, set), name), args) if predef == "scala.Predef" && set == "Set" &&
          //            name.toString == "apply"                =>

          //           case Apply(Select(Select(wtf, set), apply), args) =>
          //             unit.warning(tree.pos, s"muhaha, predef=$wtf, set=$set, apply=$apply, name=$name, $args")
          //             Ident(predef)
          //             "scala.Predef"
          //             newTermName("Set")
          //             newTermName("apply")

          case Apply(r@Select(rcvr@Select(predef, set), name), args) if name.toString == "Set" =>
            val msg = s"Instantiated Set(*) predef=$predef, set=$set, name=$name, $args, predef.tpe ${r.tpe.typeSymbol}"
            unit.warning(tree.pos, msg)
          //            println(name.toTermName)
          //            println(name.toTypeName)
          //            println(rcvr.tpe.typeSymbol)
          //            println(rcvr.tpe.termSymbol)
          //            println("a: " + rcvr.attachments)
          //            tree.attachments.get[OriginalTypeAttachment] match {
          //              case Some(att) => println((tree, att.tp))
          //              case None =>
          //            }
          //            println("tree")
          //            println(tree)
          case Select(Apply(seq, args), toSet) if toSet.toString == "toSet" =>
            unit.warning(tree.pos, "toSet " + toSet)
            println("args " + args)

          //            Select(Apply(sss), newTermName("toSet"))


          //            newTermName("toSet")
          //          case _ if tree.isTerm && !tree.isEmpty =>
          //            tree.attachments.get[OriginalTypeAttachment] match {
          //              case Some(att) => println((tree, att.tp))
          //              case None =>
          //            }
          case _ =>
        }
        super.traverse(tree)
      }
    }

  }

}
