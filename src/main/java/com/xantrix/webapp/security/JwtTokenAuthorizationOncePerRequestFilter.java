package com.xantrix.webapp.security;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Log
public class JwtTokenAuthorizationOncePerRequestFilter extends OncePerRequestFilter
{
	private UserDetailsService userDetailsService;

	private JwtTokenUtil jwtTokenUtil;

	public JwtTokenAuthorizationOncePerRequestFilter(@Qualifier("customUtenteDetailsService") UserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) {
		this.userDetailsService = userDetailsService;
		this.jwtTokenUtil = jwtTokenUtil;
	}

	@Value("${sicurezza.header}")
	private String tokenHeader = "";

	@Override
	@SneakyThrows
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {

		String path = request.getRequestURI();
		// Salta il filtro per l'endpoint di autenticazione
		if ("/auth".equals(path)) {
			chain.doFilter(request, response);
			return;
		}

		log.info(String.format("Authentication Request For '%s'", request.getRequestURL()));

		final String requestTokenHeader = request.getHeader(this.tokenHeader);
		log.warning("Token: " + requestTokenHeader);

		String username = null;
		String jwtToken = null;
		boolean isTokenExpired = false;

		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			} catch (IllegalArgumentException e) {
				logger.error("IMPOSSIBILE OTTENERE LA USERID", e);
				sendErrorResponse(response, "Token non valido");
				return;
			} catch (ExpiredJwtException e) {
				isTokenExpired = true;
				username = e.getClaims().getSubject(); // Ottieni username dal token scaduto
				logger.warn("TOKEN SCADUTO", e);
				sendErrorResponse(response, "Token scaduto");
				return;
			}
		} else {
			logger.warn("TOKEN NON VALIDO");
			sendErrorResponse(response, "Token di autorizzazione assente o non valido");
			return;
		}

		log.warning(String.format("JWT_TOKEN_USERNAME_VALUE '%s'", username));

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

			if (!isTokenExpired && jwtTokenUtil.validateToken(jwtToken, userDetails)) {
				UsernamePasswordAuthenticationToken authentication =
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);

				log.info(">>>>Authenticated user: " + username + " with roles: " + userDetails.getAuthorities());
			} else {
				log.warning(">>>>Token NON valido per l'utente " + username);
				sendErrorResponse(response, "Token non valido");
				return;
			}
		}

		chain.doFilter(request, response);
	}

	@SneakyThrows
	private void sendErrorResponse(HttpServletResponse response, String message) {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().write("{\"error\":\"" + message + "\"}");
		response.getWriter().flush();
	}
}
