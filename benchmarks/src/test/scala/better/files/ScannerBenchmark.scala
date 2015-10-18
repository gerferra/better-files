package better.files

import java.io.BufferedReader

object ScannerBenchmark extends App {
  val file = (home / "Downloads" / "tmp.txt").createIfNotExists().clear()
  val n = 1000
  repeat(n) {
    file.appendLine(-n to n mkString " ")
        .appendLine("hello " * n)
        .appendLine("world " * n)
  }

  def run(scanner: AbstractScanner) = repeat(n) {
    assert(scanner.hasNext)
    val ints = List.fill(2*n + 1)(scanner.nextInt())
    val line = scanner.nextLine()
    val words = IndexedSeq.fill(n)(scanner.next())
    (ints, words)
  }

  def profile[A](f: => A): (A, Long) = {
    val t = System.nanoTime()
    (f, ((System.nanoTime() - t)/1e6).toLong)
  }

  def test(f: BufferedReader => AbstractScanner) = {
    val scanner = f(file.newBufferedReader)
    val result = profile(run(scanner))
    scanner.close()
    result
  }

  val (r3, t3) = test(new JavaScanner(_))
  val (r1, t1) = test(new IterableScanner(_))
  val (r2, t2) = test(new IteratorScanner(_))
  //val (r4, t4) = test(new StreamingScanner(_))

  assert(r1 == r2)
  assert(r2 == r3)
  //assert(r3 == r4)

  println(s"""
    |File = $file
    |Iterable  : $t1 ms
    |Iterator  : $t2 ms
    |Scanner   : $t3 ms
    |Streamer  : t4 ms
   """.stripMargin
  )
}