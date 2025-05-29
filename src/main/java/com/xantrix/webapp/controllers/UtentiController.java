package com.xantrix.webapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.dtos.PageResponse;
import com.xantrix.webapp.dtos.UtenteDto;
import com.xantrix.webapp.exception.BindingException;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.services.UtentiService;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Log
@RestController
@RequestMapping("/utenti")
public class UtentiController {

    private UtentiService utentiService;
    private ResourceBundleMessageSource errMessage;
    //private BCryptPasswordEncoder passwordEncoder;

    private UtentiController(UtentiService utentiService, ResourceBundleMessageSource errMessage) {
        this.utentiService = utentiService;
        this.errMessage = errMessage;
    }

    @GetMapping(value = "/cerca/tutti")
    public List<UtenteDto> getAllUser()
    {
        log.info("Otteniamo tutti gli utenti");
        return utentiService.selAll();
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

    // ------------------- OTTENIMENTO LISTA COSTUMER CON PAGING  ------------------------------------
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

    // ------------------- INSERIMENTO / MODIFICA COSTUMER ------------------------------------
    @PostMapping(value = "/inserisci")
    @SneakyThrows
    public ResponseEntity<InfoMsg> insertUtente(
            @RequestBody UtenteDto utente,
            BindingResult bindingResult)
    {
        if(utente.getId() != null){
            log.info(">>>Modifica Utente");
        } else
            log.info(">>>Inserimento Nuovo Utente");

        if(bindingResult.hasErrors()){
            String msgErr = errMessage.getMessage(Objects.requireNonNull(bindingResult.getFieldError()), LocaleContextHolder.getLocale());
            log.warning(msgErr);
            throw new BindingException(msgErr);
        }

        utentiService.insertCostumer(utente);

        return new ResponseEntity<InfoMsg>(new InfoMsg(LocalDate.now(),
                String.format("Inserimento Utente %s Eseguita Con Successo", utente.getEmail())), HttpStatus.CREATED);
    }

    // ------------------- ELIMINAZIONE UTENTE ------------------------------------
    @DeleteMapping(value = "/elimina/{id}")
    @SneakyThrows
    public ResponseEntity<?> deleteUser(@PathVariable("id") String userId)
    {
        log.info("Eliminiamo l'utente con id " + userId);

        UtenteDto utente = utentiService.selById(Integer.parseInt(userId));
        if (utente == null)
        {
            String MsgErr = String.format("Utente %s non presente in anagrafica! ", userId);
            log.warning(MsgErr);
            throw new NotFoundException(MsgErr);
        }

        utentiService.deleteCostumer(utente.getId());

        HttpHeaders headers = new HttpHeaders();
        ObjectMapper mapper = new ObjectMapper();

        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectNode responseNode = mapper.createObjectNode();
        responseNode.put("code", HttpStatus.OK.toString());
        responseNode.put("message", "Eliminazione Utente " + userId + " Eseguita Con Successo");

        return new ResponseEntity<>(responseNode, headers, HttpStatus.OK);
    }

}
