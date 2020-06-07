package com.pj.squashrestapp.model;

import com.pj.squashrestapp.model.util.EntityVisitor;
import com.pj.squashrestapp.model.util.Identifiable;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "set_results")
@Getter
@Setter
@NoArgsConstructor
public class SetResult implements Identifiable {

  public static EntityVisitor<SetResult, Match> ENTITY_VISITOR = new EntityVisitor<SetResult, Match>(SetResult.class) {

    @Override
    public Match getParent(SetResult visitingObject) {
      return visitingObject.getMatch();
    }

    @Override
    public List<SetResult> getChildren(Match parent) {
      return parent.getSetResults();
    }

    @Override
    public void setChildren(Match parent) {
      parent.setSetResults(new ArrayList<SetResult>());
    }
  };

  private static final String REGEX = "(\\s*\\d{1,2})\\D+(\\d{1,2}\\s*)";
  private static final Pattern PATTERN = Pattern.compile(REGEX);

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

  @Column(name = "first_player_score")
  private int firstPlayerScore;

  @Column(name = "second_player_score")
  private int secondPlayerScore;

  @ManyToOne
  @JoinColumn(name = "match_id", referencedColumnName = "id")
  private Match match;

  public SetResult(final int number, final Match match) {
    this.number = number;
    this.match = match;
  }

  @Override
  public String toString() {
    return firstPlayerScore + ":" + secondPlayerScore;
  }

  public Player getWinner() {
    return firstPlayerScore > secondPlayerScore
            ? match.getFirstPlayer()
            : match.getSecondPlayer();
  }

}
