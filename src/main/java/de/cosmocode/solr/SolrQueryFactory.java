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

/**
 * This Factory creates {@link SolrQuery}s.
 * 
 * @author olorenz
 */
public final class SolrQueryFactory {
    
    
    // this is a static class, so no constructor needed
    private SolrQueryFactory() {
    }
    
    
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
