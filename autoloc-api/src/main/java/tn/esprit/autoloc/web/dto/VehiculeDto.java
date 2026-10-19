package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;

/**
 * Objet d'échange de l'API pour un véhicule.
 * Les associations sont exprimées par des identifiants (agenceId, equipementIds) :
 * pas de boucle infinie JSON, pas de LazyInitializationException.
 */
public record VehiculeDto(
        Long idVehicule,
        @NotBlank String immatriculation,
        @NotBlank String marque,
        @NotBlank String modele,
        @NotNull CategorieVehicule categorie,
        @NotNull @PositiveOrZero BigDecimal tarifJournalier,
        @NotNull StatutVehicule statut,
        Long agenceId,
        Set<Long> equipementIds) {

    public static VehiculeDto from(Vehicule v) {
        Long agenceId = v.getAgence() == null ? null : v.getAgence().getIdAgence();
        Set<Long> equipementIds = v.getEquipements().stream()
                .map(Equipement::getIdEquipement)
                .collect(Collectors.toSet());
        return new VehiculeDto(v.getIdVehicule(), v.getImmatriculation(), v.getMarque(), v.getModele(),
                v.getCategorie(), v.getTarifJournalier(), v.getStatut(), agenceId, equipementIds);
    }

    public Vehicule toEntity() {
        Vehicule v = new Vehicule();
        v.setImmatriculation(immatriculation);
        v.setMarque(marque);
        v.setModele(modele);
        v.setCategorie(categorie);
        v.setTarifJournalier(tarifJournalier);
        v.setStatut(statut);
        if (agenceId != null) {
            Agence a = new Agence();
            a.setIdAgence(agenceId);
            v.setAgence(a);
        }
        Set<Equipement> equipements = new HashSet<>();
        if (equipementIds != null) {
            for (Long id : equipementIds) {
                Equipement e = new Equipement();
                e.setIdEquipement(id);
                equipements.add(e);
            }
        }
        v.setEquipements(equipements);
        return v;
    }
}
