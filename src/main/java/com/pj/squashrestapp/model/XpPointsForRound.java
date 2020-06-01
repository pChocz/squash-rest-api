package com.pj.squashrestapp.model;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Table(name = "xp_points_for_round")
@Getter
@Setter
@NoArgsConstructor
public class XpPointsForRound {

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

  @Column(name = "split")
  private String split;

  @Column(name = "number_of_players")
  private int numberOfPlayers;

  @OneToMany(mappedBy = "xpPointsForRound", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<XpPointsForRoundGroup> xpPointsForRoundGroups;

  public XpPointsForRound(final int[] splitAsArray, final int[][] points) {
    this.numberOfPlayers = Arrays.stream(splitAsArray).sum();
    this.split = intArrayToString(splitAsArray);
    this.xpPointsForRoundGroups = new ArrayList<>(splitAsArray.length);
    for (int i = 1; i <= points.length; i++) {
      this.xpPointsForRoundGroups.add(new XpPointsForRoundGroup(i, points, this));
    }
  }

  public static String intArrayToString(final int[] intArray) {
    return integerListToString(
            intArrayToList(intArray));
  }

  /**
   * Converts list of Integer to nicely formatted String,
   * ex: 1 | 3 | 4
   *
   * @param integerList list of integers to format
   * @return nicely formatted String
   */
  public static String integerListToString(final List<Integer> integerList) {
    return integerList
            .stream()
            .map(Object::toString)
            .collect(Collectors.joining(" | "));
  }

  public static List<Integer> intArrayToList(final int[] integerList) {
    return Arrays
            .stream(integerList)
            .boxed()
            .collect(Collectors.toList());
  }

}
