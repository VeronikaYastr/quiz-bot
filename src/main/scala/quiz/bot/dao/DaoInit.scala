package quiz.bot.dao

import cats.effect.{Async, Blocker, ConcurrentEffect, ContextShift, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.flywaydb.core.Flyway
import quiz.bot.AppConfig.DbConfig

object DaoInit {
  def initialize[F[_]](transactor: HikariTransactor[F])(implicit F: ConcurrentEffect[F]): F[Unit] =
    transactor.configure { dataSource =>
      F.delay {
        Flyway
          .configure()
          .dataSource(dataSource)
          .load()
          .migrate()
      }
    }

  def transactor[F[_]: Async: ContextShift](dbConfig: DbConfig): Resource[F, HikariTransactor[F]] = {
    for {
      context    <- ExecutionContexts.fixedThreadPool[F](32)
      blocker    <- Blocker[F]
      transactor <- HikariTransactor.newHikariTransactor[F](
        dbConfig.driverName,
        dbConfig.url,
        dbConfig.username,
        dbConfig.password,
        context,
        blocker
      )
    } yield transactor
  }
}
