package net.pervukhin.eventhandler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ZeebeService {
    private static final Logger logger = LoggerFactory.getLogger(ZeebeService.class);

    @Autowired
    private ZeebeClient zeebeClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ResourcePatternResolver resourceResolver;

    @PostConstruct
    public void init() throws IOException {
        deploy();
        subscribeCollectMoney();
        subscribeFetchItems();
    }

    public void startProcess(
            String processName,
            Map<String, Object> variables) throws JsonProcessingException {
        zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(processName)
                .latestVersion()
                .variables(variables)
                .send();
    }

    public void sendMessage(
            String correlationKey,
            Map<String, Object> variables) throws JsonProcessingException {
        zeebeClient.newPublishMessageCommand()
                .messageName("Money Collected")
                .correlationKey(correlationKey)
                .variables(variables)
                .send();
    }

    private void deploy() throws IOException {
        Arrays.stream(resourceResolver.getResources("classpath:workflow/*.bpmn"))
                .forEach(resource -> {
                    try {
                        zeebeClient.newDeployCommand()
                                .addResourceStream(resource.getInputStream(), resource.getFilename())
                                .send().join();
                        logger.info("Deployed: {}", resource.getFilename());
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                });
    }


    private void subscribeCollectMoney() {
        zeebeClient.newWorker().jobType("collect-money").handler(
                (jobClient, activatedJob) -> {
                    logger.debug("Received message from collect-money");
                    Map<String, Object> variables = new HashMap<>();
                    variables.put("orderId", UUID.randomUUID().toString());
                    jobClient.newCompleteCommand(activatedJob.getKey())
                            .variables(variables)
                            .send()
                            .join();
                }
        ).open();
    }

    private void subscribeFetchItems() {
        zeebeClient.newWorker().jobType("fetch-items").handler(
                (jobClient, activatedJob) -> {
                    logger.debug("Received message fetch-items");
                    logger.debug(String.format("sum=%s", activatedJob.getCustomHeaders().get("sum")));
                    jobClient.newCompleteCommand(activatedJob.getKey())
                            .send()
                            .join();
                }
        ).open();
    }
}
