package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Vehicule;

public record MaintenanceDto(
        Long idMaintenance,
        @NotNull LocalDate dateDebut,
        LocalDate dateFin,
        @Size(max = 255) String description,
        @NotNull Long vehiculeId) {

    public static MaintenanceDto from(Maintenance m) {
        Long vehiculeId = m.getVehicule() == null ? null : m.getVehicule().getIdVehicule();
        return new MaintenanceDto(m.getIdMaintenance(), m.getDateDebut(), m.getDateFin(),
                m.getDescription(), vehiculeId);
    }

    public Maintenance toEntity() {
        Maintenance m = new Maintenance();
        m.setDateDebut(dateDebut);
        m.setDateFin(dateFin);
        m.setDescription(description);
        Vehicule v = new Vehicule();
        v.setIdVehicule(vehiculeId);
        m.setVehicule(v);
        return m;
    }
}
