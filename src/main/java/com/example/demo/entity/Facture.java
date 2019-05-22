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

    public Set<LigneFacture> getLigneFactures() {
        return ligneFactures;
    }

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

    public Double getTotal() {
        Double total = 0.0;
        for (LigneFacture ligneFacture : ligneFactures) {
            total = total + ligneFacture.getSousTotal();
        }
        return total;
    }


}
