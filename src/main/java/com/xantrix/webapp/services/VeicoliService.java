package com.xantrix.webapp.services;

import com.xantrix.webapp.dtos.VeicoloDto;

import java.util.List;

public interface VeicoliService {

    public List<VeicoloDto> selAll();

    List<VeicoloDto> searchVeicoli(String filtro, String campoFiltro, int realPage, int recForPage);

    int getNumRecords();

    public void insertVeicolo(VeicoloDto veicolo);

    void delVeicoloById(Integer id);

    public VeicoloDto selByTarga(String targa);

    public VeicoloDto selById(Integer id);
}
