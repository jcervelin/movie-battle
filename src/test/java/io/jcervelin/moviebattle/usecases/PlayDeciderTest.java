package io.jcervelin.moviebattle.usecases;

import static io.jcervelin.moviebattle.TestUtils.SESSION_ID;
import static io.jcervelin.moviebattle.TestUtils.createMoviePair;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jcervelin.moviebattle.domains.BattleProperties;
import io.jcervelin.moviebattle.domains.Movie;
import io.jcervelin.moviebattle.domains.MoviePair;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
public class PlayDeciderTest {

  @InjectMocks private PlayDecider target;

  @Mock private BattleProperties properties;
  @Mock private GameHistoryManagement gameHistoryManagement;

  @Test
  public void isEndGameIfNumErrorsIs3() {
    when(properties.getMaxErrors()).thenReturn(3);
    assertThat(target.isEndGame(3)).isTrue();
  }

  @Test
  public void isEndGameIfNumErrorsIsMoreThan3() {
    when(properties.getMaxErrors()).thenReturn(3);
    assertThat(target.isEndGame(6)).isTrue();
  }

  @Test
  public void isEndGameIfNumErrorsIsLessThan3() {
    when(properties.getMaxErrors()).thenReturn(3);
    assertThat(target.isEndGame(2)).isFalse();
  }

  @Test
  public void theWinnerIsChoice1() {

    final MoviePair moviePair = createMoviePair("movieId1", "movieId2");
    moviePair.getChoice1().setImdbRating(BigDecimal.valueOf(7.6));
    moviePair.getChoice1().setImdbVotes(BigDecimal.valueOf(100));

    moviePair.getChoice2().setImdbRating(BigDecimal.valueOf(7.5));
    moviePair.getChoice2().setImdbVotes(BigDecimal.valueOf(100));

    when(gameHistoryManagement.getOpenedQuestion(SESSION_ID)).thenReturn(moviePair);

    final Movie result = target.theWinnerIs(SESSION_ID);

    assertThat(result).isEqualToComparingFieldByField(moviePair.getChoice1());
    verify(gameHistoryManagement).markAsAnswered(SESSION_ID);
  }

  @Test
  public void theWinnerIsChoice2() {

    final MoviePair moviePair = createMoviePair("movieId1", "movieId2");
    moviePair.getChoice1().setImdbRating(BigDecimal.valueOf(7.6));
    moviePair.getChoice1().setImdbVotes(BigDecimal.valueOf(100));

    moviePair.getChoice2().setImdbRating(BigDecimal.valueOf(7.5));
    moviePair.getChoice2().setImdbVotes(BigDecimal.valueOf(110));

    when(gameHistoryManagement.getOpenedQuestion(SESSION_ID)).thenReturn(moviePair);

    final Movie result = target.theWinnerIs(SESSION_ID);

    assertThat(result).isEqualToComparingFieldByField(moviePair.getChoice2());
    verify(gameHistoryManagement).markAsAnswered(SESSION_ID);
  }
}
