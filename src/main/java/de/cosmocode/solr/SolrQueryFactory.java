package de.cosmocode.solr;


public final class SolrQueryFactory {
    
    
    // this is a static class, so no constructor needed
    private SolrQueryFactory() {};
    
    
    /**
     * Returns a default implementation of SolrQuery that is not thread-safe.<br>
     * It sets start to 0, max to {@link SolrQuery#MAX} and wildcarded to true.
     * 
     * @return a SolrQuery which is not thread-safe
     */
    public static SolrQuery createSolrQuery() {
        return new DefaultSolrQuery(0, SolrQuery.MAX, true);
    }
    
    
    /**
     * Returns a new SolrQuery that is not thread-safe and is initialized with the query: "+dtype_s:"+dtype.<br>
     * It sets start to 0, max to {@link SolrQuery#MAX} and wildcarded to true.
     * 
     * @param dtype the "dtype_s" field is automatically initialized with this value
     * @return a new SolrQuery which is not thread-safe
     */
    public static SolrQuery createSolrQuery(final String dtype) {
        return new DefaultSolrQuery(dtype, 0, SolrQuery.MAX, true);
    }
            
    

}
