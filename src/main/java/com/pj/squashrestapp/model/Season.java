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
import javax.persistence.Table;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "seasons")
@Getter
@NoArgsConstructor
public class Season implements Identifiable {

  public static EntityVisitor<Season, League> ENTITY_VISITOR_FINAL = new EntityVisitor<>(Season.class) {
  };

  public static EntityVisitor<Season, League> ENTITY_VISITOR = new EntityVisitor<>(Season.class) {
    @Override
    public League getParent(final Season visitingObject) {
      return visitingObject.getLeague();
    }

    @Override
    public List<Season> getChildren(final League parent) {
      return parent.getSeasons();
    }

    @Override
    public void setChildren(final League parent) {
      parent.setSeasons(new ArrayList<Season>());
    }
  };

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
  @Column(name = "number")
  private int number;

  @Setter
  @Column(name = "start_date")
  private Date startDate;

  @Setter
  @OneToMany(
          mappedBy = "season",
          cascade = CascadeType.ALL,
          fetch = FetchType.LAZY,
          orphanRemoval = true)
  private List<Round> rounds = new ArrayList<>();

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

}
