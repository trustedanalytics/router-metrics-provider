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

import org.trustedanalytics.routermetrics.gathering.LatencyCollector;
import org.trustedanalytics.routermetrics.rest.MetricsController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles({"integration-test", "disable-gathering"})
public class LatencyMetricsIT {

    @Value("http://localhost:${local.server.port}")
    private String BASE_URL;

    @Autowired
    private LatencyCollector latencyCollector;

    private Map<String, Map<String, Double>> histogramsToBeReturned;

    @Before
    public void setup() {
        Map<String, Double> histogram1 = new HashMap<>();
        histogram1.put("50", 0.1);
        histogram1.put("75", 0.2);
        Map<String, Double> histogram2 = new HashMap<>();
        histogram2.put("50", 0.11);
        histogram2.put("75", 0.27);

        histogramsToBeReturned = new HashMap<>();
        histogramsToBeReturned.put("gorouter1", histogram1);
        histogramsToBeReturned.put("gorouter1", histogram2);
    }

    @Test
    public void callLoadPerSecEndpoint_noInputRequired_shouldReturnLoadData() {

        String url = BASE_URL + MetricsController.LATENCY_HIST_URL;

        when(latencyCollector.getLatencyHistograms()).thenReturn(histogramsToBeReturned);

        TestRestTemplate testRestTemplate = new TestRestTemplate();
        ResponseEntity<Map> response =
            testRestTemplate.getForEntity(url, Map.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), equalTo(histogramsToBeReturned));
    }
}
