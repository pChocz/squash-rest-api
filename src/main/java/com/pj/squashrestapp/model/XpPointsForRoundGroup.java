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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "xp_points_for_round_group")
@Getter
@Setter
@NoArgsConstructor
public class XpPointsForRoundGroup {

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

  @Column(name = "round_group_number")
  private int roundGroupNumber;

  @OneToMany(mappedBy = "xpPointsForRoundGroup", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<XpPointsForPlace> xpPointsForPlaces;

  @ManyToOne
  @JoinColumn(name = "xp_points_for_round_id", referencedColumnName = "id")
  private XpPointsForRound xpPointsForRound;

  public XpPointsForRoundGroup(final int roundGroupNumber, final int[][] allPoints, XpPointsForRound xpPointsForRound) {
    this.roundGroupNumber = roundGroupNumber;
    this.xpPointsForRound = xpPointsForRound;

    int placesInAllRoundsBefore = 0;
    for (int i = 1; i < roundGroupNumber; i++) {
      placesInAllRoundsBefore += allPoints[i].length;
    }

    this.xpPointsForPlaces = new ArrayList<>();
    for (int i = 1; i <= allPoints[roundGroupNumber-1].length; i++) {
      this.xpPointsForPlaces.add(new XpPointsForPlace(i, placesInAllRoundsBefore, allPoints[roundGroupNumber-1][i-1], this));
    }
  }

}
