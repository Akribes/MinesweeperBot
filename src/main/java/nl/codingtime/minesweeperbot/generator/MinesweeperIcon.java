package nl.codingtime.minesweeperbot.generator;

public enum MinesweeperIcon {
    ZERO('0', ":white_large_square:"),
    ONE('1', ":one:"),
    TWO('2', ":two:"),
    THREE('3', ":three:"),
    FOUR('4', ":four:"),
    FIVE('5', ":five:"),
    SIX('6', ":six:"),
    SEVEN('7', ":seven:"),
    EIGHT('8', ":eight:"),
    MINE('m', ":bomb:");

    private final char character;
    private final String discord;

    MinesweeperIcon(char character, String discordString) {
        this.character = character;
        this.discord = discordString;
    }

    public static MinesweeperIcon valueOf(char character) {
        MinesweeperIcon result;
        switch (character){
            case '0':
                result = ZERO;
                break;
            case '1':
                result = ONE;
                break;
            case '2':
                result = TWO;
                break;
            case '3':
                result = THREE;
                break;
            case '4':
                result = FOUR;
                break;
            case '5':
                result = FIVE;
                break;
            case '6':
                result = SIX;
                break;
            case '7':
                result = SEVEN;
                break;
            case '8':
                result = EIGHT;
                break;
            case 'm':
                result = MINE;
                break;
            default:
                result = null;
        }
        return result;
    }

    public char getCharacter() {
        return character;
    }

    public String getDiscord() {
        return discord;
    }

    @Override
    public String toString() {
        return getDiscord();
    }
}
