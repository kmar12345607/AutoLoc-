package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;
import tn.esprit.autoloc.domain.Equipement;

public record EquipementDto(
        Long idEquipement,
        @NotBlank String libelle) {

    public static EquipementDto from(Equipement e) {
        return new EquipementDto(e.getIdEquipement(), e.getLibelle());
    }

    public Equipement toEntity() {
        Equipement e = new Equipement();
        e.setLibelle(libelle);
        return e;
    }
}
