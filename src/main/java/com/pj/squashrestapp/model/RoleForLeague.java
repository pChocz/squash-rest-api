package com.pj.squashrestapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles_for_leagues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleForLeague {

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

  @ManyToOne
  @JoinColumn(name = "league_id", referencedColumnName = "id")
  private League league;

  @ManyToMany
  @JoinTable(
          joinColumns = @JoinColumn(name = "roles_for_leagues_id", referencedColumnName = "id"),
          inverseJoinColumns = @JoinColumn(name = "player_id", referencedColumnName = "id")
  )
  private List<Player> players;

  @Column(name="league_role")
  @Enumerated(EnumType.STRING)
  private LeagueRole leagueRole;

  public RoleForLeague(final League league, final LeagueRole leagueRole) {
    this.players = new ArrayList<>();
    this.league = league;
    this.leagueRole = leagueRole;
  }

  @Override
  public String toString() {
    return leagueRole.name();
  }

}
