package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "additional_matches")
@Getter
@NoArgsConstructor
public class AdditionalMatch implements Comparable<AdditionalMatch> {

  private static final int DEFAULT_NUMBER_OF_SETS = 3;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name = "uuid",
          nullable = false)
  private UUID uuid = UUID.randomUUID();

  @Setter
  @Column(name = "date")
  private LocalDate date;

  @Setter
  @Column(name = "season_number")
  private int seasonNumber;

  @Setter
  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private AdditionalMatchType type;

  @Setter
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "first_player_id")
  private Player firstPlayer;

  @Setter
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "second_player_id")
  private Player secondPlayer;

  @Setter
  @OneToMany(
          mappedBy = "match",
          cascade = CascadeType.ALL,
          fetch = FetchType.LAZY,
          orphanRemoval = true)
  private Set<AdditonalSetResult> setResults = new TreeSet<>();

  @JsonIgnore
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "league_id")
  private League league;


  public AdditionalMatch(final Player firstPlayer, final Player secondPlayer) {
    this.firstPlayer = firstPlayer;
    this.secondPlayer = secondPlayer;
  }

  public void addSetResult(final AdditonalSetResult setResult) {
    this.setResults.add(setResult);
    setResult.setMatch(this);
  }

  @Override
  public String toString() {
    return "[" + uuid + "] " + firstPlayer + " vs. " + secondPlayer + " : " + setResultsOrderedNonNull();
  }

  private List<AdditonalSetResult> setResultsOrderedNonNull() {
    return setResults
            .stream()
            .filter(AdditonalSetResult::nonNull)
            .sorted(Comparator.comparingInt(AdditonalSetResult::getNumber))
            .collect(Collectors.toList());
  }

  public List<AdditonalSetResult> getSetResultsOrdered() {
    return setResults
            .stream()
            .sorted(Comparator.comparingInt(AdditonalSetResult::getNumber))
            .collect(Collectors.toList());
  }

  @Override
  public int compareTo(final AdditionalMatch that) {
    return Comparator
            .comparing(AdditionalMatch::getDate)
            .compare(this, that);
  }

  public String detailedInfo() {
    return "[" + uuid + "] " + firstPlayer + " vs. " + secondPlayer + " : " + setResultsOrderedNonNull()
           + " (S: " + seasonNumber + " | T: " + type + " | " + date + ")";
  }

}
