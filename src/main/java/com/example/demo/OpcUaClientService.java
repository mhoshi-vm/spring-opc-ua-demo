package com.example.demo;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@Component
class OpcUaClientService {

    OpcUaClient client;

    Integer namespaceId;

    Integer[] nodeIds;

    public OpcUaClientService(MiloClient miloClient,
                              OpcUaClientProperties opcUaClientProperties) throws UaException, ExecutionException, InterruptedException {
        this.client = miloClient.opcUaClient();
        this.namespaceId = opcUaClientProperties.namespaceId();
        this.nodeIds = opcUaClientProperties.nodeId();

        client.connect();
    }

    NodeId GetNodeId(int value) {
        return new NodeId(namespaceId, Unsigned.uint(value));
    }

    @Bean
    Supplier<List<Publisher>> run() {

        return () -> {
            List<Publisher> publishers = new ArrayList<>();
            Arrays.stream(nodeIds).forEach(nodeId -> {
                DataValue dataValue;
                try {
                    dataValue = client.readValue(0, TimestampsToReturn.Both, GetNodeId(nodeId)).get();

                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }

                Variant variant = dataValue.getValue();
                publishers.add(new Publisher(this.namespaceId, nodeId, variant.getValue().toString()));
            });
            return publishers;
        };
    }

}

record Publisher(
        Integer namespaceId,
        Integer nodeId,
        String nodeValue
) {
}