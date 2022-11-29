package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.entityvisitor.EntityVisitor;
import com.pj.squashrestapp.model.entityvisitor.Identifiable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name = "xp_points_for_place")
@Getter
@NoArgsConstructor
public class XpPointsForPlace implements Identifiable, Comparable<XpPointsForPlace> {

    public static final EntityVisitor<XpPointsForPlace, XpPointsForRoundGroup> ENTITY_VISITOR =
            new EntityVisitor<>(XpPointsForPlace.class) {
                @Override
                public XpPointsForRoundGroup getParent(final XpPointsForPlace visitingObject) {
                    return visitingObject.getXpPointsForRoundGroup();
                }

                @Override
                public Set<XpPointsForPlace> getChildren(final XpPointsForRoundGroup parent) {
                    return parent.getXpPointsForPlaces();
                }

                @Override
                public void setChildren(final XpPointsForRoundGroup parent) {
                    parent.setXpPointsForPlaces(new TreeSet<>());
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
    @JoinColumn(name = "xp_points_for_round_group_id", foreignKey = @ForeignKey(name = "fk_xp_points_place_round_group"))
    private XpPointsForRoundGroup xpPointsForRoundGroup;

    public XpPointsForPlace(
            final int place,
            final int placesInAllRoundsBefore,
            final int points,
            final XpPointsForRoundGroup xpPointsForRoundGroup) {
        this.placeInRound = placesInAllRoundsBefore + place;
        this.placeInRoundGroup = place;
        this.points = points;
        this.xpPointsForRoundGroup = xpPointsForRoundGroup;
    }

    @Override
    public String toString() {
        return "P: " + points + " | R: " + placeInRound + " | G: " + placeInRoundGroup;
    }

    @Override
    public int compareTo(final XpPointsForPlace that) {
        return Comparator.comparingInt(XpPointsForPlace::getPlaceInRound).compare(this, that);
    }
}
