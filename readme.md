# Kafka Streams Demo

This project is to test Kafka Streams usage for the processing of event change on workforce data, and publishing updated workforce data to an external source (in this case Mongo).

The demo is made up of five projects:

- Producer - Spring/Boot MVC project for generating workforce data, and sending in change requets.
- Consumer - Spring/Boot project that listens for changes and publishes them.  It has a ReactJs application that the consumer communications with via web socket.  The ReactJS app displays results of the change requests.
- Streams - A Spring/Boot app that was used to explore different options for using Streams DSL and Processor API.
 - Simple - A simple DSL that loads data from a topic and publishes to another topic.
 - Advanced - DSLs that use a KTable and GlobalKTable.
 - Bad Messages - DSL that explores how to handle bad messages.
 - Process - Processor API that implements the same functionality as Advanced, but using a StateStore and GlobalStateStore respectively.   Also includes a ProcessorAPI that instead of using a state store, uses Mongo as the store.
- CloudStreams - Spring Cloud Streams with Kafka binder that listens to process change requests.
- Common - Spring/Boot project that has common functionality, including a set of merge services.
- Mongo - Spring/Boot project that implements service to read and write data to Mongo.

# Diagram

https://textik.com/#e5be2591094473ff

                                                                                                                                          
+------------------------------------------+                 +--------------------------+                                                 
|     Kafka Streams                        |                 |                          |                                                 
|                                          |                 | kafka producer web api   |                                                 
|             +-------------------------+  |                 |                          |                                                 
|             | dsl & ktable            -\ |                 +-------------|------------+                                                 
|             +-------------------------+ --\                              |                                                              
|                                          | --\                           |                                                              
|             +-------------------------+  |    ---\                       |                                                              
|             | processor & local store ---\        --\       +------------|------------+                                                 
|             +-------------------------+  |-------\   --\    |                         |                                                 
|                                          |        --------\ |                         |                     +--------------------------+
| +-----------------------+                | ------------------         kafka           -----\                | spring cloud streams     |
| | processor external    |-----------------/                 |                         |     ----------\     | kafka consumer           |
| +-----------|-----------+                |                  |                         |                ------                          |
+-------------|----------------------------+                  +-------------------------+                     |                          |
               \                                                         -/ \-                           /-----                          |
               |                                                       -/     \-              /----------     +--------------------------+
                \                                                    -/         \-  /---------                                            
                |                                                  -/    /----------                                                      
                |                                             /----------           \                                                     
                 \                                 /---------- -/                    \-                                                   
         +--------------+                /---------          -/                        \-                                                 
         |              |     /----------                  -/                            \-                                               
         |    mongo     ------        +-------------------/--------------------------------\--------------------+                         
         |              |  \----      |                -/         kafka consumer             \-                 |                         
         +--------------+       \---  |    +---------------------+                  +----------\----------+     |                         
                                    \----  |                     |                  |                     |     |                         
                                      |  \--                     |                  |                     |     |                         
                                      |    | merge processor     |                  |  transaction output |     |                         
                                      |    +---------------------+                  +----------|----------+     |                         
                                      +--------------------------------------------------------|----------------+                         
                                                                                               |                                          
                                                                                               |                                          
                                                                                    +----------|----------+                               
                                                                                    |   end client        |                               
                                                          -                         |                     |                               
                                                                                    +---------------------+                               
                                                                                                                               

# Running

## Kafka docker

The demo requires a Kafka servce to run.   There are many options for running a Kafka server.  The two that were used are:

### Lenses

This is a single docker image that includes the Lenses app, as well as Kafka.  You need to register to get the download from https://lenses.io/downloads.
Once you've got the registered link, you can run the docker as follows:

docker run -e ADV_HOST=127.0.0.1 -e EULA="<replace with your key>" --rm -p  3030:3030 -p 9092:9092 landoop/kafka-lenses-dev

### Wurstmeister

Also used the wurstmeister docker images with docker-compose.yml (instance\docker-compose.yml):

```
version: '2.4'
services:
  zookeeper:
    image: wurstmeister/zookeeper:latest
    expose:
    - "2181"

  kafka:
    image: wurstmeister/kafka:latest
    depends_on:
    - zookeeper
    ports:
    - "9092:9092"
    environment:
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
```

Save the docker-compose.yml into a directory.
Run using

docker-compose up -d

To stop

docker-compose stop

## Mongo

If you are using the external Processor API example, then MongoDb is also needed.   By default the connection strings are expecting that the MongoDb is installed locally.

# Kafka Links

Some links on Kafka that were found useful.

https://static.rainfocus.com/oracle/oraclecode18/sess/1515078353150001APG6/PF/Rethinking%20Stream%20Processing%20with%20KStreams%20and%20KSQL%20-%20OracleCode%20NYC%20-%2003-08-2018_1521054216095001D1Pu.pdf
https://medium.com/@stephane.maarek/the-kafka-api-battle-producer-vs-consumer-vs-kafka-connect-vs-kafka-streams-vs-ksql-ef584274c1e

https://docs.confluent.io/current/streams/index.html
https://docs.confluent.io/current/streams/quickstart.html
https://www.confluent.io/blog/apache-kafka-spring-boot-application
https://stackoverflow.com/questions/42666756/handling-bad-messages-using-kafkas-streams-api
https://supergloo.com/kafka-streams/kafka-streams-joins-examples/
https://www.confluent.io/blog/put-several-event-types-kafka-topic/
https://stackoverflow.com/questions/51860481/is-there-a-way-to-send-data-to-a-kafka-topic-directly-from-within-processor

https://www.confluent.jp/blog/predicting-flight-arrivals-with-the-apache-kafka-streams-api/
https://github.com/confluentinc/online-inferencing-blog-application

https://blog.newrelic.com/engineering/effective-strategies-kafka-topic-partitioning/
https://medium.com/@andy.bryant/kafka-streams-work-allocation-4f31c24753cc

# License

The MIT License

Copyright (c) 2019 thzero.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.