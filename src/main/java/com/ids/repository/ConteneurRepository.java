package com.ids.repository;

import com.ids.domain.Conteneur;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Conteneur entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConteneurRepository extends JpaRepository<Conteneur, Long>, JpaSpecificationExecutor<Conteneur> {}