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
import org.trustedanalytics.routermetrics.storage.LoadStore;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class GatheringJob implements Runnable {

    private final LoadStore loadStore;
    private final GorouterOperations gorouterOperations;
    private GorouterAddressRetriever gorouterAddresses;

    public GatheringJob(LoadStore loadStore, GorouterOperations gorouterOperations,
        GorouterAddressRetriever gorouterAddresses) {

        this.loadStore = loadStore;
        this.gorouterOperations = gorouterOperations;
        this.gorouterAddresses = gorouterAddresses;
    }

    @Override public void run() {
        List<CompletableFuture<GorouterMetrics>> futures =
            AsyncDownloader.getMetrics(gorouterOperations, gorouterAddresses);

        List<GorouterMetrics> metrics =
            futures.stream().map(CompletableFuture::join).collect(toList());

        loadStore.save(aggregateMetrics(metrics));
    }

    private double aggregateMetrics(List<GorouterMetrics> metrics) {
        return metrics.stream()
            .collect(Collectors.summarizingDouble(GorouterMetrics::getRequestPerSec)).getSum();
    }
}
