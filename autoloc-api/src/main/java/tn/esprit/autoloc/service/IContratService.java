package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;

public interface IContratService {

    Contrat create(Contrat contrat);

    Contrat findById(Long id);

    List<Contrat> findAll();

    Contrat update(Long id, Contrat contrat);

    void deleteById(Long id);

    // Un paiement est une composition du contrat : il se gère à travers lui.

    Paiement addPaiement(Long idContrat, Paiement paiement);

    Paiement updatePaiement(Long idContrat, Long idPaiement, Paiement paiement);

    void removePaiement(Long idContrat, Long idPaiement);
}