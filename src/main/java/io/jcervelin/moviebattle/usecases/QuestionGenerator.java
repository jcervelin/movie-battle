package io.jcervelin.moviebattle.usecases;

import io.jcervelin.moviebattle.domains.Movie;
import io.jcervelin.moviebattle.domains.MovieChoice;
import io.jcervelin.moviebattle.domains.MoviePair;
import io.jcervelin.moviebattle.domains.QuestionResponse;
import io.jcervelin.moviebattle.usecases.questionvalidators.QuestionValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionGenerator {

  private final MovieGenerator movieGenerator;
  private final GameHistoryManagement gameHistoryManagement;
  private final List<QuestionValidator> questionValidators;

  public QuestionResponse getMoviePair(String sessionId) {

    // Se o usuario tiver uma questao aberta, retornar a mesma questao
    if (gameHistoryManagement.hasAnyOpenQuestion(sessionId)) {
      var openedQuestions = gameHistoryManagement.getOpenedQuestion(sessionId);
      return createResponse(openedQuestions.getChoice1(), openedQuestions.getChoice2());
    }
    return validateAndLinkSessionToMovies(sessionId);
  }

  private QuestionResponse validateAndLinkSessionToMovies(String sessionId) {

    // Criar dois filmes randomicamente
    var choice1 = movieGenerator.get();
    var choice2 = movieGenerator.get();

    var moviePair = createMoviePairDto(sessionId, choice1, choice2);

    // Se forem iguais, tentar novamente por recursao
    // Verificar se questoes ja nao foram previamente respondidas.
    // Se ja foram, tentar novamente por recursao
    final boolean shouldTryNewQuestions =
        questionValidators.stream()
            .anyMatch(questionValidator -> questionValidator.tryNewQuestions(moviePair));

    if (shouldTryNewQuestions) {
      return validateAndLinkSessionToMovies(sessionId);
    }

    // Linkar usuario com a questao/movies, para evitar que usuario faca novas
    // perguntas antes de responder as abertas
    gameHistoryManagement.linkQuestionToUser(moviePair);

    return createResponse(choice1, choice2);
  }

  private MoviePair createMoviePairDto(String sessionId, Movie choice1, Movie choice2) {
    return MoviePair.builder().sessionId(sessionId).choice1(choice1).choice2(choice2).build();
  }

  private QuestionResponse createResponse(Movie choice1, Movie choice2) {
    return QuestionResponse.builder()
        .choice1(MovieChoice.builder().id(choice1.getId()).name(choice1.getTitle()).build())
        .choice2(MovieChoice.builder().id(choice2.getId()).name(choice2.getTitle()).build())
        .build();
  }
}
