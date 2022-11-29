package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.dto.Trophy;
import com.pj.squashrestapp.model.audit.Audit;
import com.pj.squashrestapp.model.audit.Auditable;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "trophies_for_leagues")
@Getter
@NoArgsConstructor
public class TrophyForLeague implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Embedded
    private Audit audit = new Audit();

    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id", foreignKey = @ForeignKey(name = "fk_trophy_for_league_league"))
    private League league;

    @Setter
    @Column(name = "season_number")
    private int seasonNumber;

    @Setter
    @OneToOne(targetEntity = Player.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id", foreignKey = @ForeignKey(name = "fk_trophy_for_league_player"))
    private Player player;

    @Setter
    @Column(name = "trophy")
    @Enumerated(EnumType.STRING)
    private Trophy trophy;

    public TrophyForLeague(final int seasonNumber, final Player player, final Trophy trophy) {
        this.seasonNumber = seasonNumber;
        this.player = player;
        this.trophy = trophy;
    }

    @Override
    public String toString() {
        return trophy.toString() + " - " + player.getUsername();
    }
}
