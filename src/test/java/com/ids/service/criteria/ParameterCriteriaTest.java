package com.ids.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ParameterCriteriaTest {

    @Test
    void newParameterCriteriaHasAllFiltersNullTest() {
        var parameterCriteria = new ParameterCriteria();
        assertThat(parameterCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void parameterCriteriaFluentMethodsCreatesFiltersTest() {
        var parameterCriteria = new ParameterCriteria();

        setAllFilters(parameterCriteria);

        assertThat(parameterCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void parameterCriteriaCopyCreatesNullFilterTest() {
        var parameterCriteria = new ParameterCriteria();
        var copy = parameterCriteria.copy();

        assertThat(parameterCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(parameterCriteria)
        );
    }

    @Test
    void parameterCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var parameterCriteria = new ParameterCriteria();
        setAllFilters(parameterCriteria);

        var copy = parameterCriteria.copy();

        assertThat(parameterCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(parameterCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var parameterCriteria = new ParameterCriteria();

        assertThat(parameterCriteria).hasToString("ParameterCriteria{}");
    }

    private static void setAllFilters(ParameterCriteria parameterCriteria) {
        parameterCriteria.id();
        parameterCriteria.label();
        parameterCriteria.activated();
        parameterCriteria.lib2();
        parameterCriteria.lib3();
        parameterCriteria.refExterne();
        parameterCriteria.val1();
        parameterCriteria.val2();
        parameterCriteria.val3();
        parameterCriteria.ordre();
        parameterCriteria.typeId();
        parameterCriteria.paraentId();
        parameterCriteria.distinct();
    }

    private static Condition<ParameterCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getLabel()) &&
                condition.apply(criteria.getActivated()) &&
                condition.apply(criteria.getLib2()) &&
                condition.apply(criteria.getLib3()) &&
                condition.apply(criteria.getRefExterne()) &&
                condition.apply(criteria.getVal1()) &&
                condition.apply(criteria.getVal2()) &&
                condition.apply(criteria.getVal3()) &&
                condition.apply(criteria.getOrdre()) &&
                condition.apply(criteria.getTypeId()) &&
                condition.apply(criteria.getParaentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ParameterCriteria> copyFiltersAre(ParameterCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getLabel(), copy.getLabel()) &&
                condition.apply(criteria.getActivated(), copy.getActivated()) &&
                condition.apply(criteria.getLib2(), copy.getLib2()) &&
                condition.apply(criteria.getLib3(), copy.getLib3()) &&
                condition.apply(criteria.getRefExterne(), copy.getRefExterne()) &&
                condition.apply(criteria.getVal1(), copy.getVal1()) &&
                condition.apply(criteria.getVal2(), copy.getVal2()) &&
                condition.apply(criteria.getVal3(), copy.getVal3()) &&
                condition.apply(criteria.getOrdre(), copy.getOrdre()) &&
                condition.apply(criteria.getTypeId(), copy.getTypeId()) &&
                condition.apply(criteria.getParaentId(), copy.getParaentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
