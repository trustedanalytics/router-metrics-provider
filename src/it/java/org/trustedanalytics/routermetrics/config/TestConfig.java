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
package org.trustedanalytics.routermetrics.config;

import org.trustedanalytics.routermetrics.gathering.GatheringJob;
import org.trustedanalytics.routermetrics.gathering.LatencyCollector;
import org.trustedanalytics.routermetrics.gathering.adapters.gorouter.GorouterClient;
import org.trustedanalytics.routermetrics.nats.DefaultGorouterAddressRetriever;
import org.trustedanalytics.routermetrics.storage.LoadStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.Arrays;

import static org.mockito.Mockito.mock;

@Configuration
@Profile("integration-test")
public class TestConfig {

    @Bean
    public LoadStore loadStore() {
        return mock(LoadStore.class);
    }

    @Bean
    public Runnable gatheringJob(LoadStore loadStore) {
        return new GatheringJob(loadStore, new GorouterClient(new AsyncRestTemplate(), null),
            new DefaultGorouterAddressRetriever(Arrays
                .asList("http://localhost:36888/test/gorouterMock/1",
                    "http://localhost:36888/test/gorouterMock/2")));
    }

    @Bean
    public LatencyCollector latencyCollector() {
        return mock(LatencyCollector.class);
    }
}
