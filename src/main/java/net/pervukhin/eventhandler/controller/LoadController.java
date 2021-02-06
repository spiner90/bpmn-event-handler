package net.pervukhin.eventhandler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.pervukhin.eventhandler.service.ZeebeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/start")
public class LoadController {

    private final String processName;

    private final ZeebeService zeebeService;

    public LoadController(@Value("${process.name}") String processName, ZeebeService zeebeService) {
        this.processName = processName;
        this.zeebeService = zeebeService;
    }

    @GetMapping
    public String getLoad(@RequestParam Integer sum, @RequestParam Double limit) throws JsonProcessingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("sum", sum);
        variables.put("limit", limit);
        zeebeService.startProcess(processName, variables);
        return "Process started";
    }
}
