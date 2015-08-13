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
package org.trustedanalytics.routermetrics.gathering;

import org.trustedanalytics.routermetrics.gathering.adapters.gorouter.AsyncDownloader;
import org.trustedanalytics.routermetrics.gathering.adapters.gorouter.GorouterMetrics;
import org.trustedanalytics.routermetrics.gathering.adapters.gorouter.GorouterOperations;
import org.trustedanalytics.routermetrics.nats.GorouterAddressRetriever;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GorouterLatencyCollector implements LatencyCollector {

    private GorouterOperations template;
    private GorouterAddressRetriever hosts;

    public GorouterLatencyCollector(GorouterOperations template, GorouterAddressRetriever hosts) {
        this.template = template;
        this.hosts = hosts;
    }

    @Override
    public Map<String, Map<String, Double>> getLatencyHistograms() {
        List<CompletableFuture<GorouterMetrics>> futures =
            AsyncDownloader.getMetrics(template, hosts);

        return futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toMap(GorouterMetrics::getHost, GorouterMetrics::getLatency));
    }
}
