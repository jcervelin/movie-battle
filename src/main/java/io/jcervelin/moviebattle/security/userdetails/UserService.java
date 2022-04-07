package io.jcervelin.moviebattle.security.userdetails;

import io.jcervelin.moviebattle.security.domains.Role;
import io.jcervelin.moviebattle.security.domains.User;
import io.jcervelin.moviebattle.security.entities.RoleEntity;
import io.jcervelin.moviebattle.security.entities.UserEntity;
import io.jcervelin.moviebattle.security.repositories.UserRepository;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity userEntity =
        userRepository
            .findByEmail(username)
            .orElseThrow(
                () -> {
                  log.error("Email not found: " + username);
                  throw new UsernameNotFoundException("Email not found");
                });

    User userDetails = convertEntityToUser(userEntity);

    log.info("Email found: " + username);
    return userDetails;
  }

  private User convertEntityToUser(UserEntity userEntity) {
    return User.builder()
        .id(userEntity.getId())
        .name(userEntity.getName())
        .username(userEntity.getEmail())
        .password(userEntity.getPassword())
        .roles(convertEntityToRole(userEntity.getRoles()))
        .build();
  }

  private Role convertEntityToRole(RoleEntity roleEntity) {
    return Role.builder().roleName(roleEntity.getRoleName()).id(roleEntity.getId()).build();
  }

  private Set<Role> convertEntityToRole(Set<RoleEntity> roleEntities) {
    return roleEntities.stream().map(this::convertEntityToRole).collect(Collectors.toSet());
  }
}
