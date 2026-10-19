package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.service.IContratService;
import tn.esprit.autoloc.web.dto.ContratDto;
import tn.esprit.autoloc.web.dto.PaiementDto;

/**
 * CRUD du contrat. Un paiement est une composition du contrat : il se crée,
 * se modifie et se supprime à travers /api/contrats/{id}/paiements.
 */
@Tag(name = "Contrats", description = "Gestion des contrats de location et de leurs paiements")
@RestController
@RequestMapping("/contrats")
@RequiredArgsConstructor
public class ContratController {

    private final IContratService contratService;

    @Operation(summary = "Créer un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Création réussie"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<ContratDto> create(@Valid @RequestBody ContratDto dto) {
        Contrat cree = contratService.create(dto.toEntity());
        URI location = URI.create("/api/contrats/" + cree.getIdContrat());
        return ResponseEntity.created(location).body(ContratDto.from(cree));
    }

    @Operation(summary = "Lister les contrats")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste retournée")
    })
    @GetMapping
    public List<ContratDto> findAll() {
        return contratService.findAll().stream().map(ContratDto::from).toList();
    }

    @Operation(summary = "Consulter un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ressource trouvée"),
            @ApiResponse(responseCode = "404", description = "Ressource introuvable")
    })
    @GetMapping("/{id}")
    public ContratDto findById(@Parameter(description = "Identifiant") @PathVariable("id") Long id) {
        return ContratDto.from(contratService.findById(id));
    }

    @Operation(summary = "Modifier un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Modification réussie"),
            @ApiResponse(responseCode = "404", description = "Ressource introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ContratDto update(@Parameter(description = "Identifiant") @PathVariable("id") Long id,
                             @Valid @RequestBody ContratDto dto) {
        return ContratDto.from(contratService.update(id, dto.toEntity()));
    }

    @Operation(summary = "Supprimer un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Suppression réussie"),
            @ApiResponse(responseCode = "404", description = "Ressource introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "Identifiant") @PathVariable("id") Long id) {
        contratService.deleteById(id);
    }

    // ----- Paiements du contrat -----

    @Operation(summary = "Ajouter un paiement à un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paiement créé"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping("/{id}/paiements")
    public ResponseEntity<PaiementDto> addPaiement(@Parameter(description = "Identifiant") @PathVariable("id") Long id,
                                                   @Valid @RequestBody PaiementDto dto) {
        Paiement cree = contratService.addPaiement(id, dto.toEntity());
        URI location = URI.create("/api/paiements/" + cree.getIdPaiement());
        return ResponseEntity.created(location).body(PaiementDto.from(cree));
    }

    @Operation(summary = "Modifier un paiement d'un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Modification réussie"),
            @ApiResponse(responseCode = "404", description = "Contrat ou paiement introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}/paiements/{idPaiement}")
    public PaiementDto updatePaiement(@Parameter(description = "Identifiant") @PathVariable("id") Long id,
                                      @Parameter(description = "Identifiant du paiement") @PathVariable("idPaiement") Long idPaiement,
                                      @Valid @RequestBody PaiementDto dto) {
        return PaiementDto.from(contratService.updatePaiement(id, idPaiement, dto.toEntity()));
    }

    @Operation(summary = "Retirer un paiement d'un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Suppression réussie"),
            @ApiResponse(responseCode = "404", description = "Contrat ou paiement introuvable")
    })
    @DeleteMapping("/{id}/paiements/{idPaiement}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePaiement(@Parameter(description = "Identifiant") @PathVariable("id") Long id,
                               @Parameter(description = "Identifiant du paiement") @PathVariable("idPaiement") Long idPaiement) {
        contratService.removePaiement(id, idPaiement);
    }
}