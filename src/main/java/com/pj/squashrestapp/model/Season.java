package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.util.EntityVisitor;
import com.pj.squashrestapp.model.util.Identifiable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "seasons")
@Getter
@NoArgsConstructor
public class Season implements Identifiable, Comparable<Season> {

  public static EntityVisitor<Season, League> ENTITY_VISITOR_FINAL = new EntityVisitor<>(Season.class) {
  };

  public static EntityVisitor<Season, League> ENTITY_VISITOR = new EntityVisitor<>(Season.class) {
    @Override
    public League getParent(final Season visitingObject) {
      return visitingObject.getLeague();
    }

    @Override
    public Set<Season> getChildren(final League parent) {
      return parent.getSeasons();
    }

    @Override
    public void setChildren(final League parent) {
      parent.setSeasons(new TreeSet<Season>());
    }
  };

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name = "uuid",
          nullable = false,
          updatable = false)
  private UUID uuid = UUID.randomUUID();

  @Setter
  @Column(name = "number")
  private int number;

  @Setter
  @Column(name = "start_date")
  private LocalDate startDate;

  @Setter
  @OneToMany(
          mappedBy = "season",
          cascade = CascadeType.ALL,
          fetch = FetchType.LAZY,
          orphanRemoval = true)
  private Set<Round> rounds = new TreeSet<>();

  @Setter
  @OneToMany(
          mappedBy = "season",
          cascade = CascadeType.ALL,
          fetch = FetchType.LAZY,
          orphanRemoval = true)
  private Set<BonusPoint> bonusPoints = new HashSet<>();

  @JsonIgnore
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "league_id")
  private League league;

  public Season(final int number, final LocalDate startDate) {
    this.number = number;
    this.startDate = startDate;
  }

  public void addRound(final Round round) {
    this.rounds.add(round);
    round.setSeason(this);
  }

  public void addBonusPoint(final BonusPoint bonusPoint) {
    this.bonusPoints.add(bonusPoint);
    bonusPoint.setSeason(this);
  }

  @Override
  public String toString() {
    return "Season " + number + " | start date: " + startDate;
  }

  public List<Round> getRoundsOrdered() {
    return this
            .getRounds()
            .stream()
            .sorted(Comparator.comparingInt(Round::getNumber))
            .collect(Collectors.toList());
  }

  @Override
  public int compareTo(final Season that) {
    return Comparator
            .comparingInt(Season::getNumber)
            .compare(this, that);
  }

}
