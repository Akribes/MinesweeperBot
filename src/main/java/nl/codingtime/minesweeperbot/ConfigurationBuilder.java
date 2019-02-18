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

    public ConfigurationBuilder withMaxPuzzleSize(int maxPuzzleSize) {
        value.setMaxSize(maxPuzzleSize);
        return this;
    }

    public Configuration build() {
        return value;
    }
}
