package nl.codingtime.minesweeperbot;

import com.fatboyindustrial.gsonjavatime.OffsetDateTimeConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import nl.codingtime.minesweeperbot.generator.MinesweeperPuzzleBuilder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.discordbots.api.client.DiscordBotListAPI;
import org.discordbots.api.client.io.UnsuccessfulHttpException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MinesweeperBotListenerAdapter extends ListenerAdapter {
    private static final String GETTING_STARTED_MESSAGE = "Hi, I'm MinesweeperBot. I can generate Minesweeper puzzles" +
            " for you! To get started, DM or ping me the size and amount of mines for the puzzle. For example, if you" +
            " say `5/6/7` (in servers: `@Minesweeper Bot#6157 5/6/7`), I'll send you a puzzle of 5 by 6 squares with 7 mines. If you're completely new to" + // TODO: Format the mention
            " Minesweeper, read *How to play it?*, or you might get blown up by a mine!\n\n" +
            "For support, you can join the Minesweeper Bot Discord server: https://discord.gg/uRXUEE4";
    private static final String HOW_TO_PLAY_MESSAGE = "A Minesweeper puzzle is a grid of squares you can click on." +
            " On this grid, several mines are located. The goal is to click all squares except for the squares with" +
            " mines. When you click on a square, one of these symbols is revealed:\n" +
            "- Nothing: this means that all eight squares around the square you clicked don't have mines.\n" +
            "- A number: this means that in the eight surrounding squares, that number of mines are hidden.\n" +
            "- A mine: game over!\n" +
            "There should be very little guessing involved. Think carefully!";
    private static final String INVITE_MESSAGE = "If you want me to, I can be in your server too! Here's an " +
            "authorization link: ";
    private static final String STATS_MESSAGE = "I keep track of what I'm doing. Here are some statistics about me:\n" +
            "I've hidden **%mines% mines** in **%puzzles% puzzles** for **%users% users** in **%guilds% servers** and" +
            " **%privateChannels% private channels.**";
    private MinesweeperBot bot;
    private DiscordBotListAPI api;
    private Gson gson;
    private OkHttpClient httpClient;

    public MinesweeperBotListenerAdapter(MinesweeperBot bot) {
        this.bot = bot;
        this.gson = (new GsonBuilder()).registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeConverter()).create();
    }

    @Override
    public void onReady(ReadyEvent event) {
        bot.setActive(true);
        bot.setupStats();

        if (bot.getConfig().getDiscordBotsToken() != null) {
            this.api = new DiscordBotListAPI.Builder()
                    .token(bot.getConfig().getDiscordBotsToken())
                    .botId(bot.getJda().getSelfUser().getId())
                    .build();
        }


        this.httpClient = (new OkHttpClient.Builder()).addInterceptor((chain) -> {
            Request req = chain.request().newBuilder().addHeader("authorization", bot.getConfig().getDivineToken()).build();
            return chain.proceed(req);
        }).build();

        try {
            publishStats();
        } catch (IOException e) {
            System.out.println("Can't post stats to bot lists!");
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String content = event.getMessage().getContentRaw();
        if (content.startsWith("<@" + bot.getJda().getSelfUser().getId() + ">") ||
                content.startsWith("<@!" + bot.getJda().getSelfUser().getId() + ">")) {
            handleCommand(content, event.getAuthor(), event.getChannel(), false);
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        handleCommand(event.getMessage().getContentRaw(), event.getAuthor(), event.getChannel(), true);
    }

    private void handleCommand(String message, User sender, MessageChannel channel, boolean privateChannel) {
        System.out.println(sender + ": " + message);
        String command =  message.replace("!", "")
                .replace("<@" + bot.getJda().getSelfUser().getId() + ">", "").trim();
        String[] puzzle = command.split("/");
        String result;
        if (!command.replaceFirst("/", "").contains("/")) {
            channel.sendMessage(new EmbedBuilder().setTitle("About MinesweeperBot")
                    .setDescription("Here's some information about me!")
                    .setColor(0xFFA901)
                    .setThumbnail(bot.getJda().getSelfUser().getAvatarUrl())
                    .addField("Let's play Minesweeper!", GETTING_STARTED_MESSAGE, false)
                    .addField("How to play it?", HOW_TO_PLAY_MESSAGE, false)
                    .addField("May I blow up your server?",
                            INVITE_MESSAGE + "https://bit.ly/MinesweeperBot", false)
                    .addField("Stats", STATS_MESSAGE
                            .replaceAll("%mines%", String.valueOf(bot.getStats().getMines()))
                            .replaceAll("%puzzles%", String.valueOf(bot.getStats().getUses()))
                            .replaceAll("%users%", String.valueOf(bot.getStats().getUserIds().size()))
                            .replaceAll("%guilds%", String.valueOf(bot.getStats().getServers()))
                            .replaceAll("%privateChannels%", String.valueOf(bot.getStats().getPrivateChannelUserIds().size())),
                            false)
                    .setFooter("Made by Mart#1056.", bot.getJda().getUserById(350609220846223362L).getAvatarUrl())
                    .build()).queue();
            return;
        } else if (Integer.parseInt(puzzle[0]) * Integer.parseInt(puzzle[1]) > bot.getConfig().getMaxSize()) {
            result = "%ping%, the puzzle you want me to generate will be too big!";
        } else {
            try {
                result = new MinesweeperPuzzleBuilder().withWidth(Integer.parseInt(puzzle[0]))
                        .withHeight(Integer.parseInt(puzzle[1])).withAmountOfMines(Integer.parseInt(puzzle[2])).build().toString();
                bot.getStats().incrementUses();
                bot.getStats().incrementMines(Long.parseLong(puzzle[2]));
                bot.getStats().registerUser(sender.getIdLong());
                if (privateChannel) {
                    bot.getStats().registerPrivateChannelUser(sender.getIdLong());
                }
            } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                result = "%ping%, make sure to follow this format: `width/height/mines` (all positive numbers)!";
            }
        }

        String[] lines = result.split("\n");

        List<String> messages = new ArrayList<>();
        StringBuilder part = new StringBuilder();
        for (String line : lines) {
            if ((part + line + "\n").length() < 2000) {
                part.append(line).append("\n");
            } else {
                messages.add(part.toString());
                part = new StringBuilder(line).append("\n");
            }
        }

        if (!part.toString().equals("")) {
            messages.add(part.toString());
        }

        for (int i = 0; i < messages.size(); i++) {
            String sentMessage = messages.get(i);
            messages.set(i, sentMessage.replace("%ping%", "<@" + sender.getId() + ">"));
        }

        for (String s : messages) {
            channel.sendMessage(s).queue();
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        try {
            this.publishStats();
        } catch (IOException e) {
            System.out.println("Can't post stats to bot lists!");
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        try {
            this.publishStats();
        } catch (IOException e) {
            System.out.println("Can't post stats to bot lists!");
        }
    }

    private void publishStats() throws IOException {
        // Divine Discord Bot List
        if (bot.getConfig().getDivineToken() != null ) {
            /*URL url2 = new URL("https://divinediscordbots.com/bot/" + bot.getJda().getSelfUser().getId() + "/stats");
            HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            byte[] out = ("{\"server_count\":" + bot.getStats().getServers() + "}").getBytes(StandardCharsets.UTF_8);

            connection.setFixedLengthStreamingMode(out.length);
            connection.setRequestProperty("authorization", bot.getConfig().getDivineToken());
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "UTF-8");
            connection.connect();
            connection.getOutputStream().write(out);*/

            System.out.println(String.format("Published server count (%d) to Divine Discord Bot List",
                    bot.getStats().getServers()));


            JSONObject json = (new JSONObject()).put("server_count", bot.getStats().getServers());
            HttpUrl url =  new HttpUrl.Builder()
                    .scheme("https")
                    .host("devinediscordbots.com")
                    .addPathSegment("bot")
                    .addPathSegment(bot.getJda().getSelfUser().getId())
                    .addPathSegment("stats")
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, json.toString());
            Request request = (new okhttp3.Request.Builder()).post(body).url(url).build();
            Call call = this.httpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {}

                @Override
                public void onResponse(Call call, Response response) {}
            });
        }
        // discordbots.org
        if (bot.getConfig().getDiscordBotsToken() != null) {
            api.setStats((int) bot.getStats().getServers());
            System.out.println(String.format("Published server count (%d) to discordbots.org",
                    bot.getStats().getServers()));
        }
    }
}
