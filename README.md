[![Circle CI](https://circleci.com/gh/Capgemini/gregor.svg?style=svg)](https://circleci.com/gh/Capgemini/gregor)
# gregor
A utility library that simplifies consuming from and sending messages to a Kafka broker.

## Configuring
In order to integrate gregor into your Spring application, two simple steps must be followed:

1. Ensure that the `kafka.addresses` property is available in your environment.  This should contain a comma serparated list of kafka addresses in your system (not all addresses have to be specified as the others will automatically be established after connecting to the machines specified in this property).

2. Either your main method application class or a Configuration class that has been imported should be annotated with the `@EnableKafkaConsumers` and/or `@EnableKafkaProducers`, depending on the behaviour of your application.

## Consuming Messages From Kafka

## Sending Messages To Kafka
