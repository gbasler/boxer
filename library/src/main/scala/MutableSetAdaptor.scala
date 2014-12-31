//import scala.collection.immutable.{Set, SetProxy}
//import scala.collection.mutable
//
//object MutableSetAdaptor {
//  def apply[A](elems: A*): MutableSetAdaptor[A] = ??? //new MutableSetAdaptor(mutable.LinkedHashSet[A](elems: _*))
//}
//
//case class MutableSetAdaptor[A](val self: mutable.Set[A]) extends SetProxy[A] {
//
////  override def newBuilder = new mutable.SetBuilder[A, Set[A]](MutableSetAdaptor())
//
//  override def +(elem: A): MutableSetAdaptor[A] = {
//    new MutableSetAdaptor(self + elem)
//  }
//
//  override def -(elem: A): MutableSetAdaptor[A] = {
//    new MutableSetAdaptor(self - elem)
//  }
//}
