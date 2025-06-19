package com.xantrix.webapp.controllers;

import com.xantrix.webapp.dtos.*;
import com.xantrix.webapp.exception.DateNotValidException;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.services.PrenotazioniService;
import com.xantrix.webapp.services.UtentiService;
import com.xantrix.webapp.services.VeicoliService;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Log
@RestController
@RequestMapping("/prenotazioni")
public class PrenotazioniController {

    private PrenotazioniService prenotazioniService;
    private UtentiService utentiService;
    private VeicoliService veicoliService;
    private ResourceBundleMessageSource errMessage;

    public PrenotazioniController(PrenotazioniService prenotazioniService, ResourceBundleMessageSource errMessage, UtentiService utentiService, VeicoliService veicoliService) {
        this.prenotazioniService = prenotazioniService;
        this.errMessage = errMessage;
        this.utentiService = utentiService;
        this.veicoliService = veicoliService;
    }

    @GetMapping(value = "/cerca/userid/{userId}")
    @SneakyThrows
    public ResponseEntity <PageResponse<PrenotazioneDto>> getPrenotazioneByUserId(
            @PathVariable("userId") String userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int recordsPerPage,
            @RequestParam(name = "diff", defaultValue = "0") int diffPage,
            @RequestParam(required = false, defaultValue = "") String dataInit,
            @RequestParam(required = false, defaultValue = "") String dataFin)
    {
        UtenteDto utente = utentiService.selById(Integer.parseInt(userId));
        if (utente == null) {
            String ErrMsg = String.format("Lo userId inserto %s non e' presente nel database!", userId);
            log.warning(ErrMsg);
            throw new NotFoundException(ErrMsg);
        } else {
            log.info(String.format("L'utente %s e' stato trovato!", userId));
        }

        if (pageNum >= 1) {
            pageNum += diffPage;
        } else {
            pageNum = 1;
        }

        int pageZeroBased = pageNum - 1;

        List<PrenotazioneDto> prenotazioni = prenotazioniService.selByIdUtente(Integer.parseInt(userId), pageZeroBased, recordsPerPage, dataInit, dataFin);
        int totalRecords = utentiService.getNumRecords();

        PageResponse<PrenotazioneDto> response = new PageResponse<>(
                prenotazioni,
                pageZeroBased,
                recordsPerPage,
                totalRecords
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/cerca/email/{email}")
    @SneakyThrows
    public ResponseEntity <PageResponse<PrenotazioneDto>> getPrenotazioneByEmail(
            @PathVariable("email") String email,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int recordsPerPage,
            @RequestParam(name = "diff", defaultValue = "0") int diffPage,
            @RequestParam(required = false, defaultValue = "") String dataInit,
            @RequestParam(required = false, defaultValue = "") String dataFin)
    {
        UtenteDto utente = utentiService.selByEmail(email);
        if (utente == null) {
            String ErrMsg = String.format("Lo userId inserto %s non e' presente nel database!", email);
            log.warning(ErrMsg);
            throw new NotFoundException(ErrMsg);
        } else {
            log.info(String.format("L'utente %s e' stato trovato!", email));
        }

        if (pageNum >= 1) {
            pageNum += diffPage;
        } else {
            pageNum = 1;
        }

        int pageZeroBased = pageNum - 1;

        List<PrenotazioneDto> prenotazioni = prenotazioniService.selByIdUtente(utente.getId(), pageZeroBased, recordsPerPage, dataInit, dataFin);
        int totalRecords = utentiService.getNumRecords();

        PageResponse<PrenotazioneDto> response = new PageResponse<>(
                prenotazioni,
                pageZeroBased,
                recordsPerPage,
                totalRecords
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/modifica/{idPrenotazione}")
    @SneakyThrows
    public ResponseEntity<PrenotazioneFormDto> getModificaPrenotazionePage(
            @PathVariable(name = "idPrenotazione") String idPrenotazione){

        PrenotazioneDto prenotazione = prenotazioniService.selById(Integer.parseInt(idPrenotazione));
        if(prenotazione == null) {
            throw new NotFoundException("La prenotazione che si vuol modificare non esiste!");
        }

        VeicoloDto veicolo = veicoliService.selById(prenotazione.getIdVeicolo());
        veicolo.setPrenotazioni(null);
        UtenteDto utente = utentiService.selById(prenotazione.getIdUtente());
        utente.setPrenotazioni(null);

        PrenotazioneFormDto prenotazioneFormData = new PrenotazioneFormDto(
                prenotazione,
                veicolo,
                utente,
                LocalDate.now().plusDays(3));

        return ResponseEntity.ok(prenotazioneFormData);
    }

    @GetMapping("/visualizzaprenotazioni/userid/{userId}")
    @SneakyThrows
    public ResponseEntity <PageResponse<PrenotazioneDto>> getVisualizzaPrenotazioniUtente(@PathVariable("userId") String userId){
        List<PrenotazioneDto> prenotazioni = prenotazioniService.selByIdUtente(Integer.parseInt(userId), 0, 99, "","");

        PageResponse<PrenotazioneDto> response = new PageResponse<>(
                prenotazioni,
                0,
                99,
                prenotazioni.size()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/inserisci")
    @SneakyThrows
    public ResponseEntity<?> insertPrenotazioni(@RequestBody PrenotazioneDto prenotazione) {
        log.info("Prenotazione passata: "+prenotazione);
        if(prenotazione.getIdPrenotazione() != null) {
            log.info(">>>Modifica Prenotazione");
            if (prenotazioniService.isPrenotazioneNotEditable(prenotazione.getDataInizio())) {
                return ResponseEntity.badRequest()
                        .body(new DateNotValidException("ERRORE: Non è piu' possibile modificare la prenotazione"));
            }
        } else {
            log.info(">>>Inserimento Nuova Prenotazione");
            if (prenotazioniService.isPrenotazioneNotEditable(prenotazione.getDataInizio())) {
                return ResponseEntity.badRequest()
                        .body(new InfoMsg(LocalDate.now(), "ERRORE: La data di inizio non e' valida!"));
            }
        }

        if (prenotazione.getDataInizio().after(prenotazione.getDataFine())) {
            return ResponseEntity.badRequest()
                    .body(new InfoMsg(LocalDate.now(),"ERRORE: Date inserite non valide"));
        }

        prenotazione.setIsPrenotazioneValid(true);
        prenotazione.setStato("IN ATTESA");

        prenotazioniService.insertPrenotazione(prenotazione);

        return new ResponseEntity<InfoMsg>(new InfoMsg(LocalDate.now(),
                String.format("Inserimento Prenotazione %s Eseguita Con Successo", prenotazione.getIdPrenotazione())), HttpStatus.CREATED);
    }

    @PostMapping("/valida/{idPrenotazione}")
    public ResponseEntity<?> validaPrenotazione(
            @PathVariable(name = "idPrenotazione") String idPrenotazione,
            @RequestParam(name = "modificaStato", required = true) String modificaStato) {

        PrenotazioneDto prenotazione = prenotazioniService.selById(Integer.parseInt(idPrenotazione));
        if(prenotazione == null){
            return ResponseEntity.badRequest()
                    .body(new NotFoundException(">>Id Prenotazione non esistente!"));
        }

        if(modificaStato.equals("APPROVATO")){
            prenotazione.setStato("APPROVATO");
        } else
            prenotazione.setStato("DECLINATO");

        prenotazioniService.insertPrenotazione(prenotazione);

        return new ResponseEntity<InfoMsg>(new InfoMsg(LocalDate.now(),
                String.format("Lo stato della prenotazione %s è stato correttamente modificato", prenotazione.getIdPrenotazione())), HttpStatus.CREATED);
    }

    @DeleteMapping("/elimina/{idPrenotazione}")
    public ResponseEntity<?> eliminaPrenotazione(
            @PathVariable(name = "idPrenotazione", required = true) String idPrenotazione){

        PrenotazioneDto prenotazioneDto = prenotazioniService.selById(Integer.parseInt(idPrenotazione));

        if(prenotazioneDto == null){
            return ResponseEntity.badRequest()
                    .body(new NotFoundException(">>L'idPrenotazione inserito non e' valido o inesistente!"));
        }

        if(prenotazioniService.isPrenotazioneFromThePast(Integer.parseInt(idPrenotazione))){
            prenotazioniService.eliminaPrenotazione(Integer.parseInt(idPrenotazione));
            return new ResponseEntity<InfoMsg>(new InfoMsg(LocalDate.now(),
                    String.format("La prenotazione %s è stata elimina con successo", idPrenotazione)), HttpStatus.CREATED);
        } else if(prenotazioniService.isPrenotazioneNotEditable(Integer.parseInt(idPrenotazione))){
            return ResponseEntity.badRequest()
                    .body(new DateNotValidException(">>La prenotazione non può più essere cancellata poiché la data di inizio e' vicina!"));
        }

        prenotazioniService.eliminaPrenotazione(Integer.parseInt(idPrenotazione));
        return new ResponseEntity<InfoMsg>(new InfoMsg(LocalDate.now(),
                String.format("La prenotazione %s è stata elimina con successo", idPrenotazione)), HttpStatus.CREATED);
    }


}
