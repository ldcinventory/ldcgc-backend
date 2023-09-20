package org.ldcgc.backend.util.retrieving;

import org.ldcgc.backend.configuration.MessagesProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Messages {

    private static final Map<String, String> infoMessages = new HashMap<>();
    private static final Map<String, String> appMessages = new HashMap<>();
    private static final Map<String, String> errorMessages = new HashMap<>();

    private Messages(MessagesProperties messagesProperties) {
        infoMessages.putAll(messagesProperties.getInfo());
        appMessages.putAll(messagesProperties.getApp());
        errorMessages.putAll(messagesProperties.getErrors());
    }

    public static String getInfoMessage(String key) {
        return infoMessages.get(key);
    }
    public static String getAppMessage(String key) {
        return appMessages.get(key);
    }
    public static String getErrorMessage(String key) {
        return errorMessages.get(key);
    }

}
