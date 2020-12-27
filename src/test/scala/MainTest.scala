import cats.effect.{ContextShift, IO, Timer}
import fs2.Stream
import fs2.concurrent.{SignallingRef, Topic}

object MainTest {

  def main(args: Array[String]): Unit = {

    import scala.concurrent.ExecutionContext
    import scala.concurrent.duration._

    implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
    implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)

    val r = Stream(1, 2, 3).evalMap(value => IO {println(value)}).delayBy(1.seconds)

    r.compile.drain.unsafeRunSync()
  }
}
