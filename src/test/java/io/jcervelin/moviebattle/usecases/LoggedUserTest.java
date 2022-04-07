package io.jcervelin.moviebattle.usecases;

import static io.jcervelin.moviebattle.TestUtils.USER_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import io.jcervelin.moviebattle.security.domains.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class LoggedUserTest {

  @Test
  public void shouldReturnUsernameInTheContext() {
    final LoggedUser target = new LoggedUser();
    SecurityContextHolder.getContext()
        .setAuthentication(
            new TestingAuthenticationToken(User.builder().username(USER_NAME).build(), null));

    final String username = target.getUsername();
    assertThat(username).isEqualTo(USER_NAME);
  }
}
