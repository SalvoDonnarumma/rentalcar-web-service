package com.xantrix.webapp.repository;

import com.xantrix.webapp.entities.Utente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UtentiRepository extends JpaRepository<Utente, Integer> {
    Page<Utente> findByNome(String nome, Pageable pageable);
    Page<Utente> findByCognome(String nome, Pageable pageable);
    Page<Utente> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Optional<Utente> findByEmailAndIdUtenteNot(String email, Integer idUtente);
    Optional<Utente> findByEmail(String email);

    @Query("SELECT u FROM Utente u WHERE FUNCTION('YEAR', u.dataNascita) = :anno")
    Page<Utente> findByAnnoNascita(@Param("anno") int anno, Pageable pageable);
}
