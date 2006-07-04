package de.cosmocode.solr;

public final class SolrQueryFactory {
    
    
    // this is a static class, so no constructor needed
    private SolrQueryFactory() {};
    
    
    /**
     * @return a SolrQuery which is not thread-safe
     */
    public static SolrQuery createSolrQuery() {
        return new DefaultSolrQuery();
    }
    
    
    /**
     * Returns a new SolrQuery that is not thread-safe and is initialized with the query: "+dtype_s:"+dtype.
     * @return a new SolrQuery which is not thread-safe
     * @param dtype the "dtype_s" field is automatically initialized with this value
     */
    public static SolrQuery createSolrQuery(final String dtype) {
        return new DefaultSolrQuery(dtype);
    }
    

}
