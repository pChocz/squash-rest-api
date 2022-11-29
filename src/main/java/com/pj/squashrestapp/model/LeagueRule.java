package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.audit.Audit;
import com.pj.squashrestapp.model.audit.Auditable;
import com.pj.squashrestapp.model.enums.LeagueRuleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "league_rules")
@Getter
@NoArgsConstructor
public class LeagueRule implements Auditable {

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
    @Column(name = "orderValue")
    private Double orderValue;

    @Setter
    @Column(name = "rule", length = 1000)
    private String rule;

    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id", foreignKey = @ForeignKey(name = "fk_league_rule_league"))
    private League league;

    @Setter
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private LeagueRuleType type;

    public LeagueRule(final String rule, final LeagueRuleType type) {
        this.rule = rule;
        this.type = type;
    }
}
