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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.common.params.CommonParams;

import de.cosmocode.lucene.ForwardingLuceneQuery;
import de.cosmocode.lucene.LuceneQuery;

/**
 * <p> A {@link SolrQuery} that is backed by the apache implementation of a solr query
 * for the SolrJ framework.
 * </p>
 * <p> The method {@link #getSolrJ()} returns the underlying SolrJ implementation.
 * It can be used to search with SolrJ or perform more operations.
 * Every call, except {@link org.apache.solr.client.solrj.SolrQuery#setQuery(String)},
 * will be reflected in the final result.
 * </p>
 *
 * @author Oliver Lorenz
 */
public final class SolrJQuery extends ForwardingLuceneQuery implements SolrQuery {
    
    private final org.apache.solr.client.solrj.SolrQuery solrJQuery;
    
    private final LuceneQuery delegated;
    
    public SolrJQuery(final int start, final int rows, final LuceneQuery delegated) {
        this.solrJQuery = new org.apache.solr.client.solrj.SolrQuery();
        this.setStart(start);
        this.setRows(rows);
        this.delegated = delegated;
    }
    
    @Override
    protected LuceneQuery delegate() {
        return delegated;
    }
    
    /**
     * <p> Returns the underlying SolrJ instance (unfortunately also named SolrQuery).
     * It can be used to search with SolrJ or perform several more operations
     * that the SolrQuery interface does not support (yet).
     * </p>
     * @return the underlying solrj instance
     */
    public org.apache.solr.client.solrj.SolrQuery getSolrJ() {
        solrJQuery.setQuery(getQuery());
        return solrJQuery;
    }

    @Override
    public void addFacetField(String facetFieldName) {
        solrJQuery.addFacetField(facetFieldName);
    }

    @Override
    public void addFacetFields(String... facetFields) {
        solrJQuery.addFacetField(facetFields);
    }

    @Override
    public int getMax() {
        return solrJQuery.getRows();
    }

    @Override
    public Set<Entry<String, Object>> getRequestArgumentSet() {
        return getRequestArguments().entrySet();
    }

    @Override
    public Map<String, Object> getRequestArguments() {
        solrJQuery.setQuery(getQuery());
        final Map<String, Object> args = new LinkedHashMap<String, Object>();
        for (final String param : solrJQuery.getParameterNames()) {
            final String[] paramValues = solrJQuery.getParams(param);
            if (paramValues == null || paramValues.length == 0) {
                continue;
            } else if (paramValues.length == 1) {
                args.put(param, paramValues[0]);
            } else {
                args.put(param, paramValues);
            }
        }
        return args;
    }

    @Override
    public int getRows() {
        return solrJQuery.getRows();
    }

    @Override
    public String getSelectFields() {
        return solrJQuery.getFields();
    }

    @Override
    public String getSortFields() {
        return solrJQuery.getSortField();
    }

    @Override
    public int getStart() {
        return solrJQuery.getStart();
    }

    @Override
    public void selectFields(String... fields) {
        solrJQuery.setFields(fields);
    }

    @Override
    public void setMax(int max) {
        solrJQuery.setRows(max);
    }

    @Override
    public void setRequestArgument(String name, String value) {
        solrJQuery.set(name, value);
    }

    @Override
    public void setRows(int rows) {
        solrJQuery.setRows(rows);
    }

    @Override
    public void setStart(int start) {
        solrJQuery.setStart(start);
    }

    @Override
    public void sortFields(String... sortFields) {
        solrJQuery.remove(CommonParams.SORT);
        for (String field : sortFields) {
            solrJQuery.addSortField(field, ORDER.desc);
        }
    }

}
