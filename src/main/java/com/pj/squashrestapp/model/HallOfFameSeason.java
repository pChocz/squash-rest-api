package com.pj.squashrestapp.model;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "hall_of_fame_season")
@Getter @Setter
@NoArgsConstructor
public class HallOfFameSeason {

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(referencedColumnName = "id")
  private League league;

  @Column(name = "season_number")
  private int seasonNumber;

  @Column(name = "league_1st")
  private String league1stPlace;

  @Column(name = "league_2nd")
  private String league2ndPlace;

  @Column(name = "league_3rd")
  private String league3rdPlace;

  @Column(name = "cup_1st")
  private String cup1stPlace;

  @Column(name = "cup_2nd")
  private String cup2ndPlace;

  @Column(name = "cup_3rd")
  private String cup3rdPlace;

  @Column(name = "super_cup")
  private String superCupWinner;

  public HallOfFameSeason(final League league, final int seasonNumber) {
    this.league = league;
    this.seasonNumber = seasonNumber;
  }

}
