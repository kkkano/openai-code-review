package plus.gaga.middleware.sdk.types.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;

public class ReviewConfigLoader {

    private static final String DEFAULT_CONFIG_FILE = ".github/code-review.yml";

    public static ReviewConfig load() {
        ReviewConfig config = loadFromFile();
        applyEnvOverrides(config);
        validate(config);
        return config;
    }

    private static ReviewConfig loadFromFile() {
        String configPath = getenv("REVIEW_CONFIG_FILE", DEFAULT_CONFIG_FILE);
        File file = new File(configPath);
        if (!file.exists()) return new ReviewConfig();

        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            return mapper.readValue(file, ReviewConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("failed to load review config from " + configPath, e);
        }
    }

    private static void applyEnvOverrides(ReviewConfig config) {
        config.setProvider(getenv("REVIEW_PROVIDER", config.getProvider()));
        config.setModel(getenv("REVIEW_MODEL", config.getModel()));
        config.setAuthScheme(getenv("OPENAI_AUTH_SCHEME", config.getAuthScheme()));
        config.setPromptTemplate(getenv("REVIEW_PROMPT_TEMPLATE", config.getPromptTemplate()));

        String apiHost = firstNonBlank(
                System.getenv("OPENAI_APIHOST"),
                System.getenv("CHATGLM_APIHOST"),
                config.getApiHost()
        );
        String apiKey = firstNonBlank(
                System.getenv("OPENAI_APIKEY"),
                System.getenv("CHATGLM_APIKEYSECRET"),
                config.getApiKey()
        );

        config.setApiHost(apiHost);
        config.setApiKey(apiKey);
    }

    private static void validate(ReviewConfig config) {
        if (blank(config.getApiHost())) {
            throw new RuntimeException("apiHost is required. set in .github/code-review.yml or OPENAI_APIHOST/CHATGLM_APIHOST");
        }
        if (blank(config.getApiKey())) {
            throw new RuntimeException("apiKey is required. set in .github/code-review.yml or OPENAI_APIKEY/CHATGLM_APIKEYSECRET");
        }
    }

    private static String getenv(String key, String defaultValue) {
        String v = System.getenv(key);
        return blank(v) ? defaultValue : v;
    }

    private static boolean blank(String v) {
        return v == null || v.trim().isEmpty();
    }

    private static String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (!blank(v)) return v;
        }
        return null;
    }
}
