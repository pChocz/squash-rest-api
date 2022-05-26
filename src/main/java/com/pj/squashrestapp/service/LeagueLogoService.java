package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.RedisCacheConfig;
import com.pj.squashrestapp.repository.LeagueLogoRepository;
import com.pj.squashrestapp.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeagueLogoService {

    private final LeagueLogoRepository leagueLogoRepository;

    @Cacheable(value = RedisCacheConfig.LEAGUE_LOGOS_CACHE, key = "#leagueUuid")
    public byte[] extractLeagueLogo(final UUID leagueUuid) {
        final byte[] leagueLogoBytes = leagueLogoRepository
                .extractLogoBlobByLeagueUuid(leagueUuid)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.LOGO_NOT_FOUND));
        return leagueLogoBytes;
    }
}
