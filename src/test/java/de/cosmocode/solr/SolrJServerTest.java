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

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrConfig;
import org.apache.solr.core.SolrCore;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

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
    
    private static File tmpData;
    private static File tmpIndex;
    
    /**
     * Starts the Solr server before all tests run.
     * 
     * @throws SAXException if an error occurs
     * @throws IOException if an error occurs
     * @throws ParserConfigurationException if an error occurs
     */
    @BeforeClass
    public static void startServer() throws ParserConfigurationException, IOException, SAXException {
        LOG.debug("Starting Solr Server...");
        
        final File solrDirectory = new File("src/test/resources/solr");
        final File xml = new File(solrDirectory, "conf/solrconfig.xml");
        final File data = new File(solrDirectory, "data");
        
        tmpData = new File(solrDirectory, "tmpData");
        FileUtils.copyDirectory(data, tmpData);
        tmpIndex = new File(tmpData, "index");
        
        final CoreContainer container = new CoreContainer();
        final SolrConfig config = new SolrConfig(solrDirectory.getAbsolutePath(), xml.getAbsolutePath(), null);
        final CoreDescriptor descriptor = new CoreDescriptor(container, "core1", tmpIndex.getAbsolutePath());
        
        core = new SolrCore("core1", tmpData.getAbsolutePath(), config, null, descriptor);
        container.register("core1", core, false);
        server = new EmbeddedSolrServer(container, "core1");
        
        LOG.debug("Started Solr Server");
    }
    
    /**
     * Stops the Solr server after all tests are run.
     */
    @AfterClass
    public static void stopServer() {
        LOG.debug("Stopping Solr Server...");
        core.close();
        LOG.debug("Stopped Solr Server");
        
        // cleanup temporary data directory
        final boolean tmpDeleted = FileUtils.deleteQuietly(tmpData);
        if (tmpDeleted) {
            LOG.debug("Temporary data directory deleted");
        } else {
            LOG.warn("Could not delete temporary data directory");
        }
    }
    
    
    /*
     * Start of real tests, after solr is up and running
     */
    
    /**
     * Tests the integration of the SolrJQuery into the EmbeddedSolrServer.
     * @throws SolrServerException if some exception occurred in the solr server
     */
    @Test
    public void query() throws SolrServerException {
        final SolrJQuery query = SolrQueryFactory.createSolrJQuery();
        query.addField("dtype_s", "city", SolrQuery.MOD_ID);
        LOG.debug("Sent query {} with maximum {}", query.getQuery(), query.getMax());
        final QueryResponse response = server.query(query.getSolrJ());
        LOG.debug("SolrServer running, got {} results", response.getResults().size());
        Assert.assertEquals(1012, response.getResults().size());
    }
    
    /**
     * Tests the integration of the SolrJQuery into the EmbeddedSolrServer.
     * Tests SolrJQuery with start set to MAX.
     * @throws SolrServerException if some exception occurred in the solr server
     */
    @Test
    public void queryWithStart() throws SolrServerException {
        final SolrJQuery query = SolrQueryFactory.createSolrJQuery();
        query.setStart(SolrQuery.MAX);
        query.addField("dtype_s", "city", SolrQuery.MOD_ID);
        LOG.debug("Sent query {} with start: {} and max: {}", 
            new Object[] {query.getQuery(), query.getStart(), query.getMax()});
        final QueryResponse response = server.query(query.getSolrJ());
        LOG.debug("SolrServer running, got {} results", response.getResults().size());
        Assert.assertEquals(0, response.getResults().size());
    }
    
    /**
     * <p> Tests adding a solr document and reading it with SolrJ afterwards.
     * </p>
     * <p> After the test has run it deletes the created document
     * to provide a clean solr instance for the next test.
     * </p>
     * 
     * @throws SolrServerException if some exception occurred in the solr server
     * @throws IOException if some low-level exception occurred (should not happen)
     */
    @Test
    public void addAndQuery() throws SolrServerException, IOException {
        final String newId = "new_element_id_" + Math.floor(Math.random() * 10000);
        
        try {
            // add document
            final SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", newId);
            doc.addField("dynamic_t", "Blubbs, ein Text ... {look here}");
            doc.addField("other_id_s", "12345");
            server.add(doc);
            server.commit();
            
            // read document
            final SolrJQuery query = SolrQueryFactory.createSolrJQuery();
            query.addField("dynamic_t", "here}", SolrQuery.MOD_TEXT);
            query.addField("other_id_s", "12345", SolrQuery.MOD_ID);
            final QueryResponse response = server.query(query.getSolrJ());
            Assert.assertEquals("false id", newId, response.getResults().get(0).get("id"));
            Assert.assertEquals("Too many documents", 1, response.getResults().size());
            
        } finally {
            // delete document
            server.deleteById(newId);
            server.commit();
            server.optimize();
        }
    }

}
