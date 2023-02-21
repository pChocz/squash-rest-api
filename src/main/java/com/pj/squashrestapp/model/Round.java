package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.audit.Audit;
import com.pj.squashrestapp.model.audit.Auditable;
import com.pj.squashrestapp.model.entityvisitor.EntityVisitor;
import com.pj.squashrestapp.model.entityvisitor.Identifiable;
import com.pj.squashrestapp.repository.RoundLeagueUuidDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NamedNativeQuery(
        name = "Round.findAllForPlayersEncounters",
        query =
                """
                  SELECT l.uuid as league_uuid,
                         r.uuid as round_uuid
                  FROM leagues l
                           join seasons s on l.id = s.league_id
                           join rounds r on s.id = r.season_id
                           join round_groups rg on r.id = rg.round_id
                           join matches m on rg.id = m.round_group_id
                           join players p1 on p1.id = m.first_player_id
                           join players p2 on p2.id = m.second_player_id
                                                     where p1.uuid = :playerOneUuid
                                                        or p2.uuid = :playerOneUuid

                  INTERSECT

                  SELECT l.uuid as league_uuid,
                         r.uuid as round_uuid
                  FROM leagues l
                           join seasons s on l.id = s.league_id
                           join rounds r on s.id = r.season_id
                           join round_groups rg on r.id = rg.round_id
                           join matches m on rg.id = m.round_group_id
                           join players p1 on p1.id = m.first_player_id
                           join players p2 on p2.id = m.second_player_id
                                                     where p1.uuid = :playerTwoUuid
                                                        or p2.uuid = :playerTwoUuid ;
                        """,
        resultSetMapping = "roundLeagueUuidDtoMapping")
@SqlResultSetMapping(
        name = "roundLeagueUuidDtoMapping",
        classes =
                @ConstructorResult(
                        targetClass = RoundLeagueUuidDto.class,
                        columns = {
                            @ColumnResult(name = "league_uuid", type = UUID.class),
                            @ColumnResult(name = "round_uuid", type = UUID.class)
                        }))
@Entity
@Table(
        name = "rounds",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_season_and_round_number",
                    columnNames = {"season_id", "number"})
        })
@Getter
@NoArgsConstructor
public class Round implements Identifiable, Comparable<Round>, Auditable {

    public static final EntityVisitor<Round, Season> ENTITY_VISITOR_FINAL = new EntityVisitor<>(Round.class) {};

    public static final EntityVisitor<Round, Season> ENTITY_VISITOR = new EntityVisitor<>(Round.class) {
        @Override
        public Season getParent(final Round visitingObject) {
            return visitingObject.getSeason();
        }

        @Override
        public Set<Round> getChildren(final Season parent) {
            return parent.getRounds();
        }

        @Override
        public void setChildren(final Season parent) {
            parent.setRounds(new TreeSet<>());
        }
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Embedded
    private Audit audit = new Audit();

    @Setter
    @Column(name = "uuid", nullable = false)
    private UUID uuid = UUID.randomUUID();

    @Setter
    @Column(name = "number")
    private int number;

    @Setter
    @Column(name = "date")
    private LocalDate date;

    @Setter
    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<RoundGroup> roundGroups = new TreeSet<>();

    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", foreignKey = @ForeignKey(name = "fk_round_season"))
    private Season season;

    @Setter
    @Column(name = "finished")
    private boolean finished;

    @Setter
    @Column(name = "split")
    private String split;

    public Round(final int number, final LocalDate date) {
        this.number = number;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Season " + season.getNumber() + " | Round " + number + " | Date: " + date;
    }

    public void addRoundGroup(final RoundGroup roundGroup) {
        this.roundGroups.add(roundGroup);
        roundGroup.setRound(this);
    }

    public List<RoundGroup> getRoundGroupsOrdered() {
        return this.getRoundGroups().stream()
                .sorted(Comparator.comparingInt(RoundGroup::getNumber))
                .collect(Collectors.toList());
    }

    public List<UUID[]> extractPlayersUuidsPerGroup() {
        final List<UUID[]> playersUuidsPerGroup = new ArrayList<>();

        for (final RoundGroup roundGroup : getRoundGroupsOrdered()) {
            Stream<UUID> uuidStream1 = roundGroup.getMatches().stream()
                    .map(match -> match.getFirstPlayer().getUuid());

            Stream<UUID> uuidStream2 = roundGroup.getMatches().stream()
                    .map(match -> match.getSecondPlayer().getUuid());

            UUID[] uuidsSorted =
                    Stream.concat(uuidStream1, uuidStream2).sorted().distinct().toArray(UUID[]::new);

            playersUuidsPerGroup.add(uuidsSorted);
        }

        return playersUuidsPerGroup;
    }

    @Override
    public int compareTo(final Round that) {
        return Comparator.comparingInt(Round::getNumber).compare(this, that);
    }
}
