package nl.codingtime.minesweeperbot;

public class Configuration {
    private String token;
    private int maxSize;
    private String divineToken;
    private String discordBotsToken;

    public Configuration(String token, Integer maxSize, String divineBotListToken, String discordBotsToken) {
        this.token = token;
        this.maxSize = maxSize == null ? 900 : maxSize;
        this.divineToken = divineBotListToken;
        this.discordBotsToken = discordBotsToken;
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

    public String getDiscordBotsToken() {
        return this.discordBotsToken;
    }
}
