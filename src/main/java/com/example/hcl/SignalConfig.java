package com.example.hcl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Service to parse configuration file and return a map of signal to list of actions
 */
@Slf4j
@Configuration
@NoArgsConstructor
public class SignalConfig implements InitializingBean {
    // Map of signal to list of actions in String format
    private Map<String, List<String>> signalActions;

    /*
     * This runs after the bean is created and properties are set
     */
    @Override
    public void afterPropertiesSet() throws IOException {
        signalActions = parseMethodConfig();
    }

    /**
     * Parse configuration file and return a map of signal to list of actions
     *
     * @return Map of signal to list of actions
     * @throws IOException if configuration file is not found
     */
    public Map<String, List<String>> parseMethodConfig() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String configFile = "static/configuration.json";
        try (InputStream stream = new ClassPathResource(configFile).getInputStream()) {
            return objectMapper.readValue(stream, Map.class);
        } catch (IOException e) {
            throw new IOException("loading configuration file: " + configFile);
        }
    }

    public Map<String, List<String>> getSignalActions() {
        return signalActions;
    }
}
