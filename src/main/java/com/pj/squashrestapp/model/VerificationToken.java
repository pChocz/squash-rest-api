package com.pj.squashrestapp.model;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/** */
@Entity
@Table(name = "verification_tokens")
@Getter
@NoArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @OneToOne(targetEntity = Player.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id", foreignKey = @ForeignKey(name = "fk_verification_token_player"))
    private Player player;

    @Setter
    @Column(name = "token")
    private UUID token;

    @Setter
    @Column(name = "expiration_date_time", nullable = false, updatable = false)
    private LocalDateTime expirationDateTime;

    public VerificationToken(final UUID token, final Player player, final LocalDateTime expirationDateTime) {
        this.token = token;
        this.player = player;
        this.expirationDateTime = expirationDateTime;
    }
}
