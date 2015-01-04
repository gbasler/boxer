package demo

import scala.collection.mutable

object Demo2 extends App {

  val a: mutable.Set[Int] = mutable.Set(1, 2, 3, 4, 5)
//  val b: mutable.Set[Int] = mutable.LinkedHashSet(1, 2, 3, 4, 5)
  println(a)
  //  val b = TreeSet(1, 2, 3)
  //  val c = Seq(1, 2, 3).toSet
}
