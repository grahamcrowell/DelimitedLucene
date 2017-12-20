class Bob(val a: String) {
  def getA : String = a
}

object Foo {
  def goo(x: Int)(y: Int): Double = {
    val z = x * -1
    // even for the same function instance, f = goo(x=?)
    // a new Bob is created each time f(y=?) is called
    val bob = new Bob(z.toString)
    println(bob)
    y * 1.0 * z
  }
}

val foo = Foo
val goo = foo.goo(9) _
println(goo)
val bar = goo(8)
println(bar)
println(goo(10))
