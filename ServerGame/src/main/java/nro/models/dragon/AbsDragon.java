package nro.models.dragon;

import lombok.Getter;
import lombok.Setter;
import nro.consts.Cmd;
import nro.models.player.Player;
import nro.network.io.Message;
import nro.services.Service;
import nro.utils.Util;

import java.io.IOException;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */

@Setter
@Getter
public abstract class AbsDragon implements Runnable {

    public String content;
    public String[] wishes;
    public String tutorial;
    public Player summoner;
    public long summonerID;
    public String name;
    public boolean appear;
    public long lastTimeAppear;

    public AbsDragon(Player player) {
        this.summoner = player;
        this.summonerID = player.id;
    }

    public abstract void openMenu();

    public abstract void summon();

    public abstract void reSummon();

    public abstract void showWishes();

    public void sendNotify() {
        Message m = Message.create(Cmd.SERVER_MESSAGE);
        try {
            m.writer().writeUTF(summoner.name + " vừa gọi " + name + " tại "
                    + summoner.zone.map.mapName + " khu vực " + summoner.zone.zoneId);
            Service.getInstance().sendMessAllPlayerIgnoreMe(summoner, m);
        } catch (IOException e) {
        }
    }

    public abstract void callDragon();

    public abstract void leave();

    @Override
    public void run() {
        while (appear) {
            if (Util.canDoWithTime(lastTimeAppear, 60000 * 5)) {
                leave();
            }
        }
    }
}