package nl.codingtime.minesweeperbot;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Scanner;

public class App {
    private final static File CONFIGURATION_FILE = new File("config.properties");
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        if (!CONFIGURATION_FILE.exists()) {
            try {
                generateConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Configuration config = null;
        try {
            config = readConfiguration();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final MinesweeperBot[] bot = new MinesweeperBot[1];

        if (config != null) {
            Configuration finalConfig = config;
            new Thread(() -> {
                try {
                    bot[0] = new MinesweeperBot(finalConfig.getToken());
                } catch (LoginException e) {
                    System.out.println("Failed to login!");
                }
            }).start();
        } else {
            System.out.println("Failed to load the configuration!");
        }

        System.out.println("Press enter to exit.");
        scanner.nextLine();
        bot[0].shutdown();
    }

    private static void generateConfig() throws IOException {
        Properties config = new Properties();

        OutputStream out = new FileOutputStream(CONFIGURATION_FILE);

        System.out.println("Please enter a token: ");
        config.setProperty("token", scanner.nextLine());

        scanner.close();

        config.store(out, "This is the configuration for Minesweeper Bot.");
        out.close();
    }

    private static Configuration readConfiguration() throws IOException {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        Properties config = new Properties();

        InputStream in = new FileInputStream(CONFIGURATION_FILE);
        config.load(in);
        in.close();

        return builder.withToken(config.getProperty("token")).build();
    }
}
