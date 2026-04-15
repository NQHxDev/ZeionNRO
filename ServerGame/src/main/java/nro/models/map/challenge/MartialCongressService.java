package nro.models.map.challenge;

import nro.consts.ConstMap;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.server.io.Message;
import nro.services.MapService;
import nro.services.Service;
import nro.services.func.ChangeMapService;
import nro.utils.Util;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
public class MartialCongressService {

    private static MartialCongressService i;

    public static MartialCongressService gI() {
        if (i == null) {
            i = new MartialCongressService();
        }
        return i;
    }

    public void startChallenge(Player player) {
        Zone zone = getMapChalllenge(ConstMap.DAI_HOI_VO_THUAT_129);
        if (zone != null) {
            ChangeMapService.gI().changeMap(player, zone, player.location.x, 360);
            Util.setTimeout(() -> {
                MartialCongress mc = new MartialCongress();
                mc.setPlayer(player);
                mc.setNpc(zone.getReferee());
                mc.toTheNextRound();
                MartialCongressManager.gI().add(mc);
                Service.getInstance().sendThongBao(player, "Số thứ tự của ngươi là 1\n chuẩn bị thi đấu nhé");
            }, 500);
        } else {

        }
    }

    public void moveFast(Player pl, int x, int y) {
        Message msg;
        try {
            msg = new Message(58);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            msg.writer().writeInt((int) pl.id);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendTypePK(Player player, Player boss) {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 35);
            msg.writer().writeInt((int) boss.id);
            msg.writer().writeByte(3);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public Zone getMapChalllenge(int mapId) {
        Zone map = MapService.gI().getMapWithRandZone(mapId);
        if (map.getNumOfBosses() < 1) {
            return map;
        }
        return null;
    }
}
