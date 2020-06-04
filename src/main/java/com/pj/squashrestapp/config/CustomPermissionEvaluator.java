package com.pj.squashrestapp.config;

import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.Collection;

/**
 *
 */
@Slf4j
public class CustomPermissionEvaluator implements PermissionEvaluator {

  @Override
  public boolean hasPermission(final Authentication auth,
                               final Object targetDomainObject,
                               final Object permission) {
    throw new NotYetImplementedException("hasPermission not implemented and should never be called!");
  }

  @Override
  public boolean hasPermission(final Authentication auth,
                               final Serializable targetId,
                               final String targetType,
                               final Object permission) {
    if ((auth == null) || (targetType == null) || !(permission instanceof String role)) {
      return false;
    }
    final PlayerAuthDetails playerAuthDetails = (PlayerAuthDetails) auth.getPrincipal();
    if (playerAuthDetails.isAdmin()) {
      return true;
    }
    final Long leagueId = (Long) targetId;
    return playerAuthDetails.hasRoleForLeague(leagueId, role);
  }

}
