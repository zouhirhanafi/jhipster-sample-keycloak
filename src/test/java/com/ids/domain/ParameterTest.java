package com.ids.domain;

import static com.ids.domain.ParameterTestSamples.*;
import static com.ids.domain.ParameterTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ids.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ParameterTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Parameter.class);
        Parameter parameter1 = getParameterSample1();
        Parameter parameter2 = new Parameter();
        assertThat(parameter1).isNotEqualTo(parameter2);

        parameter2.setId(parameter1.getId());
        assertThat(parameter1).isEqualTo(parameter2);

        parameter2 = getParameterSample2();
        assertThat(parameter1).isNotEqualTo(parameter2);
    }

    @Test
    void typeTest() {
        Parameter parameter = getParameterRandomSampleGenerator();
        Parameter parameterBack = getParameterRandomSampleGenerator();

        parameter.setType(parameterBack);
        assertThat(parameter.getType()).isEqualTo(parameterBack);

        parameter.type(null);
        assertThat(parameter.getType()).isNull();
    }

    @Test
    void paraentTest() {
        Parameter parameter = getParameterRandomSampleGenerator();
        Parameter parameterBack = getParameterRandomSampleGenerator();

        parameter.setParaent(parameterBack);
        assertThat(parameter.getParaent()).isEqualTo(parameterBack);

        parameter.paraent(null);
        assertThat(parameter.getParaent()).isNull();
    }
}
