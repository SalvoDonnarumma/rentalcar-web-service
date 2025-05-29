package com.xantrix.webapp.repository;

import com.xantrix.webapp.entities.Prenotazione;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;

public interface PrenotazioniRepository extends JpaRepository<Prenotazione, Integer> {

    Page<Prenotazione> findByUtenteIdUtente(Integer idUtente, Pageable pageable);
    Page<Prenotazione> findByVeicoloIdVeicolo(Integer idVeicolo, Pageable pageable);
    Page<Prenotazione> findByIdPrenotazione(Integer idPrenotazione, Pageable pageable);

    Page<Prenotazione> findByUtenteIdUtenteAndDataInizioBetween(Integer idUtente, Date dataInizio, Date dataFine, Pageable pageable);
    Page<Prenotazione> findByUtenteIdUtenteAndDataInizioGreaterThanEqual(Integer idUtente, Date dataInizio, Pageable pageable);
    Page<Prenotazione> findByUtenteIdUtenteAndDataInizioLessThanEqual(Integer idUtente, Date dataFine, Pageable pageable);

}
