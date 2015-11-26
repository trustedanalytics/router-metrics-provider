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

import org.trustedanalytics.routermetrics.storage.cloud.InfluxLoadStore;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Database;
import org.influxdb.dto.Serie;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InfluxLoadStoreTests {

    private InfluxLoadStore sut;

    @Mock
    private InfluxDB influxDb;

    @Test
    public void newInfluxLoadStore_databaseExists_shouldNotCreateDatabase() {

        when(influxDb.describeDatabases())
            .thenReturn(Arrays.asList(new Database("trustedanalytics-global-metrics")));

        sut = new InfluxLoadStore(influxDb, "1m", "4h");

        verify(influxDb, never()).createDatabase("trustedanalytics-global-metrics");
    }

    @Test
    public void newInfluxLoadStore_databaseNotExist_shouldCreateDatabase() {

        when(influxDb.describeDatabases())
            .thenReturn(Arrays.asList());

        sut = new InfluxLoadStore(influxDb, "1m", "4h");

        verify(influxDb, times(1)).createDatabase("trustedanalytics-global-metrics");
    }

    @Test
    public void save_someValue_shouldCallInfluxDBClientToSaveValueInSerie() {

        final double TEST_VALUE = 0.1;

        sut = new InfluxLoadStore(influxDb, "1m", "4h");
        sut.save(TEST_VALUE);

        ArgumentCaptor<Serie> argument = ArgumentCaptor.forClass(Serie.class);
        verify(influxDb)
            .write(eq("trustedanalytics-global-metrics"), eq(TimeUnit.MILLISECONDS), argument.capture());
        assertProperSerieSaved("gorouterMetrics", "loadPerSec", TEST_VALUE, argument.getValue());
    }

    private void assertProperSerieSaved(String expectedSerieName, String columnName,
        double TEST_VALUE, Serie actualSerie) {

        assertEquals(expectedSerieName, actualSerie.getName());
        assertEquals(columnName, actualSerie.getColumns()[0]);
        assertEquals(1, actualSerie.getColumns().length);
        assertEquals(1, actualSerie.getRows().size());
        assertEquals(1, actualSerie.getRows().get(0).size());
        assertEquals(TEST_VALUE, actualSerie.getRows().get(0).get(columnName));
    }
}
