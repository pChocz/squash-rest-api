package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.email.EmailSendConfig;
import com.pj.squashrestapp.config.email.EmailTemplate;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.AllStatsRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppStatsSendingService {

  private final PlayerRepository playerRepository;
  private final AllStatsRepository allStatsRepository;
  private final EmailSendConfig emailSendConfig;


  /**
   * Finds all expired temporary tokens in the database and removes them permanently.
   */
  public void sendStatsRelatedEmail() {
    final Player admin = playerRepository.findByUuid(GeneralUtil.ADMIN_UUID);
    final String receiver = admin.getEmail();
    final String subject = "App Stats";

    final Long playersCounts = (Long) allStatsRepository.findPlayerCounts();
    final Object[] leagueRoundsMatchesCounts = (Object[]) allStatsRepository.findLeagueRoundsMatchesRelevantCounts();
    final Object[] leagueAdditionalMatchesCounts = (Object[]) allStatsRepository.findAdditionalMatchesRelevantCounts();

    final String message = buildStatsMessage(playersCounts, leagueRoundsMatchesCounts, leagueAdditionalMatchesCounts);

    final String htmlContent = EmailTemplate.builder()
            .isWithButton(false)
            .title(subject)
            .username("Admin")
            .beginContent(message)
            .endContent("Just informing, no action required.")
            .build()
            .createHtmlContent();


    emailSendConfig.sendEmail(receiver, subject, htmlContent);
    log.info("Sent an email based on AFTER MIDNIGHT MONDAY CRON");
  }

  private String buildStatsMessage(final Long playersCounts,
                                   final Object[] leagueRoundsMatchesCounts,
                                   final Object[] leagueAdditionalMatchesCounts) {
    final Long leaguesCount = (Long) leagueRoundsMatchesCounts[0];
    final Long seasonsCount = (Long) leagueRoundsMatchesCounts[1];
    final Long roundsCount = (Long) leagueRoundsMatchesCounts[2];
    final Long roundMatchesCount = (Long) leagueRoundsMatchesCounts[3];
    final Long roundMatchesRalliesCount = (Long) leagueRoundsMatchesCounts[4];
    final Long additionalMatchesCount = (Long) leagueAdditionalMatchesCounts[0];
    final Long additionalMatchesRalliesCount = (Long) leagueAdditionalMatchesCounts[1];
    final String message = String.format("""
                    Some app statistics have been collected for you:
                    <br>
                    <br>Players: %s
                    <br>Leagues: %s
                    <br>Seasons: %s
                    <br>Rounds: %s                  
                    <br>Matches: %s                    
                    <br>Rallies: %s
                    <br>
                    """,
            formatValue(playersCounts, GeneralUtil.DECIMAL_FORMAT),
            formatValue(leaguesCount, GeneralUtil.DECIMAL_FORMAT),
            formatValue(seasonsCount, GeneralUtil.DECIMAL_FORMAT),
            formatValue(roundsCount, GeneralUtil.DECIMAL_FORMAT),
            formatValue(roundMatchesCount + additionalMatchesCount, GeneralUtil.DECIMAL_FORMAT),
            formatValue(roundMatchesRalliesCount + additionalMatchesRalliesCount, GeneralUtil.DECIMAL_FORMAT)
    );

    return message;
  }

  private String formatValue(final Number value, final String formatString) {
    final DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
    formatSymbols.setDecimalSeparator(GeneralUtil.DOT_DECIMAL_SEPARATOR);
    formatSymbols.setGroupingSeparator(GeneralUtil.SPACE_GROUPING_SEPARATOR);
    final DecimalFormat formatter = new DecimalFormat(formatString, formatSymbols);
    return formatter.format(value);
  }

}
