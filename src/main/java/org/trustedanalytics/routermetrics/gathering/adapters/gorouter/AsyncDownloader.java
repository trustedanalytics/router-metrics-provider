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
package org.trustedanalytics.routermetrics.gathering.adapters.gorouter;

import org.trustedanalytics.routermetrics.nats.GorouterAddressRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;

public class AsyncDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncDownloader.class);

    private AsyncDownloader() {
    }

    public static List<CompletableFuture<GorouterMetrics>> getMetrics(
        GorouterOperations gorouterOperations, GorouterAddressRetriever gorouterAddresses) {

        LOG.debug("Gathering metrics triggered.");

        List<CompletableFuture<GorouterMetrics>> futures =
            gorouterAddresses.getAddresses().stream().map(
                host -> downloadTask(gorouterOperations, host)).collect(toList());

        LOG.debug("Futures created.");
        return futures;
    }

    private static CompletableFuture<GorouterMetrics> downloadTask(GorouterOperations router,
        String host) {

        LOG.debug("Starting download from '{}'", host);

        return router.getData(host)
            .thenApply(response -> {
                GorouterMetrics metrics = response.getBody();
                metrics.setHost(host);
                LOG.debug("Downloaded: '{}'", metrics);
                return metrics;
            });
    }
}
