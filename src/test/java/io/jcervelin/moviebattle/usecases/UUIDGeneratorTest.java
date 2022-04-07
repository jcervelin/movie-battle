package io.jcervelin.moviebattle.usecases;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class UUIDGeneratorTest {

  @Test
  public void shouldGenerateRandomStrings() {
    final UUIDGenerator uuidGenerator = new UUIDGenerator();
    final String id1 = uuidGenerator.generate();
    final String id2 = uuidGenerator.generate();

    assertThat(id1).isNotEqualTo(id2);
  }
}
