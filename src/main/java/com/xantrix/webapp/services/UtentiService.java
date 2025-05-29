package com.xantrix.webapp.services;

import com.xantrix.webapp.dtos.UtenteDto;
import com.xantrix.webapp.entities.Utente;

import java.util.List;

public interface UtentiService {

    public List<UtenteDto> SelAll();

    List<UtenteDto> SearchCostumers(String filtro, String campoFiltro, int pageNum, int recForPage);

    int NumRecords();

    public void InsertCostumer(UtenteDto utente);

    public void DeleteCostumer(Integer id);

    public UtenteDto SelById(Integer id);

    public UtenteDto SelByEmail(String email);

    public boolean EmailExists(String email, Integer idUtente);
    public boolean EmailExists(String email);

    public List<UtenteDto> SelTutti();
}
