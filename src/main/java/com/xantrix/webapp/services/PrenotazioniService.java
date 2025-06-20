package com.xantrix.webapp.services;

import com.xantrix.webapp.dtos.PrenotazioneDto;
import com.xantrix.webapp.entities.Veicolo;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface PrenotazioniService {

    public PrenotazioneDto selById(Integer id);
    public List<PrenotazioneDto> selByIdUtente(Integer idUtente, int pageNum, int recForPage, String dataInit, String dataFin);
    public List<PrenotazioneDto> selByIdVeicolo(Integer idVeicolo, int pageNum, int recForPage);
    public List<PrenotazioneDto> selByVeicolo(Veicolo veicolo, int pageNum, int recForPage);

    public void insertPrenotazione(PrenotazioneDto prenotazione);

    void eliminaPrenotazione(Integer id);
    public void aggiornaStatoPrenotazione(List<PrenotazioneDto> prenotazioni);
    public void aggiornaStatoPrenotazione(PrenotazioneDto prenotazione);
    public LocalDate convertDateToLocalDate(java.util.Date data);
    public boolean isPrenotazioneNotEditable(Integer id);
    public boolean isPrenotazioneNotEditable(java.util.Date dataInizio);
    public boolean isPrenotazioneFromThePast(Integer id);
}
