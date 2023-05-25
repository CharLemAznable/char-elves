package com.github.charlemaznable.core.kafka;

import com.github.charlemaznable.apollo.MockApolloServer;
import com.github.charlemaznable.etcdconf.EtcdConfigService;
import com.github.charlemaznable.etcdconf.test.EmbeddedEtcdCluster;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.n3r.diamond.client.impl.MockDiamondServer;

import static com.github.charlemaznable.core.kafka.KafkaConfigElf.KAFKA_CONFIG_DIAMOND_GROUP_NAME;
import static com.github.charlemaznable.core.kafka.KafkaConfigElf.KAFKA_CONFIG_ETCD_NAMESPACE;
import static com.github.charlemaznable.core.kafka.KafkaConfigElf.getApolloProperty;
import static com.github.charlemaznable.core.kafka.KafkaConfigElf.getDiamondStone;
import static com.github.charlemaznable.core.kafka.KafkaConfigElf.getEtcdValue;
import static com.github.charlemaznable.core.lang.Propertiess.parseStringToProperties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class KafkaConfigElfTest {

    @Test
    public void testKafkaConfigElfInApollo() {
        MockApolloServer.setUpMockServer();

        val producerConfigStone = getApolloProperty("producer");
        assertProducerConfigValue(producerConfigStone);

        val consumerConfigStone = getApolloProperty("consumer");
        assertConsumerConfigValue(consumerConfigStone);

        MockApolloServer.tearDownMockServer();
    }

    @Test
    public void testKafkaConfigElfInDiamond() {
        MockDiamondServer.setUpMockServer();
        MockDiamondServer.setConfigInfo(KAFKA_CONFIG_DIAMOND_GROUP_NAME, "producer", """
                bootstrap.servers=127.0.0.1:9092
                key.serializer=org.apache.kafka.common.serialization.StringSerializer
                value.serializer=org.apache.kafka.common.serialization.StringSerializer
                """);
        MockDiamondServer.setConfigInfo(KAFKA_CONFIG_DIAMOND_GROUP_NAME, "consumer", """
                bootstrap.servers=127.0.0.1:9092
                key.deserializer=org.apache.kafka.common.serialization.StringDeserializer
                value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
                """);

        val producerConfigStone = getDiamondStone("producer");
        assertProducerConfigValue(producerConfigStone);

        val consumerConfigStone = getDiamondStone("consumer");
        assertConsumerConfigValue(consumerConfigStone);

        MockDiamondServer.tearDownMockServer();
    }

    @Test
    public void testKafkaConfigElfInEtcd() {
        EtcdConfigService.setUpTestMode();
        EmbeddedEtcdCluster.addOrModifyProperty(KAFKA_CONFIG_ETCD_NAMESPACE, "producer", """
                bootstrap.servers=127.0.0.1:9092
                key.serializer=org.apache.kafka.common.serialization.StringSerializer
                value.serializer=org.apache.kafka.common.serialization.StringSerializer
                """);
        EmbeddedEtcdCluster.addOrModifyProperty(KAFKA_CONFIG_ETCD_NAMESPACE, "consumer", """
                bootstrap.servers=127.0.0.1:9092
                key.deserializer=org.apache.kafka.common.serialization.StringDeserializer
                value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
                """);

        val producerConfigValue = getEtcdValue("producer");
        assertProducerConfigValue(producerConfigValue);

        val consumerConfigValue = getEtcdValue("consumer");
        assertConsumerConfigValue(consumerConfigValue);

        EtcdConfigService.tearDownTestMode();
    }

    private void assertProducerConfigValue(String configValue) {
        assertNotNull(configValue);
        val configs = parseStringToProperties(configValue);
        assertEquals("127.0.0.1:9092", configs.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(StringSerializer.class.getName(), configs.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(StringSerializer.class.getName(), configs.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
    }

    private void assertConsumerConfigValue(String configValue) {
        assertNotNull(configValue);
        val configs = parseStringToProperties(configValue);
        assertEquals("127.0.0.1:9092", configs.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(StringDeserializer.class.getName(), configs.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        assertEquals(StringDeserializer.class.getName(), configs.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
    }
}
