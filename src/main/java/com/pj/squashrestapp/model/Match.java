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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
public class Match implements Identifiable {

  public static EntityVisitor<Match, RoundGroup> ENTITY_VISITOR_FINAL = new EntityVisitor<>(Match.class) {
  };

  public static EntityVisitor<Match, RoundGroup> ENTITY_VISITOR = new EntityVisitor<>(Match.class) {
    @Override
    public RoundGroup getParent(final Match visitingObject) {
      return visitingObject.getRoundGroup();
    }

    @Override
    public List<Match> getChildren(final RoundGroup parent) {
      return parent.getMatches();
    }

    @Override
    public void setChildren(final RoundGroup parent) {
      parent.setMatches(new ArrayList<Match>());
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

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "first_player_id", referencedColumnName = "id")
  private Player firstPlayer;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "second_player_id", referencedColumnName = "id")
  private Player secondPlayer;

  @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<SetResult> setResults;

  private transient String _1stSetResult;
  private transient String _2ndSetResult;
  private transient String _3rdSetResult;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "round_group_id", referencedColumnName = "id")
  private RoundGroup roundGroup;

  public Match(final RoundGroup roundGroup, final Player firstPlayer, final Player secondPlayer, final boolean withAllEmptySets) {
    this.roundGroup = roundGroup;
    this.firstPlayer = firstPlayer;
    this.secondPlayer = secondPlayer;
    this.setResults = new ArrayList<>();
    this.setResults.add(new SetResult(1, this));
    this.setResults.add(new SetResult(2, this));
    this.setResults.add(new SetResult(3, this));
  }

  public Match(final RoundGroup roundGroup, final Player firstPlayer, final Player secondPlayer) {
    this.roundGroup = roundGroup;
    this.firstPlayer = firstPlayer;
    this.secondPlayer = secondPlayer;
  }

  @Override
  public String toString() {
    return "[" + dbId() + "] " + firstPlayer + " vs. " + secondPlayer + " : " + setResults;
  }

  private String dbId() {
    return id == null
            ? "no id"
            : String.valueOf(id);
  }

}
