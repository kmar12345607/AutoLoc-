package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import tn.esprit.autoloc.domain.Client;

/** dateInscription est facultative : la date du jour est utilisée si elle est absente. */
public record ClientDto(
        Long idClient,
        @NotBlank String nom,
        @NotBlank String prenom,
        @NotBlank @Email String email,
        String telephone,
        @NotBlank String numPermis,
        LocalDate dateInscription) {

    public static ClientDto from(Client c) {
        return new ClientDto(c.getIdClient(), c.getNom(), c.getPrenom(), c.getEmail(),
                c.getTelephone(), c.getNumPermis(), c.getDateInscription());
    }

    public Client toEntity() {
        Client c = new Client();
        c.setNom(nom);
        c.setPrenom(prenom);
        c.setEmail(email);
        c.setTelephone(telephone);
        c.setNumPermis(numPermis);
        c.setDateInscription(dateInscription != null ? dateInscription : LocalDate.now());
        return c;
    }
}
