package io.jcervelin.moviebattle.usecases;

import static org.assertj.core.api.Assertions.assertThat;

import io.jcervelin.moviebattle.domains.BattleProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"app.max-errors=5"})
public class BattlePropertiesTest {

  @Autowired private BattleProperties target;

  @Test
  public void shouldReadFromPropertiesFile() {
    assertThat(target.getMaxErrors()).isEqualTo(5);
  }
}
