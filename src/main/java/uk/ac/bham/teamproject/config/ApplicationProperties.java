package uk.ac.bham.teamproject.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Teamproject.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private String baseUrl;
    private String googleClientId;
    private String googleClientSecret;
    private String googleRedirect;
    private String googleEncryptionKeyToken;
    private String googleEncryptionKeyState;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getGoogleClientId() {
        return googleClientId;
    }

    public void setGoogleClientId(String googleClientId) {
        this.googleClientId = googleClientId;
    }

    public String getGoogleClientSecret() {
        return googleClientSecret;
    }

    public void setGoogleClientSecret(String googleClientSecret) {
        this.googleClientSecret = googleClientSecret;
    }

    public String getGoogleRedirect() {
        return googleRedirect;
    }

    public void setGoogleRedirect(String googleRedirect) {
        this.googleRedirect = googleRedirect;
    }

    public String getGoogleEncryptionKeyToken() {
        return googleEncryptionKeyToken;
    }

    public void setGoogleEncryptionKeyToken(String googleEncryptionKeyToken) {
        this.googleEncryptionKeyToken = googleEncryptionKeyToken;
    }

    public String getGoogleEncryptionKeyState() {
        return googleEncryptionKeyState;
    }

    public void setGoogleEncryptionKeyState(String googleEncryptionKeyState) {
        this.googleEncryptionKeyState = googleEncryptionKeyState;
    }

    // jhipster-needle-application-properties-property
    // jhipster-needle-application-properties-property-getter
    // jhipster-needle-application-properties-property-class
}
