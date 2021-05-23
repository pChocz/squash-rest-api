package com.pj.squashrestapp.model;

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
  @JoinColumn(name = "player_id")
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
