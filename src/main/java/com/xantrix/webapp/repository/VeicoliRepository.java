package com.xantrix.webapp.repository;

import com.xantrix.webapp.entities.Veicolo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VeicoliRepository extends JpaRepository<Veicolo, Integer> {

    Page<Veicolo> findByTarga(String filtro, Pageable pageAndRecords);
    Optional<Veicolo> findById(Integer id);
    Page<Veicolo> findByModello(String filtro, Pageable pageAndRecords);

    Page<Veicolo> findByTipologia(String filtro, Pageable pageAndRecords);
    Page<Veicolo> findByCasaProduttrice(String filtro, Pageable pageAndRecords);
}
