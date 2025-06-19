package com.xantrix.webapp.services;

import com.xantrix.webapp.dtos.PrenotazioneDto;
import com.xantrix.webapp.entities.Prenotazione;
import com.xantrix.webapp.entities.Utente;
import com.xantrix.webapp.entities.Veicolo;
import com.xantrix.webapp.repository.PrenotazioniRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PrenotazioniServiceImpl implements PrenotazioniService {

    @Autowired
    private PrenotazioniRepository prenotazioniRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PrenotazioneDto selById(Integer id) {
        PrenotazioneDto prenotazioneDto = null;
        Pageable pageAndRecords = PageRequest.of(0, 1);
        if(prenotazioniRepository.existsById(id)) {
            prenotazioneDto = ConvertToDto(prenotazioniRepository.findByIdPrenotazione(id, pageAndRecords).getContent().get(0));
            aggiornaStatoPrenotazione(prenotazioneDto);
            return prenotazioneDto;
        } else
            return null;
    }

    @Override
    public List<PrenotazioneDto> selByIdUtente(Integer idUtente, int pageNum, int recForPage, String dataInit, String dataFin) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date inizio = null;
        Date fine = null;

        Pageable pageAndRecords = PageRequest.of(pageNum, recForPage);
        Page<Prenotazione> prenotazioni = null;

        try {
                if(!dataInit.isBlank() && !dataFin.isBlank()) {
                    inizio = new Date(formatter.parse(dataInit).getTime());
                    fine = new Date(formatter.parse(dataFin).getTime());
                    prenotazioni = prenotazioniRepository.findByUtenteIdUtenteAndDataInizioBetween(idUtente, inizio, fine, pageAndRecords);
                } else if(!dataFin.isBlank()) {
                    fine = new Date(formatter.parse(dataFin).getTime());
                    prenotazioni = prenotazioniRepository.findByUtenteIdUtenteAndDataInizioLessThanEqual(idUtente, fine, pageAndRecords);
                } else if(!dataInit.isBlank()) {
                    inizio = new Date(formatter.parse(dataInit).getTime());
                    prenotazioni = prenotazioniRepository.findByUtenteIdUtenteAndDataInizioGreaterThanEqual(idUtente, inizio, pageAndRecords);
                } else {
                    prenotazioni = prenotazioniRepository.findByUtenteIdUtente(idUtente, pageAndRecords);
                }
        } catch (ParseException e) {
                throw new RuntimeException(e);
        }

        List<PrenotazioneDto> prenotazioneDtoList = ConvertToDto(prenotazioni.getContent());
        aggiornaStatoPrenotazione(prenotazioneDtoList);
        return prenotazioneDtoList;
    }

    @Override
    public List<PrenotazioneDto> selByIdVeicolo(Integer idVeicolo, int pageNum, int recForPage) {
        Pageable pageAndRecords = PageRequest.of(pageNum, recForPage);
        return ConvertToDto(prenotazioniRepository.findByVeicoloIdVeicolo(idVeicolo, pageAndRecords).getContent());
    }

    @Override
    public List<PrenotazioneDto> selByVeicolo(Veicolo veicolo, int pageNum, int recForPage) {
        return List.of();
    }

    @Override
    public void insertPrenotazione(PrenotazioneDto prenotazione) {
        prenotazioniRepository.save(ConvertFromDto(prenotazione));
    }

    @Override
    @Transactional
    public void eliminaPrenotazione(Integer id) {
        Prenotazione prenotazione = prenotazioniRepository.findById(id).get();

        Utente utente = prenotazione.getUtente();
        if(utente != null){
            utente.getPrenotazioni().remove(prenotazione);
        }

        Veicolo veicolo = prenotazione.getVeicolo();
        if(veicolo != null){
            veicolo.getPrenotazioni().remove(prenotazione);
        }

        prenotazioniRepository.delete(prenotazione);
    }

    private List<PrenotazioneDto> ConvertToDto(List<Prenotazione> prenotazioni) {
        return prenotazioni
                .stream()
                .map(source -> modelMapper.map(source, PrenotazioneDto.class))
                .collect(Collectors.toList());
    }

    private PrenotazioneDto ConvertToDto(Prenotazione prenotazione) {
        PrenotazioneDto prenotazioneDto = null;
        if(prenotazione != null) {
            prenotazioneDto = modelMapper.map(prenotazione, PrenotazioneDto.class);
        }
        return prenotazioneDto;
    }

    private Prenotazione ConvertFromDto(PrenotazioneDto prenotazioneDto) {
        Prenotazione prenotazione = null;
        if(prenotazioneDto != null) {
            prenotazione = modelMapper.map(prenotazioneDto, Prenotazione.class);
        }
        return prenotazione;
    }

    public void aggiornaStatoPrenotazione(List<PrenotazioneDto> prenotazioni){
        LocalDate limite = LocalDate.now().plusDays(2);
        for (PrenotazioneDto p : prenotazioni) {
            LocalDate dataInizio = convertDateToLocalDate(p.getDataInizio());
            if (!dataInizio.isAfter(limite) && p.getStato().equals("IN ATTESA")) {
                p.setStato("DECLINATO");
                insertPrenotazione(p);
            }

            p.setIsPrenotazioneValid(dataInizio.isAfter(limite));
        }
    }

    public void aggiornaStatoPrenotazione(PrenotazioneDto prenotazione){
        LocalDate limite = LocalDate.now().plusDays(2);
        LocalDate dataInizio = convertDateToLocalDate(prenotazione.getDataInizio());

        if (!dataInizio.isAfter(limite) && prenotazione.getStato().equals("IN ATTESA")) {
            prenotazione.setStato("DECLINATO");
                insertPrenotazione(prenotazione);
            }

        prenotazione.setIsPrenotazioneValid(dataInizio.isAfter(limite));
    }

    public LocalDate convertDateToLocalDate(java.util.Date data) {
        return data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public boolean isPrenotazioneNotEditable(Integer id){
        PrenotazioneDto prenotazione = selById(id);
        java.util.Date dataP = prenotazione.getDataInizio();
        LocalDate dataPrenotazione = convertDateToLocalDate(dataP);
        LocalDate dataOdierna = LocalDate.now();

        return !dataPrenotazione.isAfter(dataOdierna.plusDays(2));
    }

    public boolean isPrenotazioneNotEditable(java.util.Date dataInizio){
        LocalDate dataPrenotazione = convertDateToLocalDate(dataInizio);
        LocalDate dataOdierna = LocalDate.now();

        return !dataPrenotazione.isAfter(dataOdierna.plusDays(2));
    }

    public boolean isPrenotazioneFromThePast(Integer id){
        PrenotazioneDto prenotazione = selById(id);
        java.util.Date dataP = prenotazione.getDataFine();
        LocalDate dataPrenotazione = convertDateToLocalDate(dataP);
        LocalDate dataOdierna = LocalDate.now();

        return dataPrenotazione.isBefore(dataOdierna.minusDays(1));
    }
}
