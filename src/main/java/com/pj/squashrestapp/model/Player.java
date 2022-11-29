package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "players",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_email",
                        columnNames = {"email"}),
                @UniqueConstraint(
                        name = "uk_username",
                        columnNames = {"username"}
                )
        })
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Player {

    @JsonIgnore
    @ManyToMany(mappedBy = "players", fetch = FetchType.LAZY)
    private final Set<Authority> authorities = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "players", fetch = FetchType.LAZY)
    private final Set<RoleForLeague> roles = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "uuid", nullable = false)
    @EqualsAndHashCode.Include
    private UUID uuid = UUID.randomUUID();

    @Setter
    @Column(name = "username")
    private String username;

    @Setter
    @Column(name = "emoji")
    private String emoji;

    @JsonIgnore
    @Setter
    @Column(name = "password")
    private String password;

    @Setter
    @Column(name = "enabled")
    private boolean enabled;

    @Setter
    @Column(name = "non_locked")
    private boolean nonLocked;

    @Setter
    @Column(name = "registration_date_time")
    @JsonFormat(pattern = GeneralUtil.DATE_TIME_FORMAT)
    private LocalDateTime registrationDateTime;

    @Setter
    @Column(name = "last_logged_in_date_time")
    @JsonFormat(pattern = GeneralUtil.DATE_TIME_FORMAT)
    private LocalDateTime lastLoggedInDateTime;

    @Setter
    @Column(name = "successful_login_attempts")
    private Long successfulLoginAttempts;

    @Setter
    @Column(name = "wants_emails")
    private Boolean wantsEmails;

    @Setter
    @Column(name = "locale")
    private String locale;

    @Setter
    @Column(name = "email")
    private String email;

    @Setter
    @Column(name = "password_session_uuid")
    private UUID passwordSessionUuid;

    public Player(final String username, final String email) {
        this.username = username;
        this.email = email;
        this.enabled = false;
        this.passwordSessionUuid = UUID.randomUUID();
    }

    public Player(final String username) {
        this.username = username;
        this.enabled = false;
        this.passwordSessionUuid = UUID.randomUUID();
    }

    public void addRole(final RoleForLeague roleForLeague) {
        this.roles.add(roleForLeague);
        roleForLeague.getPlayers().add(this);
    }

    public void removeRole(final RoleForLeague roleForLeague) {
        this.roles.remove(roleForLeague);
        roleForLeague.getPlayers().remove(this);
    }

    public void addAuthority(final Authority authority) {
        this.authorities.add(authority);
        authority.getPlayers().add(this);
    }

    public void removeAuthority(final Authority authority) {
        this.authorities.remove(authority);
        authority.getPlayers().remove(this);
    }

    @Override
    public String toString() {
        return username;
    }

    public void incrementSuccessfulLoginAttempts() {
        final Long successfulLoginAttemptsSoFar = this.getSuccessfulLoginAttempts();
        if (successfulLoginAttemptsSoFar == null) {
            this.setSuccessfulLoginAttempts(1L);
        } else {
            this.setSuccessfulLoginAttempts(successfulLoginAttemptsSoFar + 1);
        }
    }
}
