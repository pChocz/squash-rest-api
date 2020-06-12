package com.pj.squashrestapp.model;

import com.pj.squashrestapp.model.util.EntityVisitor;
import com.pj.squashrestapp.model.util.Identifiable;
import com.pj.squashrestapp.util.GeneralUtil;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Table(name = "xp_points_for_round")
@Getter
@NoArgsConstructor
public class XpPointsForRound implements Identifiable {

  public static EntityVisitor<XpPointsForRound, Round> ENTITY_VISITOR_FINAL = new EntityVisitor<>(XpPointsForRound.class) {
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
  private List<XpPointsForRoundGroup> xpPointsForRoundGroups;

  public XpPointsForRound(final int[] splitAsArray, final int[][] points) {
    this.numberOfPlayers = Arrays.stream(splitAsArray).sum();
    this.split = GeneralUtil.intArrayToString(splitAsArray);
    this.xpPointsForRoundGroups = new ArrayList<>(splitAsArray.length);
    for (int i = 1; i <= points.length; i++) {
      this.xpPointsForRoundGroups.add(new XpPointsForRoundGroup(i, points, this));
    }
  }

}
