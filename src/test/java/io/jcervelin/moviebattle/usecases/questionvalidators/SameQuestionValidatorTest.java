package io.jcervelin.moviebattle.usecases.questionvalidators;

import static io.jcervelin.moviebattle.TestUtils.createMoviePair;
import static io.jcervelin.moviebattle.TestUtils.createMovies;
import static org.assertj.core.api.Assertions.assertThat;

import io.jcervelin.moviebattle.domains.Movie;
import io.jcervelin.moviebattle.domains.MoviePair;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SameQuestionValidatorTest {

  @InjectMocks private SameQuestionValidator target;

  @Test
  public void shouldNotTryAgainIfSameQuestion() {
    final List<Movie> movies = createMovies(2);
    final MoviePair moviePair = createMoviePair(movies.get(0), movies.get(1));

    assertThat(target.tryNewQuestions(moviePair)).isFalse();
  }

  @Test
  public void shouldTryAgainIfSameQuestion() {
    final List<Movie> movies = createMovies(1);
    final MoviePair moviePair = createMoviePair(movies.get(0), movies.get(0));

    assertThat(target.tryNewQuestions(moviePair)).isTrue();
  }
}
