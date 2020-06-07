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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Entity
@Table(name = "round_groups")
@Getter
@Setter
@NoArgsConstructor
public class RoundGroup implements Identifiable {

  public static EntityVisitor<RoundGroup, Round> ENTITY_VISITOR_FINAL = new EntityVisitor<>(RoundGroup.class) {
  };

  public static EntityVisitor<RoundGroup, Round> ENTITY_VISITOR = new EntityVisitor<>(RoundGroup.class) {

    @Override
    public Round getParent(RoundGroup visitingObject) {
      return visitingObject.getRound();
    }

    @Override
    public List<RoundGroup> getChildren(Round parent) {
      return parent.getRoundGroups();
    }

    @Override
    public void setChildren(Round parent) {
      parent.setRoundGroups(new ArrayList<RoundGroup>());
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

  @Column(name = "number")
  private int number;

  @ManyToMany
  @JoinTable(
          joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"),
          inverseJoinColumns = @JoinColumn(name = "player_id", referencedColumnName = "id")
  )
  private List<Player> players;

  @OneToMany(mappedBy = "roundGroup", cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.REMOVE})
  private List<Match> matches;

  @ManyToOne
  @JoinColumn(name = "round_id", referencedColumnName = "id")
  private Round round;

  public RoundGroup(final int number, final List<Player> players, final Round round) {
    this.number = number;
    this.players = players;
    this.round = round;

    this.matches = new ArrayList<>();
  }

  public void generateEmptyMatches() {
    for (int i=0; i < players.size(); i++) {
      for (int j=i+1; j < players.size(); j++) {
        this.matches.add(new Match(this, players.get(i), players.get(j), true));
      }
    }
  }

  @Override
  public String toString() {
    return "Round " + round.getNumber() + " / Group " + number + " / " + players.size() + " players";
  }
//
//  public VerticalLayout createGroupLabel() {
//    final VerticalLayout layout = new VerticalLayout();
//    layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
//    layout.setAlignItems(FlexComponent.Alignment.CENTER);
//    layout.getStyle().set("padding", "0px");
//
//    final int playersCount = getPlayers().size();
//    final int number = getNumber();
//
//    final String numeral
//            = number == 1 ? "st"
//            : number == 2 ? "nd"
//            : number == 3 ? "rd"
//            : "th";
//
//    final Span groupSpan = new Span(number + numeral + " group");
//    groupSpan.getStyle().set("margin-bottom", "-1px");
//    groupSpan.getStyle().set("padding-top", "0px");
//
//    final Span playersSpan = new Span(playersCount + " players");
//    playersSpan.getStyle().set("margin-top", "-1px");
//    playersSpan.getStyle().set("padding-bottom", "0px");
//
//    layout.add(groupSpan);
//    layout.add(playersSpan);
//    return layout;
//  }

}
