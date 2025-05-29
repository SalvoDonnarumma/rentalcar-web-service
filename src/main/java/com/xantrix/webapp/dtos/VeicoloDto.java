package com.xantrix.webapp.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class VeicoloDto {

    private Integer id;

    private String targa;

    private String annoImmatricolazione;

    private String modello;

    private String casaProduttrice;

    private String tipologia;

    private Set<PrenotazioneDto> prenotazioni = new HashSet<>();
}
