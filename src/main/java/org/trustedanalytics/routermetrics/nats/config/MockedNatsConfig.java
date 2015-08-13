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
package org.trustedanalytics.routermetrics.nats.config;

import org.trustedanalytics.routermetrics.nats.DefaultGorouterAddressRetriever;
import org.trustedanalytics.routermetrics.nats.GorouterAddressRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.List;

@Configuration
@Profile("default")
public class MockedNatsConfig {

    @Value("${services.gorouter.hosts}")
    private String gorouters;

    @Bean
    public GorouterAddressRetriever gorouterAddresses() {
        return new DefaultGorouterAddressRetriever(getDefaultAddresses());
    }

    private List<String> getDefaultAddresses() {
        return Arrays.asList(gorouters.split(" "));
    }
}
