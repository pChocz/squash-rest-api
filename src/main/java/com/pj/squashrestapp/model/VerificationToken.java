package com.pj.squashrestapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import static com.pj.squashrestapp.util.GeneralUtil.UTC_ZONE_ID;

/**
 *
 */
@Entity
@Table(name = "verification_tokens")
@Getter
@NoArgsConstructor
public class VerificationToken {

  private static final long EXPIRATION_TIME_DAYS = 1;

  @Id
  @Column(name = "id",
          nullable = false,
          updatable = false)
  @GeneratedValue(
          strategy = GenerationType.AUTO,
          generator = "native")
  @GenericGenerator(
          name = "native",
          strategy = "native")
  private Long id;

  @Setter
  @Column(name = "token")
  private String token;

  @Setter
  @OneToOne(targetEntity = Player.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "player_id")
  private Player player;

  @Setter
  @Column(name = "expiration_date_time",
          nullable = false,
          updatable = false)
  private LocalDateTime expirationDateTime;

  public VerificationToken(final String token, final Player player) {
    this.token = token;
    this.player = player;
    this.expirationDateTime = LocalDateTime.now(UTC_ZONE_ID).plusDays(EXPIRATION_TIME_DAYS);
  }

}
