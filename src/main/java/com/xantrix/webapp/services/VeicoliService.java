package com.xantrix.webapp.services;

import com.xantrix.webapp.dtos.VeicoloDto;

import java.util.List;

public interface VeicoliService {

    public List<VeicoloDto> SelAll();

    List<VeicoloDto> SearchVeicoli(String filtro, String campoFiltro, int realPage, int recForPage);

    int NumRecords();

    public void InsertVeicolo(VeicoloDto veicolo);

    void DelVeicoloById(Integer id);

    void DelVeicoloByTarga(String targa);

    public VeicoloDto SelByTarga(String targa);

    public VeicoloDto SelById(Integer id);
}
