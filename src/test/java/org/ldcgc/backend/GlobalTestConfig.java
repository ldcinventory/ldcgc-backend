package org.ldcgc.backend;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

class GlobalTestConfig implements BeforeAllCallback, AfterAllCallback {

    public void beforeAll(ExtensionContext extensionContext) {
        // Set environment variables for testing
        System.setProperty("ENVIRONMENT_PROFILE", "dev");
    }

    public void afterAll(ExtensionContext extensionContext) {
        // Clear environment variables for testing
        System.clearProperty("ENVIRONMENT_PROFILE");
    }

}
