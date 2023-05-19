package com.github.charlemaznable.core.etcd;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.test.EtcdClusterExtension;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.concurrent.CompletableFuture;

public class EtcdDemoTest {

    @RegisterExtension
    public static final EtcdClusterExtension cluster = EtcdClusterExtension.builder()
            .withNodes(1)
            .build();

    @SneakyThrows
    @Test
    public void testEtcdDemo() {
        Client client = Client.builder().endpoints(cluster.clientEndpoints()).build();
        KV kvClient = client.getKVClient();
        ByteSequence key = ByteSequence.from("test_key".getBytes());
        ByteSequence value = ByteSequence.from("test_value".getBytes());

        kvClient.put(key, value).get();

        CompletableFuture<GetResponse> getFuture = kvClient.get(key);

        GetResponse response = getFuture.get();
        System.out.println(response.getKvs().get(0).getValue().toString());

        kvClient.delete(key).get();
    }
}
