package io.jcervelin.moviebattle.security.entities;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true) // anotação para não deixar o email se repetir.
  private String email;

  private String name;
  private String password;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "tb_user_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<RoleEntity> roles;
}
