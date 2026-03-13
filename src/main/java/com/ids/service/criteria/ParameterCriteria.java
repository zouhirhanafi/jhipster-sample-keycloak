package com.ids.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ids.domain.Parameter} entity. This class is used
 * in {@link com.ids.web.rest.ParameterResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /parameters?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParameterCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter label;

    private BooleanFilter activated;

    private StringFilter lib2;

    private StringFilter lib3;

    private StringFilter refExterne;

    private StringFilter val1;

    private StringFilter val2;

    private StringFilter val3;

    private IntegerFilter ordre;

    private LongFilter typeId;

    private LongFilter paraentId;

    private Boolean distinct;

    public ParameterCriteria() {}

    public ParameterCriteria(ParameterCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.label = other.optionalLabel().map(StringFilter::copy).orElse(null);
        this.activated = other.optionalActivated().map(BooleanFilter::copy).orElse(null);
        this.lib2 = other.optionalLib2().map(StringFilter::copy).orElse(null);
        this.lib3 = other.optionalLib3().map(StringFilter::copy).orElse(null);
        this.refExterne = other.optionalRefExterne().map(StringFilter::copy).orElse(null);
        this.val1 = other.optionalVal1().map(StringFilter::copy).orElse(null);
        this.val2 = other.optionalVal2().map(StringFilter::copy).orElse(null);
        this.val3 = other.optionalVal3().map(StringFilter::copy).orElse(null);
        this.ordre = other.optionalOrdre().map(IntegerFilter::copy).orElse(null);
        this.typeId = other.optionalTypeId().map(LongFilter::copy).orElse(null);
        this.paraentId = other.optionalParaentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ParameterCriteria copy() {
        return new ParameterCriteria(this);
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

    public StringFilter getLabel() {
        return label;
    }

    public Optional<StringFilter> optionalLabel() {
        return Optional.ofNullable(label);
    }

    public StringFilter label() {
        if (label == null) {
            setLabel(new StringFilter());
        }
        return label;
    }

    public void setLabel(StringFilter label) {
        this.label = label;
    }

    public BooleanFilter getActivated() {
        return activated;
    }

    public Optional<BooleanFilter> optionalActivated() {
        return Optional.ofNullable(activated);
    }

    public BooleanFilter activated() {
        if (activated == null) {
            setActivated(new BooleanFilter());
        }
        return activated;
    }

    public void setActivated(BooleanFilter activated) {
        this.activated = activated;
    }

    public StringFilter getLib2() {
        return lib2;
    }

    public Optional<StringFilter> optionalLib2() {
        return Optional.ofNullable(lib2);
    }

    public StringFilter lib2() {
        if (lib2 == null) {
            setLib2(new StringFilter());
        }
        return lib2;
    }

    public void setLib2(StringFilter lib2) {
        this.lib2 = lib2;
    }

    public StringFilter getLib3() {
        return lib3;
    }

    public Optional<StringFilter> optionalLib3() {
        return Optional.ofNullable(lib3);
    }

    public StringFilter lib3() {
        if (lib3 == null) {
            setLib3(new StringFilter());
        }
        return lib3;
    }

    public void setLib3(StringFilter lib3) {
        this.lib3 = lib3;
    }

    public StringFilter getRefExterne() {
        return refExterne;
    }

    public Optional<StringFilter> optionalRefExterne() {
        return Optional.ofNullable(refExterne);
    }

    public StringFilter refExterne() {
        if (refExterne == null) {
            setRefExterne(new StringFilter());
        }
        return refExterne;
    }

    public void setRefExterne(StringFilter refExterne) {
        this.refExterne = refExterne;
    }

    public StringFilter getVal1() {
        return val1;
    }

    public Optional<StringFilter> optionalVal1() {
        return Optional.ofNullable(val1);
    }

    public StringFilter val1() {
        if (val1 == null) {
            setVal1(new StringFilter());
        }
        return val1;
    }

    public void setVal1(StringFilter val1) {
        this.val1 = val1;
    }

    public StringFilter getVal2() {
        return val2;
    }

    public Optional<StringFilter> optionalVal2() {
        return Optional.ofNullable(val2);
    }

    public StringFilter val2() {
        if (val2 == null) {
            setVal2(new StringFilter());
        }
        return val2;
    }

    public void setVal2(StringFilter val2) {
        this.val2 = val2;
    }

    public StringFilter getVal3() {
        return val3;
    }

    public Optional<StringFilter> optionalVal3() {
        return Optional.ofNullable(val3);
    }

    public StringFilter val3() {
        if (val3 == null) {
            setVal3(new StringFilter());
        }
        return val3;
    }

    public void setVal3(StringFilter val3) {
        this.val3 = val3;
    }

    public IntegerFilter getOrdre() {
        return ordre;
    }

    public Optional<IntegerFilter> optionalOrdre() {
        return Optional.ofNullable(ordre);
    }

    public IntegerFilter ordre() {
        if (ordre == null) {
            setOrdre(new IntegerFilter());
        }
        return ordre;
    }

    public void setOrdre(IntegerFilter ordre) {
        this.ordre = ordre;
    }

    public LongFilter getTypeId() {
        return typeId;
    }

    public Optional<LongFilter> optionalTypeId() {
        return Optional.ofNullable(typeId);
    }

    public LongFilter typeId() {
        if (typeId == null) {
            setTypeId(new LongFilter());
        }
        return typeId;
    }

    public void setTypeId(LongFilter typeId) {
        this.typeId = typeId;
    }

    public LongFilter getParaentId() {
        return paraentId;
    }

    public Optional<LongFilter> optionalParaentId() {
        return Optional.ofNullable(paraentId);
    }

    public LongFilter paraentId() {
        if (paraentId == null) {
            setParaentId(new LongFilter());
        }
        return paraentId;
    }

    public void setParaentId(LongFilter paraentId) {
        this.paraentId = paraentId;
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
        final ParameterCriteria that = (ParameterCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(label, that.label) &&
            Objects.equals(activated, that.activated) &&
            Objects.equals(lib2, that.lib2) &&
            Objects.equals(lib3, that.lib3) &&
            Objects.equals(refExterne, that.refExterne) &&
            Objects.equals(val1, that.val1) &&
            Objects.equals(val2, that.val2) &&
            Objects.equals(val3, that.val3) &&
            Objects.equals(ordre, that.ordre) &&
            Objects.equals(typeId, that.typeId) &&
            Objects.equals(paraentId, that.paraentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label, activated, lib2, lib3, refExterne, val1, val2, val3, ordre, typeId, paraentId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParameterCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalLabel().map(f -> "label=" + f + ", ").orElse("") +
            optionalActivated().map(f -> "activated=" + f + ", ").orElse("") +
            optionalLib2().map(f -> "lib2=" + f + ", ").orElse("") +
            optionalLib3().map(f -> "lib3=" + f + ", ").orElse("") +
            optionalRefExterne().map(f -> "refExterne=" + f + ", ").orElse("") +
            optionalVal1().map(f -> "val1=" + f + ", ").orElse("") +
            optionalVal2().map(f -> "val2=" + f + ", ").orElse("") +
            optionalVal3().map(f -> "val3=" + f + ", ").orElse("") +
            optionalOrdre().map(f -> "ordre=" + f + ", ").orElse("") +
            optionalTypeId().map(f -> "typeId=" + f + ", ").orElse("") +
            optionalParaentId().map(f -> "paraentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
