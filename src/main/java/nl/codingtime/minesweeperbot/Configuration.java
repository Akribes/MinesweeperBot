package nl.codingtime.minesweeperbot;

public class Configuration {
    private String token;
    private int maxSize;
    private String divineToken;

    public Configuration(String token, Integer maxSize, String divineBotListToken) {
        this.token = token;
        this.maxSize = maxSize == null ? 900 : maxSize;
        this.divineToken = divineBotListToken;
    }

    public String getToken() {
        return token;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public String getDivineToken() {
        return this.divineToken;
    }
}
