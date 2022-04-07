package io.jcervelin.moviebattle.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.jcervelin.moviebattle.domains.HallPosition;
import io.jcervelin.moviebattle.domains.entities.IHallPositionProjection;
import io.jcervelin.moviebattle.gateways.databases.GameSessionRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HallOfFameGeneratorTest {

  @InjectMocks private HallOfFameGenerator target;
  @Mock private GameSessionRepository repository;

  @Test
  public void shouldDisplayTop10WithPlacePopulated() {

    final List<IHallPositionProjection> projections = createProjections(3);

    when(repository.findTop10Players()).thenReturn(projections);

    final List<HallPosition> top10 = target.getTop10();

    assertThat(top10.get(0).getPlace()).isEqualTo(1);
    assertThat(top10.get(0).getUsername()).isEqualTo("user1");
    assertThat(top10.get(0).getAbsoluteScore()).isEqualTo(1);
    assertThat(top10.get(1).getPlace()).isEqualTo(2);
    assertThat(top10.get(1).getUsername()).isEqualTo("user2");
    assertThat(top10.get(1).getAbsoluteScore()).isEqualTo(2);
    assertThat(top10.get(2).getPlace()).isEqualTo(3);
    assertThat(top10.get(2).getUsername()).isEqualTo("user3");
    assertThat(top10.get(2).getAbsoluteScore()).isEqualTo(3);
  }

  @Test
  public void shouldReturnEmptyListWhenHallIsEmpty() {

    when(repository.findTop10Players()).thenReturn(Collections.emptyList());

    final List<HallPosition> top10 = target.getTop10();

    assertThat(top10).isEmpty();
  }

  List<IHallPositionProjection> createProjections(Integer num) {
    return IntStream.rangeClosed(1, num)
        .boxed()
        .map(i -> new TestHallPosition("user" + i, i))
        .collect(Collectors.toList());
  }

  class TestHallPosition implements IHallPositionProjection {

    public TestHallPosition(String username, Integer absoluteScore) {
      this.username = username;
      this.absoluteScore = absoluteScore;
    }

    private String username;
    private Integer absoluteScore;

    @Override
    public String getUsername() {
      return username;
    }

    @Override
    public Integer getAbsoluteScore() {
      return absoluteScore;
    }
  }
}
