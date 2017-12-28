val iter = List().toIterator

iter.isEmpty

val foo = 1


val goo : Option[Int] = foo match {
  case 0 => None
  case 1 => Option(1)
}

goo.map(_ * 10)

