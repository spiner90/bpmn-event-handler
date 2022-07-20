package net.pervukhin.eventhandler.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.impl.ZeebeObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZeebeConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ZeebeConfiguration.class);

    @Value("${connection-zeebe-service.host}")
    private String zeebeHost;

    @Value("${connection-zeebe-service.port}")
    private String zeebePort;

    @Value("${connection-zeebe-service.workers}")
    private Integer workers;

    @Value("${connection-zeebe-service.enabled:true}")
    private Boolean enabled;

    @Bean
    public ZeebeClient zeebeClient() {
        if (enabled) {
            logger.info("Connecting to Zeebe:" + zeebeHost + ":" + zeebePort);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            final ZeebeClient zeebeClient = ZeebeClient.newClientBuilder()
                    .gatewayAddress(zeebeHost + ":" + zeebePort)
                    .numJobWorkerExecutionThreads(workers)
                    .withJsonMapper(new ZeebeObjectMapper(objectMapper))
                    .usePlaintext()
                    .build();
            logger.info("Zeebe connection successful");
            return zeebeClient;
        } else {
            return null;
        }
    }
}
