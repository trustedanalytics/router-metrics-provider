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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Scheduler {

    private static final Logger LOG = LoggerFactory.getLogger(Scheduler.class);

    private final String triggerExpression;
    private final Runnable gatheringJob;

    @Autowired
    public Scheduler(String triggerExpression, Runnable gatheringJob) {
        this.triggerExpression = triggerExpression;
        this.gatheringJob = gatheringJob;
    }

    @PostConstruct
    public void start() {
        TaskScheduler s = new DefaultManagedTaskScheduler();
        s.schedule(gatheringJob::run, new CronTrigger(triggerExpression));
        LOG.info("Scheduler started {}", triggerExpression);
    }
}
