package nl.codingtime.minesweeperbot;

public class ConfigurationBuilder {
    private Configuration value;

    public ConfigurationBuilder() {
        value = new Configuration();
    }

    public ConfigurationBuilder withToken(String token) {
        value.setToken(token);
        return this;
    }

    public Configuration build() {
        return value;
    }
}
