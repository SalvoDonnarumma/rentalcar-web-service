package com.xantrix.webapp.services;

import com.xantrix.webapp.dtos.PrenotazioneDto;
import com.xantrix.webapp.entities.Veicolo;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface PrenotazioniService {

    public PrenotazioneDto SelById(Integer id);
    public List<PrenotazioneDto> SelByIdUtente(Integer idUtente, int pageNum, int recForPage, String dataInit, String dataFin);
    public List<PrenotazioneDto> SelByIdVeicolo(Integer idVeicolo, int pageNum, int recForPage);
    public List<PrenotazioneDto> SelByVeicolo(Veicolo veicolo, int pageNum, int recForPage);

    public void InsertPrenotazione(PrenotazioneDto prenotazione);

    void EliminaPrenotazione(Integer id);
    public void AggiornaStatoPrenotazione(Set<PrenotazioneDto> prenotazioni);
    public LocalDate ConvertDateToLocalDate(java.util.Date data);
    public boolean IsPrenotazioneInvalid(Integer id);
    public boolean IsPrenotazioneInvalid(java.util.Date dataInizio);
    public boolean IsPrenotazioneFromThePast(Integer id);
}
