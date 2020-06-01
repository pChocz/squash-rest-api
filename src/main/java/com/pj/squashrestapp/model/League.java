package com.pj.squashrestapp.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "leagues")
@Getter
@Setter
@NoArgsConstructor
public class League {

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

  @Column(name = "name", unique = true)
  private String name;

  @OneToMany(mappedBy = "league")
  private List<Season> seasons;

  @OneToMany(mappedBy = "league")
  private List<RoleForLeague> rolesForLeague;

  @Column(name = "logo")
  private Blob logo;

  @OneToMany(mappedBy = "league", cascade = CascadeType.REFRESH)
  private List<HallOfFameSeason> hallOfFameSeasons;

  public League(final String name) {
    this.name = name;
  }

  public void addSeason(final Season season) {
    if (this.seasons == null) {
      this.seasons = new ArrayList<>();
    }
    this.seasons.add(season);
  }

  @Override
  public String toString() {
    return name + " (" + seasons.size() + " seasons)";
  }

}
