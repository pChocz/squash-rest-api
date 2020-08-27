package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "players")
@Getter
@NoArgsConstructor
public class Player {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name = "uuid",
          nullable = false)
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

  public Player(final String username, final String email) {
    this.username = username;
    this.email = email;
    this.enabled = false;
    this.passwordSessionUuid = UUID.randomUUID();
  }

  public void addRole(final RoleForLeague roleForLeague) {
    this.roles.add(roleForLeague);
    roleForLeague.getPlayers().add(this);
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