package com.xantrix.webapp.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;

@Component
@Log
public class JwtUnAuthorizedResponseAuthenticationEntryPoint extends BasicAuthenticationEntryPoint
{
	private static String REALM = "REAME";
	
	@Override
	@SneakyThrows
	public void commence(
			final HttpServletRequest request, 
			final HttpServletResponse response,
			final AuthenticationException authException) 
	{
		String ErrMsg = "Token di autorizzazione assente o non valido!";
		
		log.warning("Errore Sicurezza: " + authException.getMessage());
		
		// Authentication failed, send error response.
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");
		

		PrintWriter writer = response.getWriter();
		writer.println(ErrMsg);
	}
	
	@Override
	public void afterPropertiesSet()
	{
		setRealmName(REALM);
		super.afterPropertiesSet();
	}
}
