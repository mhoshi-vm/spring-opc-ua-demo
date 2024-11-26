package com.example.demo;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.util.EndpointUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
class MiloClient {

    String endpointUrl;
    Integer endpointPort;
    String endpointPath;

    public MiloClient(OpcUaClientProperties opcUaClientProperties) {
        this.endpointUrl = opcUaClientProperties.endpointUrl();
        this.endpointPort = opcUaClientProperties.endpointPort();
        this.endpointPath = opcUaClientProperties.endpointPath();
    }

    OpcUaClient opcUaClient() throws ExecutionException, InterruptedException, UaException {
        String endpoint = String.format("opc.tcp://%s:%s/%s", endpointUrl, endpointPort, endpointPath);
        List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints(endpoint).get();
        EndpointDescription configPoint = EndpointUtil.updateUrl(endpoints.get(0), endpointUrl, endpointPort);

        return OpcUaClient.create(new OpcUaClientConfigBuilder()
                .setEndpoint(configPoint)
                .build());
    }

}
