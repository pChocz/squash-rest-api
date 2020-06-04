package com.pj.squashrestapp.model;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "rounds")
@Getter
@Setter
@NoArgsConstructor
public class Round {


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

  @Column(name = "date")
  private Date date;

  @OneToMany(mappedBy = "round", cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.REMOVE})
  private List<RoundGroup> roundGroups;

  @ManyToOne
  @JoinColumn(name = "season_id", referencedColumnName = "id")
  private Season season;

  @Column(name = "finished")
  private boolean finished;

  @Column(name = "split")
  private String split;

  public Round(final int number, final Date date, final Season season) {
    this.number = number;
    this.date = date;
    this.season = season;
  }

  /**
   * Used to create new empty Round from scratch
   *
   * @param number
   * @param date
   * @param season
   * @param mapOfPlayersForEachGroup
   */
  public Round(final int number, final Date date, final Season season, final Map<Integer, List<Player>> mapOfPlayersForEachGroup) {
    this.number = number;
    this.date = date;
    this.season = season;

    this.roundGroups = new ArrayList<>();
    for (final Integer groupNumber : mapOfPlayersForEachGroup.keySet()) {
      final RoundGroup roundGroup = new RoundGroup(groupNumber, mapOfPlayersForEachGroup.get(groupNumber), this);
      roundGroup.generateEmptyMatches();
      this.roundGroups.add(roundGroup);
    }
  }

  @Override
  public String toString() {
    return "Season " + season.getNumber() + " / Round " + number + " / Date: " + date;
  }

  public int getNumberOfPlayers() {
    return this
            .roundGroups
            .stream()
            .mapToInt(roundGroup -> roundGroup.getPlayers().size())
            .sum();
  }

  public int getNumberOfPlayersInGroupOfNumber(int groupNumber) {
    return this
            .roundGroups
            .stream()
            .filter(roundGroup -> roundGroup.getNumber() == groupNumber)
            .findFirst()
            .get()
            .getPlayers()
            .size();
  }

  public int getNumberOfGroups() {
    return this
            .roundGroups
            .size();
  }

  public void addRoundGroup(RoundGroup roundGroup) {
    if (this.roundGroups == null) {
      this.roundGroups = new ArrayList<>();
    }
    this.roundGroups.add(roundGroup);
  }

}
