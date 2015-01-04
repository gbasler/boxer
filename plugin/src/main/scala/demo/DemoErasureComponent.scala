package demo

import scala.collection.immutable.HashSet
import scala.collection.mutable
import scala.reflect.api.Trees.SelectExtractor
import scala.tools.nsc.ast.TreeDSL
import scala.tools.nsc.plugins.PluginComponent
import scala.tools.nsc.transform.{Transform, TypingTransformers}
import scala.tools.nsc.Global

abstract class DemoErasureComponent extends PluginComponent
with Transform
with TypingTransformers
with TreeDSL {

  import global._
  import definitions._

  val phaseName = "rewrite-sets"

  def newTransformer(unit: CompilationUnit) = new SetTransformer(unit)

  class SetTransformer(unit: CompilationUnit) extends TypingTransformer(unit) {
    //      outer.global.gen.mkAttributedCast()

    //      outer.global.gen.mkAttributedRef(typeOf[mutable.LinkedHashSet[_]].typeSymbol)

    override def transform(tree: Tree): Tree = {
      def rssi(b: String, c: String) =
        Select(Select(Ident("_root_"), b), newTypeName(c))

      tree match {
        // TODO: compare symbol instead of string
        // immutable
        case a@Apply(r@Select(rcvr@Select(predef, set), name), args) if name.toString == "Set" =>
          // crashes
          //        val setSym = global.typeOf[scala.collection.immutable.Set[_]].typeSymbol

          //                localTyper.typed(treeCopy.Apply(tree, Ident(newTermName("LinkedHashSet")), args))

          //        val shiftR = newTermName("LinkedHashSet")
          //        val ModCPS = rootMirror.getPackage(TermName("nondet.utils"))
          //        val MethShiftR = definitions.getMember(ModCPS, shiftR)
          //        val cls = rootMirror.getRequiredClass("nondet.utils.LinkedHashSet")
          val cls = rootMirror.getRequiredModule("nondet.utils.LinkedHashSet")
          val target = rootMirror.getRequiredClass("scala.collection.immutable.Set")
          val ref = gen.mkAttributedRef(cls).setType(a.tpe) //.setType(target.tpe)
          //        val ref = gen.mkAttributedRef(cls).setType(typeOf[scala.collection.immutable.Set[_]])
          //        gen.mkAttributedRef(typeOf[nondet.utils.LinkedHashSet[_]].typeSymbol)

//          println(show(rcvr.symbol))
//          println(show(tree.symbol))

          val sol = localTyper.typed(treeCopy.Apply(tree, ref, args))
//          println(show(sol.symbol))
          println("not now")
          sol

//        case t@Select(Select(Select(Select(Ident(scala), scala.collection), scala.collection.mutable), scala.collection.mutable.Set), TermName("apply"))
        case t@Select(fun@Select(Select(Select(qual, name), name2), name3), name4) if name4 == newTermName("apply") && false =>

//         Select(Apply(Select(Select(Select(Select(Ident(scala), scala.collection), scala.collection.mutable), scala.collection.mutable.Set), TermName("apply")),
//           List(Apply(Select(Select(This(TypeName("scala")), scala.Predef), TermName("wrapIntArray")),
//             List(ArrayValue(TypeTree(), List(Literal(Constant(1)), Literal(Constant(2)), Literal(Constant(3)), Literal(Constant(4)), Literal(Constant(5)))))))), TermName("$asInstanceOf"))
//              println( name4 == newTermName("apply"))
          println(showRaw(t))
          println(fun.symbol)
          val target = rootMirror.requiredClass[scala.collection.mutable.Set[_]]
          println(qual.symbol == target)
             t

        case a@Apply(wtf@TypeApply(fun, targs), args) if false =>
          println("who are you? " + a)
          val target = rootMirror.requiredClass[scala.collection.mutable.Set[_]]
          val method = newTermName("apply")
          val methShiftUnit: Symbol = definitions.getMember(target, method)
//          println(":" + methShiftUnit)
          println(":" + fun.symbol)
          println(":" + targs)
          println(":" + args)
          println(":" + wtf)
//          println("yes? " + methShiftUnit == fun.symbol)
             a
        // mutable
        case a@Apply(r@Select(rcvr@Select(predef, set), name), args) if false =>

          //          rootMirror.typeOf[scala.collection.immutable.HashSet[_]].typeSymbol
          val target = rootMirror.requiredClass[scala.collection.mutable.Set[_]]
          val target2 = rootMirror.requiredClass[scala.collection.GenTraversable[_]]
          //          val target = rootMirror.getRequiredClass("scala.collection.mutable.Set")
          println(show(name))
          println(show(predef))
          println(show(set))
          println(show(target.tpe))
          println(show(rcvr.symbol))
          println(rcvr.symbol.tpe =:= target.tpe)
          println(rcvr.symbol == target)
          println("tree " + show(tree.symbol))
          val cls = rootMirror.requiredModule[scala.collection.mutable.LinkedHashSet[_]]
          val ref = gen.mkAttributedRef(cls).setType(target2.tpe)
          val sol = localTyper.typed(treeCopy.Apply(tree, ref, args))
//          println(show(sol.symbol))
          a

        case t =>
//          println(showRaw(t))
          super.transform(tree)
      }
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
