package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.RoleEmploye;

/** L'agence est désignée par son identifiant (agenceId), jamais par l'objet complet. */
public record EmployeDto(
        Long idEmploye,
        @NotBlank String nom,
        @NotBlank String prenom,
        @NotNull RoleEmploye role,
        Long agenceId) {

    public static EmployeDto from(Employe e) {
        Long agenceId = e.getAgence() == null ? null : e.getAgence().getIdAgence();
        return new EmployeDto(e.getIdEmploye(), e.getNom(), e.getPrenom(), e.getRole(), agenceId);
    }

    public Employe toEntity() {
        Employe e = new Employe();
        e.setNom(nom);
        e.setPrenom(prenom);
        e.setRole(role);
        if (agenceId != null) {
            Agence a = new Agence();
            a.setIdAgence(agenceId);
            e.setAgence(a);
        }
        return e;
    }
}
