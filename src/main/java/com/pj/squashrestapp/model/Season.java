package com.pj.squashrestapp.model;

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
import java.util.List;

@Entity
@Table(name = "seasons")
@Getter
@Setter
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

  @Column(name = "number")
  private int number;

  @Column(name = "start_date")
  private Date startDate;

  @OneToMany(mappedBy = "season", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
  private List<Round> rounds;

//  @OneToMany(mappedBy = "season", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
//  private List<SeasonResult> seasonResults;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "league_id", referencedColumnName = "id")
  private League league;

  public Season(final League league, final int number, final Date startDate) {
    this.league = league;
    this.number = number;
    this.startDate = startDate;

    this.rounds = new ArrayList<>();
//    this.seasonResults = new ArrayList<>();
  }

  public void addRound(final Round round) {
    this.rounds.add(round);
  }

  @Override
  public String toString() {
    return "Season " + number + " | start date: " + startDate;
  }

}
