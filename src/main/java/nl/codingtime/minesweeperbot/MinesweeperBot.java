package nl.codingtime.minesweeperbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MinesweeperBot {
    private final static File STATS_FILE = new File("stats.json");
    private StatsUpdater statsUpdater;
    private Stats stats;
    private JDA jda;
    private boolean active = false;
    private Configuration config;


    public MinesweeperBot(Configuration configuration) throws LoginException {
        this.config = configuration;
        jda = new JDABuilder(config.getToken()).addEventListener(new MinesweeperBotListenerAdapter(this)).build();
        jda.getPresence().setGame(Game.playing("DM or ping me!"));
    }

    JDA getJda() {
        return jda;
    }

    Stats getStats() {
        return stats;
    }

    private Stats readStats() throws IOException {
        Gson gson = new GsonBuilder().create();
        new Gson();
        return gson.fromJson(new InputStreamReader(new FileInputStream(STATS_FILE)), Stats.class);
    }

    public void shutdown() {
        System.out.println("Stopping now...");
        setActive(false);
        statsUpdater.interrupt();
        jda.shutdownNow();
        System.exit(0);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setupStats() {
        stats = null;
        try {
            stats = readStats();
        } catch (IOException e) {
            // No stats available, no problem
        }

        if (stats == null) {
            stats = new Stats();
        }

        statsUpdater = new StatsUpdater(stats, this);
        statsUpdater.start();
    }

    public Configuration getConfig() {
        return config;
    }
}
