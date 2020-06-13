package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "bonus_points")
@Getter
@NoArgsConstructor
public class BonusPoint {

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

  @JsonIgnore
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "season_id")
  private Season season;

  @Setter
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "player_id")
  private Player player;

  @Setter
  private int points;

  @Override
  public String toString() {
    return id + " -> Player: " + player.getUsername() + " | Points: " + points;
  }

}
