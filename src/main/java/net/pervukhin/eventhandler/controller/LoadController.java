package net.pervukhin.eventhandler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.pervukhin.eventhandler.service.ZeebeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class LoadController {

    private final String processName;

    private final ZeebeService zeebeService;

    public LoadController(@Value("${process.name}") String processName, ZeebeService zeebeService) {
        this.processName = processName;
        this.zeebeService = zeebeService;
    }

    @GetMapping("/start/{var}")
    public String getLoad(@PathVariable Integer var) throws JsonProcessingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("var", var);
        zeebeService.startProcess(processName, variables);
        return "Process started";
    }

    @GetMapping("/continue")
    public String moneyCollected(@RequestParam String correlationId, @RequestParam String sum) throws JsonProcessingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("sum", sum);
        zeebeService.sendMessage(correlationId, variables);
        return "Process resumed";
    }
}
