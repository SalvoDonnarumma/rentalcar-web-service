package com.xantrix.webapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "veicoli")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Veicolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idveicolo")
    private Integer idVeicolo;

    @Column(name = "targa")
    private String targa;

    @Column(name = "anno_immatricolazione")
    private String annoImmatricolazione;

    @Column(name = "modello")
    private String modello;

    @Column(name = "casa_produttrice")
    private String casaProduttrice;

    @Column(name = "tipologia")
    private String tipologia;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "veicolo", orphanRemoval = true)
    private Set<Prenotazione> prenotazioni = new HashSet<>();
}
