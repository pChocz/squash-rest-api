package com.pj.squashrestapp.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authorities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Authority {

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

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
          joinColumns = @JoinColumn(name = "authorities_id", referencedColumnName = "id"),
          inverseJoinColumns = @JoinColumn(name = "player_id", referencedColumnName = "id")
  )
  private List<Player> players;

  @Enumerated(EnumType.STRING)
  private AuthorityType type;

  public Authority(final AuthorityType authorityType) {
    this.players = new ArrayList<>();
    this.type = authorityType;
  }

}
