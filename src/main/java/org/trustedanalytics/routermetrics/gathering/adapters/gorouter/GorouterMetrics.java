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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GorouterMetrics {

    private double requestPerSec;
    private Map<String, Double> latency = new HashMap<>();

    @JsonIgnoreProperties
    private String host;

    @JsonProperty("requests_per_sec")
    public void setRequestPerSec(double requestPerSec) {
        this.requestPerSec = requestPerSec;
    }

    public double getRequestPerSec() {
        return requestPerSec;
    }

    public Map<String, Double> getLatency() {
        return latency;
    }

    public void setLatency(Map<String, Double> latency) {
        this.latency = latency;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override public String toString() {

        String latencyToString = latency.entrySet().stream()
            .map(x -> String.format("'%s'='%s'", x.getKey(), x.getValue()))
            .collect(Collectors.joining(", "));

        return "GorouterMetrics{" +
            "host='" + host + "'," +
            "requestPerSec=" + requestPerSec +
            ", latency={" + latencyToString + "}" +
            "}";
    }
}
