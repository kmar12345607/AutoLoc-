package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import tn.esprit.autoloc.domain.ModePaiement;
import tn.esprit.autoloc.domain.Paiement;

/** Un paiement appartient à un contrat : contratId est renseigné par l'API (lecture seule). */
public record PaiementDto(
        Long idPaiement,
        @NotNull @Positive BigDecimal montant,
        @NotNull LocalDate datePaiement,
        @NotNull ModePaiement modePaiement,
        Long contratId) {

    public static PaiementDto from(Paiement p) {
        Long contratId = p.getContrat() == null ? null : p.getContrat().getIdContrat();
        return new PaiementDto(p.getIdPaiement(), p.getMontant(), p.getDatePaiement(),
                p.getModePaiement(), contratId);
    }

    public Paiement toEntity() {
        Paiement p = new Paiement();
        p.setMontant(montant);
        p.setDatePaiement(datePaiement);
        p.setModePaiement(modePaiement);
        return p;
    }
}
