package io.jcervelin.moviebattle.usecases;

import io.jcervelin.moviebattle.domains.Movie;
import io.jcervelin.moviebattle.domains.MovieChoice;
import io.jcervelin.moviebattle.domains.MoviePair;
import io.jcervelin.moviebattle.domains.QuestionResponse;
import io.jcervelin.moviebattle.usecases.questionvalidators.QuestionValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionGenerator {

  private final MovieGenerator movieGenerator;
  private final GameHistoryManagement gameHistoryManagement;
  private final List<QuestionValidator> questionValidators;

  @Transactional
  public QuestionResponse getMoviePair(String sessionId) {

    // If user has any open question, returns it instead of creating a new one
    if (gameHistoryManagement.hasAnyOpenQuestion(sessionId)) {
      var openedQuestions = gameHistoryManagement.getOpenedQuestion(sessionId);
      return createResponse(openedQuestions.getChoice1(), openedQuestions.getChoice2());
    }
    return validateAndLinkSessionToMovies(sessionId);
  }

  private QuestionResponse validateAndLinkSessionToMovies(String sessionId) {

    // Generates two random movies
    var choice1 = movieGenerator.get();
    var choice2 = movieGenerator.get();

    var moviePair = createMoviePairDto(sessionId, choice1, choice2);

    // If movies are the same or user already answered that question, retry using recursion
    var shouldTryNewQuestions =
        questionValidators.stream()
            .anyMatch(questionValidator -> questionValidator.tryNewQuestions(moviePair));

    if (shouldTryNewQuestions) {
      return validateAndLinkSessionToMovies(sessionId);
    }

    // Link user to question/movie to avoid this user to open new questions without answering
    // the previous ones
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
