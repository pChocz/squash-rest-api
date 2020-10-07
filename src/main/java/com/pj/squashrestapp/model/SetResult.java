package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.entityvisitor.EntityVisitor;
import com.pj.squashrestapp.model.entityvisitor.Identifiable;
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
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name = "set_results")
@Getter
@NoArgsConstructor
public class SetResult implements Identifiable, Comparable<SetResult> {

  public static EntityVisitor<SetResult, Match> ENTITY_VISITOR = new EntityVisitor<>(SetResult.class) {
    @Override
    public Match getParent(final SetResult visitingObject) {
      return visitingObject.getMatch();
    }

    @Override
    public Set<SetResult> getChildren(final Match parent) {
      return parent.getSetResults();
    }

    @Override
    public void setChildren(final Match parent) {
      parent.setSetResults(new TreeSet<SetResult>());
    }
  };

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name = "number")
  private int number;

  @Setter
  @Column(name = "first_player_score")
  private Integer firstPlayerScore;

  @Setter
  @Column(name = "second_player_score")
  private Integer secondPlayerScore;

  @JsonIgnore
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "match_id")
  private Match match;

  public SetResult(final int number, final Integer firstPlayerScore, final Integer secondPlayerScore) {
    this.number = number;
    this.firstPlayerScore = firstPlayerScore;
    this.secondPlayerScore = secondPlayerScore;
  }

  @Override
  public String toString() {
    return firstPlayerScore + ":" + secondPlayerScore;
  }

  // todo: move somewhere else later
  public Player checkWinner() {
    return firstPlayerScore > secondPlayerScore
            ? match.getFirstPlayer()
            : firstPlayerScore < secondPlayerScore
            ? match.getSecondPlayer()
            : null;
  }

  @Override
  public int compareTo(final SetResult that) {
    return Comparator
            .comparingInt(SetResult::getNumber)
            .compare(this, that);
  }

  public boolean nonNull() {
    return this.getFirstPlayerScore() != null
            && this.getSecondPlayerScore() != null;
  }

}
