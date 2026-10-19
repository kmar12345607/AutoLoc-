package tn.esprit.autoloc.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;

/**
 * La réservation est désignée par reservationId.
 * "paiements" peut être fourni à la création (enregistrés en cascade) ;
 * ensuite, ils se gèrent avec /api/contrats/{id}/paiements.
 */
public record ContratDto(
        Long idContrat,
        @NotNull LocalDate dateSignature,
        @NotNull @PositiveOrZero BigDecimal montantTotal,
        boolean valide,
        Long reservationId,
        @Valid List<PaiementDto> paiements) {

    public static ContratDto from(Contrat c) {
        Long reservationId = c.getReservation() == null ? null : c.getReservation().getIdReservation();
        List<PaiementDto> paiements = c.getPaiements().stream().map(PaiementDto::from).toList();
        return new ContratDto(c.getIdContrat(), c.getDateSignature(), c.getMontantTotal(),
                c.isValide(), reservationId, paiements);
    }

    public Contrat toEntity() {
        Contrat c = new Contrat();
        c.setDateSignature(dateSignature);
        c.setMontantTotal(montantTotal);
        c.setValide(valide);
        if (reservationId != null) {
            Reservation r = new Reservation();
            r.setIdReservation(reservationId);
            c.setReservation(r);
        }
        if (paiements != null) {
            paiements.forEach(p -> c.getPaiements().add(p.toEntity()));
        }
        return c;
    }
}
