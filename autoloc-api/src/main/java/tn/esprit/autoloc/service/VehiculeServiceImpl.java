package tn.esprit.autoloc.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;
    private final IAgenceRepository agenceRepository;
    private final IEquipementRepository equipementRepository;

    @Override
    public Vehicule create(Vehicule vehicule) {
        if (vehicule.getIdVehicule() != null) {
            throw new IllegalArgumentException("Un nouveau véhicule ne doit pas avoir d'identifiant");
        }
        verifier(vehicule);
        vehicule.setAgence(resoudreAgence(vehicule.getAgence()));
        vehicule.setEquipements(resoudreEquipements(vehicule.getEquipements()));
        return vehiculeRepository.save(vehicule);
    }

    @Override
    public Vehicule findById(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
    }

    @Override
    public List<Vehicule> findAll() {
        return vehiculeRepository.findAll();
    }

    @Override
    public Vehicule update(Long id, Vehicule vehicule) {
        Vehicule existant = findById(id);
        verifier(vehicule);
        existant.setImmatriculation(vehicule.getImmatriculation());
        existant.setMarque(vehicule.getMarque());
        existant.setModele(vehicule.getModele());
        existant.setCategorie(vehicule.getCategorie());
        existant.setTarifJournalier(vehicule.getTarifJournalier());
        existant.setStatut(vehicule.getStatut());
        existant.setAgence(resoudreAgence(vehicule.getAgence()));
        existant.getEquipements().clear();
        existant.getEquipements().addAll(resoudreEquipements(vehicule.getEquipements()));
        return vehiculeRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!vehiculeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicule", id);
        }
        vehiculeRepository.deleteById(id);
    }

    private void verifier(Vehicule vehicule) {
        if (vehicule.getTarifJournalier() == null || vehicule.getTarifJournalier().signum() < 0) {
            throw new IllegalArgumentException("Le tarif journalier est obligatoire et ne peut pas être négatif");
        }
    }

    /** Remplace la référence (seul l'identifiant est renseigné) par l'agence réelle, ou lève une 404. */
    private Agence resoudreAgence(Agence reference) {
        if (reference == null || reference.getIdAgence() == null) {
            return null;
        }
        return agenceRepository.findById(reference.getIdAgence())
                .orElseThrow(() -> new ResourceNotFoundException("Agence", reference.getIdAgence()));
    }

    private Set<Equipement> resoudreEquipements(Set<Equipement> references) {
        Set<Equipement> equipements = new HashSet<>();
        if (references != null) {
            for (Equipement reference : references) {
                equipements.add(equipementRepository.findById(reference.getIdEquipement())
                        .orElseThrow(() -> new ResourceNotFoundException("Equipement", reference.getIdEquipement())));
            }
        }
        return equipements;
    }
}