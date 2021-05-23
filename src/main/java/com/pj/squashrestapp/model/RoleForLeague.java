package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles_for_leagues")
@Getter
@NoArgsConstructor
public class RoleForLeague {

  @JsonIgnore
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      joinColumns = @JoinColumn(name = "roles_for_leagues_id"),
      inverseJoinColumns = @JoinColumn(name = "player_id"))
  private final Set<Player> players = new HashSet<>();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "league_id")
  private League league;

  @Setter
  @Column(name = "league_role")
  @Enumerated(EnumType.STRING)
  private LeagueRole leagueRole;

  public RoleForLeague(final LeagueRole leagueRole) {
    this.leagueRole = leagueRole;
  }

  @Override
  public String toString() {
    return leagueRole.name();
  }
}
