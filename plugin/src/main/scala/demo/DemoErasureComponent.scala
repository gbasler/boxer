package demo

import scala.collection.immutable.HashSet
import scala.collection.mutable
import scala.tools.nsc.plugins.PluginComponent
import scala.tools.nsc.transform.{Transform, TypingTransformers}
import scala.tools.nsc.Global

abstract class DemoErasureComponent extends PluginComponent with Transform with TypingTransformers {

  import global._
  import definitions._

  val phaseName = "rewrite-sets"

  def newTransformer(unit: CompilationUnit) = new SetTransformer(unit)

  class SetTransformer(unit: CompilationUnit) extends TypingTransformer(unit) {
    //      outer.global.gen.mkAttributedCast()

    //      outer.global.gen.mkAttributedRef(typeOf[mutable.LinkedHashSet[_]].typeSymbol)

    // crashes
    val setSym = typeOf[scala.collection.immutable.Set[_]].typeSymbol

    override def transform(tree: Tree): Tree = tree match {
      // TODO: compare symbol instead of string
      case a@Apply(r@Select(rcvr@Select(predef, set), name), args) if name.toString == "Set" =>
        //                localTyper.typed(treeCopy.Apply(tree, Ident(newTermName("LinkedHashSet")), args))

        //        val shiftR = newTermName("LinkedHashSet")
        //        val ModCPS = rootMirror.getPackage(TermName("nondet.utils"))
        //        val MethShiftR = definitions.getMember(ModCPS, shiftR)
//        val cls = rootMirror.getRequiredClass("nondet.utils.LinkedHashSet")
        val cls = rootMirror.getRequiredModule("nondet.utils.LinkedHashSet")
        val target = rootMirror.getRequiredClass("scala.collection.immutable.Set")
        val ref = gen.mkAttributedRef(cls).setType(target.tpe)
//        val ref = gen.mkAttributedRef(cls).setType(typeOf[scala.collection.immutable.Set[_]])
        //        gen.mkAttributedRef(typeOf[nondet.utils.LinkedHashSet[_]].typeSymbol)

        localTyper.typed(treeCopy.Apply(tree, ref, args))
        a
      case t =>

        super.transform(tree)
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
        //          case New(tpt) if afterTyper(isSet(tpt.tpe)) =>
        //            unit.warning(tree.pos, s"Instantiated set!")
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
