package com.pj.squashrestapp.model;

import static com.pj.squashrestapp.util.GeneralUtil.UTC_ZONE_ID;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 */
@Entity
@Table(name = "password_reset_tokens")
@Getter
@NoArgsConstructor
public class PasswordResetToken {

  private static final long EXPIRATION_TIME_HOURS = 1;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name = "token")
  private UUID token;

  @Setter
  @OneToOne(targetEntity = Player.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "player_id")
  private Player player;

  @Setter
  @Column(name = "expiration_date_time",
          nullable = false,
          updatable = false)
  private LocalDateTime expirationDateTime;

  public PasswordResetToken(final UUID token, final Player player) {
    this.token = token;
    this.player = player;
    this.expirationDateTime = LocalDateTime.now(UTC_ZONE_ID).plusHours(EXPIRATION_TIME_HOURS);
  }

}
