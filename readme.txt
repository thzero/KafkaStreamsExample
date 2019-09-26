Kafka docker

Used Lenses.io docker image for lenses + kafka
docker run -e ADV_HOST=127.0.0.1 -e EULA="" --rm -p  3030:3030 -p 9092:9092 landoop/kafka-lenses-dev

Also used wurstmeister docker images with docker-composed

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

https://www.kaaproject.org/kafka-docker


Kafka links
https://static.rainfocus.com/oracle/oraclecode18/sess/1515078353150001APG6/PF/Rethinking%20Stream%20Processing%20with%20KStreams%20and%20KSQL%20-%20OracleCode%20NYC%20-%2003-08-2018_1521054216095001D1Pu.pdf
https://medium.com/@stephane.maarek/the-kafka-api-battle-producer-vs-consumer-vs-kafka-connect-vs-kafka-streams-vs-ksql-ef584274c1e

https://docs.confluent.io/current/streams/index.html
https://docs.confluent.io/current/streams/quickstart.html
https://www.confluent.io/blog/apache-kafka-spring-boot-application
https://stackoverflow.com/questions/42666756/handling-bad-messages-using-kafkas-streams-api
https://supergloo.com/kafka-streams/kafka-streams-joins-examples/
https://www.confluent.io/blog/put-several-event-types-kafka-topic/
https://stackoverflow.com/questions/51860481/is-there-a-way-to-send-data-to-a-kafka-topic-directly-from-within-processor

https://blog.newrelic.com/engineering/effective-strategies-kafka-topic-partitioning/
https://medium.com/@andy.bryant/kafka-streams-work-allocation-4f31c24753cc
