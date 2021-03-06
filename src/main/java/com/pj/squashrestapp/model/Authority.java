package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "authorities")
@Getter
@NoArgsConstructor
public class Authority {

  @JsonIgnore
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      joinColumns = @JoinColumn(name = "authorities_id"),
      inverseJoinColumns = @JoinColumn(name = "player_id"))
  private final Set<Player> players = new HashSet<>();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Enumerated(EnumType.STRING)
  private AuthorityType type;

  public Authority(final AuthorityType type) {
    this.type = type;
  }
}
