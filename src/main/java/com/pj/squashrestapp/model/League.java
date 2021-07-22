package com.pj.squashrestapp.model;

import com.pj.squashrestapp.model.entityvisitor.EntityVisitor;
import com.pj.squashrestapp.model.entityvisitor.Identifiable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "leagues")
@Getter
@NoArgsConstructor
public class League implements Identifiable {

  public static EntityVisitor<League, Identifiable> ENTITY_VISITOR_FINAL =
      new EntityVisitor<>(League.class) {};

  @OneToMany(
      mappedBy = "league",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private final List<RoleForLeague> rolesForLeague = new ArrayList<>();

  @OneToMany(
      mappedBy = "league",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private final List<TrophyForLeague> trophiesForLeague = new ArrayList<>();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name = "uuid", nullable = false)
  private UUID uuid = UUID.randomUUID();

  @Setter
  @Column(name = "name", unique = true)
  private String name;

  @Setter
  @Column(name = "location")
  private String location;

  @Setter
  @Column(name = "time")
  private String time;

  @Setter
  @Enumerated(EnumType.STRING)
  private MatchFormatType matchFormatType;

  @Setter
  @Enumerated(EnumType.STRING)
  private SetWinningType regularSetWinningType;

  @Setter
  @Enumerated(EnumType.STRING)
  private SetWinningType tiebreakWinningType;

  @Setter
  @Column(name = "regular_set_winning_points")
  private int regularSetWinningPoints;

  @Setter
  @Column(name = "tie_break_winning_points")
  private int tiebreakWinningPoints;

  @Setter
  @Column(name = "number_of_rounds_per_season")
  private int numberOfRoundsPerSeason;

  @Setter
  @Column(name = "rounds_to_be_deducted")
  private int roundsToBeDeducted;

  @Setter
  @Column(name = "date_of_creation")
  private LocalDateTime dateOfCreation;

  @Setter
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "league_logo_id")
  private LeagueLogo leagueLogo;

  @Setter
  @OneToMany(
      mappedBy = "league",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private Set<Season> seasons = new TreeSet<>();

  @Setter
  @OneToMany(
      mappedBy = "league",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private Set<LeagueRule> rules = new HashSet<>();

  @Setter
  @OneToMany(
      mappedBy = "league",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private Set<AdditionalMatch> additionalMatches = new TreeSet<>();

  public League(final String name) {
    this.name = name;
  }

  public void addSeason(final Season season) {
    this.seasons.add(season);
    season.setLeague(this);
  }

  public void addRoleForLeague(final RoleForLeague roleForLeague) {
    this.rolesForLeague.add(roleForLeague);
    roleForLeague.setLeague(this);
  }

  public void addTrophyForLeague(final TrophyForLeague trophyForLeague) {
    this.trophiesForLeague.add(trophyForLeague);
    trophyForLeague.setLeague(this);
  }

  public void addRuleForLeague(final LeagueRule leagueRule) {
    this.rules.add(leagueRule);
    leagueRule.setLeague(this);
  }

  public void addAdditionalMatch(final AdditionalMatch match) {
    this.additionalMatches.add(match);
    match.setLeague(this);
  }

  @Override
  public String toString() {
    return name + " (" + seasons.size() + " seasons)";
  }

  public List<Season> getSeasonsOrdered() {
    return this.getSeasons().stream()
        .sorted(Comparator.comparingInt(Season::getNumber))
        .collect(Collectors.toList());
  }
}
