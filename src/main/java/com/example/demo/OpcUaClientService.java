package com.example.demo;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
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

    List<NodeId> nodeIds;

    List<Publisher> publishers;

    public OpcUaClientService(MiloClient miloClient,
                              OpcUaClientProperties opcUaClientProperties) throws UaException, ExecutionException, InterruptedException {
        this.client = miloClient.opcUaClient();

        this.nodeIds = new ArrayList<>();
        Arrays.stream(opcUaClientProperties.nodeId()).forEach(nodeId -> nodeIds.add(new NodeId(opcUaClientProperties.namespaceId(), Unsigned.uint(nodeId))));
        client.connect();
    }

    @Bean
    Supplier<List<Publisher>> run() {

        return () -> {
            nodeIds.forEach(nodeId -> {
                DataValue dataValue;
                try {
                    dataValue = client.readValue(0, TimestampsToReturn.Both, nodeId).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
                Variant variant = dataValue.getValue();
                publishers.add(new Publisher(nodeId.getNamespaceIndex(), nodeId.getIdentifier().toString(), variant.getValue().toString()));
            });
            return publishers;
        };
    }

}

record Publisher(
        UShort namespaceId,
        String nodeId,
        String nodeValue
) {
}