package com.pj.squashrestapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "emails_queue")
@Getter
@NoArgsConstructor
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "sent")
    private boolean sent;

    @Setter
    @Column(name = "sent_datetime")
    private LocalDateTime sentDatetime;

    @Setter
    @Column(name = "to_address", length = 1_000)
    private String toAddress;

    @Setter
    @Column(name = "cc_address", length = 1_000)
    private String ccAddress;

    @Setter
    @Column(name = "bcc_address", length = 1_000)
    private String bccAddress;

    @Setter
    @Column(name = "subject")
    private String subject;

    @Setter
    @Column(name = "html_content", length = 100_000)
    private String htmlContent;

    @Setter
    @Column(name = "send_after_datetime")
    private LocalDateTime sendAfterDatetime;

    @Setter
    @Column(name = "send_before_datetime")
    private LocalDateTime sendBeforeDatetime;

    @Setter
    @Column(name = "tries_count")
    private int triesCount;
}
