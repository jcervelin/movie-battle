package io.jcervelin.moviebattle.configs;

import io.jcervelin.moviebattle.domains.Movie;
import io.jcervelin.moviebattle.domains.entities.MovieEntity;
import io.jcervelin.moviebattle.gateways.databases.MovieRepository;
import io.jcervelin.moviebattle.usecases.MovieMapCache;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BattleConfig {
  @Bean
  public Random randomGenerator() {
    return new Random();
  }

  @Bean
  public MovieMapCache movieMapCache(MovieRepository movieRepository) {
    final List<MovieEntity> omdbEntities = movieRepository.findAll();
    final Map<String, Movie> movies = new ConcurrentHashMap<>();

    omdbEntities.forEach(
        omdbEntity -> {
          final Movie movie =
              Movie.builder()
                  .id(omdbEntity.getId())
                  .imdbRating(omdbEntity.getImdbRating())
                  .imdbVotes(omdbEntity.getImdbVotes())
                  .title(omdbEntity.getTitle())
                  .build();
          movies.put(movie.getId(), movie);
        });
    return new MovieMapCache(movies);
  }

  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }
}
