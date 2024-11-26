package com.example.demo;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

@Component
class OpcUaClientService {

    private final StreamBridge streamBridge;

    OpcUaClient client;

    Integer namespaceId;

    Integer[] nodeIds;

    public OpcUaClientService(MiloClient miloClient,
                              OpcUaClientProperties opcUaClientProperties,
                              StreamBridge streamBridge) throws UaException, ExecutionException, InterruptedException {
        this.client = miloClient.opcUaClient();
        this.namespaceId = opcUaClientProperties.namespaceId();
        this.nodeIds = opcUaClientProperties.nodeId();
        this.streamBridge = streamBridge;

        client.connect();
    }

    NodeId GetNodeId(int value) {
        return new NodeId(namespaceId, Unsigned.uint(value));
    }

    @Scheduled(fixedRateString = "#{@'opcua.client-com.example.demo.OpcUaClientProperties'.pollingRateMs}")
    void run() {

        Arrays.stream(nodeIds).forEach(nodeId -> {
            DataValue dataValue;
            try {
                dataValue = client.readValue(0, TimestampsToReturn.Both, GetNodeId(nodeId)).get();

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            Variant variant = dataValue.getValue();
            Publisher publisher = new Publisher(this.namespaceId, nodeId, variant.getValue().toString());
            this.streamBridge.send("output", publisher);
        });


    }

}

record Publisher(
        Integer namespaceId,
        Integer nodeId,
        String nodeValue
) {
}