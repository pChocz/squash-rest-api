package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.entityvisitor.EntityVisitor;
import com.pj.squashrestapp.model.entityvisitor.Identifiable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@Entity
@Table(name = "matches")
@Getter
@NoArgsConstructor
public class Match implements Identifiable, Comparable<Match> {

  private static final int DEFAULT_NUMBER_OF_SETS = 3;

  public static EntityVisitor<Match, RoundGroup> ENTITY_VISITOR_FINAL = new EntityVisitor<>(Match.class) {
  };

  public static EntityVisitor<Match, RoundGroup> ENTITY_VISITOR = new EntityVisitor<>(Match.class) {
    @Override
    public RoundGroup getParent(final Match visitingObject) {
      return visitingObject.getRoundGroup();
    }

    @Override
    public Set<Match> getChildren(final RoundGroup parent) {
      return parent.getMatches();
    }

    @Override
    public void setChildren(final RoundGroup parent) {
      parent.setMatches(new TreeSet<>());
    }
  };

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name = "uuid",
          nullable = false)
  private UUID uuid = UUID.randomUUID();

  @Setter
  @Column(name = "number")
  private int number;

  @Setter
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "first_player_id")
  private Player firstPlayer;

  @Setter
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "second_player_id")
  private Player secondPlayer;

  @Setter
  @OneToMany(
          mappedBy = "match",
          cascade = CascadeType.ALL,
          fetch = FetchType.LAZY,
          orphanRemoval = true)
  private Set<SetResult> setResults = new TreeSet<>();

  @JsonIgnore
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "round_group_id")
  private RoundGroup roundGroup;

  public Match(final Player firstPlayer, final Player secondPlayer) {
    this.firstPlayer = firstPlayer;
    this.secondPlayer = secondPlayer;
  }

  public void addSetResult(final SetResult setResult) {
    this.setResults.add(setResult);
    setResult.setMatch(this);
  }

  @Override
  public String toString() {
    return "[" + getUuid() + "] " + firstPlayer + " vs. " + secondPlayer + " : " + setResultsOrderedNonNull();
  }

  private List<SetResult> setResultsOrderedNonNull() {
    return setResults
            .stream()
            .filter(SetResult::nonNull)
            .sorted(Comparator.comparingInt(SetResult::getNumber))
            .collect(Collectors.toList());
  }

  public List<SetResult> getSetResultsOrdered() {
    return setResults
            .stream()
            .sorted(Comparator.comparingInt(SetResult::getNumber))
            .collect(Collectors.toList());
  }

  @Override
  public int compareTo(final Match that) {
    return Comparator
            .comparingLong(Match::getNumber)
            .compare(this, that);
  }

}
