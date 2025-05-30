package com.xantrix.webapp.services;

import com.xantrix.webapp.dtos.PrenotazioneDto;
import com.xantrix.webapp.dtos.UtenteDto;
import com.xantrix.webapp.dtos.VeicoloDto;
import com.xantrix.webapp.entities.Prenotazione;
import com.xantrix.webapp.entities.Utente;
import com.xantrix.webapp.entities.Veicolo;
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
        modelMapper.addMappings(veicoloMapping);
        modelMapper.addMappings(veicoloDtoMapping);
        modelMapper.addMappings(prenotazioneMapping);
        modelMapper.addMappings(prenotazioneDtoMapping);

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
            map().setVecchiaPassword(source.getPassword());
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

    PropertyMap<Veicolo, VeicoloDto> veicoloMapping = new PropertyMap<Veicolo, VeicoloDto>() {
        protected void configure() {
            map().setId(source.getIdVeicolo());
            map().setTarga(source.getTarga());
            map().setModello(source.getModello());
            map().setTipologia(source.getTipologia());
            map().setAnnoImmatricolazione(source.getAnnoImmatricolazione());
            map().setCasaProduttrice(source.getCasaProduttrice());
        }
    };

    PropertyMap<VeicoloDto, Veicolo> veicoloDtoMapping = new PropertyMap<VeicoloDto, Veicolo>() {
        protected void configure() {
            map().setIdVeicolo(source.getId());
            map().setTarga(source.getTarga());
            map().setModello(source.getModello());
            map().setTipologia(source.getTipologia());
            map().setAnnoImmatricolazione(source.getAnnoImmatricolazione());
            map().setCasaProduttrice(source.getCasaProduttrice());
        }
    };

    PropertyMap<Prenotazione, PrenotazioneDto> prenotazioneMapping = new PropertyMap<Prenotazione, PrenotazioneDto>() {
        protected void configure() {
            map().setDataFine(source.getDataFine());
            map().setDataInizio(source.getDataInizio());
            map().setIdUtente(source.getUtente().getIdUtente());
            map().setIdVeicolo(source.getVeicolo().getIdVeicolo());
            map().setStato(source.getStato());
        }
    };

    PropertyMap<PrenotazioneDto, Prenotazione> prenotazioneDtoMapping = new PropertyMap<PrenotazioneDto, Prenotazione>() {
        protected void configure() {
            map().setDataFine(source.getDataFine());
            map().setDataInizio(source.getDataInizio());
            map().getUtente().setIdUtente(source.getIdUtente());
            map().getVeicolo().setIdVeicolo(source.getIdVeicolo());
            map().setStato(source.getStato());
        }
    };
}
