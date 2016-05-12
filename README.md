[![Build Status](https://travis-ci.org/trustedanalytics/router-metrics-provider.svg?branch=master)](https://travis-ci.org/trustedanalytics/router-metrics-provider)
[![Dependency Status](https://www.versioneye.com/user/projects/57236fdaba37ce004309f4c8/badge.svg?style=flat)](https://www.versioneye.com/user/projects/57236fdaba37ce004309f4c8)

# router-metrics
Providing global load metrics. 

Load data is periodically downloaded from gorouters and stored in InfluxDB. There is an endpoint providing data to client.
Application provide also latency data - it's downloaded from gorouters too, but it is not stored anywhere, it's going back to client in a response.

### Application endpoints
* latency
  * *url:* /rest/metrics/latency-histograms
  * *data format:* one histogram per router, example below
  ```
    {
    	"http://127.0.0.1:18080": {
    		"50": 0.010766583,
    		"75": 0.030895952,
    		"90": 0.1291480549,
    		"95": 0.8906567838499999,
    		"99": 0.9552081019900003,
    		"samples": 1.0,
    		"value": 5.0E-7
    	},
    	"http://127.0.0.1:28080": {
    		"50": 0.010908412,
    		"75": 0.03146656775,
    		"90": 0.1423239731,
    		"95": 0.8995987759499999,
    		"99": 0.9677017562700001,
    		"samples": 1.0,
    		"value": 5.0E-7
    	}
    }
  ```
* load
  * *url:* /rest/metrics/load-data
  * *data format:* collection of timestamp/value pairs, example below:
  ```
    [{
    	"timestamp": 1430687640000,
    	"value": 0.050287701530544404
    },
    {
    	"timestamp": 1430687520000,
    	"value": 0.042636148649652365
    },
    ...
    {
    	"timestamp": 1430681760000,
    	"value": 0.046435773519793465
    }]
  ```

### Local development
#### Running
##### Tests
###### Unit
```mvn test```
###### Integration
```mvn integration-test ```
##### Application
###### Prerequisites
* InfluxDB
  * You need to install and run it locally. You can find instruction here: http://influxdb.com/docs/v0.8/introduction/installation.html
  ```
  wget http://s3.amazonaws.com/influxdb/influxdb_latest_amd64.deb
  sudo dpkg -i influxdb_latest_amd64.deb
  ```         
  Configuration file is located at /opt/influxdb/shared/config.toml or /usr/local/etc/influxdb.conf
  There you can check or change ports used by InfluxFB. By default there will be 8083, 8086, 8090, and 8099.

  To start InfluxDB type: ```sudo /etc/init.d/influxdb start```
  
  You can then access admin panel, by default accessible at: ```localhost:8083```
  
  After going there for first time, remember to create username and password. ```root:root``` seems to be a good choice.
  
* Gorouters
  * Access to cloudfoundry gorouters is required. The simplest way to establish it, is to configure ssh tunnels through jumpbox. Your ~/.ssh/config would look like this:
  ```
  Host jumpeu
        Hostname jump.<platform_domain>
        User ubuntu
        IdentityFile <path to your public key>
        LocalForward 0.0.0.0:18080 10.10.3.11:8080
        LocalForward 0.0.0.0:28080 10.10.3.25:8080
  ```
  Then set tunnel by typing ```ssh jumpeu```
  * Change app configuration to use ssh tunnels. Edit ```src/main/resources/application.yml``` gorouter.hosts section from:
  ```
    gorouter:
      hosts: http://10.10.3.11:8080 http://10.10.3.25:8080
             #for local development use below settings (remember to set ssh tunnels)
             #http://127.0.0.1:18080 http://127.0.0.1:28080
  ```
  to:
  ```
    gorouter:
      hosts: #http://10.10.3.11:8080 http://10.10.3.25:8080
             #for local development use below settings (remember to set ssh tunnels)
             http://127.0.0.1:18080 http://127.0.0.1:28080
  ```
  
To run the application type:
```mvn spring-boot:run```

Once application is running for some time you can explore gathered data. Access your InfluxDB admin panel, choose ```trustedanalytics-global-metrics``` database and perform query ```select loadPerSec from gorouterMetrics;```
