package demo

import scala.collection.immutable.HashSet
import scala.tools.nsc.transform.{TypingTransformers, Transform}
import scala.tools.nsc.{Global, Phase}
import scala.tools.nsc.plugins.{Plugin, PluginComponent}

class DemoPlugin(val global: Global) extends Plugin {

  import global._
  import global.definitions._

  val name = "demo-plugin"
  val description = "saves original types"
  val components = List[PluginComponent](DemoErasureComponent)



  private object DemoErasureComponent extends PluginComponent with Transform {
    val global: DemoPlugin.this.global.type = DemoPlugin.this.global

    import global._

    override val runsAfter = List("erasure")

    val phaseName = "demo-plugin-erasure"

//    override def newPhase(prev: Phase): StdPhase = new StdPhase(prev) {
//      override def apply(unit: CompilationUnit) {
////        new DemoTraverser(unit) traverse unit.body
//        println("transforming..." + unit.body)
//        val r = new DemoTransformer(unit) transform unit.body
//        println("after..." + unit.body)
//        r
//      }
//    }

    def newTransformer(unit: CompilationUnit) = new DemoTransformer(unit)

    class DemoTransformer(unit: CompilationUnit) extends Transformer {

      override def transform(tree: Tree): Tree = tree match {
        case a@Apply(r@Select(rcvr@Select(predef, set), name), args) if name.toString == "Set" =>
          val res = Apply(Ident(newTermName("LinkedHashSet")), args)
          unit.warning(tree.pos, s"Transformed $name $a into \n$res")
          println("wtf")
          val ident = Ident(newTermName("LinkedHashSet"))
//          if(a.hasSymbol) {
            println(s"*** ${a.symbol}")
//          }
          val r = a.copy(fun = ident)
//          r.tpe = a.tpe
//          r.symbol = a.symbol
//          r
//          val owner = tree.symbol.owner
          // New symbol. What's a Position good for?
//          val symbol = new TypeSymbol(owner, NoPosition, ident)
//          definitions.EmptyPackage
          val r2 = treeCopy.Apply(tree, ident, args)
//          r2.symbol = newFreeTypeSymbol(name = newTypeName("wtf"), origin = "")
//          localTyper.typed
          r2.symbol = NoSymbol
          r2
//          Apply(TypeApply(Select(Apply(Select(Apply(Select(Select(This(newTypeName("scala")), scala.Predef), newTermName("Set")), List()), newTermName("apply")), List(Apply(Select(Select(This(newTypeName("scala")), scala.Predef), newTermName("wrapIntArray")), List(ArrayValue(TypeTree(), List(Literal(Constant(1)), Literal(Constant(2)), Literal(Constant(3)))))))), newTermName("$asInstanceOf")), List(TypeTree())), List())), DefDef(Modifiers(METHOD | STABLE | ACCESSOR), newTermName("a"), List(), List(List()), TypeTree(), Select(This(newTypeName("Cell")), newTermName("a "))))))))

        case t => super.transform(tree)
//          println(showRaw(tree))
//        case ValDef(modifiers, varName, Apply(name, List(Ident(TypeName("Int")))), Apply(Ident(TermName("Set")), List(Literal(Constant(1)), Literal(Constant(2)), Literal(Constant(3)))))))) if name.toString == "Set" =>
//
//          ValDef(Modifiers(), TermName("b"), AppliedTypeTree(Ident(TypeName("Set")), List(Ident(TypeName("Int")))), Apply(Ident(TermName("LinkedHashSet")), List(Literal(Constant(1)), Literal(Constant(2)), Literal(Constant(3))))))))
//
//        Apply(Select(Select(Select(Select(Ident($line30.$read), newTermName("$iw")), newTermName("$iw")), newTermName("LinkedHashSet")), newTermName("apply")), args)
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

}
