package com.ids.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ids.domain.Conteneur} entity. This class is used
 * in {@link com.ids.web.rest.ConteneurResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /conteneurs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ConteneurCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter statut;

    private InstantFilter dateEntree;

    private ZonedDateTimeFilter dateSortie;

    private IntegerFilter zone;

    private IntegerFilter ligne;

    private IntegerFilter colonne;

    private StringFilter commentaire;

    private Boolean distinct;

    public ConteneurCriteria() {}

    public ConteneurCriteria(ConteneurCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(IntegerFilter::copy).orElse(null);
        this.dateEntree = other.optionalDateEntree().map(InstantFilter::copy).orElse(null);
        this.dateSortie = other.optionalDateSortie().map(ZonedDateTimeFilter::copy).orElse(null);
        this.zone = other.optionalZone().map(IntegerFilter::copy).orElse(null);
        this.ligne = other.optionalLigne().map(IntegerFilter::copy).orElse(null);
        this.colonne = other.optionalColonne().map(IntegerFilter::copy).orElse(null);
        this.commentaire = other.optionalCommentaire().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ConteneurCriteria copy() {
        return new ConteneurCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getStatut() {
        return statut;
    }

    public Optional<IntegerFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public IntegerFilter statut() {
        if (statut == null) {
            setStatut(new IntegerFilter());
        }
        return statut;
    }

    public void setStatut(IntegerFilter statut) {
        this.statut = statut;
    }

    public InstantFilter getDateEntree() {
        return dateEntree;
    }

    public Optional<InstantFilter> optionalDateEntree() {
        return Optional.ofNullable(dateEntree);
    }

    public InstantFilter dateEntree() {
        if (dateEntree == null) {
            setDateEntree(new InstantFilter());
        }
        return dateEntree;
    }

    public void setDateEntree(InstantFilter dateEntree) {
        this.dateEntree = dateEntree;
    }

    public ZonedDateTimeFilter getDateSortie() {
        return dateSortie;
    }

    public Optional<ZonedDateTimeFilter> optionalDateSortie() {
        return Optional.ofNullable(dateSortie);
    }

    public ZonedDateTimeFilter dateSortie() {
        if (dateSortie == null) {
            setDateSortie(new ZonedDateTimeFilter());
        }
        return dateSortie;
    }

    public void setDateSortie(ZonedDateTimeFilter dateSortie) {
        this.dateSortie = dateSortie;
    }

    public IntegerFilter getZone() {
        return zone;
    }

    public Optional<IntegerFilter> optionalZone() {
        return Optional.ofNullable(zone);
    }

    public IntegerFilter zone() {
        if (zone == null) {
            setZone(new IntegerFilter());
        }
        return zone;
    }

    public void setZone(IntegerFilter zone) {
        this.zone = zone;
    }

    public IntegerFilter getLigne() {
        return ligne;
    }

    public Optional<IntegerFilter> optionalLigne() {
        return Optional.ofNullable(ligne);
    }

    public IntegerFilter ligne() {
        if (ligne == null) {
            setLigne(new IntegerFilter());
        }
        return ligne;
    }

    public void setLigne(IntegerFilter ligne) {
        this.ligne = ligne;
    }

    public IntegerFilter getColonne() {
        return colonne;
    }

    public Optional<IntegerFilter> optionalColonne() {
        return Optional.ofNullable(colonne);
    }

    public IntegerFilter colonne() {
        if (colonne == null) {
            setColonne(new IntegerFilter());
        }
        return colonne;
    }

    public void setColonne(IntegerFilter colonne) {
        this.colonne = colonne;
    }

    public StringFilter getCommentaire() {
        return commentaire;
    }

    public Optional<StringFilter> optionalCommentaire() {
        return Optional.ofNullable(commentaire);
    }

    public StringFilter commentaire() {
        if (commentaire == null) {
            setCommentaire(new StringFilter());
        }
        return commentaire;
    }

    public void setCommentaire(StringFilter commentaire) {
        this.commentaire = commentaire;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ConteneurCriteria that = (ConteneurCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(dateEntree, that.dateEntree) &&
            Objects.equals(dateSortie, that.dateSortie) &&
            Objects.equals(zone, that.zone) &&
            Objects.equals(ligne, that.ligne) &&
            Objects.equals(colonne, that.colonne) &&
            Objects.equals(commentaire, that.commentaire) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, statut, dateEntree, dateSortie, zone, ligne, colonne, commentaire, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ConteneurCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalDateEntree().map(f -> "dateEntree=" + f + ", ").orElse("") +
            optionalDateSortie().map(f -> "dateSortie=" + f + ", ").orElse("") +
            optionalZone().map(f -> "zone=" + f + ", ").orElse("") +
            optionalLigne().map(f -> "ligne=" + f + ", ").orElse("") +
            optionalColonne().map(f -> "colonne=" + f + ", ").orElse("") +
            optionalCommentaire().map(f -> "commentaire=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
