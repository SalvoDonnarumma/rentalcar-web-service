package com.xantrix.webapp.services;

import com.xantrix.webapp.dtos.VeicoloDto;
import com.xantrix.webapp.entities.Veicolo;
import com.xantrix.webapp.repository.VeicoliRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VeicoliServiceImpl implements VeicoliService {

    @Autowired
    private VeicoliRepository veicoliRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public int NumRecords() {
        return (int) veicoliRepository.count();
    }

    @Override
    public void InsertVeicolo(VeicoloDto veicoloDto) {
        veicoliRepository.save(ConvertFromDto(veicoloDto));
    }

    @Override
    public void DelVeicoloById(Integer id) {
        veicoliRepository.deleteById(id);
    }

    @Override
    public VeicoloDto SelByTarga(String targa) {
        Pageable pageAndRecords = PageRequest.of(0, 1);
        Veicolo veicolo = (veicoliRepository.findByTarga(targa,pageAndRecords).getContent().get(0));
        return ConvertToDto(veicolo);
    }

    @Override
    public VeicoloDto SelById(Integer id) {
        Pageable pageAndRecords = PageRequest.of(0, 1);
        Veicolo veicolo = (veicoliRepository.findById(id).get());
        return ConvertToDto(veicolo);
    }

    @Override
    public void DelVeicoloByTarga(String targa) {
        Veicolo daCancellare = ConvertFromDto(SelByTarga(targa));
        veicoliRepository.delete(daCancellare);
    }

    @Override
    public List<VeicoloDto> SelAll() {
        return ConvertToDto(veicoliRepository.findAll());
    }

    @Override
    public List<VeicoloDto> SearchVeicoli(String filtro, String campoFiltro, int realPage, int recForPage) {
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
