package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;
import tn.esprit.autoloc.domain.Agence;

public record AgenceDto(
        Long idAgence,
        @NotBlank String nom,
        @NotBlank String ville,
        @NotBlank String adresse,
        String telephone) {

    public static AgenceDto from(Agence a) {
        return new AgenceDto(a.getIdAgence(), a.getNom(), a.getVille(), a.getAdresse(), a.getTelephone());
    }

    public Agence toEntity() {
        Agence a = new Agence();
        a.setNom(nom);
        a.setVille(ville);
        a.setAdresse(adresse);
        a.setTelephone(telephone);
        return a;
    }
}
