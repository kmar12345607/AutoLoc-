package tn.esprit.autoloc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEmployeRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeServiceImpl implements IEmployeService {

    private final IEmployeRepository employeRepository;
    private final IAgenceRepository agenceRepository;

    @Override
    public Employe create(Employe employe) {
        if (employe.getIdEmploye() != null) {
            throw new IllegalArgumentException("Un nouvel employé ne doit pas avoir d'identifiant");
        }
        verifier(employe);
        employe.setAgence(resoudreAgence(employe.getAgence()));
        return employeRepository.save(employe);
    }

    @Override
    public Employe findById(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", id));
    }

    @Override
    public List<Employe> findAll() {
        return employeRepository.findAll();
    }

    @Override
    public Employe update(Long id, Employe employe) {
        Employe existant = findById(id);
        verifier(employe);
        existant.setNom(employe.getNom());
        existant.setPrenom(employe.getPrenom());
        existant.setRole(employe.getRole());
        existant.setAgence(resoudreAgence(employe.getAgence()));
        return employeRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!employeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employe", id);
        }
        employeRepository.deleteById(id);
    }

    private void verifier(Employe employe) {
        if (!StringUtils.hasText(employe.getNom())
                || !StringUtils.hasText(employe.getPrenom())
                || employe.getRole() == null) {
            throw new IllegalArgumentException("Le nom, le prénom et le rôle de l'employé sont obligatoires");
        }
    }

    /** Remplace la référence (seul l'identifiant est renseigné) par l'agence réelle, ou lève une 404. */
    private Agence resoudreAgence(Agence reference) {
        if (reference == null || reference.getIdAgence() == null) {
            return null;
        }
        return agenceRepository.findById(reference.getIdAgence())
                .orElseThrow(() -> new ResourceNotFoundException("Agence", reference.getIdAgence()));
    }
}