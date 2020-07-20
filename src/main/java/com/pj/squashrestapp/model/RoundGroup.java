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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "round_groups")
@Getter
@NoArgsConstructor
public class RoundGroup implements Identifiable {

  public static EntityVisitor<RoundGroup, Round> ENTITY_VISITOR_FINAL = new EntityVisitor<>(RoundGroup.class) {
  };

  public static EntityVisitor<RoundGroup, Round> ENTITY_VISITOR = new EntityVisitor<>(RoundGroup.class) {
    @Override
    public Round getParent(final RoundGroup visitingObject) {
      return visitingObject.getRound();
    }

    @Override
    public List<RoundGroup> getChildren(final Round parent) {
      return parent.getRoundGroups();
    }

    @Override
    public void setChildren(final Round parent) {
      parent.setRoundGroups(new ArrayList<RoundGroup>());
    }
  };

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name = "number")
  private int number;

  @Setter
  @OneToMany(
          mappedBy = "roundGroup",
          cascade = CascadeType.ALL,
          fetch = FetchType.LAZY,
          orphanRemoval = true)
  private List<Match> matches = new ArrayList<>();

  @JsonIgnore
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "round_id")
  private Round round;

  public RoundGroup(final int number) {
    this.number = number;
  }

  public void addMatch(final Match match) {
    this.matches.add(match);
    match.setRoundGroup(this);
  }

  @Override
  public String toString() {
    return "Round " + round.getNumber() + " | Group " + number;
  }

}
