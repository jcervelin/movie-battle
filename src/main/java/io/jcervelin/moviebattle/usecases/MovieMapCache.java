package io.jcervelin.moviebattle.usecases;

import io.jcervelin.moviebattle.domains.Movie;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MovieMapCache {
  private final Map<String, Movie> movies;
}
