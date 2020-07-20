package com.pj.squashrestapp.model;

import com.pj.squashrestapp.model.util.EntityVisitor;
import com.pj.squashrestapp.model.util.Identifiable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "leagues")
@Getter
@NoArgsConstructor
public class League implements Identifiable {

  public static EntityVisitor<League, Identifiable> ENTITY_VISITOR_FINAL = new EntityVisitor<>(League.class) {
  };

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name = "name", unique = true)
  private String name;

  @Setter
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "league_logo_id")
  private LeagueLogo leagueLogo;

  @Setter
  @OneToMany(
          mappedBy = "league",
          cascade = CascadeType.ALL,
          fetch = FetchType.LAZY,
          orphanRemoval = true)
  private List<Season> seasons = new ArrayList<>();

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
  private final List<HallOfFameSeason> hallOfFameSeasons = new ArrayList<>();

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

  public void addHallOfFameSeason(final HallOfFameSeason hallOfFameSeason) {
    this.hallOfFameSeasons.add(hallOfFameSeason);
    hallOfFameSeason.setLeague(this);
  }

  @Override
  public String toString() {
    return name + " (" + seasons.size() + " seasons)";
  }

  public List<Season> getSeasonsOrdered() {
    return this
            .getSeasons()
            .stream()
            .sorted(Comparator.comparingInt(Season::getNumber))
            .collect(Collectors.toList());
  }

}
