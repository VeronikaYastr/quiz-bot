package quiz.bot.http

import cats.effect._
import org.http4s.HttpRoutes
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import quiz.bot.AppConfig.HttpConfig
import quiz.bot.dao.CategoryDao
import quiz.bot.http.BotMsg.In.Update

import scala.concurrent.ExecutionContext


class HttpServer[F[_]: ConcurrentEffect: Timer](routes: HttpRoutes[F]) {
  def start(executionContext: ExecutionContext, httpConfig: HttpConfig): F[Unit] = {
    BlazeServerBuilder[F](executionContext)
      .bindHttp(httpConfig.port, httpConfig.host)
      .withHttpApp(routes.orNotFound)
      .withWebSockets(false)
      .serve
      .compile
      .drain
  }
}

object HttpServer {

  def apply[F[_]: ConcurrentEffect: Timer](botClient: BotClient[F], dao: CategoryDao[F], botService: BotService[F]): HttpServer[F] = {

    val routes = Router[F](
      "/" -> botService.routes,
      "/hello" -> botService.routesGet
    )

    new HttpServer[F](routes)
  }

}