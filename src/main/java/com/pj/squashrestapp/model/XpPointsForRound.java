package com.pj.squashrestapp.model;

import com.pj.squashrestapp.model.entityvisitor.EntityVisitor;
import com.pj.squashrestapp.model.entityvisitor.Identifiable;
import com.pj.squashrestapp.util.GeneralUtil;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;


@Entity
@Table(name = "xp_points_for_round")
@Getter
@NoArgsConstructor
public class XpPointsForRound implements Identifiable, Comparable<XpPointsForRound> {

  public static EntityVisitor<XpPointsForRound, Round> ENTITY_VISITOR_FINAL = new EntityVisitor<>(XpPointsForRound.class) {
  };

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name = "type")
  private String type;

  @Setter
  @Column(name = "split")
  private String split;

  @Setter
  @Column(name = "number_of_players")
  private int numberOfPlayers;

  @Setter
  @OneToMany(
          mappedBy = "xpPointsForRound",
          cascade = CascadeType.ALL,
          fetch = FetchType.LAZY,
          orphanRemoval = true)
  private Set<XpPointsForRoundGroup> xpPointsForRoundGroups;

  public XpPointsForRound(final String type, final int[] splitAsArray, final int[][] points) {
    this.type = type;
    this.numberOfPlayers = Arrays.stream(splitAsArray).sum();
    this.split = GeneralUtil.intArrayToString(splitAsArray);
    this.xpPointsForRoundGroups = new TreeSet<>();
    for (int i = 1; i <= points.length; i++) {
      final XpPointsForRoundGroup xpPointsForRoundGroup = new XpPointsForRoundGroup(i, points, this);
      this.xpPointsForRoundGroups.add(xpPointsForRoundGroup);
    }
  }

  @Override
  public int compareTo(final XpPointsForRound that) {
    return Comparator
            .comparingInt(XpPointsForRound::getNumberOfPlayers)
            .compare(this, that);
  }

}
