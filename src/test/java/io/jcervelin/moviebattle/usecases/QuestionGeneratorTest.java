package io.jcervelin.moviebattle.usecases;

import static io.jcervelin.moviebattle.TestUtils.SESSION_ID;
import static io.jcervelin.moviebattle.TestUtils.createMoviePair;
import static io.jcervelin.moviebattle.TestUtils.createMovies;
import static io.jcervelin.moviebattle.TestUtils.createQuestionResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jcervelin.moviebattle.domains.Movie;
import io.jcervelin.moviebattle.domains.MoviePair;
import io.jcervelin.moviebattle.domains.QuestionResponse;
import io.jcervelin.moviebattle.usecases.questionvalidators.QuestionAnsweredValidator;
import io.jcervelin.moviebattle.usecases.questionvalidators.SameQuestionValidator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class QuestionGeneratorTest {

  private QuestionGenerator target;
  @Mock private MovieGenerator movieGenerator;
  @Mock private GameHistoryManagement gameHistoryManagement;
  @Mock private SameQuestionValidator sameQuestionValidator;
  @Mock private QuestionAnsweredValidator questionAnsweredValidator;

  @BeforeEach
  public void setup() {
    target =
        new QuestionGenerator(
            movieGenerator,
            gameHistoryManagement,
            List.of(sameQuestionValidator, questionAnsweredValidator));
  }

  @Test
  public void shouldReturnPairOfMovies() {

    final List<Movie> movies = createMovies(2);
    final MoviePair expectedMoviePair = createMoviePair(movies.get(0), movies.get(1));
    final QuestionResponse expectedQuestionResponse = createQuestionResponse(expectedMoviePair);

    when(gameHistoryManagement.hasAnyOpenQuestion(SESSION_ID)).thenReturn(false);
    when(sameQuestionValidator.tryNewQuestions(expectedMoviePair)).thenReturn(false);
    when(questionAnsweredValidator.tryNewQuestions(expectedMoviePair)).thenReturn(false);
    when(movieGenerator.get()).thenReturn(movies.get(0), movies.get(1));

    final QuestionResponse questionResponse = target.getMoviePair(SESSION_ID);

    verify(gameHistoryManagement).linkQuestionToUser(expectedMoviePair);

    assertThat(questionResponse).isEqualToComparingFieldByField(expectedQuestionResponse);
  }

  @Test
  public void ifMovieGeneratorFetchesSameMovieTryAgainAndReturnSuccess() {

    final List<Movie> movies = createMovies(3);

    final MoviePair expectedMoviePair = createMoviePair(movies.get(0), movies.get(0));
    final MoviePair expectedMoviePair2 = createMoviePair(movies.get(1), movies.get(2));

    final QuestionResponse expectedQuestionResponse = createQuestionResponse(expectedMoviePair2);

    // Dois primeiros filmes sao duplicados, entao tente novamente
    when(gameHistoryManagement.hasAnyOpenQuestion(SESSION_ID)).thenReturn(false);
    when(sameQuestionValidator.tryNewQuestions(expectedMoviePair)).thenReturn(true);
    when(sameQuestionValidator.tryNewQuestions(expectedMoviePair2)).thenReturn(false);
    when(questionAnsweredValidator.tryNewQuestions(expectedMoviePair2)).thenReturn(false);
    when(movieGenerator.get())
        .thenReturn(movies.get(0), movies.get(0), movies.get(1), movies.get(2));

    final QuestionResponse questionResponse = target.getMoviePair(SESSION_ID);

    verify(gameHistoryManagement).linkQuestionToUser(expectedMoviePair2);

    assertThat(questionResponse).isEqualToComparingFieldByField(expectedQuestionResponse);
  }

  @Test
  public void ifHasAnyOpenQuestionItShouldReturnOpenedQuestion() {

    final List<Movie> movies = createMovies(2);
    final MoviePair expectedMoviePair = createMoviePair(movies.get(0), movies.get(1));
    final QuestionResponse expectedQuestionResponse = createQuestionResponse(expectedMoviePair);

    // Tentou criar uma questao, mas o usuario ja havia outra
    // pergunta aberta, entao retornou a aberta
    when(gameHistoryManagement.hasAnyOpenQuestion(SESSION_ID)).thenReturn(true);
    when(gameHistoryManagement.getOpenedQuestion(SESSION_ID))
        .thenReturn(createMoviePair(movies.get(0), movies.get(1)));

    final QuestionResponse questionResponse = target.getMoviePair(SESSION_ID);

    verify(gameHistoryManagement).hasAnyOpenQuestion(anyString());
    verify(gameHistoryManagement, never()).linkQuestionToUser(expectedMoviePair);

    assertThat(questionResponse).isEqualToComparingFieldByField(expectedQuestionResponse);
  }

  @Test
  public void ifHasAnsweredQuestionTryAgainAndReturnSuccess() {

    final List<Movie> movies = createMovies(4);

    final MoviePair expectedMoviePair = createMoviePair(movies.get(0), movies.get(1));
    final MoviePair expectedMoviePair2 = createMoviePair(movies.get(2), movies.get(3));

    final QuestionResponse expectedQuestionResponse = createQuestionResponse(expectedMoviePair2);

    // Dois primeiros filmes ja foram respondidos, entao tente novamente
    when(gameHistoryManagement.hasAnyOpenQuestion(SESSION_ID)).thenReturn(false);
    when(sameQuestionValidator.tryNewQuestions(expectedMoviePair)).thenReturn(false);
    when(sameQuestionValidator.tryNewQuestions(expectedMoviePair2)).thenReturn(false);
    when(questionAnsweredValidator.tryNewQuestions(expectedMoviePair)).thenReturn(true);
    when(questionAnsweredValidator.tryNewQuestions(expectedMoviePair2)).thenReturn(false);
    when(movieGenerator.get())
        .thenReturn(movies.get(0), movies.get(1), movies.get(2), movies.get(3));

    final QuestionResponse questionResponse = target.getMoviePair(SESSION_ID);

    verify(gameHistoryManagement).linkQuestionToUser(expectedMoviePair2);

    assertThat(questionResponse).isEqualToComparingFieldByField(expectedQuestionResponse);
  }
}
