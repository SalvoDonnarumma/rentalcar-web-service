package com.xantrix.webapp.security;

import com.xantrix.webapp.dtos.UtenteDto;
import com.xantrix.webapp.services.UtentiService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("customUtenteDetailsService")
public class CustomUtenteDetailsService implements UserDetailsService {

    private UtentiService utentiService;

    public CustomUtenteDetailsService(UtentiService utentiService) {
        this.utentiService = utentiService;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UtenteDto utente = utentiService.selByEmail(email);

        if (utente == null) {
            throw new UsernameNotFoundException("Utente non trovato");
        }

        if(utente.getRuolo().equalsIgnoreCase("admin"))
            utente.setRuolo("ROLE_ADMIN");
        else
            utente.setRuolo("ROLE_USER");

        String ruolo = utente.getRuolo();

        return new User(
                utente.getEmail(),
                utente.getPassword(),
                List.of(new SimpleGrantedAuthority(ruolo))
        );
    }

}
