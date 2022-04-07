package io.jcervelin.moviebattle.gateways.databases;

import io.jcervelin.moviebattle.domains.entities.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<MovieEntity, String> {}
