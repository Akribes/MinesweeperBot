package nl.codingtime.minesweeperbot;

import java.util.HashSet;
import java.util.Set;

public class Stats {
    private long uses;
    private long mines;
    private long servers;
    private Set<Long> users;
    private Set<Long> privateChannelUsers;

    public Stats() {
        this(0, 0, 0, new HashSet<>(), new HashSet<>());
    }

    public Stats(long uses, long mines, long servers, Set<Long> users, Set<Long> privateChannelUsers) {
        this.uses = uses;
        this.mines = mines;
        this.servers = servers;
        this.users = users;
        this.privateChannelUsers = privateChannelUsers;
    }

    public long getUses() {
        return uses;
    }

    public void incrementUses() {
        this.uses++;
    }

    public long getMines() {
        return mines;
    }

    public void incrementMines(long amount) {
        this.mines += amount;
    }

    public long getServers() {
        return servers;
    }

    public void setServers(long servers) {
        this.servers = servers;
    }

    public Set<Long> getUserIds() {
        return users;
    }

    public void registerUser(long userId) {
        try {
            users.add(userId);
        } catch (UnsupportedOperationException e) {
            // Do nothing, user has already used me before
        }
    }

    public Set<Long> getPrivateChannelUserIds() {
        return privateChannelUsers;
    }

    public void registerPrivateChannelUser(long userId) {
        try {
            privateChannelUsers.add(userId);
        } catch (UnsupportedOperationException e) {
            // Do nothing, user has already messaged me before
        }
    }
}
