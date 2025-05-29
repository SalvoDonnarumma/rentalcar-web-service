package com.xantrix.webapp.services;

import com.xantrix.webapp.dtos.UtenteDto;
import com.xantrix.webapp.entities.Utente;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    private final ModelMapper modelMapper = new ModelMapper();

    @Bean
    public ModelMapper modelMapper() {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.addMappings(utenteMapping);
        modelMapper.addMappings(utenteDtoMapping);

        return modelMapper;
    }

    PropertyMap<Utente, UtenteDto> utenteMapping = new PropertyMap<Utente, UtenteDto>() {
        protected void configure() {
            map().setId(source.getIdUtente());
            map().setNome(source.getNome());
            map().setCognome(source.getCognome());
            map().setDataNascita(source.getDataNascita());
            map().setRuolo(source.getRuolo());
            map().setEmail(source.getEmail());
            map().setPassword(source.getPassword());
        }
    };

    PropertyMap<UtenteDto, Utente> utenteDtoMapping = new PropertyMap<UtenteDto, Utente>() {
        protected void configure() {
            map().setIdUtente(source.getId());
            map().setNome(source.getNome());
            map().setCognome(source.getCognome());
            map().setDataNascita(source.getDataNascita());
            map().setRuolo(source.getRuolo());
            map().setEmail(source.getEmail());
            map().setPassword(source.getPassword());
        }
    };

}
