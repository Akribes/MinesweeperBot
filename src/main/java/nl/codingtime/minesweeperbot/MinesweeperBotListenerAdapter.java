package nl.codingtime.minesweeperbot;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import nl.codingtime.discordminesweepergenerator.MinesweeperPuzzleBuilder;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperBotListenerAdapter extends ListenerAdapter {
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
        if (event.getMessage().getContentRaw().startsWith("<@" + bot.getJda().getSelfUser().getId() + ">")) {
            generateMessages(event.getMessage().getContentRaw(), event.getAuthor()).forEach(message -> event.getChannel().sendMessage(
                    message).queue());
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        generateMessages(event.getMessage().getContentRaw(), event.getAuthor()).forEach(message -> event.getChannel().sendMessage(
                message).queue());
    }

    private List<String> generateMessages(String message, User requestingUser) {
        String command =  message.replace("<@" + bot.getJda().getSelfUser().getId() + ">", "").trim();
        String[] puzzle = command.split("/");
        String result = "%ping%, make sure to follow this format: `width/height/mines` (all positive numbers)!";
        if (Integer.parseInt(puzzle[0]) * Integer.parseInt(puzzle[1]) > bot.getConfig().getMaxSize()) {
            result = "%ping%, the puzzle you want me to generate will be too big!";
        }
        try {
            result = new MinesweeperPuzzleBuilder().withWidth(Integer.parseInt(puzzle[0]))
                    .withHeight(Integer.parseInt(puzzle[1])).withAmountOfMines(Integer.parseInt(puzzle[2])).build().toString();
            bot.getStats().incrementUses();
            bot.getStats().incrementMines(Long.parseLong(puzzle[2]));
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            // Nothing wrong, just tell the user that he made a mistake
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
            messages.set(i, sentMessage.replace("%ping%", "<@" + requestingUser.getId() + ">"));
        }

        bot.getStats().registerUser(requestingUser.getIdLong());

        return messages;
    }
}
