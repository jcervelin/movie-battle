package io.jcervelin.moviebattle.usecases.questionvalidators;

import static io.jcervelin.moviebattle.TestUtils.createMoviePair;
import static io.jcervelin.moviebattle.TestUtils.createMovies;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.jcervelin.moviebattle.domains.Movie;
import io.jcervelin.moviebattle.domains.MoviePair;
import io.jcervelin.moviebattle.usecases.GameHistoryManagement;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class QuestionAnsweredValidatorTest {

  @InjectMocks private QuestionAnsweredValidator target;
  @Mock private GameHistoryManagement gameHistoryManagement;

  @Test
  public void shouldNotTryAgainIfHasNotAnswered() {
    final List<Movie> movies = createMovies(2);
    final MoviePair moviePair = createMoviePair(movies.get(0), movies.get(1));
    when(gameHistoryManagement.hasUserAnsweredThisQuestion(moviePair)).thenReturn(false);

    assertThat(target.tryNewQuestions(moviePair)).isFalse();
  }

  @Test
  public void shouldTryAgainIfHasAnswered() {
    final List<Movie> movies = createMovies(2);
    final MoviePair moviePair = createMoviePair(movies.get(0), movies.get(1));
    when(gameHistoryManagement.hasUserAnsweredThisQuestion(moviePair)).thenReturn(true);

    assertThat(target.tryNewQuestions(moviePair)).isTrue();
  }
}
