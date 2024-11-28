package com.example.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("opcua.client")
record OpcUaClientProperties(
        @DefaultValue("2")
        Integer namespaceId,
        @DefaultValue("2,4")
        Integer[] nodeId,
        @DefaultValue("0.0.0.0")
        String endpointUrl,
        @DefaultValue("4840")
        Integer endpointPort,
        @DefaultValue("freeopcua/server/")
        String endpointPath
) {
}

