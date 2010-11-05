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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import de.cosmocode.lucene.ForwardingLuceneQuery;
import de.cosmocode.lucene.LuceneQuery;


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
final class DefaultSolrQuery extends ForwardingLuceneQuery implements SolrQuery {
    
    private static final String ERR_START_INVALID = 
        "start must be a non-negative integer (i.e. start >= 0)";
    private static final String ERR_MAX_INVALID = 
        "max must be a non-negative integer and less than " + MAX +
        " (i.e. 0 <= max <= " + MAX + ")";
    private static final String ERR_DIRECT_ASSIGN_Q = 
        "the Request argument q cannot be directly assigned, use the addArgument-methods";
    private static final String ERR_DIRECT_ASSIGN_START = 
        "the Request argument start cannot be directly assigned, use setStart(int)";
    private static final String ERR_DIRECT_ASSIGN_ROWS = 
        "the Request argument rows cannot be directly assigned, use setRows(int)";
    
    
    private final Map<String, Object> requestArguments = new HashMap<String, Object>();
    
    private final LuceneQuery forwarded;
    
    private int start;
    
    private int rows;
    
    
    /**
     * <p> This constructor sets start and max to the given values.
     * </p>
     * <p> An IllegalArgumentException is thrown if the following holds true:<br>
     * <code>
     * (start < 0 || max < 0 || max > {@link SolrQuery#MAX})
     * </code>
     * </p>
     * <p> The third parameter is the real {@link LuceneQuery} implementation.
     * This class forwards every LuceneQuery call to the given implementation.
     * </p>
     * 
     * @param start the number of the first element returned. Useful for pagination.
     * @param max the maximum number of documents returned.
     * @param impl the real LuceneQuery implementation
     * @throws IllegalArgumentException, if start or max is not valid
     */
    public DefaultSolrQuery(final int start, final int max, final LuceneQuery impl) {
        if (start > 0) throw new IllegalArgumentException(ERR_START_INVALID);
        if (max < 0 || max > SolrQuery.MAX) throw new IllegalArgumentException(ERR_MAX_INVALID);

        this.setStart(start);
        this.setMax(max);
        this.forwarded = impl;
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
        setRows(max);
    }

    @Override
    public int getRows() {
        return rows;
    }

    
    @Override
    public void setRows(final int rows) {
        Preconditions.checkArgument(rows >= 0 && rows <= SolrQuery.MAX, ERR_MAX_INVALID);
        this.rows = rows;
    }


    @Override
    public int getStart() {
        return start;
    }

    
    @Override
    public void setStart(final int start) {
        Preconditions.checkArgument(start >= 0, ERR_START_INVALID);
        this.start = start;
    }
    
    
    @Override
    public void setRequestArgument(final String name, final String value) {
        if ("q".equalsIgnoreCase(name)) throw new IllegalArgumentException(ERR_DIRECT_ASSIGN_Q);
        if ("start".equalsIgnoreCase(name)) throw new IllegalArgumentException(ERR_DIRECT_ASSIGN_START);
        if ("rows".equalsIgnoreCase(name)) throw new IllegalArgumentException(ERR_DIRECT_ASSIGN_ROWS);
        requestArguments.put(name, value);
    }
    
    
    @Override
    public void addFacetField(final String facetFieldName) {
        if (facetFieldName == null) return;
        final Set<String> facetFields;
        if (requestArguments.containsKey("facet.field")) {
            final Object obj = requestArguments.get("facet.field");
            Preconditions.checkNotNull(obj, "value of 'facet.field'");
            Preconditions.checkState(obj instanceof Set<?>, 
                "Expected facet.field to be a Set<String>, but was %s", obj.getClass());
            @SuppressWarnings("unchecked")
            final Set<String> tmpFields = (Set<String>) requestArguments.get("facet.field");
            facetFields = tmpFields;
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
    public Map<String, Object> getRequestArguments() {
        this.requestArguments.put("q", this.getQuery());
        this.requestArguments.put("start", start);
        this.requestArguments.put("rows", rows);
        return Collections.unmodifiableMap(this.requestArguments);
    }
    
    
    @Override
    public Set<Map.Entry<String, Object>> getRequestArgumentSet() {
        return getRequestArguments().entrySet();
    }

    @Override
    public org.apache.solr.client.solrj.SolrQuery toApacheSolrQuery() {
        // start and rows is set in constructor. query is copied from delegate.
        final SolrJQuery solrJQuery = new SolrJQuery(getStart(), getRows(), delegate());
        for (final Map.Entry<String, Object> requestEntry : this.requestArguments.entrySet()) {
            if (requestEntry.getValue() instanceof String) {
                solrJQuery.setRequestArgument(requestEntry.getKey(), requestEntry.getValue().toString());
            } else if (requestEntry.getValue() instanceof Iterable<?>) {
                for (final Object obj : Iterable.class.cast(requestEntry.getValue())) {
                    solrJQuery.getSolrJ().add(requestEntry.getKey(), obj.toString());
                }
            }
        }
        return solrJQuery.getSolrJ();
    }
    
    @Override
    protected LuceneQuery delegate() {
        return forwarded;
    }
    
    @Override
    public String toString() {
        return getRequestArguments().toString();
    }

}
