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

import java.util.Map;
import java.util.Set;

import de.cosmocode.lucene.LuceneQuery;

/**
 * <p>
 * This is a shortcut for building Solr queries. 
 * It can be directly passed to the SearchClient, which then converts it as needed.
 * </p>
 * 
 * @see DefaultSolrQuery
 * @see SolrQueryFactory
 * 
 * @author olorenz
 *
 */
public interface SolrQuery extends LuceneQuery {
    
    // TODO: find out real max value
    
    /**
     * This is the maximum value for "max" that solr can handle, which is {@value}.
     */
    int MAX = 10000000;
    
    
    /**
     * Returns the selected fields for the query - cannot be null, but "*" indicates that all are selected.
     * @return the selected fields for the query - cannot be null, but "*" indicates that all are selected
     */
    String getSelectFields();
    
    
    /**
     * Set the fields returned by the query - can be null; null indicates that all are selected.
     * @param fields the fields returned by the query - can be null; null indicates that all are selected.
     */
    void selectFields(final String... fields);
    

    /**
     * Returns the fields on which the result is sorted on - can be null.
     * @return the fields on which the result is sorted on - can be null.
     */
    String getSortFields();
    

    /**
     * Set the sort fields on which the documents are sorted on.
     * The syntax is SQL style (e.g. "field1 desc", "field2 asc", ...). 
     * @param sortFields the fields on which the result is sorted on - can be null.
     */
    void sortFields(String... sortFields);
    

    /**
     * Returns the maximum number of results/documents that this query will return.
     * 
     * @see SolrQuery#getRows()
     * @return the maximum number of results/documents that this query will return.
     */
    int getMax();
    

    /**
     * Sets the maximum number of results/documents that this query will return.
     * <br>The maximum value is {@link #MAX}
     * @param max the maximum number of results/documents that this query will return.
     * 
     * @see #setRows(int)
     */
    void setMax(final int max);
    

    /**
     * Returns the maximum number of results/documents that this query will return.
     * 
     * @see #getMax()
     * @return the maximum number of results/documents that this query will return.
     */
    int getRows();
    

    /**
     * Sets the maximum number of results/documents that this query will return.
     * <br>The maximum value is {@link #MAX}
     * @param rows the maximum number of results/documents that this query will return.
     * 
     * @see #setMax(int)
     */
    void setRows(final int rows);
    

    /**
     * Returns the number of the first element returned.
     * <br>The returned number is always a non-negative integer.
     * @return the number of the first element returned
     */
    int getStart();
    

    /**
     * Sets the index of the first element returned.
     * <br>If start is less than 0 or greater than {@value #MAX} then an {@link IllegalArgumentException}
     *   is thrown. 
     * @param start the index of the first element returned
     */
    void setStart(final int start);
    
    
    /**
     * Directly set a request argument.
     * <h4>The following request argument names are not allowed
     *    and must be set through their setters (or other methods):</h4>
     * <ul>
     * <li>q  --  use the addArgument-, addArrayArgument-, addUnescaped-, addField- and addFieldAsArray-methods
     * <li>start  --  {@link #setStart(int)}
     * <li>rows  --  {@link #setRows(int)}
     * </ul>
     * @param name the name of the request argument; must not be one of: q, start, rows
     * @param value the value of that argument
     */
    void setRequestArgument(final String name, final String value);
    
    
    /**
     * Add a facet field to this query.<br>
     * A facet field is a list of the count of each of the multiple values of a field.
     * It can be fetched from a SearchResult via the getFacetFields() method.
     * @param facetFieldName the facet field to add to this query; null is omitted
     */
    void addFacetField(final String facetFieldName);
    
    
    /**
     * Add multiple facet fields to this query.<br>
     * A facet field is a list of the count of each of the multiple values of a field.
     * It can be fetched from a SearchResult via the getFacetFields() method.
     * @param facetFields the facet fields to add to this query; null is omitted
     */
    void addFacetFields(final String... facetFields);
    
    
    /**
     * Returns the keys of the request arguments.
     * @return the keys of the request arguments
     */
    Set<Map.Entry<String, Object>> getRequestArgumentSet();
    
    
    /**
     * Returns the request arguments in an immutable Map.
     * <br>That means that no modifications on this SolrQuery are possible through this method.
     * @return the request arguments
     */
    Map<String, Object> getRequestArguments();
    

}
