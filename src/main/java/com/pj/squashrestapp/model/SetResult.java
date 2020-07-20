package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.util.EntityVisitor;
import com.pj.squashrestapp.model.util.Identifiable;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "set_results")
@Getter
@NoArgsConstructor
public class SetResult implements Identifiable {

  public static EntityVisitor<SetResult, Match> ENTITY_VISITOR = new EntityVisitor<>(SetResult.class) {
    @Override
    public Match getParent(final SetResult visitingObject) {
      return visitingObject.getMatch();
    }

    @Override
    public List<SetResult> getChildren(final Match parent) {
      return parent.getSetResults();
    }

    @Override
    public void setChildren(final Match parent) {
      parent.setSetResults(new ArrayList<SetResult>());
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
  private int firstPlayerScore;

  @Setter
  @Column(name = "second_player_score")
  private int secondPlayerScore;

  @JsonIgnore
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "match_id")
  private Match match;

  public SetResult(final int number, final int firstPlayerScore, final int secondPlayerScore) {
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

}
