/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.solr;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

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
 * @author Oliver Lorenz
 *
 */
final class DefaultSolrQuery extends AbstractLuceneQuery implements SolrQuery {
    
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
        if (max < 0 || max > SolrQuery.MAX) throw new IllegalArgumentException(ERR_MAX_INVALID);
        setRows(max);
    }

    // TODO i smell a NullPointerException here
    @Override
    public int getRows() {
        return (Integer) requestArguments.get("rows");
    }

    
    @Override
    public void setRows(final int rows) {
        if (rows < 0 || rows > SolrQuery.MAX) throw new IllegalArgumentException(ERR_MAX_INVALID);
        requestArguments.put("rows", rows);
    }


    // TODO i smell a NullPointerException here
    @Override
    public int getStart() {
        return (Integer) requestArguments.get("start");
    }

    
    @Override
    public void setStart(final int start) {
        // TODO take a look at Preconditions (google)
        if (start < 0) throw new IllegalArgumentException(ERR_START_INVALID);
        requestArguments.put("start", start);
    }
    
    
    @Override
    public void setRequestArgument(final String name, final String value) {
        if ("q".equalsIgnoreCase(name)) throw new IllegalArgumentException(ERR_DIRECT_ASSIGN_Q);
        if ("start".equalsIgnoreCase(name)) throw new IllegalArgumentException(ERR_DIRECT_ASSIGN_START);
        if ("rows".equalsIgnoreCase(name)) throw new IllegalArgumentException(ERR_DIRECT_ASSIGN_ROWS);
        requestArguments.put(name, value);
    }
    
    
    @SuppressWarnings("unchecked")
    @Override
    // TODO don't use suppress on the whole method
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
        if (facetFields == null) throw new NullPointerException("facetFields must not be null");
        
        for (final String facetField : facetFields) {
            this.addFacetField(facetField);
        }
    }
    
    
    @Override
    public String getQuery() {
        return this.queryArguments.toString();
    }
    
    
    @Override
    public Map<String, Object> getRequestArguments() {
        this.requestArguments.put("q", this.queryArguments.toString());
        return Collections.unmodifiableMap(this.requestArguments);
    }
    
    
    @Override
    public Set<Map.Entry<String, Object>> getRequestArgumentSet() {
        return getRequestArguments().entrySet();
    }
    
    
    /* ---------------------------
     *     addArgument-methods
     */
    
    
    @Override
    public DefaultSolrQuery addFuzzyArgument(final String value, 
            final QueryModifier modifier, final double fuzzyness) {
        if (fuzzyness < 0.0 || fuzzyness >= 1.0)
            throw new IllegalArgumentException(ERR_FUZZY_OUT_OF_BOUNDS);
        
        // TODO: copy QueryModifier and set fuzzyness, then redirect to addArgument(String, QueryModifier)
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
        // TODO: implement isFuzzyEnabled() and fuzzyness of QueryModifier
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
    

    /* ---------------------------
     *     addArgumentAs...-methods
     */
    
    
    @Override
    public DefaultSolrQuery addArgumentAsCollection(final Collection<?> values, final QueryModifier modifier) {
        if (values == null || values.size() == 0) return this;
        
        // start
        queryArguments.append("(");

        // add items
        final QueryModifier valueModifier = modifier.getFieldValueModifier();
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
        
        return this;
    }
    
    
    @Override
    public <K> DefaultSolrQuery addArgumentAsArray(final K[] values, final QueryModifier modifier) {
        // quick return
        if (values == null || values.length == 0) return this;
        
        // start
        queryArguments.append("(");
        
        // add items
        final QueryModifier valueModifier = modifier.getFieldValueModifier();
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
        
        return this;
    }
    
    // TODO clients should care about Objects, we should require typed arrays
    @Override
    public DefaultSolrQuery addArgumentAsArray(Object values, final QueryModifier modifier) {
        if (values == null) return this;
        
        if (values.getClass().isArray() && Array.getLength(values) > 0) {
            final int arrayLength = Array.getLength(values);
            
            // start
            queryArguments.append("(");
            
            // add all items
            final QueryModifier valueModifier = modifier.getFieldValueModifier();
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
        if (value == null) return this;

        final CharSequence subQuery = value.getQuery();
        if (subQuery.length() == 0) return this;
        
        if (mandatory) queryArguments.append("+");
        queryArguments.append("(").append(subQuery).append(") ");
        
        return this;
    }
    
    
    @Override
    public DefaultSolrQuery addSubquery(final LuceneQuery value, final QueryModifier modifiers) {
        if (value == null) return this;

        final CharSequence subQuery = value.getQuery();
        if (subQuery.length() == 0) return this;
        
        queryArguments.append(modifiers.getTermPrefix());
        queryArguments.append("(").append(subQuery).append(") ");
        
        return this;
    }
    
    
    
    //---------------------------
    //     addUnescaped-methods
    //---------------------------
    
    
    @Override
    public DefaultSolrQuery addUnescapedField(final String key, final CharSequence value, final boolean mandatory) {
        if (key == null || value == null || value.length() == 0) return this;
        
        if (mandatory) queryArguments.append("+");
        queryArguments.append(key).append(":").append(value).append(" ");
        
        return this;
    }
    
    
    @Override
    public DefaultSolrQuery addUnescaped(final CharSequence value, final boolean mandatory) {
        if (value == null || value.length() == 0) return this;
        
        if (mandatory) queryArguments.append("+");
        queryArguments.append(value).append(" ");
        
        return this;
    }
    
    
    
    /*---------------------------
     *     addField-methods
     */
    
    
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
    

    /*---------------------------
     *  helper methods
     */
    
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
        return getRequestArguments().toString();
    }
    

}
