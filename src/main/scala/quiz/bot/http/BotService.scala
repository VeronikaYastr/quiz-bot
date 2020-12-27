package quiz.bot.http

import cats.effect.Concurrent
import cats.effect.concurrent.Ref
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}
import quiz.bot.dao.CategoryDao
import quiz.bot.dao.DaoModel.Question
import quiz.bot.http.BotMsg.In.{Message, Update}
import quiz.bot.http.BotMsg._
import quiz.bot.utils.MessageText._


final class BotService[F[_]: Concurrent](
  client: BotClient[F],
  dao: CategoryDao[F],
  ref: Ref[F, List[Question]]
) extends Http4sDsl[F] {
  implicit private val messageResponseDecoder: EntityDecoder[F, Update] = jsonOf[F, Update]

  private def startGame(chatId: ChatId): F[Unit] = {
    for {
      questions <- dao.getQuestions
      _         <- ref.set(questions)
      _         <- client.sendMessage(chatId, questions.headOption.map(_.text).getOrElse(gameEndMessage))
    } yield ()
  }

  def sendQuestions(chatId: ChatId, questions: List[Question]) = {

  }


/*  def sendAndCheck(chatId: ChatId, questionId: QuestionId, message: String, buttons: InlineButtons = List.empty): F[Unit] = for {
    questionMessage <- client.sendMessage(chatId, message, buttons)
    _ <- timer.sleep(10.seconds)
    _ <- api.editMessage(chatId, questionMessage.result.message_id, s"⏱ $message", buttons)
    _ <- timer.sleep(5.seconds)
    rightAnswer <- getRightAnswerMessage(chatId, questionId)
    _ <- api.editMessage(chatId, questionMessage.result.message_id, s"$message \n\n $rightAnswer")
    _ <- timer.sleep(1.seconds)
  } yield ()*/



  private def answerQuestion(chatId: Long, answer: String): F[Unit] = {
    ref.get.flatMap( questions =>
      questions.headOption match {
        case Some(question) if question.answer == answer.toLowerCase =>
          ref.set(questions.tail) *>
            client.sendMessage(chatId, questions.tail.headOption.map(_.text).getOrElse(gameEndMessage))
        case Some(_) => client.sendMessage(chatId, "Ошибочка вышла)")
        case None => client.sendMessage(chatId, gameEndMessage)
      }
    )
  }

  private def processUpdate(updateMessage: Message): F[Unit] = updateMessage match {
    case Message(_, chat, Some("/start")) => startGame(chat.id)
    case Message(_, chat, Some("/stop"))  => client.sendMessage(chat.id, gameStopMessage)
    case Message(_, chat, answer)         => answerQuestion(chat.id, answer.getOrElse(""))
  }

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root =>
      req.decode[Update] { update =>
        processUpdate(update.message)
          .flatMap(_ => Ok())
      }
  }

  val routesGet: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root => Ok("Hello")
  }
}

object BotService {
  def apply[F[_]: Concurrent](client: BotClient[F], dao: CategoryDao[F]): F[BotService[F]] = {
    for {
      ref <- Ref[F].of(List.empty[Question])
    } yield new BotService(client, dao, ref)
  }
}
