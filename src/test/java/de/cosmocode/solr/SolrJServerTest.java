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

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrConfig;
import org.apache.solr.core.SolrCore;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.cosmocode.lucene.LuceneHelper;

/**
 * Tests the {@link SolrJQuery} on an embedded solr server.
 *
 * @author Oliver Lorenz
 */
public class SolrJServerTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(SolrJServerTest.class);
    
    /*
     * Solr embedding comes from the TLF project, module: user-api
     * (thanks to Willi :))
     */
    
    private static SolrCore core;
    private static SolrServer server;
    
    /**
     * Starts the Solr server before all tests run.
     * 
     * @throws SAXException if an error occurs
     * @throws IOException if an error occurs
     * @throws ParserConfigurationException if an error occurs
     */
    @BeforeClass
    public static void beforeClass() throws ParserConfigurationException, IOException, SAXException {
        LOG.debug("Starting Solr Server...");
        
        final File solrDirectory = new File("src/test/resources/solr");
        final File xml = new File(solrDirectory, "conf/solrconfig.xml");
        final File data = new File(solrDirectory, "data");
        final File index = new File(data, "index");
        
        final CoreContainer container = new CoreContainer();
        final SolrConfig config = new SolrConfig(solrDirectory.getAbsolutePath(), xml.getAbsolutePath(), null);
        final CoreDescriptor descriptor = new CoreDescriptor(container, "core1", index.getAbsolutePath());
        
        core = new SolrCore("core1", data.getAbsolutePath(), config, null, descriptor);
        container.register("core1", core, false);
        server = new EmbeddedSolrServer(container, "core1");
        
        LOG.debug("Started Solr Server");
    }
    
    /**
     * Stops the Solr server after all tests are run.
     */
    @AfterClass
    public static void afterClass() {
        LOG.debug("Stopping Solr Server...");
        core.close();
        LOG.debug("Stopped Solr Server");
    }
    
    
    /*
     * Start of real tests, after solr is up and running
     */
    
    /**
     * Tests the integration of the SolrJQuery into the EmbeddedSolrServer.
     * @throws SolrServerException if some exception occurred in the solr server
     */
    @Test
    public void testServer() throws SolrServerException {
        final SolrJQuery query = SolrQueryFactory.createSolrJQuery();
        query.addField("dtype_s", "city", LuceneHelper.MOD_ID);
        final QueryResponse response = server.query(query.getSolrJ());
        LOG.debug("SolrServer running, got {} results", response.getResults().size());
    }

}
