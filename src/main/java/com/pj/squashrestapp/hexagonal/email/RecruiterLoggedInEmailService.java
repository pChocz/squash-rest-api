package com.pj.squashrestapp.hexagonal.email;

import static com.pj.squashrestapp.hexagonal.email.EmailConstants.TEMPLATE_PLAIN;
import static com.pj.squashrestapp.util.GeneralUtil.ADMIN_UUID;

import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.PlayerRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class RecruiterLoggedInEmailService {

  private final SendEmailService sendEmailService;
  private final PlayerRepository playerRepository;

  void sendEmail() {
    final Player admin = playerRepository.findByUuid(ADMIN_UUID);

    final Map<String, Object> model = new HashMap<>();
    model.put("preheader", "Recruiter has logged in!");
    model.put("hiMessage", "Cześć Admin");
    model.put("contentLines", new Object[] {"Recruiter has logged in!.....", "dasdsadadada"});
    model.put("doNotReplyMessage", "Do not reply!");
    model.put("intendedFor", "Intended for admin only.");
    model.put("devMessage", "Pjoter");

    sendEmailService.sendEmailWithModel(admin.getEmail(), "Recruiter login", model, TEMPLATE_PLAIN);
  }
}
