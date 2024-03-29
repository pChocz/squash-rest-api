package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.audit.Audit;
import com.pj.squashrestapp.model.audit.Auditable;
import com.pj.squashrestapp.model.entityvisitor.EntityVisitor;
import com.pj.squashrestapp.model.entityvisitor.Identifiable;

import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.pj.squashrestapp.model.enums.MatchFormatType;
import com.pj.squashrestapp.model.enums.SetWinningType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "seasons",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_league_and_season_number",
                        columnNames = {"league_id", "number"}
                )
        })
@Getter
@NoArgsConstructor
public class Season implements Identifiable, Comparable<Season>, Auditable {

    public static final EntityVisitor<Season, League> ENTITY_VISITOR_FINAL = new EntityVisitor<>(Season.class) {};

    public static final EntityVisitor<Season, League> ENTITY_VISITOR = new EntityVisitor<>(Season.class) {
        @Override
        public League getParent(final Season visitingObject) {
            return visitingObject.getLeague();
        }

        @Override
        public Set<Season> getChildren(final League parent) {
            return parent.getSeasons();
        }

        @Override
        public void setChildren(final League parent) {
            parent.setSeasons(new TreeSet<>());
        }
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Embedded
    private Audit audit = new Audit();

    @Setter
    @Column(name = "uuid", nullable = false)
    private UUID uuid = UUID.randomUUID();

    @Setter
    @Column(name = "description")
    private String description;

    @Setter
    @Column(name = "xp_points_type")
    private String xpPointsType;

    @Setter
    @Column(name = "number")
    private int number;

    @Setter
    @Column(name = "start_date")
    private LocalDate startDate;

    @Setter
    @OneToMany(mappedBy = "season", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Round> rounds = new TreeSet<>();

    @Setter
    @OneToMany(mappedBy = "season", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<BonusPoint> bonusPoints = new HashSet<>();

    @Setter
    @OneToMany(mappedBy = "season", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<LostBall> lostBalls = new HashSet<>();

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
    @Column(name = "number_of_rounds")
    private int numberOfRounds;

    @Setter
    @Column(name = "rounds_to_be_deducted")
    private int roundsToBeDeducted;

    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id", foreignKey = @ForeignKey(name = "fk_season_league"))
    private League league;

    public Season(final int number, final LocalDate startDate, final String xpPointsType, final League league) {
        this.number = number;
        this.startDate = startDate;
        this.xpPointsType = xpPointsType;
        this.matchFormatType = league.getMatchFormatType();
        this.regularSetWinningType = league.getRegularSetWinningType();
        this.tiebreakWinningType = league.getTiebreakWinningType();
        this.regularSetWinningPoints = league.getRegularSetWinningPoints();
        this.tiebreakWinningPoints = league.getTiebreakWinningPoints();
        this.numberOfRounds = league.getNumberOfRoundsPerSeason();
        this.roundsToBeDeducted = league.getRoundsToBeDeducted();
    }

    public void addRound(final Round round) {
        this.rounds.add(round);
        round.setSeason(this);
    }

    public void addBonusPoint(final BonusPoint bonusPoint) {
        this.bonusPoints.add(bonusPoint);
        bonusPoint.setSeason(this);
    }

    public void addLostBall(final LostBall lostBall) {
        this.lostBalls.add(lostBall);
        lostBall.setSeason(this);
    }

    @Override
    public String toString() {
        return "Season " + number + " | start date: " + startDate;
    }

    public List<Round> getRoundsOrdered() {
        return this.getRounds().stream()
                .sorted(Comparator.comparingInt(Round::getNumber))
                .collect(Collectors.toList());
    }

    public List<Round> getFinishedRoundsOrdered() {
        return this.getRounds().stream()
                .filter(Round::isFinished)
                .sorted(Comparator.comparingInt(Round::getNumber))
                .collect(Collectors.toList());
    }

    @Override
    public int compareTo(final Season that) {
        return Comparator.comparingInt(Season::getNumber).compare(this, that);
    }
}
