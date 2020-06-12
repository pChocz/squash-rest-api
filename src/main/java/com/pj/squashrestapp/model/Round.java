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
import javax.persistence.Table;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
  @Column(name = "number")
  private int number;

  @Setter
  @Column(name = "date")
  private Date date;

  @Setter
  @OneToMany(
          mappedBy = "round",
          cascade = CascadeType.ALL,
          fetch = FetchType.LAZY,
          orphanRemoval = true)
  private List<RoundGroup> roundGroups = new ArrayList<>();

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

  public Round(final int number, final Date date, final Season season) {
    this.number = number;
    this.date = date;
    this.season = season;
  }

  @Override
  public String toString() {
    return "Season " + season.getNumber() + " | Round " + number + " | Date: " + date;
  }

  public void addRoundGroup(final RoundGroup roundGroup) {
    this.roundGroups.add(roundGroup);
    roundGroup.setRound(this);
  }

}
