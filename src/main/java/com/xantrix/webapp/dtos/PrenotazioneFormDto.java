package com.xantrix.webapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class PrenotazioneFormDto {
    private PrenotazioneDto prenotazione;
    private VeicoloDto veicolo;
    private UtenteDto utente;
    private LocalDate dataMinima;
}