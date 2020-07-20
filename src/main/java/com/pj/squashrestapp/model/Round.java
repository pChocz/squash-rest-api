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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "rounds")
@Getter
@NoArgsConstructor
public class Round implements Identifiable {

  public static EntityVisitor<Round, Season> ENTITY_VISITOR_FINAL = new EntityVisitor<>(Round.class) {
  };

  public static EntityVisitor<Round, Season> ENTITY_VISITOR = new EntityVisitor<>(Round.class) {
    @Override
    public Season getParent(final Round visitingObject) {
      return visitingObject.getSeason();
    }

    @Override
    public List<Round> getChildren(final Season parent) {
      return parent.getRounds();
    }

    @Override
    public void setChildren(final Season parent) {
      parent.setRounds(new ArrayList<Round>());
    }
  };

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

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
  private List<RoundGroup> roundGroups = new ArrayList<>();

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

}
