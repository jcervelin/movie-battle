package io.jcervelin.moviebattle.usecases;

import io.jcervelin.moviebattle.domains.HallPosition;
import io.jcervelin.moviebattle.domains.entities.IHallPositionProjection;
import io.jcervelin.moviebattle.gateways.databases.GameSessionRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HallOfFameGenerator {

  private final GameSessionRepository repository;

  public List<HallPosition> getTop10() {

    final List<IHallPositionProjection> top10Players = repository.findTop10Players();

    // Converte as projecoes em HallPosition e usa sua posicao
    // na lista para definir o lugar no Hall of Fame
    return IntStream.rangeClosed(1, top10Players.size())
        .boxed()
        .map(
            position -> {
              var projection = top10Players.get(position - 1);
              return HallPosition.builder()
                  .absoluteScore(projection.getAbsoluteScore())
                  .username(projection.getUsername())
                  .place(position)
                  .build();
            })
        .collect(Collectors.toList());
  }
}
