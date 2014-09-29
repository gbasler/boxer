import scala.collection.immutable.TreeSet

//class Cell[+T](val value: T)

object Cell {
//  Cell.of(1)
//
//  def of[T](value: T) = new Cell(value)

  val a = Set(1, 2, 3)
  val b = TreeSet(1, 2, 3)
  val c = Seq(1, 2, 3).toSet
}
