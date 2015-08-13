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
package org.trustedanalytics.routermetrics;

import com.google.common.collect.ImmutableMap;
import org.trustedanalytics.routermetrics.gathering.GorouterLatencyCollector;
import org.trustedanalytics.routermetrics.gathering.adapters.gorouter.GorouterMetrics;
import org.trustedanalytics.routermetrics.gathering.adapters.gorouter.GorouterOperations;
import org.trustedanalytics.routermetrics.nats.DefaultGorouterAddressRetriever;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GorouterLatencyCollectorTests {

    private GorouterLatencyCollector sut;

    private static final String TEST_HOST = "test-host-1";

    @Mock
    private GorouterOperations gorouter;

    @Before
    public void setup() {
        sut = new GorouterLatencyCollector(gorouter,
            new DefaultGorouterAddressRetriever(Arrays.asList(TEST_HOST)));
    }

    @Test
    public void getLatencyHistograms_gorouterReturnData_shouldCollectData()
        throws ExecutionException, InterruptedException {

        final ImmutableMap<String, Double> TEST_HISTOGRAM = ImmutableMap.of("50", 1.0, "75", 0.2);
        final CompletableFuture<ResponseEntity<GorouterMetrics>> GOROUTER_ANSWER =
            gorouterAnswer(TEST_HOST, TEST_HISTOGRAM);

        when(gorouter.getData(TEST_HOST)).thenReturn(GOROUTER_ANSWER);

        Map<String, Map<String, Double>> actualHistograms = sut.getLatencyHistograms();

        assertEquals(ImmutableMap.of(TEST_HOST, TEST_HISTOGRAM), actualHistograms);
    }

    private CompletableFuture<ResponseEntity<GorouterMetrics>> gorouterAnswer(String hostname,
        ImmutableMap<String, Double> latencyHistogram)

        throws ExecutionException, InterruptedException {

        GorouterMetrics gorouterMetrics = new GorouterMetrics();
        gorouterMetrics.setHost(hostname);
        gorouterMetrics.setLatency(latencyHistogram);
        ResponseEntity<GorouterMetrics> responseEntity =
            new ResponseEntity<>(gorouterMetrics, HttpStatus.OK);

        return CompletableFuture.completedFuture(responseEntity);
    }
}
