package com.github.charlemaznable.core.kafka;

import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.charlemaznable.core.kafka.KafkaClientElf.buildConsumer;
import static com.github.charlemaznable.core.kafka.KafkaClientElf.buildProducer;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class KafkaClientElfTest {

    private static final DockerImageName KAFKA_IMAGE = DockerImageName.parse("confluentinc/cp-kafka:6.2.1");
    private static final AtomicBoolean done = new AtomicBoolean(false);

    @Test
    public void testKafkaClientElf() {
        try (val kafka = new KafkaContainer(KAFKA_IMAGE)) {
            kafka.start();
            val bootstrapServers = kafka.getBootstrapServers();

            val consumerConfig = new Properties();
            consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            val consumer = buildConsumer(consumerConfig);
            consumer.assign(Collections.singleton(new TopicPartition("testtopic", 0)));

            val producerConfig = new Properties();
            producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            val producer = buildProducer(producerConfig);

            new Thread(() -> {
                while (true) {
                    val records = consumer.poll(Duration.ofSeconds(1));
                    for (val record : records) {
                        if ("testkey".equals(record.key())) {
                            assertEquals("testvalue", record.value());
                            done.set(true);
                            break;
                        }
                    }
                }
            }).start();

            producer.send(new ProducerRecord<>("testtopic", "testkey", "testvalue"), (recordMetadata, e) -> {
                // do nothing
            });

            await().forever().until(done::get);
        }
    }
}
