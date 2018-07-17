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
 *
 */

package org.apache.skywalking.apm.collector.analysis.metric.provider.worker.service.heartbeat;

import org.apache.skywalking.apm.collector.analysis.metric.define.graph.MetricGraphIdDefine;
import org.apache.skywalking.apm.collector.analysis.worker.model.base.WorkerCreateListener;
import org.apache.skywalking.apm.collector.core.graph.*;
import org.apache.skywalking.apm.collector.core.module.ModuleManager;
import org.apache.skywalking.apm.collector.remote.RemoteModule;
import org.apache.skywalking.apm.collector.remote.service.RemoteSenderService;
import org.apache.skywalking.apm.collector.storage.table.register.ServiceName;

/**
 * @author peng-yongsheng
 */
public class ServiceNameHeartBeatGraph {

    private final ModuleManager moduleManager;
    private final WorkerCreateListener workerCreateListener;

    public ServiceNameHeartBeatGraph(ModuleManager moduleManager, WorkerCreateListener workerCreateListener) {
        this.moduleManager = moduleManager;
        this.workerCreateListener = workerCreateListener;
    }

    public void create() {
        RemoteSenderService remoteSenderService = moduleManager.find(RemoteModule.NAME).getService(RemoteSenderService.class);

        Graph<ServiceName> graph = GraphManager.INSTANCE.createIfAbsent(MetricGraphIdDefine.SERVICE_HEART_BEAT_PERSISTENCE_GRAPH_ID, ServiceName.class);

        graph.addNode(new ServiceNameAggregationWorker.Factory(moduleManager).create(workerCreateListener))
            .addNext(new ServiceNameHeartBeatRemoteWorker.Factory(moduleManager, remoteSenderService, MetricGraphIdDefine.SERVICE_HEART_BEAT_PERSISTENCE_GRAPH_ID).create(workerCreateListener))
            .addNext(new ServiceNameHeartBeatPersistenceWorker.Factory(moduleManager).create(workerCreateListener));
    }
}
