package com.pj.squashrestapp.util;

import com.pj.squashrestapp.config.UserDetailsImpl;
import lombok.experimental.UtilityClass;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

/** */
@UtilityClass
public class AuthorizationUtil {

    public static void clearAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public static void configureAuthentication(final String username, final String role) {
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(role);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(new UserDetailsImpl("CRON", authorities), role, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
