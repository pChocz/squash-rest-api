package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.util.EntityVisitor;
import com.pj.squashrestapp.model.util.Identifiable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "xp_points_for_place")
@Getter
@NoArgsConstructor
public class XpPointsForPlace implements Identifiable {

  public static EntityVisitor<XpPointsForPlace, XpPointsForRoundGroup> ENTITY_VISITOR = new EntityVisitor<>(XpPointsForPlace.class) {
    @Override
    public XpPointsForRoundGroup getParent(final XpPointsForPlace visitingObject) {
      return visitingObject.getXpPointsForRoundGroup();
    }

    @Override
    public List<XpPointsForPlace> getChildren(final XpPointsForRoundGroup parent) {
      return parent.getXpPointsForPlaces();
    }

    @Override
    public void setChildren(final XpPointsForRoundGroup parent) {
      parent.setXpPointsForPlaces(new ArrayList<XpPointsForPlace>());
    }
  };

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name = "place_in_round")
  private int placeInRound;

  @Setter
  @Column(name = "place_in_round_group")
  private int placeInRoundGroup;

  @Setter
  @Column(name = "points")
  private int points;

  @JsonIgnore
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "xp_points_for_round_group_id")
  private XpPointsForRoundGroup xpPointsForRoundGroup;

  public XpPointsForPlace(final int place, final int placesInAllRoundsBefore, final int points, final XpPointsForRoundGroup xpPointsForRoundGroup) {
    this.placeInRound = placesInAllRoundsBefore + place;
    this.placeInRoundGroup = place;
    this.points = points;
    this.xpPointsForRoundGroup = xpPointsForRoundGroup;
  }

}
