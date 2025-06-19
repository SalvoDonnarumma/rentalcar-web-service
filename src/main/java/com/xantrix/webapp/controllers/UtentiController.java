package com.xantrix.webapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.dtos.InfoMsg;
import com.xantrix.webapp.dtos.PageResponse;
import com.xantrix.webapp.dtos.UtenteDto;
import com.xantrix.webapp.exception.*;
import com.xantrix.webapp.services.UtentiService;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Log
@RestController
@RequestMapping("/utenti")
public class UtentiController {

    private UtentiService utentiService;
    private ResourceBundleMessageSource errMessage;
    private PasswordEncoder passwordEncoder;

    public UtentiController(UtentiService utentiService, ResourceBundleMessageSource errMessage, PasswordEncoder passwordEncoder) {
        this.utentiService = utentiService;
        this.errMessage = errMessage;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(value = "/cerca/filtro/{filtro}")
    @SneakyThrows
    public ResponseEntity<PageResponse<UtenteDto>> searchUsersUsingEmail(@PathVariable("filtro") String filtro)
    {
        List<UtenteDto> utenti = utentiService.emailContains(filtro);
        if (utenti == null)
        {
            String ErrMsg = String.format("L'utente %s non e' stato trovato!", filtro);
            log.warning(ErrMsg);
            throw new NotFoundException(ErrMsg);
        }
        else
        {
            log.info(String.format("L'utente %s e' stato trovato!", filtro));
        }

        PageResponse<UtenteDto> response = new PageResponse<>(
                utenti,
                1,
                10,
                utenti.size()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/cerca/userid/{userId}")
    @SneakyThrows
    public UtenteDto getUtenteById(@PathVariable("userId") String userId)
    {
        log.info("Otteniamo l'utente " + userId);

        UtenteDto utente = utentiService.selById(Integer.parseInt(userId));

        if (utente == null)
        {
            String ErrMsg = String.format("L'utente %s non e' stato trovato!", userId);
            log.warning(ErrMsg);
            throw new NotFoundException(ErrMsg);
        }
        else
        {
            log.info(String.format("L'utente %s e' stato trovato!", userId));
        }

        return utente;
    }

    @GetMapping(value = "/cerca/email/{email}")
    @SneakyThrows
    public UtenteDto getUtenteByEmail(@PathVariable("email") String email)
    {
        UtenteDto utente = utentiService.selByEmail(email);

        if (utente == null)
        {
            String ErrMsg = String.format("L'utente %s non e' stato trovato!", email);
            log.warning(ErrMsg);
            throw new NotFoundException(ErrMsg);
        }
        else
        {
            log.info(String.format("L'utente %s e' stato trovato!", email));
        }

        return utente;
    }

    // ------------------- OTTENIMENTO LISTA COSTUMER CON PAGING  ------------------------------------
    /*
    @GetMapping("/admin/homepage")
    public ResponseEntity<PageResponse<UtenteDto>> searchUtenti(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int recordsPerPage,
            @RequestParam(name = "diff", defaultValue = "0") int diffPage,
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String campoFiltro
    ) {

        if (pageNum >= 1) {
            pageNum += diffPage;
        } else {
            pageNum = 1;
        }

        int pageZeroBased = pageNum - 1;

        List<UtenteDto> utenti = utentiService.searchCostumers(filtro, campoFiltro, pageZeroBased, recordsPerPage);
        int totalRecords = utentiService.getNumRecords();

        PageResponse<UtenteDto> response = new PageResponse<>(
                utenti,
                pageZeroBased,
                recordsPerPage,
                totalRecords
        );

        return ResponseEntity.ok(response);
    }
    */

    @GetMapping("/admin/homepage")
    public ResponseEntity<PageResponse<UtenteDto>> getAllUtenti(
            @RequestParam(name = "diff", defaultValue = "0") int diffPage,
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String campoFiltro
    ) {

        List<UtenteDto> utenti = utentiService.selAll();
        int totalRecords = utentiService.getNumRecords();

        PageResponse<UtenteDto> response = new PageResponse<>(
                utenti,
                1,
                10,
                totalRecords
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/modifica/{userId}")
    @SneakyThrows
    public ResponseEntity<UtenteDto> searchUtenti(
            @PathVariable(name = "userId") String userId){

        UtenteDto utente = utentiService.selById(Integer.parseInt(userId));
        if(utente == null)
            throw new NotFoundException("Errore: utente che si cerca di modificare non esiste!");

        return ResponseEntity.ok(utente);
    }

    // ------------------- INSERIMENTO / MODIFICA COSTUMER ------------------------------------
    @PostMapping(value = "/inserisci")
    @SneakyThrows
    public ResponseEntity<InfoMsg> insertUtente(
            @RequestBody UtenteDto utente)
    {
        if(utente.getId() != null){
            log.info(">>>Modifica Utente");
        } else
            log.info(">>>Inserimento Nuovo Utente");

        log.info("Utente json passato: " + utente);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String ruolo = authentication.getAuthorities().toString();

        log.info("Ruolo: " + ruolo);

        if(ruolo.equalsIgnoreCase("[ROLE_USER]") && utente.getId()==null){ //Un costumer sta provando ad inserire un nuovo costumer, azione non permessa
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new InfoMsg(LocalDate.now(), "Un customer non può inserire un nuovo customer!"));
        } else if(ruolo.equalsIgnoreCase("[ROLE_ADMIN]") && utente.getId()==null){//Un admin sta provando ad inserire un nuovo costumer.
            //Controllo email già esistente
            if(utentiService.emailExists(utente.getEmail())){
                return ResponseEntity.badRequest()
                        .body(new InfoMsg(LocalDate.now(), "L'email inserita esiste e' gia' in uso!"));
            }

            //Controllo se la password e quella di conferma coincidono

            utente.setPassword(passwordEncoder.encode(utente.getPassword()));
            utentiService.insertCostumer(utente);
            log.info("Utente inserito con successo!");
        }

        if( (ruolo.equalsIgnoreCase("[ROLE_ADMIN]") || ruolo.equalsIgnoreCase("[ROLE_USER]") ) && utente.getId()!=null) {

            if(ruolo.equalsIgnoreCase("[ROLE_USER]")){
                UtenteDto logged = utentiService.selByEmail(authentication.getName());
                if(logged.getId() != utente.getId()){ //Un costumer stra provando a modificare un altro costumer
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new InfoMsg(LocalDate.now(), "Un customer non può modificare un altro customer!"));
                }
            }

            //Controllo vecchia password valida
            String hashedPasswordOnDB = utentiService.selById(utente.getId()).getVecchiaPassword();
            if(!passwordEncoder.matches(utente.getVecchiaPassword(), hashedPasswordOnDB)){
                return ResponseEntity.badRequest()
                        .body(new InfoMsg(LocalDate.now(), "La vecchia password inserita non è corretta!"));
            }

            //Controllo nuova password e conferma password
            if(!utente.getPassword().isBlank()) {
                if (!utente.getPassword().equals(utente.getConfermaPassword())) {
                    return ResponseEntity.badRequest()
                            .body(new InfoMsg(LocalDate.now(), "La password nuova e quella di conferma non coincidono!"));
                }
                utente.setPassword(passwordEncoder.encode(utente.getConfermaPassword()));
            } else
                utente.setPassword(hashedPasswordOnDB);

            //Controlla email valida
            if(utentiService.emailExists(utente.getEmail(), utente.getId())){
                return ResponseEntity.badRequest()
                        .body(new InfoMsg(LocalDate.now(), "L'email inserita esiste e' gia' in uso!"));
            }

            log.info("Utente json passato: " + utente);

            utentiService.insertCostumer(utente);
        }

        return new ResponseEntity<InfoMsg>(new InfoMsg(LocalDate.now(),
                String.format("Inserimento Utente %s Eseguita Con Successo", utente.getEmail())), HttpStatus.CREATED);
    }

    // ------------------- ELIMINAZIONE UTENTE ------------------------------------
    @DeleteMapping(value = "/elimina/{id}")
    @SneakyThrows
    public ResponseEntity<?> deleteUser(@PathVariable("id") String userId) {
        log.info("Eliminiamo l'utente con id " + userId);


        utentiService.deleteCostumer(Integer.parseInt(userId));

        HttpHeaders headers = new HttpHeaders();
        ObjectMapper mapper = new ObjectMapper();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectNode responseNode = mapper.createObjectNode();
        responseNode.put("code", HttpStatus.OK.toString());
        responseNode.put("message", "Eliminazione Utente " + userId + " Eseguita Con Successo");

        return new ResponseEntity<>(responseNode, headers, HttpStatus.OK);
    }

}
