package com.github.charlemaznable.core.kafka;

import lombok.NoArgsConstructor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class KafkaClientElf {

    public static <K, V> KafkaProducer<K, V> buildProducer(Properties properties) {
        return new KafkaProducer<>(properties);
    }

    public static <K, V> KafkaConsumer<K, V> buildConsumer(Properties properties) {
        return new KafkaConsumer<>(properties);
    }
}
