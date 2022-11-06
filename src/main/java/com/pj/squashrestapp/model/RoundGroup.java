package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.entityvisitor.EntityVisitor;
import com.pj.squashrestapp.model.entityvisitor.Identifiable;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "round_groups",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "ROUND_AND_GROUP_NUMBER_CONSTRAINT",
                    columnNames = {"round_id", "number"})
        })
@Getter
@NoArgsConstructor
public class RoundGroup implements Identifiable, Comparable<RoundGroup> {

    public static EntityVisitor<RoundGroup, Round> ENTITY_VISITOR_FINAL = new EntityVisitor<>(RoundGroup.class) {};

    public static EntityVisitor<RoundGroup, Round> ENTITY_VISITOR = new EntityVisitor<>(RoundGroup.class) {
        @Override
        public Round getParent(final RoundGroup visitingObject) {
            return visitingObject.getRound();
        }

        @Override
        public Set<RoundGroup> getChildren(final Round parent) {
            return parent.getRoundGroups();
        }

        @Override
        public void setChildren(final Round parent) {
            parent.setRoundGroups(new TreeSet<RoundGroup>());
        }
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "number")
    private int number;

    @Setter
    @OneToMany(mappedBy = "roundGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Match> matches = new TreeSet<>();

    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id")
    private Round round;

    public RoundGroup(final int number) {
        this.number = number;
    }

    public void addMatch(final Match match) {
        this.matches.add(match);
        match.setRoundGroup(this);
    }

    @Override
    public String toString() {
        return "Round " + round.getNumber() + " | Group " + number;
    }

    public List<Match> getMatchesOrdered() {
        return matches.stream()
                .sorted(Comparator.comparingInt(Match::getNumber))
                .collect(Collectors.toList());
    }

    @Override
    public int compareTo(final RoundGroup that) {
        return Comparator.comparingInt(RoundGroup::getNumber).compare(this, that);
    }
}
