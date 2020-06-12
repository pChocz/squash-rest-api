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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "roles_for_leagues")
@Getter
@NoArgsConstructor
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

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "league_id")
  private League league;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
          joinColumns = @JoinColumn(name = "roles_for_leagues_id"),
          inverseJoinColumns = @JoinColumn(name = "player_id")
  )
  private Set<Player> players = new HashSet<>();

  @Setter
  @Column(name="league_role")
  @Enumerated(EnumType.STRING)
  private LeagueRole leagueRole;

  @Override
  public String toString() {
    return leagueRole.name();
  }

}
