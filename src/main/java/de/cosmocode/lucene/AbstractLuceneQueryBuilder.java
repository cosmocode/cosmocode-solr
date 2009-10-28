package de.cosmocode.lucene;

import java.util.Collection;


public abstract class AbstractLuceneQueryBuilder implements LuceneQueryBuilder {
    
    
    private QueryModifier defaultModifier = QueryModifier.NONE;
    
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
    public final LuceneQueryBuilder addFuzzyArgument(final String value) {
        return addFuzzyArgument(value, defaultModifier, defaultFuzzyness);
    }
    
    
    @Override
    public final LuceneQueryBuilder addFuzzyArgument(final String value, final boolean mandatory) {
        return addFuzzyArgument(value, mandatory, defaultFuzzyness);
    }
    
    
    @Override
    public LuceneQueryBuilder addFuzzyArgument(final String value, final boolean mandatory,
            final double fuzzyness) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return this.addFuzzyArgument(value, mod, fuzzyness);
    }
    
    
    @Override
    public final LuceneQueryBuilder addFuzzyArgument(final String value, final QueryModifier modifier) {
        return addFuzzyArgument(value, modifier, defaultFuzzyness);
    }
    
    
    public abstract LuceneQueryBuilder addFuzzyArgument(final String value, final QueryModifier modifier, final double fuzzyness);
    
    
    
    //---------------------------
    //     addArgument
    //---------------------------
    
    
    @Override
    public final LuceneQueryBuilder addArgument(final String value) {
        return addArgument(value, defaultModifier);
    }
    
    
    @Override
    public LuceneQueryBuilder addArgument(final String value, final boolean mandatory) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return this.addArgument(value, mod);
    }
    
    
    public abstract LuceneQueryBuilder addArgument(final String value, final QueryModifier modifiers);
    
    
    @Override
    public final LuceneQueryBuilder addArgument(Collection<?> values) {
        return addArgument(values, defaultModifier);
    }
    
    
    @Override
    public LuceneQueryBuilder addArgument(final Collection<?> value, final boolean mandatory) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return this.addArgument(value, mod);
    }
    
    
    @Override
    public final LuceneQueryBuilder addArgument(Collection<?> values, QueryModifier modifier) {
        return addArgumentAsCollection(values, modifier);
    }
    
    
    @Override
    public final <K> LuceneQueryBuilder addArgument(K[] values) {
        return addArgument(values, defaultModifier);
    }
    
    
    @Override
    public <K> LuceneQueryBuilder addArgument(K[] values, boolean mandatory) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return addArgument(values, mod);
    }
    
    
    @Override
    public final <K> LuceneQueryBuilder addArgument(K[] values, QueryModifier modifier) {
        return addArgumentAsArray(values, modifier);
    }
    
    
    /**
     * This is a utility method, that determines what to call based on "instanceof".
     * 
     * @param value the query argument to add; null is omitted
     * @param modifiers the modifiers for the addArgumentAs...-methods
     * @return this
     */
    protected LuceneQueryBuilder addArgument (final Object value, final QueryModifier modifiers) {
        if (value == null || modifiers == null) return this;
        
        if (value instanceof String) {
            return this.addArgument(String.class.cast(value), modifiers);
        } else if (value instanceof Collection<?>) {
            return this.addArgumentAsCollection(Collection.class.cast(value), modifiers);
        } else if (value.getClass().isArray()) { 
            return this.addArgumentAsArray(value, modifiers);
        } else if (value instanceof LuceneQueryBuilder) {
            return this.addSubquery(LuceneQueryBuilder.class.cast(value), modifiers);
        } else {
            return this.addArgument(value.toString(), modifiers);
        }
    }
    
    
    
    //---------------------------
    //     addArgumentAs...
    //---------------------------
    
    
    @Override
    public final <K> LuceneQueryBuilder addArgumentAsArray(K[] values) {
        return addArgumentAsArray(values, defaultModifier);
    };
    
    
    public abstract <K> LuceneQueryBuilder addArgumentAsArray(K[] values, QueryModifier modifier);
    
    
    @Override
    public final LuceneQueryBuilder addArgumentAsArray(Object values) {
        return addArgumentAsArray(values, defaultModifier);
    }
    
    
    public abstract LuceneQueryBuilder addArgumentAsArray(Object values, QueryModifier modifier);
    
    
    @Override
    public final LuceneQueryBuilder addArgumentAsCollection(Collection<?> values) {
        return addArgumentAsCollection(values, defaultModifier);
    }
    
    
    public abstract LuceneQueryBuilder addArgumentAsCollection(Collection<?> values, QueryModifier modifier);
    
    
    
    //---------------------------
    //     addSubquery
    //---------------------------
    
    
    @Override
    public final LuceneQueryBuilder addSubquery(LuceneQueryBuilder value) {
        return addSubquery(value, defaultModifier);
    }
    
    
    @Override
    public LuceneQueryBuilder addSubquery(LuceneQueryBuilder value, boolean mandatory) {
        final TermModifier tm = mandatory ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return addSubquery(value, mod);
    }
    
    
    public abstract LuceneQueryBuilder addSubquery(LuceneQueryBuilder value, QueryModifier modifiers);
    
    
    
    //---------------------------
    //     addField(String, String, ...)
    //---------------------------
    
    
    @Override
    public final LuceneQueryBuilder addField(String key, String value) {
        return addField(key, value, defaultModifier);
    }
    
    
    @Override
    public LuceneQueryBuilder addField(String key, String value, boolean mandatoryKey) {
        final TermModifier tm = mandatoryKey ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return addField(key, value, mod);
    }
    
    
    @Override
    public LuceneQueryBuilder addField(String key, String value,
            boolean mandatoryKey, double boostFactor) {
        final TermModifier tm = mandatoryKey ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return addField(key, value, mod).addBoost(boostFactor);
    }
    
    
    public abstract LuceneQueryBuilder addField(String key, String value, QueryModifier modifier);
    
    
    
    //---------------------------
    //     addField(String, Collection, ...)
    //---------------------------
    
    
    @Override
    public final LuceneQueryBuilder addField(String key, Collection<?> value) {
        return addFieldAsCollection(key, value, defaultModifier);
    }
    
    
    @Override
    public LuceneQueryBuilder addField(String key, boolean mandatoryKey,
            Collection<?> value, boolean mandatoryValue) {
        final TermModifier tm = mandatoryKey ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = new QueryModifier(tm, !mandatoryValue, defaultModifier.isWildcarded(), defaultModifier.isSplit());
        return addFieldAsCollection(key, value, mod);
    }
    
    
    @Override
    public LuceneQueryBuilder addField(String key, boolean mandatoryKey,
            Collection<?> value, boolean mandatoryValue, double boostFactor) {
        final TermModifier tm = mandatoryKey ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = new QueryModifier(tm, !mandatoryValue, defaultModifier.isWildcarded(), defaultModifier.isSplit());
        return addFieldAsCollection(key, value, mod).addBoost(boostFactor);
    }
    
    
    @Override
    public final LuceneQueryBuilder addField(String key, Collection<?> value,
            QueryModifier modifier) {
        return addFieldAsCollection(key, value, modifier);
    }
    
    
    
    //---------------------------
    //     addField(String, Array, ...)
    //---------------------------
    
    
    @Override
    public final <K> LuceneQueryBuilder addField(String key, K[] value) {
        return addFieldAsArray(key, value, defaultModifier);
    };
    
    
    @Override
    public final <K> LuceneQueryBuilder addField(String key, K[] value, QueryModifier modifier) {
        return addFieldAsArray(key, value, modifier);
    };
    
    
    //---------------------------
    //     addFuzzyField
    //---------------------------
    
    
    @Override
    public final LuceneQueryBuilder addFuzzyField(String key, String value) {
        return addFuzzyField(key, value, defaultModifier, defaultFuzzyness);
    }
    
    
    @Override
    public LuceneQueryBuilder addFuzzyField(String key, String value,
            boolean mandatoryKey) {
        final TermModifier tm = mandatoryKey ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return addFuzzyField(key, value, mod, defaultFuzzyness);
    }
    
    
    @Override
    public LuceneQueryBuilder addFuzzyField(String key, String value,
            boolean mandatoryKey, double fuzzyness) {
        final TermModifier tm = mandatoryKey ? TermModifier.REQUIRED : TermModifier.NONE;
        final QueryModifier mod = QueryModifier.merge(defaultModifier, tm);
        return addFuzzyField(key, value, mod, fuzzyness);
    }
    
    
    @Override
    public final LuceneQueryBuilder addFuzzyField(String key, String value,
            QueryModifier mod) {
        return addFuzzyField(key, value, mod, defaultFuzzyness);
    }
    
    
    public abstract LuceneQueryBuilder addFuzzyField(String key, String value,
            QueryModifier mod, double fuzzyness);
    
    
    
    //---------------------------
    //     addFieldAs...
    //---------------------------
    
    
    @Override
    public final LuceneQueryBuilder addFieldAsCollection(String key,
            Collection<?> value) {
        return addFieldAsCollection(key, value, defaultModifier);
    }
    
    
    @Override
    public LuceneQueryBuilder addFieldAsCollection(String key,
            Collection<?> value, QueryModifier modifier, double boost) {
        return addFieldAsCollection(key, value, modifier).addBoost(boost);
    }
    
    
    public abstract LuceneQueryBuilder addFieldAsCollection(String key,
            Collection<?> value, QueryModifier modifier);

    
    @Override
    public final <K> LuceneQueryBuilder addFieldAsArray(String key, K[] value) {
        return addFieldAsArray(key, value, defaultModifier);
    };
    
    
    public abstract <K> LuceneQueryBuilder addFieldAsArray(String key, K[] value, QueryModifier modifier);
    
    
    @Override
    public final LuceneQueryBuilder addFieldAsArray(String key, Object value) {
        return addFieldAsArray(key, value, defaultModifier);
    }
    
    
    public abstract LuceneQueryBuilder addFieldAsArray(String key, Object value, QueryModifier modifier);

}
