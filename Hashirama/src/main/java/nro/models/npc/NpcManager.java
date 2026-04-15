package nro.models.npc;

import nro.consts.ConstNpc;
import nro.consts.ConstTask;
import nro.models.player.Player;
import nro.server.Manager;
import nro.services.TaskService;

import java.util.ArrayList;
import java.util.List;
import nro.consts.ConstEvent;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class NpcManager {

    public static Npc getByIdAndMap(int id, int mapId) {
        for (Npc npc : Manager.NPCS) {
            if (npc.tempId == id && npc.mapId == mapId) {
                return npc;
            }
        }
        return null;
    }

    public static Npc getNpc(byte tempId) {
        for (Npc npc : Manager.NPCS) {
            if (npc.tempId == tempId) {
                return npc;
            }
        }
        return null;
    }

    public static List<Npc> getNpcsByMapPlayer(Player player) {
        List<Npc> list = new ArrayList<>();
        if (player.zone != null) {
            for (Npc npc : player.zone.map.npcs) {
                if (npc.tempId == ConstNpc.NOI_BANH && Manager.EVENT_SEVER != 4) {
                    continue;
                }
                if (npc.tempId == ConstNpc.ANDROID_AODAI && Manager.EVENT_SEVER != ConstEvent.TET_2024) {
                    continue;
                }
                if (npc.tempId == ConstNpc.QUA_TRUNG && player.mabuEgg == null) {
                    continue;
                } else if (npc.tempId == ConstNpc.CALICK && TaskService.gI().getIdTask(player) < ConstTask.TASK_20_0) {
                    continue;
                }
                list.add(npc);
            }
        }
        return list;
    }
}
