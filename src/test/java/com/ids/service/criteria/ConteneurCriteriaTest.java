package com.ids.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ConteneurCriteriaTest {

    @Test
    void newConteneurCriteriaHasAllFiltersNullTest() {
        var conteneurCriteria = new ConteneurCriteria();
        assertThat(conteneurCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void conteneurCriteriaFluentMethodsCreatesFiltersTest() {
        var conteneurCriteria = new ConteneurCriteria();

        setAllFilters(conteneurCriteria);

        assertThat(conteneurCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void conteneurCriteriaCopyCreatesNullFilterTest() {
        var conteneurCriteria = new ConteneurCriteria();
        var copy = conteneurCriteria.copy();

        assertThat(conteneurCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(conteneurCriteria)
        );
    }

    @Test
    void conteneurCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var conteneurCriteria = new ConteneurCriteria();
        setAllFilters(conteneurCriteria);

        var copy = conteneurCriteria.copy();

        assertThat(conteneurCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(conteneurCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var conteneurCriteria = new ConteneurCriteria();

        assertThat(conteneurCriteria).hasToString("ConteneurCriteria{}");
    }

    private static void setAllFilters(ConteneurCriteria conteneurCriteria) {
        conteneurCriteria.id();
        conteneurCriteria.statut();
        conteneurCriteria.dateEntree();
        conteneurCriteria.dateSortie();
        conteneurCriteria.zone();
        conteneurCriteria.ligne();
        conteneurCriteria.colonne();
        conteneurCriteria.commentaire();
        conteneurCriteria.distinct();
    }

    private static Condition<ConteneurCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getDateEntree()) &&
                condition.apply(criteria.getDateSortie()) &&
                condition.apply(criteria.getZone()) &&
                condition.apply(criteria.getLigne()) &&
                condition.apply(criteria.getColonne()) &&
                condition.apply(criteria.getCommentaire()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ConteneurCriteria> copyFiltersAre(ConteneurCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getDateEntree(), copy.getDateEntree()) &&
                condition.apply(criteria.getDateSortie(), copy.getDateSortie()) &&
                condition.apply(criteria.getZone(), copy.getZone()) &&
                condition.apply(criteria.getLigne(), copy.getLigne()) &&
                condition.apply(criteria.getColonne(), copy.getColonne()) &&
                condition.apply(criteria.getCommentaire(), copy.getCommentaire()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
