package de.cosmocode.lucene;

import java.util.Collection;


/**
 * An abstract implemententation of the LuceneQueryBuilder,
 * that takes care of the redirects to methods with
 * default values and those with old style "boolean mandatory" signature. 
 * <br>
 * In future it will implement every method, except:<br>
 * <ul>
 *   <li>{@link #addArgument(String, QueryModifier)}</li>
 *   <li>{@link #addArgumentAsCollection(Collection, QueryModifier)}</li>
 *   <li>{@link #addArgumentAsArray(Object[], QueryModifier)}</li>
 *   <li>{@link #addArgumentAsArray(Object, QueryModifier)}</li>
 *   <li>{@link #addFuzzyArgument(String, QueryModifier, double)}</li>
 *   <li>{@link #startField(String, QueryModifier)}</li>
 *   <li>{@link #endField()}</li>
 *   <li>{@link #getQuery()}</li>
 * </ul>
 * 
 * 
 * @see LuceneQuery
 * 
 * @author olorenz
 *
 */
public abstract class AbstractLuceneQuery implements LuceneQuery {
    
    
    private QueryModifier defaultModifier;
    
    
    /**
     * Initializes this {@link AbstractLuceneQuery} with the QueryModifier {@link QueryModifier#NONE}.
     */
    public AbstractLuceneQuery() {
        this.defaultModifier = QueryModifier.NONE;
    }
    
    /**
     * Initializes this {@link AbstractLuceneQuery} with the given default QueryModifier.
     * @param defaultModifier the default QueryModifier for the default calls and {@link #getDefaultQueryModifier()}.
     */
    public AbstractLuceneQuery(final QueryModifier defaultModifier) {
        this.defaultModifier = defaultModifier;
    }
    

    
    //---------------------------
    //   default getter and setter
    //---------------------------
    
    
    @Override
    public boolean isWildCarded() {
        return defaultModifier.isWildcarded();
    }
    
    @Override
    public void setWildCarded(boolean wildCarded) {
        this.defaultModifier = new QueryModifier(defaultModifier.getTermModifier(), defaultModifier.isDisjunct(), wildCarded, defaultModifier.isSplit());
    }
    
    
    @Override
    public final QueryModifier getDefaultQueryModifier() {
        return defaultModifier;
    }
    
    @Override
    public final void setDefaultQueryModifier(QueryModifier mod) {
        if (mod == null) {
            throw new NullPointerException("the default QueryModifier must not be null");
        } else {
            this.defaultModifier = mod;
        }
    }
    
    
    public abstract String getQuery();
    
    
    
    //---------------------------
    //     addFuzzyArgument
    //---------------------------
    
    
    @Override
    public final LuceneQuery addFuzzyArgument(final String value) {
        return addFuzzyArgument(value, defaultModifier, defaultFuzzyness);
    }
    
    
    @Override
    public final LuceneQuery addFuzzyArgument(final String value, final boolean mandatory) {
        return addFuzzyArgument(value, mandatory, defaultFuzzyness);
    }
    
    
    @Override
    public LuceneQuery addFuzzyArgument(final String value, final boolean mandatory,
            final double fuzzyness) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return this.addFuzzyArgument(value, mod, fuzzyness);
    }
    
    
    @Override
    public final LuceneQuery addFuzzyArgument(final String value, final QueryModifier modifier) {
        return addFuzzyArgument(value, modifier, defaultFuzzyness);
    }
    
    
    public abstract LuceneQuery addFuzzyArgument(final String value, final QueryModifier modifier, final double fuzzyness);
    
    
    
    //---------------------------
    //     addArgument
    //---------------------------
    
    
    @Override
    public final LuceneQuery addArgument(final String value) {
        return addArgument(value, defaultModifier);
    }
    
    
    @Override
    public LuceneQuery addArgument(final String value, final boolean mandatory) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return this.addArgument(value, mod);
    }
    
    
    public abstract LuceneQuery addArgument(final String value, final QueryModifier modifiers);
    
    
    @Override
    public final LuceneQuery addArgument(Collection<?> values) {
        return addArgument(values, defaultModifier);
    }
    
    
    @Override
    public LuceneQuery addArgument(final Collection<?> value, final boolean mandatory) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return this.addArgument(value, mod);
    }
    
    
    @Override
    public final LuceneQuery addArgument(Collection<?> values, QueryModifier modifier) {
        return addArgumentAsCollection(values, modifier);
    }
    
    
    @Override
    public final <K> LuceneQuery addArgument(K[] values) {
        return addArgument(values, defaultModifier);
    }
    
    
    @Override
    public <K> LuceneQuery addArgument(K[] values, boolean mandatory) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return addArgument(values, mod);
    }
    
    
    @Override
    public final <K> LuceneQuery addArgument(K[] values, QueryModifier modifier) {
        return addArgumentAsArray(values, modifier);
    }
    
    
    /**
     * This is a utility method, that determines what to call based on "instanceof".
     * 
     * @param value the query argument to add; null is omitted
     * @param modifiers the modifiers for the addArgumentAs...-methods
     * @return this
     */
    protected LuceneQuery addArgument (final Object value, final QueryModifier modifiers) {
        if (value == null || modifiers == null) return this;
        
        if (value instanceof String) {
            return this.addArgument(String.class.cast(value), modifiers);
        } else if (value instanceof Collection<?>) {
            return this.addArgumentAsCollection(Collection.class.cast(value), modifiers);
        } else if (value.getClass().isArray()) { 
            return this.addArgumentAsArray(value, modifiers);
        } else if (value instanceof LuceneQuery) {
            return this.addSubquery(LuceneQuery.class.cast(value), modifiers);
        } else {
            return this.addArgument(value.toString(), modifiers);
        }
    }
    
    
    
    //---------------------------
    //     addArgumentAs...
    //---------------------------
    
    
    @Override
    public final <K> LuceneQuery addArgumentAsArray(K[] values) {
        return addArgumentAsArray(values, defaultModifier);
    };
    
    
    public abstract <K> LuceneQuery addArgumentAsArray(K[] values, QueryModifier modifier);
    
    
    @Override
    public final LuceneQuery addArgumentAsArray(Object values) {
        return addArgumentAsArray(values, defaultModifier);
    }
    
    
    public abstract LuceneQuery addArgumentAsArray(Object values, QueryModifier modifier);
    
    
    @Override
    public final LuceneQuery addArgumentAsCollection(Collection<?> values) {
        return addArgumentAsCollection(values, defaultModifier);
    }
    
    
    public abstract LuceneQuery addArgumentAsCollection(Collection<?> values, QueryModifier modifier);
    
    
    
    //---------------------------
    //     addSubquery
    //---------------------------
    
    
    @Override
    public final LuceneQuery addSubquery(LuceneQuery value) {
        return addSubquery(value, defaultModifier);
    }
    
    
    @Override
    public LuceneQuery addSubquery(LuceneQuery value, boolean mandatory) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return addSubquery(value, mod);
    }
    
    
    public abstract LuceneQuery addSubquery(LuceneQuery value, QueryModifier modifiers);
    
    
    
    //---------------------------
    //     addField(String, String, ...)
    //---------------------------
    
    
    @Override
    public final LuceneQuery addField(String key, String value) {
        return addField(key, value, defaultModifier);
    }
    
    
    @Override
    public LuceneQuery addField(String key, String value, boolean mandatoryKey) {
        final TermModifier tm = mandatoryKey ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return addField(key, value, mod);
    }
    
    
    @Override
    public LuceneQuery addField(String key, String value,
            boolean mandatoryKey, double boostFactor) {
        final TermModifier tm = mandatoryKey ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return addField(key, value, mod).addBoost(boostFactor);
    }
    
    
    public abstract LuceneQuery addField(String key, String value, QueryModifier modifier);
    
    
    
    //---------------------------
    //     addField(String, Collection, ...)
    //---------------------------
    
    
    @Override
    public final LuceneQuery addField(String key, Collection<?> value) {
        return addFieldAsCollection(key, value, defaultModifier);
    }
    
    
    @Override
    public LuceneQuery addField(String key, boolean mandatoryKey,
            Collection<?> value, boolean mandatoryValue) {
        final TermModifier tm = mandatoryKey ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = new QueryModifier(tm, !mandatoryValue, defaultModifier.isWildcarded(), defaultModifier.isSplit());
        return addFieldAsCollection(key, value, mod);
    }
    
    
    @Override
    public LuceneQuery addField(String key, boolean mandatoryKey,
            Collection<?> value, boolean mandatoryValue, double boostFactor) {
        final TermModifier tm = mandatoryKey ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = new QueryModifier(tm, !mandatoryValue, defaultModifier.isWildcarded(), defaultModifier.isSplit());
        return addFieldAsCollection(key, value, mod).addBoost(boostFactor);
    }
    
    
    @Override
    public final LuceneQuery addField(String key, Collection<?> value,
            QueryModifier modifier) {
        return addFieldAsCollection(key, value, modifier);
    }
    
    
    
    //---------------------------
    //     addField(String, Array, ...)
    //---------------------------
    
    
    @Override
    public final <K> LuceneQuery addField(String key, K[] value) {
        return addFieldAsArray(key, value, defaultModifier);
    };
    
    
    @Override
    public final <K> LuceneQuery addField(String key, K[] value, QueryModifier modifier) {
        return addFieldAsArray(key, value, modifier);
    };
    
    
    //---------------------------
    //     addFuzzyField
    //---------------------------
    
    
    @Override
    public final LuceneQuery addFuzzyField(String key, String value) {
        return addFuzzyField(key, value, defaultModifier, defaultFuzzyness);
    }
    
    
    @Override
    public LuceneQuery addFuzzyField(String key, String value,
            boolean mandatoryKey) {
        final TermModifier tm = mandatoryKey ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return addFuzzyField(key, value, mod, defaultFuzzyness);
    }
    
    
    @Override
    public LuceneQuery addFuzzyField(String key, String value,
            boolean mandatoryKey, double fuzzyness) {
        final TermModifier tm = mandatoryKey ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return addFuzzyField(key, value, mod, fuzzyness);
    }
    
    
    @Override
    public final LuceneQuery addFuzzyField(String key, String value,
            QueryModifier mod) {
        return addFuzzyField(key, value, mod, defaultFuzzyness);
    }
    
    
    public abstract LuceneQuery addFuzzyField(String key, String value,
            QueryModifier mod, double fuzzyness);
    
    
    
    //---------------------------
    //     addFieldAs...
    //---------------------------
    
    
    @Override
    public final LuceneQuery addFieldAsCollection(String key,
            Collection<?> value) {
        return addFieldAsCollection(key, value, defaultModifier);
    }
    
    
    @Override
    public final LuceneQuery addFieldAsCollection(String key,
            Collection<?> value, QueryModifier modifier, double boost) {
        return addFieldAsCollection(key, value, modifier).addBoost(boost);
    }
    
    
    public abstract LuceneQuery addFieldAsCollection(String key,
            Collection<?> value, QueryModifier modifier);

    
    @Override
    public final <K> LuceneQuery addFieldAsArray(String key, K[] value) {
        return addFieldAsArray(key, value, defaultModifier);
    };
    
    
    public abstract <K> LuceneQuery addFieldAsArray(String key, K[] value, QueryModifier modifier);
    
    
    @Override
    public final LuceneQuery addFieldAsArray(String key, Object value) {
        return addFieldAsArray(key, value, defaultModifier);
    }
    
    
    public abstract LuceneQuery addFieldAsArray(String key, Object value, QueryModifier modifier);
    
    
    //-----------------------------------
    //     startField/endField/addBoost
    //-----------------------------------
    
    @Override
    public LuceneQuery startField(String fieldName, boolean mandatory) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return startField(fieldName, mod);
    }
    
    
    @Override
    public abstract LuceneQuery startField(String fieldName,
            QueryModifier modifier);
    
    
    @Override
    public abstract LuceneQuery endField();
    
    
    @Override
    public abstract LuceneQuery addBoost(double boostFactor);
    
    
    //-----------------------------------
    //     addUnescaped...
    //-----------------------------------
    
    @Override
    public abstract LuceneQuery addUnescaped(CharSequence value, boolean mandatory);
    
    
    @Override
    public abstract LuceneQuery addUnescapedField(String key, CharSequence value,
            boolean mandatory);
    

}
