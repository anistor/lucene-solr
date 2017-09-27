/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.solr.cloud.autoscaling.sim;

import java.io.IOException;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.cloud.autoscaling.ClusterDataProvider;
import org.apache.solr.client.solrj.cloud.autoscaling.DistribStateManager;
import org.apache.solr.client.solrj.cloud.autoscaling.SolrCloudDataProvider;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.SolrResponseBase;

/**
 * Simulated {@link SolrCloudDataProvider}.
 */
public class SimCloudDataProvider implements SolrCloudDataProvider {

  private final SimDistribStateManager stateManager;
  private final SimClusterDataProvider dataProvider;
  private final SimDistributedQueueFactory queueFactory;
  private SolrClient solrClient;
  private final SimHttpServer httpServer;

  public SimCloudDataProvider() {
    this.stateManager = new SimDistribStateManager();
    this.dataProvider = new SimClusterDataProvider();
    this.queueFactory = new SimDistributedQueueFactory();
    this.httpServer = new SimHttpServer();
  }

  public void setSolrClient(SolrClient solrClient) {
    this.solrClient = solrClient;
  }

  @Override
  public ClusterDataProvider getClusterDataProvider() {
    return dataProvider;
  }

  @Override
  public DistribStateManager getDistribStateManager() {
    return stateManager;
  }

  @Override
  public DistributedQueueFactory getDistributedQueueFactory() {
    return queueFactory;
  }

  @Override
  public SolrResponse request(SolrRequest req) throws IOException {
    if (solrClient != null) {
      try {
        return req.process(solrClient);
      } catch (SolrServerException e) {
        throw new IOException(e);
      }
    } else {
      return dataProvider.simHandleSolrRequest(req);
    }
  }

  @Override
  public byte[] httpRequest(String url, SolrRequest.METHOD method, Map<String, String> headers, String payload, int timeout, boolean followRedirects) throws IOException {
    return httpServer.httpRequest(url, method, headers, payload, timeout, followRedirects);
  }
}
