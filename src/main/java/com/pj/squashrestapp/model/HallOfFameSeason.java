package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
        name = "hall_of_fame_season",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"league_id", "season_number"})})

@Getter
@NoArgsConstructor
public class HallOfFameSeason {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "league_id")
  private League league;

  @Setter
  @Column(name = "season_number")
  private int seasonNumber;

  @Setter
  @Column(name = "league_1st")
  private String league1stPlace;

  @Setter
  @Column(name = "league_2nd")
  private String league2ndPlace;

  @Setter
  @Column(name = "league_3rd")
  private String league3rdPlace;

  @Setter
  @Column(name = "cup_1st")
  private String cup1stPlace;

  @Setter
  @Column(name = "cup_2nd")
  private String cup2ndPlace;

  @Setter
  @Column(name = "cup_3rd")
  private String cup3rdPlace;

  @Setter
  @Column(name = "super_cup")
  private String superCupWinner;

  @Setter
  @Column(name = "pretenders_cup")
  private String pretendersCupWinner;

  @Setter
  @Column(name = "covid")
  private String coviders;

  @Setter
  @Column(name = "all_rounds_attendees")
  private String allRoundsAttendees;

  public HallOfFameSeason(final int seasonNumber) {
    this.seasonNumber = seasonNumber;
  }

}
