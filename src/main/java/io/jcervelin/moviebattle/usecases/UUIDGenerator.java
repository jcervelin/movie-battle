package io.jcervelin.moviebattle.usecases;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UUIDGenerator {

  public String generate() {
    return UUID.randomUUID().toString();
  }
}
