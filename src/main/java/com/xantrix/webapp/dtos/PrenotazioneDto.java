package com.xantrix.webapp.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class PrenotazioneDto {

    private Integer idPrenotazione;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dataInizio;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dataFine;

    private String stato;

    private Integer idUtente;
    private Integer idVeicolo;

    private Boolean isPrenotazioneValid;

    @Override
    public String toString() {
        return "Prenotazione{" +
                "id=" + idPrenotazione +
                ", utenteId=" + idUtente +
                ", veicoloId=" + idVeicolo +
                ", stato=" + stato +
                ", IsDataValida=" + isPrenotazioneValid +
                '}';
    }
}
