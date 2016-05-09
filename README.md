[![Circle CI](https://circleci.com/gh/Capgemini/gregor.svg?style=svg)](https://circleci.com/gh/Capgemini/gregor)
# gregor
A utility library that makes consuming from and sending messages to a Kafka broker within a Spring project easier, by simply adding a few annotations to bean methods or interfaces.

*** Gregor is currently in a pre-alpha stage and should not be used in a production environment under any circumstances! ***

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

***Note*** - The key cannot currently be obtained from an `@KafkaConsumer` annotated method but this is in the pipeline.

## Sending Messages To Kafka
If an interface is annotated with `@KafkaClient`, gregor will automatically create an implementation bean of that interface, with each method acting as a kafka producer.

Methods must be annotated with `@KafkaProducer(topic=xxx)`, and multiple producer methods can be defined in the same interface.

### Method arguments
The producer methods should have a single argument, which defines the payload.  The strategy for serializing the payload can be defined by setting the `payloadSerializer` property on the `@KafkaProducer` annotation.  The value should be a class that implements `org.apache.kafka.common.serialization.Serializer`, and defaults to `com.capgemini.gregor.serializer.JSONSerializer`.

***Note*** - The key cannot currently be specified when producing messages, but this is in the pipeline.
