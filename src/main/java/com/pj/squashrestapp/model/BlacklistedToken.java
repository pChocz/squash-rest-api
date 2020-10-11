package com.pj.squashrestapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "blacklisted_tokens")
@Getter
@NoArgsConstructor
public class BlacklistedToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name = "token",
          length = 500,
          nullable = false,
          updatable = false)
  private String token;

  @Setter
  @Column(name = "expiration_date_time",
          nullable = false,
          updatable = false)
  private LocalDateTime expirationDateTime;

}
