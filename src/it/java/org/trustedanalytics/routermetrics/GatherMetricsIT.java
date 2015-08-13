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

import org.trustedanalytics.routermetrics.gathering.adapters.gorouter.GorouterMetrics;
import org.trustedanalytics.routermetrics.storage.LoadStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.verification.VerificationMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:36888")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles({"integration-test", "gather-every-2sec"})
public class GatherMetricsIT {

    @Autowired
    private LoadStore loadStore;

    @Test
    public void appGatherDataEvery2Seconds_wait5Seconds_dataShouldBeDownloaded2to3times()
        throws InterruptedException {

        TimeUnit.SECONDS.sleep(5);

        assertThat(GorouterMock.firstGorouterCallCount, greaterThanOrEqualTo(2));
        assertThat(GorouterMock.firstGorouterCallCount, lessThanOrEqualTo(3));
        assertThat(GorouterMock.secondGorouterCallCount, greaterThanOrEqualTo(2));
        assertThat(GorouterMock.secondGorouterCallCount, lessThanOrEqualTo(3));

        verify(loadStore, invokedBetween(2, 3)).save(0.25);
    }

    private VerificationMode invokedBetween(int minNumberOfInvocations,
        int maxNumberOfInvocations) {

        return verificationData -> {
            atLeast(minNumberOfInvocations).verify(verificationData);
            atMost(maxNumberOfInvocations).verify(verificationData);
        };
    }

    @RestController
    public static class GorouterMock {

        public static int firstGorouterCallCount = 0;
        public static int secondGorouterCallCount = 0;

        @RequestMapping(value = "/test/gorouterMock/1/varz")
        public GorouterMetrics routerMetrics() {

            firstGorouterCallCount++;

            GorouterMetrics gorouterMetrics = new GorouterMetrics();
            gorouterMetrics.setRequestPerSec(0.1);
            return gorouterMetrics;
        }

        @RequestMapping(value = "/test/gorouterMock/2/varz")
        public GorouterMetrics routerMetrics2() {

            secondGorouterCallCount++;

            GorouterMetrics gorouterMetrics = new GorouterMetrics();
            gorouterMetrics.setRequestPerSec(0.15);
            return gorouterMetrics;
        }
    }

}
