package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.entityvisitor.EntityVisitor;
import com.pj.squashrestapp.model.entityvisitor.Identifiable;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
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
import javax.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "rounds",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"season_id", "number"})})
@Getter
@NoArgsConstructor
public class Round implements Identifiable, Comparable<Round> {

  public static EntityVisitor<Round, Season> ENTITY_VISITOR_FINAL = new EntityVisitor<>(Round.class) {
  };

  public static EntityVisitor<Round, Season> ENTITY_VISITOR = new EntityVisitor<>(Round.class) {
    @Override
    public Season getParent(final Round visitingObject) {
      return visitingObject.getSeason();
    }

    @Override
    public Set<Round> getChildren(final Season parent) {
      return parent.getRounds();
    }

    @Override
    public void setChildren(final Season parent) {
      parent.setRounds(new TreeSet<Round>());
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
  @Column(name = "date")
  private LocalDate date;

  @Setter
  @OneToMany(
          mappedBy = "round",
          cascade = CascadeType.ALL,
          fetch = FetchType.LAZY,
          orphanRemoval = true)
  private Set<RoundGroup> roundGroups = new TreeSet<>();

  @JsonIgnore
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "season_id", referencedColumnName = "id")
  private Season season;

  @Setter
  @Column(name = "finished")
  private boolean finished;

  @Setter
  @Column(name = "split")
  private String split;

  public Round(final int number, final LocalDate date) {
    this.number = number;
    this.date = date;
  }

  @Override
  public String toString() {
    return "Season " + season.getNumber() + " | Round " + number + " | Date: " + date;
  }

  public void addRoundGroup(final RoundGroup roundGroup) {
    this.roundGroups.add(roundGroup);
    roundGroup.setRound(this);
  }

  public List<RoundGroup> getRoundGroupsOrdered() {
    return this
            .getRoundGroups()
            .stream()
            .sorted(Comparator.comparingInt(RoundGroup::getNumber))
            .collect(Collectors.toList());
  }

  @Override
  public int compareTo(final Round that) {
    return Comparator
            .comparingInt(Round::getNumber)
            .compare(this, that);
  }

}
