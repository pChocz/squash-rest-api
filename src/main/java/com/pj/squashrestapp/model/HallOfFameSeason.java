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
@Getter
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

}
