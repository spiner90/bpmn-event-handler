package net.pervukhin.eventhandler.configuration;

import io.camunda.zeebe.client.ZeebeClient;
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
            final ZeebeClient zeebeClient = ZeebeClient.newClientBuilder()
                    .gatewayAddress(zeebeHost + ":" + zeebePort)
                    .numJobWorkerExecutionThreads(workers)
                    .usePlaintext()
                    .build();
            logger.info("Zeebe connection successful");
            return zeebeClient;
        } else {
            return null;
        }
    }
}
