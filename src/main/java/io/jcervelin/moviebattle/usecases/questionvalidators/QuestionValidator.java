package io.jcervelin.moviebattle.usecases.questionvalidators;

import io.jcervelin.moviebattle.domains.MoviePair;

public interface QuestionValidator {

  boolean tryNewQuestions(MoviePair moviePair);
}
