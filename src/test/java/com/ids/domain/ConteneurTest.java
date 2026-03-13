package com.ids.domain;

import static com.ids.domain.ConteneurTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ids.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ConteneurTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Conteneur.class);
        Conteneur conteneur1 = getConteneurSample1();
        Conteneur conteneur2 = new Conteneur();
        assertThat(conteneur1).isNotEqualTo(conteneur2);

        conteneur2.setId(conteneur1.getId());
        assertThat(conteneur1).isEqualTo(conteneur2);

        conteneur2 = getConteneurSample2();
        assertThat(conteneur1).isNotEqualTo(conteneur2);
    }
}
