package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {

    private static final Properties PROPS = new Properties();
    private static final String ENV;

    static {
        // 1) read env from JVM system property or OS env var
        String sysEnv = System.getProperty("env");             // -Denv=qa

        if (sysEnv == null || sysEnv.isBlank()) {
            sysEnv = System.getenv("TEST_ENV");          // TEST_ENV=qa
        }
        if (sysEnv == null || sysEnv.isBlank()) {
            sysEnv = "acc";                                    // default
        }

        ENV = sysEnv.toLowerCase();

        // (optional, but helpful for debugging)
        System.out.println(">>> Using test ENV = " + ENV);

        // 2) load config-<env>.properties
        String filename = "config-" + ENV + ".properties";
        try (InputStream in = ConfigManager.class
                .getClassLoader()
                .getResourceAsStream(filename)) {

            if (in == null) {
                throw new IllegalStateException(
                        "Could not find " + filename +
                                " on classpath (src/test/resources).");
            }
            PROPS.load(in);

        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                    "Failed to load " + filename + ": " + e.getMessage()
            );
        }
    }

    private ConfigManager() {}

    public static String getEnv() {
        return ENV;
    }

    public static String get(String key) {
        String value = PROPS.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException(
                    "Missing config key '" + key + "' in config-" + ENV + ".properties"
            );
        }
        return value.trim();
    }

    public static String getOrDefault(String key, String defaultValue) {
        String value = PROPS.getProperty(key);
        return value == null ? defaultValue : value.trim();
    }
}
