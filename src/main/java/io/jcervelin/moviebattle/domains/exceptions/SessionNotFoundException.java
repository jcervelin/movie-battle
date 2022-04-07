package io.jcervelin.moviebattle.domains.exceptions;

public class SessionNotFoundException extends RuntimeException {
  private static final String DEFAULT_ERROR_MESSAGE =
      "Session not found or doesn't belong to this user";

  public SessionNotFoundException() {
    super(DEFAULT_ERROR_MESSAGE);
  }
}
