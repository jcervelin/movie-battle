package io.jcervelin.moviebattle.usecases;

import io.jcervelin.moviebattle.domains.Movie;
import io.jcervelin.moviebattle.domains.exceptions.MovieNotFoundException;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieGenerator {

  private final Random randomGenerator;
  private final MovieMapCache movies;

  public Movie get() {
    Object[] values = movies.getMovies().values().toArray();
    if (values.length < 1) {
      throw new MovieNotFoundException();
    }
    return (Movie) values[randomGenerator.nextInt(values.length)];
  }
}
