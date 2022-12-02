package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.pj.squashrestapp.model.audit.Audit;
import com.pj.squashrestapp.model.audit.Auditable;
import com.pj.squashrestapp.model.entityvisitor.Identifiable;
import com.pj.squashrestapp.model.enums.AppealDecision;
import com.pj.squashrestapp.model.enums.ScoreEventType;
import com.pj.squashrestapp.model.enums.ServePlayer;
import com.pj.squashrestapp.model.enums.ServeSide;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Comparator;

import static com.pj.squashrestapp.model.enums.AppealDecision.NO_LET;
import static com.pj.squashrestapp.model.enums.AppealDecision.STROKE;
import static com.pj.squashrestapp.model.enums.AppealDecision.YES_LET;
import static com.pj.squashrestapp.model.enums.ScoreEventType.FIRST_PLAYER_CALLS_LET;
import static com.pj.squashrestapp.model.enums.ScoreEventType.FIRST_PLAYER_SCORES;
import static com.pj.squashrestapp.model.enums.ScoreEventType.SECOND_PLAYER_CALLS_LET;
import static com.pj.squashrestapp.model.enums.ScoreEventType.SECOND_PLAYER_SCORES;
import static com.pj.squashrestapp.model.enums.ServePlayer.FIRST_PLAYER;
import static com.pj.squashrestapp.model.enums.ServePlayer.SECOND_PLAYER;
import static com.pj.squashrestapp.model.enums.ServeSide.LEFT_SIDE;
import static com.pj.squashrestapp.model.enums.ServeSide.RIGHT_SIDE;

@Entity
@Table(name = "match_scores")
@Getter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchScore implements Identifiable, Comparable<MatchScore>, Auditable {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Embedded
    private Audit audit = new Audit();

    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", foreignKey = @ForeignKey(name = "fk_match_score_match"))
    private Match match;

    @Setter
    @Column(name = "game_number")
    private Integer gameNumber;

    @Setter
    @Column(name = "date_time")
    @JsonFormat(pattern = GeneralUtil.DATE_TIME_FORMAT)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dateTime;

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
                .comparing(MatchScore::getDateTime)
                .compare(this, that);
    }
}
