package io.jcervelin.moviebattle.usecases;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LoggedUser {

  public String getUsername() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }
}
