package tn.esprit.autoloc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IPaiementRepository;
import tn.esprit.autoloc.repository.IReservationRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;
    private final IPaiementRepository paiementRepository;
    private final IReservationRepository reservationRepository;

    @Override
    public Contrat create(Contrat contrat) {
        if (contrat.getIdContrat() != null) {
            throw new IllegalArgumentException("Un nouveau contrat ne doit pas avoir d'identifiant");
        }
        verifier(contrat);
        contrat.setReservation(resoudreReservation(contrat.getReservation()));
        // Paiements fournis à la création : enregistrés en cascade avec le contrat.
        for (Paiement paiement : contrat.getPaiements()) {
            verifierPaiement(paiement);
            paiement.setIdPaiement(null);
            paiement.setContrat(contrat);
        }
        return contratRepository.save(contrat);
    }

    @Override
    public Contrat findById(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
    }

    @Override
    public List<Contrat> findAll() {
        return contratRepository.findAll();
    }

    @Override
    public Contrat update(Long id, Contrat contrat) {
        Contrat existant = findById(id);
        verifier(contrat);
        existant.setDateSignature(contrat.getDateSignature());
        existant.setMontantTotal(contrat.getMontantTotal());
        existant.setValide(contrat.isValide());
        existant.setReservation(resoudreReservation(contrat.getReservation()));
        // Les paiements ne sont pas touchés ici (orphanRemoval) : voir add/update/removePaiement.
        return contratRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!contratRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contrat", id);
        }
        contratRepository.deleteById(id);
    }

    @Override
    public Paiement addPaiement(Long idContrat, Paiement paiement) {
        Contrat contrat = findById(idContrat);
        verifierPaiement(paiement);
        paiement.setIdPaiement(null);
        paiement.setContrat(contrat);
        Paiement enregistre = paiementRepository.save(paiement);
        contrat.getPaiements().add(enregistre);
        return enregistre;
    }

    @Override
    public Paiement updatePaiement(Long idContrat, Long idPaiement, Paiement paiement) {
        Contrat contrat = findById(idContrat);
        Paiement existant = trouverPaiement(contrat, idPaiement);
        verifierPaiement(paiement);
        existant.setMontant(paiement.getMontant());
        existant.setDatePaiement(paiement.getDatePaiement());
        existant.setModePaiement(paiement.getModePaiement());
        return paiementRepository.save(existant);
    }

    @Override
    public void removePaiement(Long idContrat, Long idPaiement) {
        Contrat contrat = findById(idContrat);
        Paiement existant = trouverPaiement(contrat, idPaiement);
        // orphanRemoval : retirer le paiement de la liste le supprime en base.
        contrat.getPaiements().remove(existant);
    }

    private Paiement trouverPaiement(Contrat contrat, Long idPaiement) {
        return contrat.getPaiements().stream()
                .filter(p -> idPaiement.equals(p.getIdPaiement()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Paiement", idPaiement));
    }

    private void verifier(Contrat contrat) {
        if (contrat.getMontantTotal() == null || contrat.getMontantTotal().signum() < 0) {
            throw new IllegalArgumentException("Le montant total est obligatoire et ne peut pas être négatif");
        }
    }

    private void verifierPaiement(Paiement paiement) {
        if (paiement.getMontant() == null || paiement.getMontant().signum() <= 0) {
            throw new IllegalArgumentException("Le montant du paiement est obligatoire et doit être positif");
        }
        if (paiement.getDatePaiement() == null || paiement.getModePaiement() == null) {
            throw new IllegalArgumentException("La date et le mode de paiement sont obligatoires");
        }
    }

    private Reservation resoudreReservation(Reservation reference) {
        if (reference == null || reference.getIdReservation() == null) {
            return null;
        }
        return reservationRepository.findById(reference.getIdReservation())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", reference.getIdReservation()));
    }
}