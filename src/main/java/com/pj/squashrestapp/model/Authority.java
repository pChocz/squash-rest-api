package com.pj.squashrestapp.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authorities")
@Getter
@NoArgsConstructor
public class Authority {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
          joinColumns = @JoinColumn(name = "authorities_id"),
          inverseJoinColumns = @JoinColumn(name = "player_id")
  )
  private final Set<Player> players = new HashSet<>();

  @Setter
  @Enumerated(EnumType.STRING)
  private AuthorityType type;

  public Authority(final AuthorityType type) {
    this.type = type;
  }

}
