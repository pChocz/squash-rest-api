package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "players")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Player {

  @JsonIgnore
  @ManyToMany(
          mappedBy = "players",
          fetch = FetchType.LAZY)
  private final Set<Authority> authorities = new HashSet<>();
  @JsonIgnore
  @ManyToMany(
          mappedBy = "players",
          fetch = FetchType.LAZY)
  private final Set<RoleForLeague> roles = new HashSet<>();
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Setter
  @Column(name = "uuid",
          nullable = false)
  @EqualsAndHashCode.Include
  private UUID uuid = UUID.randomUUID();
  @Setter
  @Column(name = "username", unique = true)
  private String username;
  @JsonIgnore
  @Setter
  @Column(name = "password")
  private String password;
  @Setter
  @Column(name = "enabled")
  private boolean enabled;
  @Setter
  @Column(name = "email", unique = true)
  private String email;
  @Setter
  @Column(name = "password_session_uuid")
  private UUID passwordSessionUuid;

  public Player(final String username, final String email) {
    this.username = username;
    this.email = email;
    this.enabled = false;
    this.passwordSessionUuid = UUID.randomUUID();
  }

  public Player(final String username) {
    this.username = username;
    this.enabled = false;
    this.passwordSessionUuid = UUID.randomUUID();
  }

  public void addRole(final RoleForLeague roleForLeague) {
    this.roles.add(roleForLeague);
    roleForLeague.getPlayers().add(this);
  }

  public void removeRole(final RoleForLeague roleForLeague) {
    this.roles.remove(roleForLeague);
    roleForLeague.getPlayers().remove(this);
  }

  public void addAuthority(final Authority authority) {
    this.authorities.add(authority);
    authority.getPlayers().add(this);
  }

  @Override
  public String toString() {
    return username;
  }

}