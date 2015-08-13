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

import org.trustedanalytics.routermetrics.rest.MetricsController;
import org.trustedanalytics.routermetrics.storage.LoadPerSecRecord;
import org.trustedanalytics.routermetrics.storage.LoadStore;
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

import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles({"integration-test", "disable-gathering"})
public class LoadMetricsIT {

    @Value("http://localhost:${local.server.port}")
    private String BASE_URL;

    @Autowired
    private LoadStore loadStore;

    @Test
    public void callLoadPerSecEndpoint_noInputRequired_shouldReturnLoadData() {

        String url = BASE_URL + MetricsController.LOAD_PER_SEC_URL;

        LoadPerSecRecord[] RECORDS_TO_BE_RETURNED = {
            new LoadPerSecRecord(new Date(), 0.1),
            new LoadPerSecRecord(new Date(), 0.2)};
        when(loadStore.read()).thenReturn(Arrays.asList(RECORDS_TO_BE_RETURNED));

        TestRestTemplate testRestTemplate = new TestRestTemplate();
        ResponseEntity<LoadPerSecRecord[]> response =
            testRestTemplate.getForEntity(url, LoadPerSecRecord[].class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), equalTo(RECORDS_TO_BE_RETURNED));
    }
}
