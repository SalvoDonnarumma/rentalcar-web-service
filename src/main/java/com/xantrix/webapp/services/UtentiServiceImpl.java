package com.xantrix.webapp.services;

import com.xantrix.webapp.dtos.UtenteDto;
import com.xantrix.webapp.entities.Utente;
import com.xantrix.webapp.exception.EntityHasReservationsException;
import com.xantrix.webapp.repository.PrenotazioniRepository;
import com.xantrix.webapp.repository.UtentiRepository;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log
@Service
public class UtentiServiceImpl implements UtentiService {

    @Autowired
    private UtentiRepository utentiRepository;
    @Autowired
    private PrenotazioniRepository prenotazioniRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<UtenteDto> selAll() {
        List<Utente> utenti = utentiRepository.findAll();
        return ConvertToDto(utenti);
    }

    @Override
    public List<UtenteDto> searchCostumers(String filtro, String campoFiltro, int pageNum, int recForPage) {
        Pageable pageAndRecords = PageRequest.of(pageNum, recForPage);
        Page<Utente> resultPage = null;

        if(filtro == null || filtro.isEmpty()) {
            resultPage = utentiRepository.findAll(pageAndRecords);
        } else if(campoFiltro.equalsIgnoreCase("id")){
            Integer id = Integer.parseInt(filtro);
            Optional<Utente> utente = utentiRepository.findById(id);

            resultPage = utente.<Page<Utente>>map(value -> new PageImpl<>(List.of(value), pageAndRecords, 1)).orElseGet(() -> new PageImpl<>(List.of(), pageAndRecords, 0));
        } else if(campoFiltro.equalsIgnoreCase("nome")) {
                resultPage = utentiRepository.findByNome(filtro, pageAndRecords);
            } else if(campoFiltro.equalsIgnoreCase("cognome")) {
                resultPage = utentiRepository.findByCognome(filtro, pageAndRecords);
                } else
                    resultPage = utentiRepository.findByEmailContainingIgnoreCase(filtro, pageAndRecords);

        List<Utente> utenti = resultPage.getContent();
        return ConvertToDto(utenti);
    }

    @Override
    public int getNumRecords() {
        return (int) utentiRepository.count();
    }

    @Override
    public void insertCostumer(UtenteDto utente) {
        utentiRepository.save(ConvertFromDto(utente));
    }

    @Override
    @SneakyThrows
    public void deleteCostumer(Integer id) {
        if(prenotazioniRepository.existsByUtenteIdUtente(id)) {
            log.info("Sono presenti delle prenotazioni per il costumer con id " + id);
            prenotazioniRepository.deleteByUserId(id);
            log.info("Le prenotazioni sono state cancellate " + id);
        }

        log.info("Ora cancelliamo il costumer");
        Utente utente = null;
        if(utentiRepository.findById(id).isPresent()) {
            utente = utentiRepository.findById(id).get();
            utentiRepository.delete(utente);
        }
    }

    @Override
    public UtenteDto selById(Integer id) {
        Utente utente = null;
        if(utentiRepository.findById(id).isPresent())
            utente = utentiRepository.findById(id).get();
        else
            return null;
        return ConvertToDto(utente);
    }

    @Override
    public UtenteDto selByEmail(String email) {
        Pageable pageAndRecords = PageRequest.of(0, 1);
        Utente utente = utentiRepository.findByEmailContainingIgnoreCase(email, pageAndRecords).getContent().get(0);
        return ConvertToDto(utente);
    }

    @Override
    public boolean emailExists(String email, Integer idUtente) {
        return utentiRepository.findByEmailAndIdUtenteNot(email, idUtente).isPresent();
    }

    @Override
    public boolean emailExists(String email) {
        return utentiRepository.findByEmail(email).isPresent();
    }

    @Override
    public List<UtenteDto> emailContains(String filtro) {
        Pageable pageAndRecords = PageRequest.of(0, 1);
        List<Utente> utente = utentiRepository.findByEmailContainingIgnoreCase(filtro, pageAndRecords).getContent();
        return ConvertToDto(utente);
    }

    private List<UtenteDto> ConvertToDto(List<Utente> utenti) {
        List<UtenteDto> utentiDtoList = utenti
                .stream()
                .map(source -> modelMapper.map(source, UtenteDto.class))
                .collect(Collectors.toList());

        return utentiDtoList;
    }

    private UtenteDto ConvertToDto(Utente utente) {
        UtenteDto utenteDto = null;
        if(utente != null) {
            utenteDto = modelMapper.map(utente, UtenteDto.class);
        }
        return utenteDto;
    }

    private Utente ConvertFromDto(UtenteDto utenteDto) {
        Utente utente = null;
        if(utenteDto != null) {
            utente = modelMapper.map(utenteDto, Utente.class);
        }
        return utente;
    }


}
