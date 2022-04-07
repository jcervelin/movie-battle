package io.jcervelin.moviebattle.usecases;

import static io.jcervelin.moviebattle.TestUtils.createMovies;
import static org.assertj.core.api.Assertions.assertThat;

import io.jcervelin.moviebattle.domains.Movie;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class MovieMapCacheTest {

  private MovieMapCache target;

  @Test
  public void movieMapCacheShouldNotBeEmpty() {
    final List<Movie> movies = createMovies(2);
    target = new MovieMapCache(Map.of("movieId1", movies.get(0), "movieId2", movies.get(1)));
    final Map<String, Movie> result = target.getMovies();
    assertThat(result.size()).isEqualTo(2);
  }
}
