package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pj.squashrestapp.model.entityvisitor.Identifiable;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.Comparator;

import static com.pj.squashrestapp.model.AppealDecision.NO_LET;
import static com.pj.squashrestapp.model.AppealDecision.STROKE;
import static com.pj.squashrestapp.model.AppealDecision.YES_LET;
import static com.pj.squashrestapp.model.ScoreEventType.FIRST_PLAYER_CALLS_LET;
import static com.pj.squashrestapp.model.ScoreEventType.FIRST_PLAYER_SCORES;
import static com.pj.squashrestapp.model.ScoreEventType.SECOND_PLAYER_CALLS_LET;
import static com.pj.squashrestapp.model.ScoreEventType.SECOND_PLAYER_SCORES;
import static com.pj.squashrestapp.model.ServePlayer.FIRST_PLAYER;
import static com.pj.squashrestapp.model.ServePlayer.SECOND_PLAYER;
import static com.pj.squashrestapp.model.ServeSide.LEFT_SIDE;
import static com.pj.squashrestapp.model.ServeSide.RIGHT_SIDE;

@Entity
@Table(name = "match_scores")
@Getter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchScore implements Identifiable, Comparable<MatchScore> {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    @Setter
    @Column(name = "game_number")
    private Integer gameNumber;

    @Setter
    @Column(name = "zoned_date_time")
    @JsonFormat(pattern = GeneralUtil.DATE_TIME_ISO_FORMAT)
    private ZonedDateTime zonedDateTime;

    @Setter
    @Column(name = "score_event_type")
    @Enumerated(EnumType.STRING)
    private ScoreEventType scoreEventType;

    @Setter
    @Column(name = "appeal_decision")
    @Enumerated(EnumType.STRING)
    private AppealDecision appealDecision;

    @Setter
    @Column(name = "serve_side")
    @Enumerated(EnumType.STRING)
    private ServeSide serveSide;

    @Setter
    @Column(name = "serve_player")
    @Enumerated(EnumType.STRING)
    private ServePlayer servePlayer;

    @Setter
    @Column(name = "next_suggested_serve_player")
    @Enumerated(EnumType.STRING)
    private ServePlayer nextSuggestedServePlayer;

    @Setter
    @Column(name = "first_player_score")
    private Integer firstPlayerScore;

    @Setter
    @Column(name = "second_player_score")
    private Integer secondPlayerScore;

    @Setter
    @Column(name = "first_player_games_won")
    private Integer firstPlayerGamesWon;

    @Setter
    @Column(name = "second_player_games_won")
    private Integer secondPlayerGamesWon;

    @Setter
    @Column(name = "can_score")
    private boolean canScore;

    @Setter
    @Column(name = "can_start_game")
    private boolean canStartGame;

    @Setter
    @Column(name = "can_end_game")
    private boolean canEndGame;

    @Setter
    @Column(name = "can_end_match")
    private boolean canEndMatch;

    @Setter
    @Column(name = "match_finished")
    private boolean matchFinished;

    public boolean isFirstPlayerScored() {
        return FIRST_PLAYER_SCORES.equals(scoreEventType)
                || (FIRST_PLAYER_CALLS_LET.equals(scoreEventType) && STROKE.equals(appealDecision))
                || (SECOND_PLAYER_CALLS_LET.equals(scoreEventType) && NO_LET.equals(appealDecision));
    }

    public boolean isSecondPlayerScored() {
        return SECOND_PLAYER_SCORES.equals(scoreEventType)
                || (SECOND_PLAYER_CALLS_LET.equals(scoreEventType) && STROKE.equals(appealDecision))
                || (FIRST_PLAYER_CALLS_LET.equals(scoreEventType) && NO_LET.equals(appealDecision));
    }

    public ServeSide getNextSuggestedServeSide() {
        if (this.appealDecision == YES_LET) {
            return this.serveSide;

        } else if ((isFirstPlayerScored() && this.servePlayer == FIRST_PLAYER)
                || (isSecondPlayerScored() && this.servePlayer == SECOND_PLAYER)) {

            return toggleServeSide();

        } else {
            return LEFT_SIDE;
        }
    }

    public boolean isRally() {
        return scoreEventType != null
                && scoreEventType.isRally();
    }

    private ServeSide toggleServeSide() {
        return this.serveSide == LEFT_SIDE
                ? RIGHT_SIDE
                : LEFT_SIDE;
    }

    @Override
    public int compareTo(final MatchScore that) {
        return Comparator
                .comparing(MatchScore::getZonedDateTime)
                .compare(this, that);
    }
}
