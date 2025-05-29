package com.xantrix.webapp.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "prenotazioni")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idprenotazioni")
    private Integer idPrenotazione;

    @Column(name = "data_inizio")
    private Date dataInizio;

    @Column(name = "data_fine")
    private Date dataFine;

    @ManyToOne
    @JoinColumn(name = "idutente_fk",  referencedColumnName = "idUtente")
    private Utente utente;

    @ManyToOne
    @JoinColumn(name = "idveicolo_fk",  referencedColumnName = "idVeicolo")
    @JsonBackReference
    private Veicolo veicolo;

    @Column(name = "stato")
    private String stato;

    @Override
    public String toString() {
        return "Prenotazione{" +
                "id=" + idPrenotazione +
                ", utenteId=" + (utente != null ? utente.getIdUtente() : "null") +
                ", veicoloId=" + (veicolo != null ? veicolo.getIdVeicolo() : "null") +
                ", stato=" + stato +
                ", dataInizio=" + dataInizio +
                ", dataFine=" + dataFine +
                '}';
    }

}
