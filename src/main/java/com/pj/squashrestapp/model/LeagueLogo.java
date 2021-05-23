package com.pj.squashrestapp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "league_logos")
@Getter
@NoArgsConstructor
public class LeagueLogo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @OneToOne(mappedBy = "leagueLogo")
  private League league;

  @Setter
  @Lob
  @Type(type = "org.hibernate.type.ImageType")
  private byte[] picture;
}
