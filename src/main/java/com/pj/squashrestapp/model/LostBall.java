package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "lost_balls")
@Getter
@NoArgsConstructor
public class LostBall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "uuid", nullable = false)
    private UUID uuid = UUID.randomUUID();

    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id")
    private Season season;

    @Setter
    @Column(name = "date")
    private LocalDate date;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @Setter
    @Column(name = "count")
    private int count;

    public LostBall(final Player player, final LocalDate date, final int count) {
        this.player = player;
        this.date = date;
        this.count = count;
    }

    @Override
    public String toString() {
        return uuid + " -> " + player.getUsername();
    }
}
