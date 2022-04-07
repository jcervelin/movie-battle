package io.jcervelin.moviebattle.usecases.questionvalidators;

import io.jcervelin.moviebattle.domains.MoviePair;
import io.jcervelin.moviebattle.usecases.GameHistoryManagement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(value = 2)
public class QuestionAnsweredValidator implements QuestionValidator {

  private final GameHistoryManagement gameHistoryManagement;

  @Override
  public boolean tryNewQuestions(MoviePair moviePair) {
    return gameHistoryManagement.hasUserAnsweredThisQuestion(moviePair);
  }
}
