package com.xantrix.webapp.services;

import com.xantrix.webapp.dtos.VeicoloDto;
import com.xantrix.webapp.entities.Veicolo;
import com.xantrix.webapp.repository.PrenotazioniRepository;
import com.xantrix.webapp.repository.VeicoliRepository;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log
@Service
public class VeicoliServiceImpl implements VeicoliService {

    @Autowired
    private VeicoliRepository veicoliRepository;
    @Autowired
    private PrenotazioniRepository prenotazioniRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public int getNumRecords() {
        return (int) veicoliRepository.count();
    }

    @Override
    public void insertVeicolo(VeicoloDto veicoloDto) {
        veicoliRepository.save(ConvertFromDto(veicoloDto));
    }

    @Override
    public void delVeicoloById(Integer id) {
        if(prenotazioniRepository.existsByVeicoloIdVeicolo(id)) {
            log.info("Sono presenti delle prenotazioni per il veicolo con id " + id);
            prenotazioniRepository.deleteByVeicoloId(id);
            log.info("Le prenotazioni sono state cancellate " + id);
        }

        log.info("Ora cancelliamo il veicolo");
        Veicolo veicolo = null;
        if(veicoliRepository.findById(id).isPresent()) {
            veicolo = veicoliRepository.findById(id).get();
            veicoliRepository.delete(veicolo);
        }
    }

    @Override
    public List<VeicoloDto> selByTarga(String targa) {
        Pageable pageAndRecords = PageRequest.of(0, 30);
        List<Veicolo> veicoli = veicoliRepository.findByTarga(targa, pageAndRecords).getContent();
        if (veicoli.isEmpty()) {
            return null;
        }
        return ConvertToDto(veicoli);
    }

    @Override
    public List<VeicoloDto> selByModello(String modello) {
        Pageable pageAndRecords = PageRequest.of(0, 30);
        List<Veicolo> veicoli = veicoliRepository.findByModello(modello, pageAndRecords).getContent();
        if (veicoli.isEmpty()) {
            return null;
        }
        return ConvertToDto(veicoli);
    }

    @Override
    public List<VeicoloDto> selByCasaProduttrice(String casaProduttrice) {
        Pageable pageAndRecords = PageRequest.of(0, 30);
        List<Veicolo> veicoli = veicoliRepository.findByCasaProduttrice(casaProduttrice, pageAndRecords).getContent();
        if (veicoli.isEmpty()) {
            return null;
        }
        return ConvertToDto(veicoli);
    }

    @Override
    public List<VeicoloDto> selByTipologia(String tipologia) {
        Pageable pageAndRecords = PageRequest.of(0, 30);
        List<Veicolo> veicoli = veicoliRepository.findByTipologia(tipologia, pageAndRecords).getContent();
        if (veicoli.isEmpty()) {
            return null;
        }
        return ConvertToDto(veicoli);
    }

    @Override
    public VeicoloDto selById(Integer id) {
        Pageable pageAndRecords = PageRequest.of(0, 1);
        Veicolo veicolo = null;
        if(veicoliRepository.findById(id).isPresent()) {
            veicolo = (veicoliRepository.findById(id).get());
        } else
            return null;
        return ConvertToDto(veicolo);
    }

    @Override
    public List<VeicoloDto> selAll() {
        return ConvertToDto(veicoliRepository.findAll());
    }

    @Override
    public List<VeicoloDto> searchVeicoli(String filtro, String campoFiltro, int realPage, int recForPage) {
        Pageable pageAndRecords = PageRequest.of(realPage, recForPage);
        Page<Veicolo> resultPage = null;

        if( filtro == null || filtro.isEmpty() ){
            resultPage = veicoliRepository.findAll(pageAndRecords);
        } else {
            if( campoFiltro.equalsIgnoreCase("targa"))
                resultPage = veicoliRepository.findByTarga(filtro, pageAndRecords);
            else if( campoFiltro.equalsIgnoreCase("modello"))
                resultPage = veicoliRepository.findByModello(filtro, pageAndRecords);
            else if( campoFiltro.equalsIgnoreCase("tipologia"))
                resultPage = veicoliRepository.findByTipologia(filtro, pageAndRecords);
                else
                    resultPage = veicoliRepository.findByCasaProduttrice(filtro, pageAndRecords);
        }

        List<Veicolo> veicoli = resultPage.getContent();
        return ConvertToDto(veicoli);
    }

    private List<VeicoloDto> ConvertToDto(List<Veicolo> veicoli) {
        List<VeicoloDto> veicoliDtoList = veicoli
                .stream()
                .map(source -> modelMapper.map(source, VeicoloDto.class))
                .collect(Collectors.toList());

        return veicoliDtoList;
    }

    private VeicoloDto ConvertToDto(Veicolo veicolo) {
        VeicoloDto veicoloDto = null;
        if(veicolo != null) {
            veicoloDto = modelMapper.map(veicolo, VeicoloDto.class);
        }
        return veicoloDto;
    }

    private Veicolo ConvertFromDto(VeicoloDto veicoloDto) {
        Veicolo veicolo = null;
        if(veicoloDto != null) {
            veicolo = modelMapper.map(veicoloDto, Veicolo.class);
        }
        return veicolo;
    }

}
