package de.cosmocode.solr;

import de.cosmocode.solr.impl.AppendingSolrQuery;


public final class SolrQueryFactory {
    
    
    // this is a static class, so no constructor needed
    private SolrQueryFactory() {};
    
    
    /**
     * @return a SolrQuery which is not thread-safe
     */
    public static SolrQuery getConsecutiveSolrQuery() {
        return new AppendingSolrQuery();
    }
    
    
    /**
     * @return a SolrQuery which is not thread-safe
     * @param dtype the "dtype_s" field is automatically initialized with this value
     */
    public static SolrQuery getConsecutiveSolrQuery(final String dtype) {
        return new AppendingSolrQuery(dtype);
    }
    
    
    /**
     * <b>Attention</b>: This method is not yet implemented.
     * @return a concurrent SolrQuery, which can be shared across threads.
     */
    public SolrQuery getConcurrentSolrQuery() {
        throw new UnsupportedOperationException("not yet implemented");
    }
    

}
