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

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.cosmocode.junit.UnitProvider;
import de.cosmocode.lucene.LuceneHelper;
import de.cosmocode.lucene.LuceneQueryTest;

/**
 * Tests {@link SolrJQuery}.
 *
 * @author Oliver Lorenz
 */
@RunWith(Suite.class)
@SuiteClasses(LuceneQueryTest.class)
public final class SolrJQueryTest implements UnitProvider<SolrJQuery> {
    
    @Override
    public SolrJQuery unit() {
        return new SolrJQuery(0, SolrQuery.MAX, LuceneHelper.newQuery());
    }
    
    /**
     * Sets up this class as the current class to test.
     * Unset happens automatically.
     */
    @BeforeClass
    public static void setupProvider() {
        LuceneQueryTest.setUnitProvider(SolrJQueryTest.class);
    }

}
