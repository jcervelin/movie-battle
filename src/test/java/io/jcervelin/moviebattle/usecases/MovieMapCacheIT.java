package io.jcervelin.moviebattle.usecases;

import static org.assertj.core.api.Assertions.assertThat;

import io.jcervelin.moviebattle.domains.Movie;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MovieMapCacheIT {

  @Autowired private MovieMapCache target;

  @Test
  public void movieMapCacheShouldNotBeEmpty() {
    final Map<String, Movie> movies = target.getMovies();
    assertThat(movies.size()).isEqualTo(198);
  }
}
