package net.thumbtack.busserver.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import net.thumbtack.busserver.dao.UserDao;
import net.thumbtack.busserver.dto.request.AuthenticationRequest;
import net.thumbtack.busserver.model.Session;
import net.thumbtack.busserver.model.User;

public class CustomUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/api/sessions",
			"POST");

	private boolean postOnly = true;

	@Autowired
	private UserDao userDao;

	@Autowired
    private SessionProvider sessionProvider;

	public CustomUsernamePasswordAuthenticationFilter() {
		super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
	}

	public CustomUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException {
		BufferedReader reader = request.getReader();
		Gson gson = new Gson();
		AuthenticationRequest user = gson.fromJson(reader, AuthenticationRequest.class);
		if (this.postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}
		String sessionId = sessionProvider.createSession(response);
        Session session = new Session(sessionId, LocalDateTime.now().plusSeconds(60).toString());
		userDao.deleteSessionsByExpiration();

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword());
		setDetails(request, authRequest);
		User userFromDB = userDao.getUserByLogin(user.getLogin());
		if (userFromDB != null) {
			userDao.insertSession(userFromDB.getId(), session);
		}
		return this.getAuthenticationManager().authenticate(authRequest);
	}

	protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
		authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
	}

}
