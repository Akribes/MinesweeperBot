package nl.codingtime.minesweeperbot;

public class Configuration {
    private String token = null;
    private int maxSize = 900;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
}
