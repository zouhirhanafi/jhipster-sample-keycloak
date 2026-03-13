package com.ids.service;

import com.ids.domain.Conteneur;
import com.ids.repository.ConteneurRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.ids.domain.Conteneur}.
 */
@Service
@Transactional
public class ConteneurService {

    private static final Logger LOG = LoggerFactory.getLogger(ConteneurService.class);

    private final ConteneurRepository conteneurRepository;

    public ConteneurService(ConteneurRepository conteneurRepository) {
        this.conteneurRepository = conteneurRepository;
    }

    /**
     * Save a conteneur.
     *
     * @param conteneur the entity to save.
     * @return the persisted entity.
     */
    public Conteneur save(Conteneur conteneur) {
        LOG.debug("Request to save Conteneur : {}", conteneur);
        return conteneurRepository.save(conteneur);
    }

    /**
     * Update a conteneur.
     *
     * @param conteneur the entity to save.
     * @return the persisted entity.
     */
    public Conteneur update(Conteneur conteneur) {
        LOG.debug("Request to update Conteneur : {}", conteneur);
        return conteneurRepository.save(conteneur);
    }

    /**
     * Partially update a conteneur.
     *
     * @param conteneur the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Conteneur> partialUpdate(Conteneur conteneur) {
        LOG.debug("Request to partially update Conteneur : {}", conteneur);

        return conteneurRepository
            .findById(conteneur.getId())
            .map(existingConteneur -> {
                if (conteneur.getStatut() != null) {
                    existingConteneur.setStatut(conteneur.getStatut());
                }
                if (conteneur.getDateEntree() != null) {
                    existingConteneur.setDateEntree(conteneur.getDateEntree());
                }
                if (conteneur.getDateSortie() != null) {
                    existingConteneur.setDateSortie(conteneur.getDateSortie());
                }
                if (conteneur.getZone() != null) {
                    existingConteneur.setZone(conteneur.getZone());
                }
                if (conteneur.getLigne() != null) {
                    existingConteneur.setLigne(conteneur.getLigne());
                }
                if (conteneur.getColonne() != null) {
                    existingConteneur.setColonne(conteneur.getColonne());
                }
                if (conteneur.getCommentaire() != null) {
                    existingConteneur.setCommentaire(conteneur.getCommentaire());
                }

                return existingConteneur;
            })
            .map(conteneurRepository::save);
    }

    /**
     * Get one conteneur by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Conteneur> findOne(Long id) {
        LOG.debug("Request to get Conteneur : {}", id);
        return conteneurRepository.findById(id);
    }

    /**
     * Delete the conteneur by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Conteneur : {}", id);
        conteneurRepository.deleteById(id);
    }
}
