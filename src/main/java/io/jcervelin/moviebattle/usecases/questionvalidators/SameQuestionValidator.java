package io.jcervelin.moviebattle.usecases.questionvalidators;

import io.jcervelin.moviebattle.domains.MoviePair;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 1)
public class SameQuestionValidator implements QuestionValidator {

  @Override
  public boolean tryNewQuestions(MoviePair moviePair) {
    return moviePair.getChoice1().equals(moviePair.getChoice2());
  }
}
