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

import org.trustedanalytics.routermetrics.gathering.adapters.gorouter.GorouterClient;
import org.trustedanalytics.routermetrics.nats.GorouterAddressRetriever;
import org.trustedanalytics.routermetrics.storage.LoadStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.AsyncRestTemplate;

import java.nio.charset.Charset;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

@Configuration
@Profile({"cloud", "default"})
public class GatheringConfig {

    @Value("${gathering.triggerExpression}")
    private String triggerExpression;

    @Autowired
    private GorouterProperties gorouterProperties;

    @Bean
    public String triggerExpression() {
        return triggerExpression;
    }

    @Bean
    public LatencyCollector latencyCollector(GorouterAddressRetriever gorouterAddresses) {
        return new GorouterLatencyCollector(getGorouterClient(), gorouterAddresses);
    }

    @Bean
    public Runnable gatheringJob(LoadStore loadStore, GorouterAddressRetriever gorouterAddresses) {
        return new GatheringJob(loadStore, getGorouterClient(), gorouterAddresses);
    }

    private GorouterClient getGorouterClient() {
        return new GorouterClient(getAsyncRestTemplate(), getGorouterHeaders());
    }

    private AsyncRestTemplate getAsyncRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setTaskExecutor(new SimpleAsyncTaskExecutor());
        factory.setConnectTimeout(gorouterProperties.getConnectTimeout());
        factory.setReadTimeout(gorouterProperties.getReadTimeout());

        return new AsyncRestTemplate(factory);
    }

    private HttpEntity<byte[]> getGorouterHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + new String(encodeBase64(
            (gorouterProperties.getUsername() + ":" + gorouterProperties.getPassword())
                .getBytes(Charset.forName("US-ASCII")))));
        return new HttpEntity<>(headers);
    }
}
