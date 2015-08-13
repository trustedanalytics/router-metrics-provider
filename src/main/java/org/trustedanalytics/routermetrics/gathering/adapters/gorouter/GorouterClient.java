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

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.AsyncRestOperations;

import java.util.concurrent.CompletableFuture;

import static net.javacrumbs.futureconverter.springjava.FutureConverter.toCompletableFuture;

public class GorouterClient implements GorouterOperations {

    private static final String PATH = "/varz";
    private final AsyncRestOperations template;
    private final HttpEntity<byte[]> entity;

    public GorouterClient(AsyncRestOperations template, HttpEntity<byte[]> entity) {
        this.template = template;
        this.entity = entity;
    }

    @Override public CompletableFuture<ResponseEntity<GorouterMetrics>> getData(String host) {
        return toCompletableFuture(
            template.exchange(host + PATH, HttpMethod.GET, entity, GorouterMetrics.class));
    }
}
