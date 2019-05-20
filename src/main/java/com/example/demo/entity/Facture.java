package com.example.demo.entity;


import javax.persistence.*;
import java.util.Set;

@Entity
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //La facture fait ref à un seul client  : ManyToOne
    @ManyToOne
    private Client client;


    //
    @OneToMany(mappedBy = "facture")
    private Set<LigneFacture> ligneFactures;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }


}
