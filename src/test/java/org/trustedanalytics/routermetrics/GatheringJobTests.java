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

import org.trustedanalytics.routermetrics.gathering.GatheringJob;
import org.trustedanalytics.routermetrics.gathering.adapters.gorouter.GorouterMetrics;
import org.trustedanalytics.routermetrics.gathering.adapters.gorouter.GorouterOperations;
import org.trustedanalytics.routermetrics.nats.DefaultGorouterAddressRetriever;
import org.trustedanalytics.routermetrics.storage.LoadStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GatheringJobTests {

    private GatheringJob sut;

    private static final String TEST_HOST = "test-host-1";

    @Mock
    private LoadStore loadStore;

    @Mock
    private GorouterOperations gorouterOperations;

    @Before
    public void setup() {
        sut = new GatheringJob(loadStore, gorouterOperations,
            new DefaultGorouterAddressRetriever(Arrays.asList(TEST_HOST)));
    }

    @Test
    public void run_gorouterReturnData_shouldGatherAndSaveInStore()
        throws ExecutionException, InterruptedException {

        final double TEST_VALUE = 0.3;
        final CompletableFuture<ResponseEntity<GorouterMetrics>> GOROUTER_ANSWER =
            gorouterAnswer(TEST_VALUE);

        when(gorouterOperations.getData(TEST_HOST)).thenReturn(GOROUTER_ANSWER);

        sut.run();

        verify(loadStore).save(TEST_VALUE);
    }

    private CompletableFuture<ResponseEntity<GorouterMetrics>> gorouterAnswer(double value)
        throws ExecutionException, InterruptedException {

        GorouterMetrics gorouterMetrics = new GorouterMetrics();
        gorouterMetrics.setRequestPerSec(value);
        ResponseEntity<GorouterMetrics> responseEntity =
            new ResponseEntity<>(gorouterMetrics, HttpStatus.OK);

        return CompletableFuture.completedFuture(responseEntity);
    }
}
