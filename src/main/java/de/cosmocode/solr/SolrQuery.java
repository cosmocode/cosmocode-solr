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
    public static final int MAX = 10000000;
    
    
    /**
     * @return the selected fields for the query - cannot be null, but "*" indicates that all are selected.
     */
    public String getSelectFields();
    
    
    /**
     * @param fields the fields returned by the query - can be null; null indicates that all are selected.
     */
    public void selectFields(final String... fields);
    

    /**
     * @return the fields on which the result is sorted on - can be null.
     */
    public String getSortFields();
    

    /**
     * @param sortFields the fields on which the result is sorted on - can be null.
     */
    public void sortFields(String... sortFields);
    

    /**
     * @return the maximum number of results/documents that this query will return.
     */
    public int getMax();
    

    /** 
     * @param max the maximum number of results/documents that this query will return.
     */
    public void setMax(final int max);
    

    /**
     * @return the maximum number of results/documents that this query will return.
     */
    public int getRows();
    

    /**
     * @param rows the maximum number of results/documents that this query will return.
     */
    public void setRows(final int rows);
    

    /**
     * @return the number of the first element returned
     */
    public int getStart();
    

    /**
     * @param start the number of the first element returned
     */
    public void setStart(final int start);
    
    
    /**
     * Directly set a request argument.
     * <h4>The following request argument names are not allowed and must be set through their setters (or other methods):</h4>
     * <ul>
     * <li>q  --  use the addArgument-, addArrayArgument-, addUnescaped-, addField- and addFieldAsArray-methods
     * <li>start  --  {@link #setStart(int)}
     * <li>rows  --  {@link #setRows(int)}
     * </ul>
     * @param name the name of the request argument; must not be one of: q, start, rows
     * @param value the value of that argument
     */
    public void setRequestArgument(final String name, final String value);
    
    
    /**
     * Add a facet field to this query.<br>
     * A facet field is a list of the count of each of the multiple values of a field.
     * It can be fetched from a SearchResult via the getFacetFields() method.
     * @param facetFieldName the facet field to add to this query; null is omitted
     */
    public void addFacetField (final String facetFieldName);
    
    
    /**
     * Add multiple facet fields to this query.<br>
     * A facet field is a list of the count of each of the multiple values of a field.
     * It can be fetched from a SearchResult via the getFacetFields() method.
     * @param facetFields the facet fields to add to this query; null is omitted
     */
    public void addFacetFields (final String... facetFields);
    
    
    /**
     * @return the keys of the request arguments
     */
    public Set<Map.Entry<String, Object>> getRequestArgumentSet();
    
    
    /**
     * @return the request arguments in an immutable Map 
     * (no modifications on this SolrQuery possible)
     */
    public Map<String, Object> getRequestArguments();
    

}
