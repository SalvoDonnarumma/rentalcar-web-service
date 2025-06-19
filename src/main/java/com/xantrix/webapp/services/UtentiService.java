package com.xantrix.webapp.services;

import com.xantrix.webapp.dtos.UtenteDto;

import java.util.List;

public interface UtentiService {

    public List<UtenteDto> selAll();

    List<UtenteDto> searchCostumers(String filtro, String campoFiltro, int pageNum, int recForPage);

    int getNumRecords();

    public void insertCostumer(UtenteDto utente);

    public void deleteCostumer(Integer id);

    public UtenteDto selById(Integer id);

    public UtenteDto selByEmail(String email);

    public boolean emailExists(String email, Integer idUtente);
    public boolean emailExists(String email);
    public List<UtenteDto> emailContains(String filtro);
}
