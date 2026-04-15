package nro.models.clan;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class ClanMessage {

    public static final byte BLACK = 0;
    public static final byte RED = 1;

    private final Clan clan;

    public int id;

    public byte type;

    public int playerId;

    public String playerName;

    public double playerPower;

    public byte role;

    public int time;

    public String text;

    public byte receiveDonate;

    public byte maxDonate;

    public byte isNewMessage;

    public byte color;

    public ClanMessage(Clan clan) {
        this.clan = clan;
        this.id = clan.clanMessageId++;
        this.isNewMessage = 1;
        this.time = (int) (System.currentTimeMillis() / 1000);
    }
}
