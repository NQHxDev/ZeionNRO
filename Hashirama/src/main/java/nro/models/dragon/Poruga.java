package nro.models.dragon;

import nro.dialog.MenuDialog;
import nro.dialog.MenuRunable;
import nro.manager.NamekBallManager;
import nro.models.clan.Buff;
import nro.models.clan.Clan;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.server.io.Message;
import nro.services.NpcService;
import nro.services.Service;

import java.io.DataOutputStream;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */

public class Poruga extends AbsDragon {

    public Poruga(Player player) {
        super(player);
        this.setWishes(new String[]{"Tăng 20%\nsức đánh", "Tăng 20% HP", "Tăng 20% KI", "Tăng 10%\nchí mạng"});
        this.setTutorial("");
        this.setContent("Ta sẽ ban cho ngươi điều ước,ngươi có 5 phút,hãy suy nghĩ thật kĩ trước khi quyết định,tác dụng của chúc phúc sẽ có hiệu lực đến 6h AM");
        this.setName("Rồng thần Namek");
    }

    @Override
    public void openMenu() {

    }

    @Override
    public void summon() {
        setAppear(true);
        callDragon();
        showWishes();
        setLastTimeAppear(System.currentTimeMillis());
        new Thread(this).start();
        sendNotify();
    }

    @Override
    public void reSummon() {

    }

    @Override
    public void showWishes() {
        Clan clan = getSummoner().clan;
        MenuDialog menu = new MenuDialog(getContent(), getWishes(), new MenuRunable() {
            @Override
            public void run() {
                switch (this.getIndexSelected()) {
                    case 0:
                        clan.setBuff(Buff.BUFF_ATK);
                        break;
                    case 1:
                        clan.setBuff(Buff.BUFF_HP);
                        break;
                    case 2:
                        clan.setBuff(Buff.BUFF_KI);
                        break;
                    case 3:
                        clan.setBuff(Buff.BUFF_CRIT);
                        break;
                }
                for (Player player : clan.membersInGame) {
                    player.setBuff(clan.getBuff());
                    Service.getInstance().point(player);
                    Service.getInstance().sendThongBao(player,"Bạn vừa nhận được chúc phúc của rồng thần Poruga");
                }
                leave();
            }
        });
        menu.show(getSummoner());
    }

    @Override
    public void callDragon() {
        Message msg = new Message(-83);
        DataOutputStream ds = msg.writer();
        try {
            ds.writeByte(isAppear() ? 0 : (byte) 1);
            if (isAppear()) {
                Zone z = getSummoner().zone;
                ds.writeShort(z.map.mapId);
                ds.writeShort(z.map.bgId);
                ds.writeByte(z.zoneId);
                ds.writeInt((int) getSummonerID());
                ds.writeUTF("");
                ds.writeShort(getSummoner().location.x);
                ds.writeShort(getSummoner().location.y);
                ds.writeByte(1);
            }
            ds.flush();
            Service.getInstance().sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    @Override
    public void leave() {
        NpcService.gI().createTutorial(getSummoner(), -1, "Điều ước của ngươi đã trở thành sự thật\nHẹn gặp ngươi lần sau, ta đi ngủ đây, bái bai");

        setAppear(false);
        callDragon();
        NamekBallManager.gI().initFossil();
    }
}
