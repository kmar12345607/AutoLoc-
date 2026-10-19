package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.Vehicule;

/** Le client et le véhicule sont désignés par leurs identifiants (clientId, vehiculeId). */
public record ReservationDto(
        Long idReservation,
        @NotNull LocalDate dateDebut,
        @NotNull LocalDate dateFin,
        @NotNull StatutReservation statut,
        @NotNull Long clientId,
        @NotNull Long vehiculeId) {

    public static ReservationDto from(Reservation r) {
        Long clientId = r.getClient() == null ? null : r.getClient().getIdClient();
        Long vehiculeId = r.getVehicule() == null ? null : r.getVehicule().getIdVehicule();
        return new ReservationDto(r.getIdReservation(), r.getDateDebut(), r.getDateFin(),
                r.getStatut(), clientId, vehiculeId);
    }

    public Reservation toEntity() {
        Reservation r = new Reservation();
        r.setDateDebut(dateDebut);
        r.setDateFin(dateFin);
        r.setStatut(statut);
        Client c = new Client();
        c.setIdClient(clientId);
        r.setClient(c);
        Vehicule v = new Vehicule();
        v.setIdVehicule(vehiculeId);
        r.setVehicule(v);
        return r;
    }
}
