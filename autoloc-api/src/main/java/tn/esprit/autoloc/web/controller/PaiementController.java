package tn.esprit.autoloc.web.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.service.IPaiementService;
import tn.esprit.autoloc.web.dto.PaiementDto;

/**
 * Lecture seule : un paiement est une composition du contrat.
 * Création, modification et suppression : voir ContratController (/api/contrats/{id}/paiements).
 */
@RestController
@RequestMapping("/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final IPaiementService paiementService;

    @GetMapping
    public List<PaiementDto> findAll() {
        return paiementService.findAll().stream().map(PaiementDto::from).toList();
    }

    @GetMapping("/{id}")
    public PaiementDto findById(@PathVariable("id") Long id) {
        return PaiementDto.from(paiementService.findById(id));
    }
}
