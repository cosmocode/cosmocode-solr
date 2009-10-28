package de.cosmocode.solr;


public final class SolrQueryFactory {
    
    
    // just a static class
    private SolrQueryFactory() {};
    
    
    /**
     * @return a SolrQuery which is not thread-safe
     */
    public static SolrQuery getConsecutiveSolrQuery() {
        return new AppendingSolrQuery();
    }
    
    
    /**
     * @return a SolrQuery which is not thread-safe
     * @param dtype the data type that is automatically set
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
