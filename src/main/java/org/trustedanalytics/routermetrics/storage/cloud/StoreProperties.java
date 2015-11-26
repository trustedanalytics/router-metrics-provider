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
package org.trustedanalytics.routermetrics.storage.cloud;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(StoreProperties.PREFIX)
@Data
public class StoreProperties {

    protected static final String PREFIX = "services.store";

    private String baseUrl; // NOSONAR
    private String apiPort; // NOSONAR
    private String username; // NOSONAR
    private String password; // NOSONAR
    private String groupingInterval; // NOSONAR
    private String timeLimit; // NOSONAR
}
