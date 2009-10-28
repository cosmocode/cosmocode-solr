package de.cosmocode.solr;

public final class SolrQueryFactory {
    
    
    // this is a static class, so no constructor needed
    private SolrQueryFactory() {};
    
    
    /**
     * @return a SolrQuery which is not thread-safe
     */
    public static SolrQuery getDefaultSolrQuery() {
        return new DefaultSolrQuery();
    }
    
    
    /**
     * @return a SolrQuery which is not thread-safe
     * @param dtype the "dtype_s" field is automatically initialized with this value
     */
    public static SolrQuery getConsecutiveSolrQuery(final String dtype) {
        return new DefaultSolrQuery(dtype);
    }
    

}
