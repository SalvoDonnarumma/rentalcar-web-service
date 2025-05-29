package com.xantrix.webapp.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "utenti")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idutente")
    private Integer idUtente;

    @Column(name = "nome")
    private String nome;

    @Column(name = "cognome")
    private String cognome;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_nascita")
    //@NotNull(message = "{NotNull.Articoli.dataCreaz.Validation}")
    private Date dataNascita;

    @Column(name = "ruolo")
    private String ruolo;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "utente", orphanRemoval = true)
    @JsonManagedReference
    private Set<Prenotazione> prenotazioni = new HashSet<>();
}
