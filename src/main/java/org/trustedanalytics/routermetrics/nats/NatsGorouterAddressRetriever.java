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
package org.trustedanalytics.routermetrics.nats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import nats.client.Message;
import nats.client.Nats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NatsGorouterAddressRetriever implements GorouterAddressRetriever {

    private static final Logger LOG = LoggerFactory.getLogger(NatsGorouterAddressRetriever.class);

    private static final String NATS_ROUTER_GREET = "router.greet";
    private static final String NATS_ROUTER_REGISTER = "trustedanalytics.metrics.router";

    private Collection<String> addresses;
    private ObjectMapper mapper;

    public NatsGorouterAddressRetriever(Nats nats) {
        this.addresses = Collections.synchronizedList(new ArrayList<>());
        this.mapper = new ObjectMapper();

        nats.subscribe(NATS_ROUTER_REGISTER, this::onMessageArrived);
        nats.publish(NATS_ROUTER_GREET, "", NATS_ROUTER_REGISTER);
    }

    public void onMessageArrived(Message msg) {
        String body = msg.getBody();
        LOG.info("gorouter greet message: " + body);

        try {
            GorouterGreetMsg gorouterGreetMsg = mapper.readValue(body, GorouterGreetMsg.class);
            addresses.add(gorouterGreetMsg.host());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override public Collection<String> getAddresses() {
        LOG.info("addresses: " + addresses);
        return addresses;
    }
}
