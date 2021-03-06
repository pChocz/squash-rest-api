package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bonus_points")
@Getter
@NoArgsConstructor
public class BonusPoint {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name = "uuid", nullable = false)
  private UUID uuid = UUID.randomUUID();

  @JsonIgnore
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "season_id")
  private Season season;

  @Setter
  @Column(name = "date")
  private LocalDate date;

  @Setter
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "winner_id")
  private Player winner;

  @Setter
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "looser_id")
  private Player looser;

  @Setter
  @Column(name = "points")
  private int points;

  public BonusPoint(
      final Player winner, final Player looser, final int points, final LocalDate date) {

    this.winner = winner;
    this.looser = looser;
    this.points = points;
    this.date = date;
  }

  @Override
  public String toString() {
    return uuid
        + " -> "
        + winner.getUsername()
        + " vs. "
        + looser.getUsername()
        + " | Points: "
        + points;
  }
}
