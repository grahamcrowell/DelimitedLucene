

object Foo {
  def foo(arg1: String, arg2: Int): String = {
    arg1 + arg2.toString
  }
}

object Goo {
  val x = Option("string")
  val y = Option("int")
  val z = x.map(_)
}


val goo = Goo
goo.z