package com.xantrix.webapp.controllers;

import com.xantrix.webapp.dtos.AuthRequest;
import com.xantrix.webapp.dtos.JwtTokenRequest;
import com.xantrix.webapp.dtos.JwtTokenResponse;
import com.xantrix.webapp.exception.AuthenticationException;
import com.xantrix.webapp.security.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jdk.jfr.Label;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@Log
public class AuthController {

    @Value("${sicurezza.header}")
    private String tokenHeader;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    @Qualifier("customUtenteDetailsService")
    private UserDetailsService userDetailsService;

    @PostMapping("${sicurezza.uri}")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authenticationRequest) {
        try {
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

            final UserDetails userDetails = userDetailsService
                    .loadUserByUsername(authenticationRequest.getUsername());

            final String token = jwtTokenUtil.generateToken(userDetails);

            return ResponseEntity.ok(new JwtTokenResponse(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping(value = "${sicurezza.refresh}")
    @SneakyThrows
    public ResponseEntity<JwtTokenResponse> refreshAndGetAuthenticationToken(HttpServletRequest request)
    {
        log.info("Tentativo Refresh Token");
        String authToken = request.getHeader(tokenHeader);

        if (authToken == null)
        {
            throw new Exception("Token assente o non valido!");
        }

        final String token = authToken;

        if (jwtTokenUtil.canTokenBeRefreshed(token))
        {
            String refreshedToken = jwtTokenUtil.refreshToken(token);

            log.warning(String.format("Refreshed Token %s", refreshedToken));

            return ResponseEntity.ok(new JwtTokenResponse(refreshedToken));
        }
        else
        {
            return ResponseEntity.badRequest().body(null);
        }
    }

    private void authenticate(String username, String password)
    {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try
        {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        }
        catch (DisabledException e)
        {
            log.warning("UTENTE DISABILITATO");
            throw new AuthenticationException("UTENTE DISABILITATO", e);
        }
        catch (BadCredentialsException e)
        {
            log.warning("CREDENZIALI NON VALIDE");
            throw new AuthenticationException("CREDENZIALI NON VALIDE", e);
        }
    }
}

