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

import static com.pj.squashrestapp.util.GeneralUtil.UTC_ZONE_ID;

/** */
@Entity
@Table(name = "magic_login_links_tokens")
@Getter
@NoArgsConstructor
public class MagicLoginLinkToken {

    private static final long EXPIRATION_TIME_MINUTES = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "token")
    private UUID token;

    @Setter
    @OneToOne(targetEntity = Player.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id", foreignKey = @ForeignKey(name = "fk_magic_login_link_token_player"))
    private Player player;

    @Setter
    @Column(name = "expiration_date_time", nullable = false, updatable = false)
    private LocalDateTime expirationDateTime;

    public MagicLoginLinkToken(final UUID token, final Player player) {
        this.token = token;
        this.player = player;
        this.expirationDateTime = LocalDateTime.now(UTC_ZONE_ID).plusMinutes(EXPIRATION_TIME_MINUTES);
    }
}
