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

import org.junit.Assert;
import org.junit.Test;

import de.cosmocode.lucene.LuceneHelper;
import de.cosmocode.lucene.LuceneQueryBuilder;

/**
 * Small test for {@link SolrQueryFactory} and {@link SolrQuery}.
 *
 * @author Oliver Lorenz
 */
public class SolrQueryFactoryTest {
    
    private static final LuceneQueryBuilder BUILDER;
    
    static {
        BUILDER = new LuceneQueryBuilder();
        BUILDER.addField("dtype_s", "shop", LuceneHelper.MOD_ID);
        BUILDER.lock();
    }
    
    /**
     * Creates a new SolrQuery with the dtype set to "shop".
     * @return a new SolrQuery with dtype "shop"
     */
    public SolrQuery create() {
        return SolrQueryFactory.createSolrQuery(BUILDER.build());
    }
    
    /**
     * Tests a non-deprecated equivalent to {@link SolrQueryFactory#createSolrQuery(String)}.
     */
    @Test
    public void testCreate() {
        final SolrQuery query = create();
        Assert.assertEquals("+dtype_s:((shop) )  ", query.getQuery());
    }
    
    /**
     * Tests {@link SolrQueryFactory#createSolrQuery(String)}.
     */
    @Test
    public void testCreateSolrQueryDtype() {
        final SolrQuery query = SolrQueryFactory.createSolrQuery("shop");
        Assert.assertEquals("+dtype_s:((shop) ) ", query.getQuery());
    }

}
