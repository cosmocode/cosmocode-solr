package de.cosmocode.solr;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cosmocode.lucene.AbstractLuceneQuery;
import de.cosmocode.lucene.LuceneHelper;
import de.cosmocode.lucene.LuceneQuery;
import de.cosmocode.lucene.QueryModifier;


/**
 * <p>
 * This is a shortcut for building Solr queries. 
 * It can be directly passed to the SearchClient, which then converts it as needed.
 * </p>
 * <p>
 * <strong>Important:</strong> The SolrQuery class is not thread-safe. It should be synchronized externally.
 * </p>
 * 
 * @see SolrQueryFactory
 * 
 * TODO Oliver Lorenz
 * @author olorenz
 *
 */
// TODO class should be final
class DefaultSolrQuery extends AbstractLuceneQuery implements SolrQuery {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultSolrQuery.class);
    
    private static final String ERR_START_INVALID = 
        "start must be a non-negative integer (i.e. start >= 0)";
    private static final String ERR_MAX_INVALID = 
        "max must be a non-negative integer, that is less than " + MAX +
        " (i.e. 0 <= max <= " + MAX + ")";
    private static final String ERR_NO_DTYPE = "SolrQuery needs a dtype";
    private static final String ERR_FUZZY_OUT_OF_BOUNDS = 
        "fuzzyness must be greater than equals 0 and less than 1 (i.e. 0 <= fuzzyness < 1)";
    private static final String ERR_DIRECT_ASSIGN_Q = 
        "the Request argument q cannot be directly assigned, use the addArgument-methods";
    private static final String ERR_DIRECT_ASSIGN_START = 
        "the Request argument start cannot be directly assigned, use setStart(int)";
    private static final String ERR_DIRECT_ASSIGN_ROWS = 
        "the Request argument rows cannot be directly assigned, use setRows(int)";
    
    
    private final Map<String, Object> requestArguments = new HashMap<String, Object>();
    
    private final StringBuilder queryArguments;
    
    
    /**
     * This constructor sets start, max and wildCarded to the given values.
     * <br><br>
     * An IllegalArgumentException is thrown if the following holds true:<br>
     * <code>
     * (start < 0 || max < 0 || max > {@link SolrQuery#MAX})
     * </code>
     * @param start the number of the first element returned. Useful for pagination.
     * @param max the maximum number of documents returned.
     * @param wildcarded {@link LuceneQuery#setWildCarded(boolean)} is called with the given value
     * @throws IllegalArgumentException, if start or max is not valid
     */
    public DefaultSolrQuery(final int start, final int max, final boolean wildCarded) {
        if (start < 0) throw new IllegalArgumentException(ERR_START_INVALID);
        if (max < 0 || max > SolrQuery.MAX) throw new IllegalArgumentException(ERR_MAX_INVALID);

        this.queryArguments = new StringBuilder();
        this.setStart(start);
        this.setMax(max);
        this.setWildCarded(wildCarded);
    }
    
    
    /**
     * This constructor sets start, max and wildCarded to the given values.<br>
     * It also calls {@link #addUnescaped(CharSequence, boolean)} with (dtype, true).
     * <br><br>
     * An IllegalArgumentException is thrown if the following holds true:<br>
     * <code>
     * (start < 0 || max < 0 || max > {@link SolrQuery#MAX})
     * </code>
     * @param dtype the dtype to append, so that the SolrQuery is initialized with: +dtype_s:`dtype`
     * @param start the number of the first element returned. Useful for pagination.
     * @param max the maximum number of documents returned.
     * @param wildcarded {@link LuceneQuery#setWildCarded(boolean)} is called with the given value
     * @throws IllegalArgumentException, if start or max is not valid
     */
    public DefaultSolrQuery(final String dtype, final int start, final int max, final boolean wildCarded) {
        this(start, max, wildCarded);
        
        if (StringUtils.isNotBlank(dtype)) {
            addUnescapedField("dtype_s", dtype, true);
        } else {
            throw new IllegalArgumentException(ERR_NO_DTYPE);
        }
    }

    @Override
    public String getSelectFields() {
        final Object field = requestArguments.get("fl");
        return field == null ? "*" : field.toString();
    }
    
    
    @Override
    public void selectFields(String... fields) {
        requestArguments.put("fl", StringUtils.join(fields, ","));
    }

    @Override
    public String getSortFields() {
        final Object field = requestArguments.get("sort");
        return field == null ? null : field.toString();
    }

    
    @Override
    public void sortFields(String... sortFields) {
        requestArguments.put("sort", StringUtils.join(sortFields, ","));
    }

    
    @Override
    public int getMax() {
        return getRows();
    }

    
    @Override
    public void setMax(final int max) {
        // TODO max negative?
        setRows(max);
    }

    // TODO i smell a NullPointerException here
    @Override
    public int getRows() {
        return (Integer) requestArguments.get("rows");
    }

    
    @Override
    public void setRows(final int rows) {
        // TODO throw IllegalArgumentException instead
        final int absMax = (rows < 0) ? Math.abs(rows) : rows;
        requestArguments.put("rows", absMax);
    }


    // TODO i smell a NullPointerException here
    @Override
    public int getStart() {
        return (Integer) requestArguments.get("start");
    }

    
    @Override
    public void setStart(final int start) {
        // TODO take a look at Preconditions (google)
        if (start < 0) throw new IllegalArgumentException("start must be a non-negative integer");
        requestArguments.put("start", start);
    }
    
    
    @Override
    public void setRequestArgument(final String name, final String value) {
        if ("q".equalsIgnoreCase(name)) throw new IllegalArgumentException(ERR_DIRECT_ASSIGN_Q);
        if ("start".equalsIgnoreCase(name)) throw new IllegalArgumentException(ERR_DIRECT_ASSIGN_START);
        if ("rows".equalsIgnoreCase(name)) throw new IllegalArgumentException(ERR_DIRECT_ASSIGN_ROWS);
        requestArguments.put(name, value);
    }
    
    
    @Override
    // TODO don't use suppress on the whole method
    @SuppressWarnings("unchecked")
    public void addFacetField(final String facetFieldName) {
        if (facetFieldName == null) return;
        final Set<String> facetFields;
        if (requestArguments.containsKey("facet.field")) {
            try {
                facetFields = (Set<String>) requestArguments.get("facet.field");
            } catch (ClassCastException e) {
                log.warn("Cannot get facet.field", e);
                // TODO instanceof check would save us an exception 
                // TODO fail here ?!
                return;
            }
        } else {
            facetFields = new HashSet<String>();
            requestArguments.put("facet.field", facetFields);
            requestArguments.put("facet", true);
        }
        facetFields.add(facetFieldName);
    }
    
    
    @Override
    public void addFacetFields(final String... facetFields) {
        // TODO null should not be omitted, throw NullPointerException
        if (facetFields == null) return;
        
        for (final String facetField : facetFields) {
            this.addFacetField(facetField);
        }
    }
    
    
    @Override
    public String getQuery() {
        return this.queryArguments.toString();
    }
    
    
    /**
     * Returns the request arguments in a new HashMap.
     * TODO logging decisions should be left to the configuration, not the application
     * @param logRequestArgs if true, then SolrQuery logs the returned map in log4j. Otherwise not.
     * @return the request arguments in a new HashMap 
     * (no modifications on this SolrQuery possible)
     */
    protected Map<String, Object> getRequestArguments(final boolean logRequestArgs) {
     // put requests into a separate map to prevent modification and to add the query
        // TODO what about Collections.unmodifiableMap(Map), can be cached
        final Map<String, Object> requestArgs = new HashMap<String, Object>(this.requestArguments);
        requestArgs.put("q", this.queryArguments);
        if (logRequestArgs && log.isDebugEnabled()) log.debug(requestArgs.toString());
        return requestArgs;
    }
    
    
    @Override
    public Map<String, Object> getRequestArguments() {
        return this.getRequestArguments(true);
    }
    
    
    @Override
    public Set<Map.Entry<String, Object>> getRequestArgumentSet() {
        return getRequestArguments(true).entrySet();
    }
    
    
    // TODO stick to javadoc style comments
    //---------------------------
    //     addArgument-methods
    //---------------------------
    
    
    @Override
    public DefaultSolrQuery addFuzzyArgument(final String value, 
            final QueryModifier modifier, final double fuzzyness) {
        if (fuzzyness < 0.0 || fuzzyness >= 1.0)
            throw new IllegalArgumentException(ERR_FUZZY_OUT_OF_BOUNDS);
        
        // TODO: implement isSplit() of QueryModifier
        if (StringUtils.isNotBlank(value) && modifier != null) {
            queryArguments.append(modifier.getTermPrefix());
            
            queryArguments.append("(").
                append(LuceneHelper.escapeAll(value)).
                append("~").append(fuzzyness);
            queryArguments.append(")");
        }
        
        return this;
    }
    
    
    @Override
    public DefaultSolrQuery addArgument(final String value, final QueryModifier modifier) {
        // TODO: implement isSplit() of QueryModifier
        if (StringUtils.isNotBlank(value) && modifier != null) {
            queryArguments.append(modifier.getTermPrefix());
            
            if (modifier.isWildcarded()) {
                // search for input wildcarded (wildcard is appended at the end). 
                // the original input is added, too, because e.g. adidas* doesn't match "adidas" on text-fields
                queryArguments.append("(").
                    append("\"").append(LuceneHelper.escapeQuotes(value)).append("\"^2").
                    append(" ").append(LuceneHelper.escapeAll(value)).append("*");
                queryArguments.append(")");
            } else {
                queryArguments.append(LuceneHelper.escapeAll(value));
            }
            queryArguments.append(" ");
        }
        
        return this;
    }
    
    
    // add collection and array
    
    
    @Override
    public DefaultSolrQuery addArgumentAsCollection(final Collection<?> values, final QueryModifier modifier) {
        if (values != null && values.size() > 0) {
            // start
            queryArguments.append("(");

            // add items
            // TODO what about leaving this decision to the QueryModifier class
            final QueryModifier valueModifier = modifier.isDisjunct() ? QueryModifier.NONE : QueryModifier.REQUIRED;
            for (Object val : values) {
                addArgument(val, valueModifier);
            }

            // end
            if (queryArguments.charAt(queryArguments.length() - 1) == '(') {
                // if the just opened bracket is still the last character, then revert it
                queryArguments.setLength(queryArguments.length() - 1);
            } else {
                queryArguments.append(") ");
            }
        }
        
        return this;
    }
    
    
    @Override
    public <K> DefaultSolrQuery addArgumentAsArray(final K[] values, final QueryModifier modifier) {
        // TODO quick return if values == null || values.length == 0
        if (values != null && values.length > 0) {
            // start
            queryArguments.append("(");
            
            // add items
            final QueryModifier valueModifier = modifier.isDisjunct() ? QueryModifier.NONE : QueryModifier.REQUIRED;
            for (K val : values) {
                addArgument(val, valueModifier);
            }
            
            // end
            if (queryArguments.charAt(queryArguments.length() - 1) == '(') {
                // if the just opened bracket is still the last character, then revert it
                queryArguments.setLength(queryArguments.length() - 1);
            } else {
                queryArguments.append(") ");
            }
        }
        
        return this;
    }
    
    // TODO clients should care about Objects, we should require typed arrays
    @Override
    public DefaultSolrQuery addArgumentAsArray(Object values, final QueryModifier modifier) {
        // TODO if (values == null) return this;
        if (values != null && values.getClass().isArray() && Array.getLength(values) > 0) {
            final int arrayLength = Array.getLength(values);
            
            // start
            queryArguments.append("(");
            
            // add all items
            final QueryModifier valueModifier = modifier.isDisjunct() ? QueryModifier.NONE : QueryModifier.REQUIRED;
            for (int i = 0; i < arrayLength; i++) {
                addArgument(Array.get(values, i), valueModifier);
            }
            
            // end
            if (queryArguments.charAt(queryArguments.length() - 1) == '(') {
                // if the just opened bracket is still the last character, then revert it
                queryArguments.setLength(queryArguments.length() - 1);
            } else {
                queryArguments.append(") ");
            }
        }
        
        return this;
    }
    
    
    
    @Override
    public DefaultSolrQuery addSubquery(final LuceneQuery value, final boolean mandatory) {
        if (value != null) {
            if (mandatory) queryArguments.append("+");
            queryArguments.append("(").append(value.getQuery()).append(") ");
        }
        
        return this;
    }
    
    
    @Override
    public DefaultSolrQuery addSubquery(final LuceneQuery value, final QueryModifier modifiers) {
        if (value != null) {
            queryArguments.append(modifiers.getTermPrefix());
            queryArguments.append("(").append(value.getQuery()).append(") ");
        }
        
        return this;
    }
    
    
    
    //---------------------------
    //     addUnescaped-methods
    //---------------------------
    
    
    @Override
    public DefaultSolrQuery addUnescapedField(final String key, final CharSequence value, final boolean mandatory) {
        if (key == null || value == null) return this;
        
        if (mandatory) queryArguments.append("+");
        queryArguments.append(key).append(":").append(value).append(" ");
        
        return this;
    }
    
    
    @Override
    public DefaultSolrQuery addUnescaped(final CharSequence value, final boolean mandatory) {
        if (value == null) return this;
        
        if (mandatory) queryArguments.append("+");
        queryArguments.append(value).append(" ");
        
        return this;
    }
    
    
    
    //---------------------------
    //     addField-methods
    //---------------------------
    
    
//    public DefaultSolrQuery addField (final String key, final Object value, final QueryModifier modifiers) {
//        if (value instanceof Collection<?>) {
//            return this.addFieldAsCollection(key, (Collection<?>)value, modifiers);
//        } else if (value instanceof String) {
//            return this.addField(key, (String)value, modifiers);
//        } else if (value != null && value.getClass().isArray()) {
//            return this.addFieldAsArray(key, value, modifiers);
//        }
//        
//        // default implementation: no check possible
//        if (StringUtils.isNotBlank(key) && value != null) {
//            startField(key, modifiers);
//            addArgument(value, modifiers);
//            endField();
//        }
//        
//        return this;
//    }
    
    
    @Override
    public DefaultSolrQuery addField(final String key, final String value, final QueryModifier modifiers) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            this.startField(key, modifiers);
            this.addArgument(value, modifiers);
            if (value.contains(" ")) {
                queryArguments.append("(");
                for (String token : value.split(" ")) {
                    this.addArgument(token, modifiers);
                }
                queryArguments.append(")^0.5");
            }
            this.endField();
        }
        
        return this;
    }
    
    
    @Override
    public DefaultSolrQuery addFuzzyField(final String key, final String value, 
            final boolean mandatoryKey, final double fuzzyness) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            this.startField(key, mandatoryKey);
            this.addFuzzyArgument(value, false, fuzzyness);
            if (value.contains(" ")) {
                queryArguments.append("(");
                for (String token : value.split(" ")) {
                    this.addFuzzyArgument(token, false, fuzzyness);
                }
                queryArguments.append(")^0.5");
            }
            this.endField();
        }
        
        return this;
    }
    
    
    @Override
    public DefaultSolrQuery addFuzzyField(final String key, final String value, 
            final QueryModifier modifier, final double fuzzyness) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            this.startField(key, modifier);
            this.addFuzzyArgument(value, false, fuzzyness);
            if (value.contains(" ")) {
                queryArguments.append("(");
                for (String token : value.split(" ")) {
                    this.addFuzzyArgument(token, false, fuzzyness);
                }
                queryArguments.append(")^0.5");
            }
            this.endField();
        }
        
        return this;
    }
    
    
    @Override
    public DefaultSolrQuery addField(String key, boolean mandatoryKey, Collection<?> value, boolean mandatoryValue) {
        if (StringUtils.isNotBlank(key) && value != null && value.size() > 0) {
            startField(key, mandatoryKey);
            addArgument(value, mandatoryValue);
            endField();
        }
        
        return this;
    }

    
    @Override
    public DefaultSolrQuery addFieldAsCollection(final String key, 
            final Collection<?> value, final QueryModifier modifiers) {
        if (StringUtils.isNotBlank(key) && value != null && value.size() > 0) {
            startField(key, modifiers);
            addArgumentAsCollection(value, modifiers);
            endField();
        }
        
        return this;
    }
    
    
    @Override
    public <K> DefaultSolrQuery addFieldAsArray(final String key, final K[] value, final QueryModifier modifiers) {
        if (StringUtils.isNotBlank(key) && value != null && value.length > 0) {
            startField(key, modifiers);
            addArgumentAsArray(value, modifiers);
            endField();
        }
        
        return this;
    }
    
    
    @Override
    public DefaultSolrQuery addFieldAsArray(final String key, final Object value, final QueryModifier modifiers) {
        if (StringUtils.isNotBlank(key) && value != null && value.getClass().isArray() && Array.getLength(value) > 0) {
            startField(key, modifiers);
            addArgumentAsArray(value, modifiers);
            endField();
        }
        
        return this;
    }
    

    //---------------------------
    //  helper methods
    //---------------------------
    
    @Override
    public DefaultSolrQuery startField(final String fieldName, final boolean mandatory) {
        if (StringUtils.isBlank(fieldName)) return this;
        
        if (mandatory) queryArguments.append("+");
        queryArguments.append(fieldName).append(":(");
        
        return this;
    }
    
    
    @Override
    public DefaultSolrQuery startField(final String fieldName, final QueryModifier modifier) {
        if (StringUtils.isBlank(fieldName)) return this;
        
        queryArguments.append(modifier.getTermPrefix());
        queryArguments.append(fieldName).append(":(");
        
        return this;
    }
    
    
    @Override
    public DefaultSolrQuery endField() {
        if (queryArguments.charAt(queryArguments.length() - 1) == '(') {
            // add an empty string, if the field was ended right after it was started
            queryArguments.append("\"\"");
        }
        queryArguments.append(") ");
        
        return this;
    }
    
    
    @Override
    public DefaultSolrQuery addBoost(final double boostFactor) {
        if (boostFactor <= 0.0 || boostFactor >= 10000000.0)
            throw new IllegalArgumentException("boostFactor must be greater than 0 and less than 10.000.000 (10 Mio.)");
        
        // optimization: only add boost factor if != 1
        if (boostFactor != 1.0) {
            final double rounded = ((int) (boostFactor * 100.0)) / 100.0;
            this.queryArguments.append("^").append(rounded).append(" ");
        }

        return this;
    }
    
    
    @Override
    public String toString() {
        return getRequestArguments(false).toString();
    }
    

}
