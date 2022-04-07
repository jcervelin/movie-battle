package io.jcervelin.moviebattle.usecases;

import static io.jcervelin.moviebattle.TestUtils.MOVIE_NOT_FOUND;
import static io.jcervelin.moviebattle.TestUtils.createMovies;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import io.jcervelin.moviebattle.domains.Movie;
import io.jcervelin.moviebattle.domains.exceptions.MovieNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MovieGeneratorTest {

  @InjectMocks private MovieGenerator target;
  @Mock private Random randomGenerator;
  @Mock private MovieMapCache movies;

  @Test
  public void movieGenerator() {
    final List<Movie> movies = createMovies(3);
    final Map map =
        new TreeMap(
            Map.of(
                "movieId1", movies.get(0), "movieId2", movies.get(1), "movieId3", movies.get(2)));
    when(this.movies.getMovies()).thenReturn(map);
    when(randomGenerator.nextInt(3)).thenReturn(1);
    final Movie result = target.get();
    assertThat(result).isEqualToComparingFieldByField(movies.get(1));
  }

  @Test
  public void movieGeneratorShouldThrowExceptionIfListIsEmpty() {
    when(this.movies.getMovies()).thenReturn(Map.of());

    final MovieNotFoundException ex =
        assertThrows(MovieNotFoundException.class, () -> target.get());
    assertThat(ex.getMessage()).isEqualTo(MOVIE_NOT_FOUND);
  }
}
