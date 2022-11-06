package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.entityvisitor.EntityVisitor;
import com.pj.squashrestapp.model.entityvisitor.Identifiable;
import com.pj.squashrestapp.model.enums.MatchFormatType;
import com.pj.squashrestapp.model.enums.SetWinningType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

/** */
@Entity
@Table(name = "matches")
@Getter
@NoArgsConstructor
public class Match implements Identifiable, Comparable<Match> {

    public static EntityVisitor<Match, RoundGroup> ENTITY_VISITOR_FINAL = new EntityVisitor<>(Match.class) {};

    public static EntityVisitor<Match, RoundGroup> ENTITY_VISITOR = new EntityVisitor<>(Match.class) {
        @Override
        public RoundGroup getParent(final Match visitingObject) {
            return visitingObject.getRoundGroup();
        }

        @Override
        public Set<Match> getChildren(final RoundGroup parent) {
            return parent.getMatches();
        }

        @Override
        public void setChildren(final RoundGroup parent) {
            parent.setMatches(new TreeSet<Match>());
        }
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "uuid", nullable = false)
    private UUID uuid = UUID.randomUUID();

    @Setter
    @Column(name = "number")
    private int number;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_player_id")
    private Player firstPlayer;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_player_id")
    private Player secondPlayer;

    @Setter
    @Enumerated(EnumType.STRING)
    private MatchFormatType matchFormatType;

    @Setter
    @Enumerated(EnumType.STRING)
    private SetWinningType regularSetWinningType;

    @Setter
    @Enumerated(EnumType.STRING)
    private SetWinningType tiebreakWinningType;

    @Setter
    @Column(name = "regular_set_winning_points")
    private int regularSetWinningPoints;

    @Setter
    @Column(name = "tie_break_winning_points")
    private int tiebreakWinningPoints;

    @Setter
    @Column(name = "footage_link")
    private String footageLink;

    @Setter
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<MatchScore> scores = new TreeSet<>();

    @Setter
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<SetResult> setResults = new TreeSet<>();

    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_group_id")
    private RoundGroup roundGroup;

    public Match(final Player firstPlayer, final Player secondPlayer, final Season season) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.matchFormatType = season.getMatchFormatType();
        this.regularSetWinningType = season.getRegularSetWinningType();
        this.regularSetWinningPoints = season.getRegularSetWinningPoints();
        this.tiebreakWinningType = season.getTiebreakWinningType();
        this.tiebreakWinningPoints = season.getTiebreakWinningPoints();
    }

    public void addSetResult(final SetResult setResult) {
        this.setResults.add(setResult);
        setResult.setMatch(this);
    }

    public void addScore(final MatchScore matchScore) {
        this.scores.add(matchScore);
        matchScore.setMatch(this);
    }

    public Optional<MatchScore> getLastScore() {
        final int count = this.getScores().size();
        return count == 0
                ? Optional.empty()
                : this.getScores().stream().sorted().skip(count - 1).findFirst();
    }

    @Override
    public String toString() {
        return "[" + getUuid() + "] " + firstPlayer + " vs. " + secondPlayer + " : " + setResultsOrderedNonNull();
    }

    private List<SetResult> setResultsOrderedNonNull() {
        return setResults.stream()
                .filter(SetResult::nonNull)
                .sorted(Comparator.comparingInt(SetResult::getNumber))
                .collect(Collectors.toList());
    }

    public List<SetResult> getSetResultsOrdered() {
        return setResults.stream()
                .sorted(Comparator.comparingInt(SetResult::getNumber))
                .collect(Collectors.toList());
    }

    public List<MatchScore> getMatchScoresOrdered() {
        return scores.stream()
                .sorted(Comparator.comparing(MatchScore::getZonedDateTime))
                .collect(Collectors.toList());
    }

    @Override
    public int compareTo(final Match that) {
        return Comparator.comparingLong(Match::getNumber).compare(this, that);
    }

    public int getNumberOfSets() {
        return setResults.size();
    }
}
