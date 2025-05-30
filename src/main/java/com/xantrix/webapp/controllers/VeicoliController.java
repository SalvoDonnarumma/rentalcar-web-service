package com.xantrix.webapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.dtos.PageResponse;
import com.xantrix.webapp.dtos.VeicoloDto;
import com.xantrix.webapp.exception.BindingException;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.services.VeicoliService;
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
@RequestMapping("/veicoli")
public class VeicoliController {

    private VeicoliService veicoliService;
    private ResourceBundleMessageSource errMessage;

    private VeicoliController(VeicoliService service, ResourceBundleMessageSource errMessage ) {
        this.veicoliService = service;
        this.errMessage = errMessage;
    }

    @GetMapping(value="/cerca/tutti")
    public List<VeicoloDto> getAllVehicles(){
        log.info("Otteniamo tutti i veicoli");
        return veicoliService.selAll();
    }

    @GetMapping("/cerca/veicolid/{veicoliId}")
    @SneakyThrows
    public VeicoloDto getVeicoli(@PathVariable("veicoliId") String veicoliId) {
        log.info("Otteniamo l'utente " + veicoliId);
        VeicoloDto veicolo = veicoliService.selById(Integer.parseInt(veicoliId));

        if(veicolo == null) {
            String ErrMsg = String.format("Il veicolo %s non e' stato trovato!", veicoliId);
            log.warning(ErrMsg);
            throw new NotFoundException(ErrMsg);
        } else
            {
                log.info(String.format("Il veicolo %s e' stato trovato!", veicoliId));
            }
        return veicolo;
    }

    @GetMapping("admin/modifica/{veicoloId}")
    @SneakyThrows
    public ResponseEntity<VeicoloDto> searchUtenti(
            @PathVariable(name = "veicoloId") String veicoloId){

        VeicoloDto veicolo = veicoliService.selById(Integer.parseInt(veicoloId));
        if(veicolo == null)
            throw new NotFoundException("Errore: il veicolo che si cerca di modificare non esiste!");

        return ResponseEntity.ok(veicolo);
    }

    // ------------------- OTTENIMENTO LISTA VEICOLI CON PAGING  ------------------------------------
    @GetMapping("/admin/parcoauto")
    public ResponseEntity<PageResponse<VeicoloDto>> searchUtenti(
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

        List<VeicoloDto> veicoli = veicoliService.searchVeicoli(filtro, campoFiltro, pageZeroBased, recordsPerPage);
        int totalRecords = veicoliService.getNumRecords();

        PageResponse<VeicoloDto> response = new PageResponse<>(
                veicoli,
                pageZeroBased,
                recordsPerPage,
                totalRecords
        );

        return ResponseEntity.ok(response);
    }

    // ------------------- INSERIMENTO / MODIFICA VEICOLO ------------------------------------
    @PostMapping(value = "/inserisci")
    @SneakyThrows
    public ResponseEntity<InfoMsg> insertVeicolo(
            @RequestBody VeicoloDto veicolo,
            BindingResult bindingResult)
    {
        if(veicolo.getId() != null){
            log.info(">>>Modifica Veicolo");
        } else
            log.info(">>>Inserimento Nuovo Veicolo");

        if(bindingResult.hasErrors()){
            String msgErr = errMessage.getMessage(Objects.requireNonNull(bindingResult.getFieldError()), LocaleContextHolder.getLocale());
            log.warning(msgErr);
            throw new BindingException(msgErr);
        }

        veicoliService.insertVeicolo(veicolo);

        return new ResponseEntity<InfoMsg>(new InfoMsg(LocalDate.now(),
                String.format("Inserimento Veicolo %s Eseguita Con Successo", veicolo.getTarga())), HttpStatus.CREATED);
    }

    // ------------------- ELIMINAZIONE UTENTE ------------------------------------
    @DeleteMapping(value = "/elimina/{id}")
    @SneakyThrows
    public ResponseEntity<?> deleteVeicolo(@PathVariable("id") String veicoliId)
    {
        log.info("Eliminiamo il veicolo con id " + veicoliId);

        VeicoloDto veicolo = veicoliService.selById(Integer.parseInt(veicoliId));
        if (veicolo == null)
        {
            String MsgErr = String.format("Veicolo %s non presente in anagrafica! ", veicoliId);
            log.warning(MsgErr);
            throw new NotFoundException(MsgErr);
        }

        veicoliService.delVeicoloById(veicolo.getId());

        HttpHeaders headers = new HttpHeaders();
        ObjectMapper mapper = new ObjectMapper();

        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectNode responseNode = mapper.createObjectNode();
        responseNode.put("code", HttpStatus.OK.toString());
        responseNode.put("message", "Eliminazione Veicolo " + veicoliId + " Eseguita Con Successo");

        return new ResponseEntity<>(responseNode, headers, HttpStatus.OK);
    }

}
