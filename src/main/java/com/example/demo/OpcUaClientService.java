package com.example.demo;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

@Component
class OpcUaClientService {


    OpcUaClient client;

    Integer namespaceId;

    Integer[] nodeIds;

    RabbitTemplate rabbitTemplate;

    public OpcUaClientService(MiloClient miloClient,
                              @Value("${opcua.namespace}") Integer namespaceId,
                              @Value("${opcua.node.id}") Integer[] nodeIds,
                              RabbitTemplate rabbitTemplate,
                              MessageConverter messageConverter) throws UaException, ExecutionException, InterruptedException {
        this.client = miloClient.opcUaClient();
        this.namespaceId = namespaceId;
        this.nodeIds = nodeIds;
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setMessageConverter(messageConverter);
        client.connect();
    }

    NodeId GetNodeId(int value) {
        return new NodeId(namespaceId, Unsigned.uint(value));
    }

    @Scheduled(fixedRate = 1000)
    void run() {

        Arrays.stream(nodeIds).forEach(nodeId -> {
            DataValue dataValue;
            try {
                dataValue = client.readValue(0, TimestampsToReturn.Both, GetNodeId(nodeId)).get();

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }


            System.out.println(dataValue.toString());
            Variant variant = dataValue.getValue();

            Publisher publisher = new Publisher(this.namespaceId, nodeId, variant.getValue().toString());
            rabbitTemplate.convertAndSend("demo", "", publisher);
            System.out.println(variant);
        });


    }

}

record Publisher(
        Integer namespaceId,
        Integer nodeId,
        String nodeValue
) {
}