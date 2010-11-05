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

import de.cosmocode.lucene.LuceneHelper;
import de.cosmocode.lucene.LuceneQuery;
import de.cosmocode.lucene.LuceneQueryBuilder;
import de.cosmocode.lucene.QueryModifier;

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
        return new DefaultSolrQuery(0, SolrQuery.MAX, LuceneHelper.newQuery());
    }
    
    
    /**
     * <p> Returns a default implementation of SolrQuery that is not thread-safe.<br>
     * It sets start to 0, max to {@link SolrQuery#MAX} and wildcarded to true.
     * It also appends a field named "dtype_s" with the specified parameter as value.
     * </p>
     * <p> This method is deprecated, because it does nothing more than calling <br />
     * <code>query.addField("dtype_s", dtype, LuceneHelper.MOD_ID);</code>. <br />
     * If you need some generic builder method that always returns the same query try the following:
     * </p>
     * <pre>

import de.cosmocode.lucene.LuceneHelper;
import de.cosmocode.lucene.LuceneQueryBuilder;

...
    
    private static final LuceneQueryBuilder BUILDER;
    
    static {
        BUILDER = new LuceneQueryBuilder();
        BUILDER.addField("dtype_s", "shop", LuceneHelper.MOD_ID);
        BUILDER.lock();
    }
    
    public SolrQuery create() {
        return SolrQueryFactory.createSolrQuery(BUILDER.build());
    }
     * </pre>
     * 
     * @deprecated use a {@link LuceneQueryBuilder} with {@link #createSolrQuery(LuceneQuery)} instead
     * @param dtype the dtype_s
     * @return a SolrQuery which is not thread-safe
     */
    @Deprecated
    public static SolrQuery createSolrQuery(final String dtype) {
        final SolrQuery query = createSolrQuery();
        query.addField("dtype_s", dtype, LuceneQuery.MOD_ID);
        return query;
    }
    
    
    /**
     * <p> Returns a default implementation of SolrQuery.
     * It delegates every method that belongs to LuceneQuery
     * to the given implementation.
     * </p>
     * <p> start is set to 0, max is set to {@link SolrQuery#MAX}.
     * </p>
     * 
     * @param luceneQuery the delegate for all LuceneQuery related methods
     * @return a SolrQuery that delegates its methods to the given LuceneQuery
     */
    public static SolrQuery createSolrQuery(final LuceneQuery luceneQuery) {
        return new DefaultSolrQuery(0, SolrQuery.MAX, luceneQuery);
    }
    
    
    /**
     * <p> Returns an implementation of SolrQuery that is backed by the
     * org.apache.solr.client.solrj.SolrQuery
     * implementation. This can be used to search with SolrJ
     * while providing the methods of the SolrQuery interface.
     * </p>
     * @return a new SolrJQuery
     */
    public static SolrJQuery createSolrJQuery() {
        return new SolrJQuery(0, SolrQuery.MAX, LuceneHelper.newQuery());
    }
    
    
    /**
     * <p> Returns an implementation of SolrQuery that is backed by the
     * org.apache.solr.client.solrj.SolrQuery
     * implementation. This can be used to search with SolrJ
     * while providing the methods of the SolrQuery interface.
     * </p>
     * <p> Every method that belongs to LuceneQuery is delegated to the given implementation.
     * </p>
     * 
     * @param query the delegate for all LuceneQuery related methods
     * @return a new SolrJQuery, backed by the given LuceneQuery
     */
    public static SolrJQuery createSolrJQuery(final LuceneQuery query) {
        return new SolrJQuery(0, SolrQuery.MAX, query);
    }
    
    
    /**
     * <p> Returns an implementation of SolrQuery that is backed by the
     * org.apache.solr.client.solrj.SolrQuery
     * implementation. This can be used to search with SolrJ
     * while providing the methods of the SolrQuery interface.
     * </p>
     * <p> The query is initialized with the given QueryModifier.
     * This has the same effect as calling {@link SolrQuery#setModifier(QueryModifier)}
     * on {@link #createSolrJQuery()}.
     * </p>
     * 
     * @param mod the default modifier for the SolrQuery
     * @return a new SolrJQuery with the given QueryModifier
     */
    public static SolrJQuery createSolrJQuery(final QueryModifier mod) {
        final SolrJQuery query = createSolrJQuery();
        query.setModifier(mod);
        return query;
    }

}
