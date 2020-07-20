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
@Table(name = "xp_points_for_round_group")
@Getter
@NoArgsConstructor
public class XpPointsForRoundGroup implements Identifiable {

  public static EntityVisitor<XpPointsForRoundGroup, XpPointsForRound> ENTITY_VISITOR_FINAL = new EntityVisitor<>(XpPointsForRoundGroup.class) {
  };

  public static EntityVisitor<XpPointsForRoundGroup, XpPointsForRound> ENTITY_VISITOR = new EntityVisitor<>(XpPointsForRoundGroup.class) {
    @Override
    public XpPointsForRound getParent(final XpPointsForRoundGroup visitingObject) {
      return visitingObject.getXpPointsForRound();
    }

    @Override
    public List<XpPointsForRoundGroup> getChildren(final XpPointsForRound parent) {
      return parent.getXpPointsForRoundGroups();
    }

    @Override
    public void setChildren(final XpPointsForRound parent) {
      parent.setXpPointsForRoundGroups(new ArrayList<XpPointsForRoundGroup>());
    }
  };

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name = "round_group_number")
  private int roundGroupNumber;

  @Setter
  @OneToMany(
          mappedBy = "xpPointsForRoundGroup",
          cascade = CascadeType.ALL,
          fetch = FetchType.LAZY,
          orphanRemoval = true)
  private List<XpPointsForPlace> xpPointsForPlaces;

  @JsonIgnore
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "xp_points_for_round_id", referencedColumnName = "id")
  private XpPointsForRound xpPointsForRound;

  public XpPointsForRoundGroup(final int roundGroupNumber, final int[][] allPoints, final XpPointsForRound xpPointsForRound) {
    this.roundGroupNumber = roundGroupNumber;
    this.xpPointsForRound = xpPointsForRound;

    int placesInAllRoundsBefore = 0;
    for (int i = 1; i < roundGroupNumber; i++) {
      placesInAllRoundsBefore += allPoints[i].length;
    }

    this.xpPointsForPlaces = new ArrayList<>();
    for (int i = 1; i <= allPoints[roundGroupNumber - 1].length; i++) {
      this.xpPointsForPlaces.add(new XpPointsForPlace(i, placesInAllRoundsBefore, allPoints[roundGroupNumber - 1][i - 1], this));
    }
  }

}
