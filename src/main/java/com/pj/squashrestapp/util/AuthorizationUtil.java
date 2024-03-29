package com.pj.squashrestapp.util;

import com.pj.squashrestapp.config.UserDetailsImpl;
import lombok.experimental.UtilityClass;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/** */
@UtilityClass
public class AuthorizationUtil {

    private static final String NULL_IP = "0.0.0.0";
    private static final String[] IP_HEADER_CANDIDATES = {
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR"
    };

    public static void clearAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public static void configureAuthentication(final String username, final String role) {
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(role);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(new UserDetailsImpl(username, authorities), role, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static String extractRequestIpAddress() {
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return NULL_IP;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        for (String header : IP_HEADER_CANDIDATES) {
            String ipList = request.getHeader(header);
            if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
                return ipList.split(",")[0];
            }
        }

        return request.getRemoteAddr();
    }
}
