package nl.codingtime.minesweeperbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class StatsUpdater extends Thread {
    private final static File STATS_FILE = new File("stats.json");
    private Stats forStats;
    private MinesweeperBot bot;

    public StatsUpdater(Stats forStats, MinesweeperBot bot) {
        this.forStats = forStats;
        this.bot = bot;
    }

    @Override
    public void run() {
        while (bot.isActive()) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                interrupt();
                break;
            }
            forStats.setServers(bot.getJda().getGuilds().size());
            forStats.setPrivateChannels(bot.getJda().getPrivateChannels().size());
            writeStats(forStats);
            System.out.println("StatsUpdater wrote stats.");
        }
    }

    public void writeStats(Stats stats) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(STATS_FILE), StandardCharsets.UTF_8))) {
            gson.toJson(stats, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
