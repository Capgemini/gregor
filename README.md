[![Circle CI](https://circleci.com/gh/Capgemini/gregor.svg?style=svg)](https://circleci.com/gh/Capgemini/gregor)
# gregor
A utility library that simplifies consuming from and sending messages to a Kafka broker.

## Configuring
In order to integrate gregor into your Spring application, two simple steps must be followed:

1. Ensure that the `kafka.addresses` property is available in your environment.  This should contain a comma serparated list of kafka addresses in your system (not all addresses have to be specified as the others will automatically be established after connecting to the machines specified in this property).

2. Either your main method application class or a Configuration class that has been imported should be annotated with the `@EnableKafkaConsumers` and/or `@EnableKafkaProducers`, depending on the behaviour of your application.

## Consuming Messages From Kafka
Gregor provides the ability to be able to annotate Spring bean methods with `@KafkaConsumer(topic=xxx)`.  This method will then be triggered when a message is sent to the specified topic.

### Method arguments
The consumer method should have a single argument.  The payload of the message will be converted to an object of this type.  This deserialization process can be specified in the following ways:

- Set the `payloadContent` value on the `@KafkaConsumer` annotation.  The current available content types are `JSON` (default) or `STRING`.


- Set the `payloadDecoder` value on the `@KafkaConsumer` annotation.  This allows you to specify a custom decoder that can be used to deserialize the kafka message into the correct argument type.  The value should be a class that implements `kafka.serializer.Decoder`.

****Note**** - The key cannot currently be obtained from an `@KafkaConsumer` annotated method but this is in the pipeline.

## Sending Messages To Kafka
