package net.pervukhin.eventhandler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

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
    }

    public void startProcess(String processName,
                             Map<String, Object> variables) throws JsonProcessingException {

        zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(processName)
                .latestVersion()
                .variables(variables)
                .send();
    }

    private void deploy() throws IOException {
        Arrays.stream(resourceResolver.getResources("classpath:workflow/*.bpmn"))
                .forEach(resource -> {
                    try {
                        zeebeClient.newDeployCommand().addResourceStream(resource.getInputStream(), resource.getFilename())
                                .send().join();
                        logger.info("Deployed: {}", resource.getFilename());
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                });
    }
}
