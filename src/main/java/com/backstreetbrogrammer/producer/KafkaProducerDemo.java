package com.backstreetbrogrammer.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaProducerDemo {
    private static final String TOPIC = "events";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

    public static void main(final String[] args) {
        final Producer<Long, String> kafkaProducer = createKafkaProducer(BOOTSTRAP_SERVERS);
        try {
            final int partition = Integer.parseInt(args[0]);
            produceMessages(10, kafkaProducer, partition);
        } catch (final ExecutionException | InterruptedException | NumberFormatException e) {
            System.err.println(e.getMessage());
        } finally {
            kafkaProducer.flush();
            kafkaProducer.close();
        }
    }

    public static void produceMessages(final int numberOfMessages, final Producer<Long, String> kafkaProducer,
                                       final int partition)
            throws ExecutionException, InterruptedException {
        for (int i = 0; i < numberOfMessages; i++) {
            final String value = String.format("event %d", i);
            final long timeStamp = System.currentTimeMillis();
            final ProducerRecord<Long, String> record;
            if (partition == -1) {
                record = new ProducerRecord<>(TOPIC, (long) i, value);
            } else {
                record = new ProducerRecord<>(TOPIC, partition, timeStamp, (long) i, value);
            }
            final RecordMetadata recordMetadata = kafkaProducer.send(record).get();

            System.out.printf("Record with (key: %s, value: %s), was sent to (partition: %d, offset: %d%n",
                              record.key(), record.value(), recordMetadata.partition(), recordMetadata.offset());
        }
    }

    public static Producer<Long, String> createKafkaProducer(final String bootstrapServers) {
        final Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "events-producer");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<>(properties);
    }

}
