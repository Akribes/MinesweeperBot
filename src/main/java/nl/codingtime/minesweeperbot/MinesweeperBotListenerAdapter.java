package nl.codingtime.minesweeperbot;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import nl.codingtime.discordminesweepergenerator.MinesweeperPuzzleBuilder;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperBotListenerAdapter extends ListenerAdapter {
    private static final String GETTING_STARTED_MESSAGE = "Hi, I'm MinesweeperBot. I can generate Minesweeper puzzles" +
            " for you! To get started, DM or ping me the size and amount of mines for the puzzle. For example, if you" +
            " say `5/6/7`, I'll send you a puzzle of 5 by 6 squares with 7 mines. If you're completely new to" +
            " Minesweeper, read *How to play it?*, or you might get blown up by a mine!";
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

    public MinesweeperBotListenerAdapter(MinesweeperBot bot) {
        this.bot = bot;
    }

    @Override
    public void onReady(ReadyEvent event) {
        bot.setActive(true);
        bot.setupStats();
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
        String command =  message.replace("<@" + bot.getJda().getSelfUser().getId() + ">", "")
                .replace("!", "").trim();
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
}
