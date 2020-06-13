package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "players")
@Getter
@NoArgsConstructor
public class Player {

  @Id
  @Column(name = "id",
          nullable = false,
          updatable = false)
  @GeneratedValue(
          strategy = GenerationType.AUTO,
          generator = "native")
  @GenericGenerator(
          name = "native",
          strategy = "native")
  private Long id;

  @Setter
  @Column(name = "username", unique = true)
  private String username;

  @JsonIgnore
  @Setter
  @Column(name = "password")
  private String password;

  @Setter
  @Column(name = "email", unique = true)
  private String email;

  @JsonIgnore
  @ManyToMany(
          mappedBy = "players",
          fetch = FetchType.LAZY)
  private Set<Authority> authorities = new HashSet<>();

  @JsonIgnore
  @ManyToMany(
          mappedBy = "players",
          fetch = FetchType.LAZY)
  private Set<RoleForLeague> roles = new HashSet<>();

  public Player(final String username, final String email) {
    this.username = username;
    this.email = email;
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