package net.thumbtack.busserver.security;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import net.thumbtack.busserver.dao.UserDao;
import net.thumbtack.busserver.model.Session;

@Component
public class SessionProvider {

    private UserDao userDao;

    private CustomUserDetailsService customUserDetailsService;

    @Value("${user_idle_timeout}")
    private int userIdleTimeout;

    @Autowired
    public SessionProvider(UserDao userDao, CustomUserDetailsService customUserDetailsService) {
        this.userDao = userDao;
        this.customUserDetailsService = customUserDetailsService;
    }

    public String createSession(HttpServletResponse response) {
        String sessionId = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("JSESSIONID", sessionId);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(userIdleTimeout);
        response.addCookie(cookie);
        return sessionId;
    }

    public String getUsernameBySession(String sessionId) {
        return userDao.getUserBySessionId(sessionId).getLogin();
    }

    public String resolveSession(HttpServletRequest request) {
        String sessionId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JSESSIONID")) {
                    sessionId = cookie.getValue();
                }
            }
        }
        return sessionId;
    }

    public Authentication getAuthentication(String sessionId) throws UsernameNotFoundException {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(getUsernameBySession(sessionId));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public boolean validateSession(String sessionId) {
        Session session = userDao.getSessionBySessionId(sessionId);
        if (session != null) {
            LocalDateTime time = LocalDateTime.parse(session.getExpiration());
            if (time.isBefore(LocalDateTime.now())) {
                return false;
            }
            return true;
        }
        return false;
    }

    public void extendExpirationCookie(Cookie cookie, HttpServletResponse response) {
        if (cookie.getName().equals("JSESSIONID")) {
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
            Cookie newCookie = new Cookie("JSESSIONID", cookie.getValue());
            newCookie.setPath("/");
            newCookie.setHttpOnly(true);
            newCookie.setMaxAge(userIdleTimeout);
            response.addCookie(newCookie);
        }
    }

}
