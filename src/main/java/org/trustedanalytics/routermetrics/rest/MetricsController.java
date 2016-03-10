/**
 * Copyright (c) 2015 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trustedanalytics.routermetrics.rest;

import org.trustedanalytics.routermetrics.gathering.LatencyCollector;
import org.trustedanalytics.routermetrics.storage.LoadPerSecRecord;
import org.trustedanalytics.routermetrics.storage.LoadStore;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class MetricsController {

    public static final String LOAD_PER_SEC_URL = "/rest/metrics/load-data";
    public static final String LATENCY_HIST_URL = "/rest/metrics/latency-histograms";

    private final LoadStore loadStore;
    private final LatencyCollector latencyCollector;

    @Autowired
    public MetricsController(LoadStore loadStore, LatencyCollector latencyCollector) {
        this.loadStore = loadStore;
        this.latencyCollector = latencyCollector;
    }

    @ApiOperation("Get load per second time series")
    @RequestMapping(value = LOAD_PER_SEC_URL, method = RequestMethod.GET)
    public List<LoadPerSecRecord> loadChart() {
        return loadStore.read();
    }

    @ApiOperation("Get latency histograms")
    @RequestMapping(value = LATENCY_HIST_URL, method = RequestMethod.GET)
    public Map<String, Map<String, Double>> latencyHistogram() {
        return latencyCollector.getLatencyHistograms();
    }
}
