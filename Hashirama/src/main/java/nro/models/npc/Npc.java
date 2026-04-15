package nro.models.npc;

import nro.consts.ConstNpc;
import nro.models.map.Map;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.server.Manager;
import nro.server.io.Message;
import nro.services.MapService;
import nro.services.Service;
import nro.services.func.ShopService;
import nro.utils.Log;
import nro.utils.Util;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public abstract class Npc implements IAtionNpc {

    public int mapId;
    public Map map;

    public int status;

    public int cx;

    public int cy;

    public int tempId;

    public int avartar;
    
    public boolean lockDir; // NEW: true = không xoay theo player

    public BaseMenu baseMenu;

    protected Npc(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        this.map = MapService.gI().getMapById(mapId);
        this.mapId = mapId;
        this.status = status;
        this.cx = cx;
        this.cy = cy;
        this.tempId = tempId;
        this.avartar = avartar;
        this.lockDir = false; // default
        Manager.NPCS.add(this);
    }

    public void initBaseMenu(String text) {
        text = text.substring(1);
        String[] data = text.split("\\|");
        baseMenu = new BaseMenu();
        baseMenu.npcId = tempId;
        baseMenu.npcSay = data[0].replaceAll("<>", "\n");
        baseMenu.menuSelect = new String[data.length - 1];
        for (int i = 0; i < baseMenu.menuSelect.length; i++) {
            baseMenu.menuSelect[i] = data[i + 1].replaceAll("<>", "\n");
        }
    }

    public void createOtherMenu(Player player, int indexMenu, String npcSay, String... menuSelect) {
        Message msg;
        try {
            player.iDMark.setIndexMenu(indexMenu);
            msg = new Message(32);
            msg.writer().writeShort(tempId);
            msg.writer().writeUTF(npcSay);
            msg.writer().writeByte(menuSelect.length);
            for (String menu : menuSelect) {
                msg.writer().writeUTF(menu);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createOtherMenu(Player player, int indexMenu, String npcSay, String[] menuSelect, Object object) {
        NpcFactory.PLAYERID_OBJECT.put(player.id, object);
        Message msg;
        try {
            player.iDMark.setIndexMenu(indexMenu);
            msg = new Message(32);
            msg.writer().writeShort(tempId);
            msg.writer().writeUTF(npcSay);
            msg.writer().writeByte(menuSelect.length);
            for (String menu : menuSelect) {
                msg.writer().writeUTF(menu);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            player.iDMark.setIndexMenu(ConstNpc.BASE_MENU);
            try {
                if (baseMenu != null) {
                    baseMenu.openMenu(player);
                } else {
                    Message msg;
                    msg = new Message(32);
                    msg.writer().writeShort(tempId);
                    msg.writer().writeUTF("NPC " + this.tempId + " map " + this.mapId + " chưa làm!");
                    msg.writer().writeByte(1);
                    msg.writer().writeUTF("Đóng");
                    player.sendMessage(msg);
                    msg.cleanup();
                }
            } catch (Exception e) {
                Log.error(Npc.class, e);
            }
        }
    }

    public void npcChat(Player player, String text) {
        Message msg;
        try {
            msg = new Message(124);
            msg.writer().writeShort(tempId);
            msg.writer().writeUTF(text);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }
    
    public void npcChat(Zone zone, String text) {
        Message msg;
        try {
            msg = new Message(124);
            msg.writer().writeShort(tempId);
            msg.writer().writeUTF(text);
            Service.getInstance().sendMessAllPlayerInMap(zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public void npcChat(String text) {
        Message msg;
        try {
            msg = new Message(124);
            msg.writer().writeShort(tempId);
            msg.writer().writeUTF(text);
            for (Zone zone : map.zones) {
                Service.getInstance().sendMessAllPlayerInMap(zone, msg);
            }
            msg.cleanup();
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public void openShopWithGender(Player player, int shopId, int order) {
        ShopService.gI().openShopNormal(player, this, shopId, order, player.gender);
    }
    public void openShopWithGender2(Player player, int shopId, int order) {
        ShopService.gI().openShopSpecial(player, this, shopId, order, player.gender);
    }


    public void openShop(Player player, int shopId, int order) {
        ShopService.gI().openShopNormal(player, this, shopId, order, -1);
    }

    public boolean canOpenNpc(Player player) {
        if (this.tempId == ConstNpc.DAU_THAN) {
            if (player.zone.map.mapId == 21
                    || player.zone.map.mapId == 22
                    || player.zone.map.mapId == 23) {
                return true;
            } else {
                Service.getInstance().hideWaitDialog(player);
                Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                return false;
            }
        }
        if (player.zone.map.mapId == this.mapId
                && Util.getDistance(this.cx, this.cy, player.location.x, player.location.y) <= 60) {
            return true;
        } else {
            Service.getInstance().hideWaitDialog(player);
            Service.getInstance().sendThongBao(player, "Không thể thực hiện khi đứng quá xa");
            return false;
        }
    }

}
