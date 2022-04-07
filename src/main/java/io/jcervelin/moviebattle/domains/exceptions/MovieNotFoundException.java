package io.jcervelin.moviebattle.domains.exceptions;

public class MovieNotFoundException extends RuntimeException {
  private static final String DEFAULT_ERROR_MESSAGE = "Movies not found";

  public MovieNotFoundException() {
    super(DEFAULT_ERROR_MESSAGE);
  }
}
