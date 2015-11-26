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

import org.trustedanalytics.routermetrics.storage.LoadPerSecRecord;
import org.trustedanalytics.routermetrics.storage.LoadStore;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Serie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

public class InfluxLoadStore implements LoadStore {

    private static final Logger LOG = LoggerFactory.getLogger(InfluxLoadStore.class);

    private static final String DATABASE_NAME = "trustedanalytics-global-metrics";
    private static final String SERIE_NAME = "gorouterMetrics";
    private static final String COLUMN_NAME = "loadPerSec";
    private final String groupingInterval;
    private final String timeLimit;

    private InfluxDB store;

    public InfluxLoadStore(InfluxDB store, String groupingInterval, String timeLimit) {
        this.store = store;
        this.groupingInterval = groupingInterval;
        this.timeLimit = timeLimit;
        initializeDatabase();
    }

    public InfluxLoadStore(String apiUrl, String username, String password, String groupingInterval,
        String timeLimit) {
        this(InfluxDBFactory.connect(apiUrl, username, password), groupingInterval, timeLimit);
    }

    private void initializeDatabase() {
        if (!databaseExists()) {
            createDatabase();
        }
    }

    private boolean databaseExists() {
        LOG.debug("Check if database exists.");
        return store.describeDatabases().stream().filter(d -> d.getName().equals(DATABASE_NAME))
            .count() > 0;
    }

    private void createDatabase() {
        LOG.debug("Creating database.");
        store.createDatabase(DATABASE_NAME);
    }

    @Override public void save(double value) {
        save(SERIE_NAME, COLUMN_NAME, value);
    }

    private void save(String serieName, String key, double value) {
        LOG.debug("Save value '{}'='{}' in serie '{}'",
            key, String.valueOf(value), serieName);

        Serie serie = new Serie.Builder(serieName)
            .columns(key)
            .values(value)
            .build();

        write(serie);
    }

    private void write(Serie serie) {
        store.write(DATABASE_NAME, TimeUnit.MILLISECONDS, serie);
    }

    @Override public List<LoadPerSecRecord> read() {
        return read(SERIE_NAME, COLUMN_NAME, groupingInterval, timeLimit);
    }

    private List<LoadPerSecRecord> read(String serieName, String key, String groupingInterval,
        String timeLimit) {

        String query = String.format("select mean(%s) from %s where time > now() - %s group by time (%s)",
            key, serieName, timeLimit, groupingInterval);
        LOG.debug(query);

        List<Serie> queryResult = store.query(DATABASE_NAME, query, TimeUnit.MILLISECONDS);
        LOG.debug("{} series read", queryResult.size());
        LOG.debug("{} rows read in first serie", queryResult.get(0).getRows().size());

        return queryResult.get(0).getRows().stream()
            .map(row -> new LoadPerSecRecord((Double) row.get("time"), (Double) row.get("mean")))
            .collect(toList());
    }
}
