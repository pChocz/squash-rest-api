package com.pj.squashrestapp.dto;

import com.pj.squashrestapp.model.LeagueRule;
import com.pj.squashrestapp.model.audit.Audit;
import com.pj.squashrestapp.model.enums.LeagueRuleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/** */
@Slf4j
@Getter
@NoArgsConstructor
public class LeagueRuleDto {

    private UUID uuid;
    private String rule;
    private LeagueRuleType type;
    private Double orderValue;
    private Audit audit;

    public LeagueRuleDto(final LeagueRule rule) {
        this.uuid = rule.getUuid();
        this.type = rule.getType();
        this.rule = rule.getRule();
        this.orderValue = rule.getOrderValue();
        this.audit = rule.getAudit();
    }
}
