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
  private List<SetResult> setResults = new ArrayList<>();

  @JsonIgnore
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "round_group_id")
  private RoundGroup roundGroup;

  public void addSetResult(final SetResult setResult) {
    this.setResults.add(setResult);
    setResult.setMatch(this);
  }

  @Override
  public String toString() {
    return "[" + getId() + "] " + firstPlayer + " vs. " + secondPlayer + " : " + setResults;
  }

}
