package com.pj.squashrestapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "round_results")
@Getter
@Setter
@NoArgsConstructor
public class RoundResult {

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

  @Column(name = "round_number")
  private int roundNumber;

  @Column(name = "points")
  private int points;

  @ManyToOne
  @JoinColumn(name = "season_result_id", referencedColumnName = "id")
  private SeasonResult seasonResult;

  public RoundResult(final int roundNumber, final SeasonResult seasonResult) {
    this.roundNumber = roundNumber;
    this.points = 0;
    this.seasonResult = seasonResult;
  }

}
