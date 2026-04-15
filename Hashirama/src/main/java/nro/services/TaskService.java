package nro.services;

import nro.consts.*;
import nro.models.boss.Boss;
import nro.models.boss.BossFactory;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.map.Zone;
import nro.models.mob.Mob;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.models.task.Achivement;
import nro.models.task.SideTaskTemplate;
import nro.models.task.SubTaskMain;
import nro.models.task.TaskMain;
import nro.server.Manager;
import nro.server.io.Message;
import nro.utils.Log;
import nro.utils.Util;
import nro.consts.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nro.jdbc.daos.PlayerDAO;
import nro.models.boss.bosstuonglai.*;
import nro.models.boss.cell.*;
import nro.models.boss.fide.*;
import nro.models.boss.nappa.*;
import nro.models.boss.robotsatthu.*;
import nro.models.boss.robotsatthu.*;
import nro.models.boss.tieudoisatthu.*;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 */
public class TaskService {

    /**
     * Làm cùng số người trong bang
     */
    private static final byte NMEMBER_DO_TASK_TOGETHER = 0;
    // id vật phẩm nhận thưởng nhiệm vụ
    private static final short ID_THUONG_NHIEMVU = 457;
    // id vật phẩm nhận thưởng thành tựu
    private static final short ID_THUONG_THANHTUU = 1567;
    // Số lượng phần thưởng nhiệm vụ
    private static final int THUONG_NV_15 = 1000;
    private static final int THUONG_NV_16 = 1000;
    private static final int THUONG_NV_17 = 1000;
    private static final int THUONG_NV_18 = 1000;
    private static final int THUONG_NV_19 = 2000;
    private static final int THUONG_NV_20 = 3000;
    private static final int THUONG_NV_21 = 4000;
    private static final int THUONG_NV_22 = 5000;
    private static final int THUONG_NV_23 = 6000;
    private static final int THUONG_NV_24 = 7000;
    private static final int THUONG_NV_25 = 8000;
    private static final int THUONG_NV_26 = 9000;
    private static final int THUONG_NV_27 = 10000;
    private static final int THUONG_NV_28 = 15000;
    private static final int THUONG_NV_29 = 17500;
    private static final int THUONG_NV_30 = 20000;
    private static final int THUONG_NV_31 = 25000;
    private static final int THUONG_NV_32 = 30000;
    private static final int THUONG_NV_33 = 35000;
    private static final int THUONG_NV_34 = 40000;
    private static final int THUONG_NV_35 = 45000;
    private static final int THUONG_NV_36 = 50000;
    private static final int THUONG_NV_37 = 60000;
    private static final int THUONG_NV_38 = 70000;
    private static final int THUONG_NV_39 = 80000;
    private static final int THUONG_NV_40 = 100000;
    

    private static TaskService i;

    public static TaskService gI() {
        if (i == null) {
            i = new TaskService();
        }
        return i;
    }

    public TaskMain getTaskMainById(Player player, int id) {
        for (TaskMain tm : Manager.TASKS) {
            if (tm.id == id) {
                TaskMain newTaskMain = new TaskMain(tm);
                newTaskMain.detail = transformName(player, newTaskMain.detail);
                for (SubTaskMain stm : newTaskMain.subTasks) {
                    stm.mapId = (short) transformMapId(player, stm.mapId);
                    stm.npcId = (byte) transformNpcId(player, stm.npcId);
                    stm.notify = transformName(player, stm.notify);
                    stm.name = transformName(player, stm.name);
                }
                return newTaskMain;
            }
        }
        return player.playerTask.taskMain;
    }

    //gửi thông tin nhiệm vụ chính
    public void sendTaskMain(Player player) {
        Message msg;
        try {
            msg = new Message(40);
            msg.writer().writeShort(player.playerTask.taskMain.id);
//            msg.writer().writeShort(12);
            msg.writer().writeByte(player.playerTask.taskMain.index);
//            msg.writer().writeUTF(player.playerTask.taskMain.name); [" + player.playerTask.taskMain.id + "]
            msg.writer().writeUTF(player.playerTask.taskMain.name + "");
            msg.writer().writeUTF(player.playerTask.taskMain.detail);
            msg.writer().writeByte(player.playerTask.taskMain.subTasks.size());
            for (SubTaskMain stm : player.playerTask.taskMain.subTasks) {
                msg.writer().writeUTF(stm.name);
                msg.writer().writeByte(stm.npcId);
                msg.writer().writeShort(stm.mapId);
                msg.writer().writeUTF(stm.notify);
            }
            msg.writer().writeShort(player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count);
            for (SubTaskMain stm : player.playerTask.taskMain.subTasks) {
                msg.writer().writeShort(stm.maxCount);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Log.error(TaskService.class, e);
        }
    }

    //chuyển sang task mới
    public void sendNextTaskMain(Player player) {
        rewardDoneTask(player);
        player.playerTask.taskMain = TaskService.gI().getTaskMainById(player, player.playerTask.taskMain.id + 1);
        sendTaskMain(player);
        Service.getInstance().sendThongBao(player, "Nhiệm vụ tiếp theo của bạn là "
                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).name);
    }

    //số lượng đã hoàn thành
    public void sendUpdateCountSubTask(Player player) {
        Message msg;
        try {
            msg = new Message(43);
            msg.writer().writeShort(player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    //chuyển sub task tiếp theo
    public void sendNextSubTask(Player player) {
        Message msg;
        try {
            msg = new Message(41);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    //gửi thông tin nhiệm vụ hiện tại
    public void sendInfoCurrentTask(Player player) {
        Service.getInstance().sendThongBao(player, "Nhiệm vụ hiện tại của bạn là "
                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).name);
    }

    public boolean checkDoneTaskTalkNpc(Player player, Npc npc) {
        switch (npc.tempId) {
            case ConstNpc.QUY_LAO_KAME:
            case ConstNpc.TRUONG_LAO_GURU:
            case ConstNpc.VUA_VEGETA:
                return (doneTask(player, ConstTask.TASK_9_1)
                        || doneTask(player, ConstTask.TASK_10_2)
                        || doneTask(player, ConstTask.TASK_11_3)
                        || doneTask(player, ConstTask.TASK_12_2)
                        || doneTask(player, ConstTask.TASK_13_1)
                        || doneTask(player, ConstTask.TASK_14_3)
                        || doneTask(player, ConstTask.TASK_15_3)
                        || doneTask(player, ConstTask.TASK_16_3));
            case ConstNpc.ONG_GOHAN:
            case ConstNpc.ONG_MOORI:
            case ConstNpc.ONG_PARAGUS:
                return (doneTask(player, ConstTask.TASK_0_2)
                        || doneTask(player, ConstTask.TASK_0_5)
                        || doneTask(player, ConstTask.TASK_1_1)
                        || doneTask(player, ConstTask.TASK_2_1)
                        || doneTask(player, ConstTask.TASK_3_2)
                        || doneTask(player, ConstTask.TASK_4_1)
                        || doneTask(player, ConstTask.TASK_5_3)
                        || doneTask(player, ConstTask.TASK_8_2)
                        || doneTask(player, ConstTask.TASK_12_1));
            case ConstNpc.DR_DRIEF:
            case ConstNpc.CARGO:
            case ConstNpc.CUI:
                return (doneTask(player, ConstTask.TASK_6_1)
                        || doneTask(player, ConstTask.TASK_7_2)
                        || player.zone.map.mapId == 19 && doneTask(player, ConstTask.TASK_17_1)
                        || player.zone.map.mapId == 19 && doneTask(player, ConstTask.TASK_18_5)
                        || player.zone.map.mapId == 19 && doneTask(player, ConstTask.TASK_19_3)
                        || player.zone.map.mapId == 19 && doneTask(player, ConstTask.TASK_20_6)
                        || player.zone.map.mapId == 19 && doneTask(player, ConstTask.TASK_21_4));
            case ConstNpc.BUNMA:
            case ConstNpc.DENDE:
            case ConstNpc.APPULE:
                return doneTask(player, ConstTask.TASK_8_1);
            case ConstNpc.BUNMA_TL:
                return (doneTask(player, ConstTask.TASK_22_0)
                        || doneTask(player, ConstTask.TASK_22_4)
                        || doneTask(player, ConstTask.TASK_23_4)
                        || doneTask(player, ConstTask.TASK_24_4)
                        || doneTask(player, ConstTask.TASK_25_5)
                        || doneTask(player, ConstTask.TASK_26_4)
                        || doneTask(player, ConstTask.TASK_27_2)
                        || doneTask(player, ConstTask.TASK_28_3)
                        || doneTask(player, ConstTask.TASK_31_4)
                        || doneTask(player, ConstTask.TASK_29_4)
                        || doneTask(player, ConstTask.TASK_30_1));
            case ConstNpc.BARDOCK:
                return (doneTask(player, ConstTask.TASK_32_1)
                        || doneTask(player, ConstTask.TASK_33_0)
                        || doneTask(player, ConstTask.TASK_33_4));
            case ConstNpc.BERRY:
                return doneTask(player, ConstTask.TASK_32_3);
            case ConstNpc.EMI_FUKADA:
                return (doneTask(player, ConstTask.TASK_34_0)
                        || doneTask(player, ConstTask.TASK_35_0)
                        || doneTask(player, ConstTask.TASK_36_0)
                        || doneTask(player, ConstTask.TASK_37_0)
                        || doneTask(player, ConstTask.TASK_38_0)
                        || doneTask(player, ConstTask.TASK_39_0)
                        || doneTask(player, ConstTask.TASK_40_0)
                        || doneTask(player, ConstTask.TASK_41_0)
                        || doneTask(player, ConstTask.TASK_42_0)
                        );
        }
        return false;
    }

    //kiểm tra hoàn thành nhiệm vụ gia nhập bang hội
    public void checkDoneTaskJoinClan(Player player) {
        if (!player.isBoss && !player.isPet) {
            doneTask(player, ConstTask.TASK_13_0);
        }
    }

    //kiểm tra hoàn thành nhiệm vụ lấy item từ rương
    public void checkDoneTaskGetItemBox(Player player) {
        if (!player.isBoss && !player.isPet) {
            doneTask(player, ConstTask.TASK_0_3);
        }
    }

    //kiểm tra hoàn thành nhiệm vụ sức mạnh
    public void checkDoneTaskPower(Player player, double power) {
        if (!player.isBoss && !player.isPet) {
            if (power >= 20000) {
                doneTask(player, ConstTask.TASK_5_0);
            }
            if (power >= 35000) {
                doneTask(player, ConstTask.TASK_5_1);
            }
            if (power >= 60000) {
                doneTask(player, ConstTask.TASK_5_2);
            }
            if (power >= 200000) {
                doneTask(player, ConstTask.TASK_10_0);
            }
            if (power >= 600000000) {
                doneTask(player, ConstTask.TASK_20_0);
            }
            if (power >= 2000000000L) {
                doneTask(player, ConstTask.TASK_21_0);
            }
        }
    }

    //kiểm tra hoàn thành nhiệm vụ khi player sử dụng tiềm năng
    public void checkDoneTaskUseTiemNang(Player player) {
        if (!player.isBoss && !player.isPet) {
            doneTask(player, ConstTask.TASK_3_0);
        }
    }

    //kiểm tra hoàn thành nhiệm vụ khi vào map nào đó
    public void checkDoneTaskGoToMap(Player player, Zone zoneJoin) {
        if (!player.isBoss && !player.isPet && !player.isMiniPet && !player.isBot) {
            switch (zoneJoin.map.mapId) {
                case 39:
                case 40:
                case 41:
                    if (player.location.x >= 635) {
                        doneTask(player, ConstTask.TASK_0_0);
                    }
                    break;
                case 21:
                case 22:
                case 23:
                    doneTask(player, ConstTask.TASK_0_1);
                    doneTask(player, ConstTask.TASK_12_0);
                    break;
                case 24:
                case 25:
                case 26:
                    doneTask(player, ConstTask.TASK_6_0);
                    break;
                case 3:
                case 11:
                case 17:
                    doneTask(player, ConstTask.TASK_7_0);
                    break;
                case 0:
                case 7:
                case 14:
                    doneTask(player, ConstTask.TASK_8_0);
                    break;
                case 5:
                case 13:
                case 20:
                    doneTask(player, ConstTask.TASK_9_0);
                    break;
                case 19:
                    doneTask(player, ConstTask.TASK_17_0);
                    break;
                case 93:
                    doneTask(player, ConstTask.TASK_22_1);
                    break;
                case 97:
                    doneTask(player, ConstTask.TASK_23_0);
                    break;
                case 100:
                    doneTask(player, ConstTask.TASK_24_0);
                    break;
                case 103:
                    doneTask(player, ConstTask.TASK_28_0);
                    break;
            }
        }
    }

    //kiểm tra hoàn thành nhiệm vụ khi nhặt item
    public void checkDoneTaskPickItem(Player player, ItemMap item) {
        if (!player.isBoss && !player.isPet && item != null) {
            switch (item.itemTemplate.id) {
                case 73: //đùi gà
                    doneTask(player, ConstTask.TASK_2_0);
                    break;
                case 78: //em bé
                    doneTask(player, ConstTask.TASK_3_1);
                    Service.getInstance().sendFlagBag(player);
                    break;
                case 15: //ngọc rồng 2s
                    if (player.zone.map.mapId == 103) {
                        doneTask(player, ConstTask.TASK_27_1);
                    }
                    break;
                case 380: //cskb
                    doneTask(player, ConstTask.TASK_26_3);
                    break;
                case 992: //nhẫn thời không
                    doneTask(player, ConstTask.TASK_31_0);
                    break;
                case 865: //kiếm Z
                    doneTask(player, ConstTask.TASK_31_1);
                    break;
                case 874: //rùa con
                    doneTask(player, ConstTask.TASK_31_2);
                    break;
                case 725: //siêu thần thủy
                    doneTask(player, ConstTask.TASK_31_3);
                    break;
                case 993: //giỏ thức ăn
                    doneTask(player, ConstTask.TASK_32_2);
                    break;
            }
        }
    }

    //kiểm tra hoàn thành nhiệm vụ kết bạn
    public void checkDoneTaskMakeFriend(Player player, Player friend) {
        if (!player.isBoss && !player.isPet) {
            switch (friend.gender) {
                case ConstPlayer.TRAI_DAT:
                    doneTask(player, ConstTask.TASK_11_0);
                    doneTask(player, ConstTask.TASK_27_0);
                    break;
                case ConstPlayer.NAMEC:
                    doneTask(player, ConstTask.TASK_11_1);
                    doneTask(player, ConstTask.TASK_27_0);
                    break;
                case ConstPlayer.XAYDA:
                    doneTask(player, ConstTask.TASK_11_2);
                    doneTask(player, ConstTask.TASK_27_0);
                    break;
            }
        }
    }

    //kiểm tra hoàn thành nhiệm vụ khi xác nhận menu npc nào đó
    public void checkDoneTaskConfirmMenuNpc(Player player, Npc npc, byte select) {
        if (!player.isBoss && !player.isPet) {
            switch (npc.tempId) {
                case ConstNpc.DAU_THAN:
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MAGIC_TREE_NON_UPGRADE_LEFT_PEA:
                        case ConstNpc.MAGIC_TREE_NON_UPGRADE_FULL_PEA:
                            if (select == 0) {
                                doneTask(player, ConstTask.TASK_0_4);
                            }
                    }
                    break;
            }
        }
    }

// kiểm tra hoàn thành nhiệm vụ khi tiêu diệt được boss

    public void checkDoneTaskKillBoss(Player player, Boss boss) {
        if (player == null || boss == null || player.isBoss || player.isPet) {
            return;
        }

        // ================= NHIỆM VỤ CŨ (19) – KUKU, MAP ĐẬU ĐINH, RAMBO =================
        if (boss instanceof Kuku) {
            doneTask(player, ConstTask.TASK_19_0);
            return;
        }
        if (boss instanceof MapDauDinh) {
            doneTask(player, ConstTask.TASK_19_1);
            return;
        }
        if (boss instanceof Rambo) {
            doneTask(player, ConstTask.TASK_19_2);
            return;
        }

        // ================= NHIỆM VỤ 20 – TIỂU ĐỘI SÁT THỦ =================
        // Cho phép cả bản _nm lẫn bản thường tính nhiệm vụ (nếu bạn muốn).
        if (boss instanceof So4) {
            doneTask(player, ConstTask.TASK_20_1);
            return;
        }
        if (boss instanceof So3) {
            doneTask(player, ConstTask.TASK_20_2);
            return;
        }
        if (boss instanceof So2) {
            doneTask(player, ConstTask.TASK_20_3);
            return;
        }
        if (boss instanceof So1) {
            doneTask(player, ConstTask.TASK_20_4);
            return;
        }
        if (boss instanceof TieuDoiTruong) {
            doneTask(player, ConstTask.TASK_20_5);
            return;
        }

        // ================= NHIỆM VỤ 21 – FIDE ĐẠI CA =================
        if (boss instanceof FideDaiCa1) {
            doneTask(player, ConstTask.TASK_21_1);
            return;
        }
        if (boss instanceof FideDaiCa2) {
            doneTask(player, ConstTask.TASK_21_2);
            return;
        }
        if (boss instanceof FideDaiCa3) {
            doneTask(player, ConstTask.TASK_21_3);
            return;
        }

        // ================= NHIỆM VỤ 22 – ANDROID 19/20 =================
        if (boss instanceof Android19) {
            doneTask(player, ConstTask.TASK_22_2);
            return;
        }
        if (boss instanceof Android20) {
            doneTask(player, ConstTask.TASK_22_3);
            return;
        }

        // ================= NHIỆM VỤ 23 – POC, PIC, KING KONG =================
        if (boss instanceof Poc) {
            doneTask(player, ConstTask.TASK_23_1);
            return;
        }
        if (boss instanceof Pic) {
            doneTask(player, ConstTask.TASK_23_2);
            return;
        }
        if (boss instanceof KingKong) {
            doneTask(player, ConstTask.TASK_23_3);
            return;
        }

        // ================= NHIỆM VỤ 24 – XEN BỌ HUNG =================
        if (boss instanceof XenBoHung1) {
            doneTask(player, ConstTask.TASK_24_1);
            return;
        }
        if (boss instanceof XenBoHung2) {
            doneTask(player, ConstTask.TASK_24_2);
            return;
        }
        if (boss instanceof XenBoHungHoanThien) {
            doneTask(player, ConstTask.TASK_24_3);
            return;
        }

        // ================= NHIỆM VỤ 28 – XEN CON / SIÊU BỌ HUNG =================
        if (boss instanceof XenCon) {
            doneTask(player, ConstTask.TASK_28_1);
            return;
        }
        if (boss instanceof SieuBoHung) {
            doneTask(player, ConstTask.TASK_28_2);
            return;
        }

        // ================= NHIỆM VỤ 30 – BLACK GOKU =================
        if (boss instanceof Blackgoku) {
            doneTask(player, ConstTask.TASK_30_0);
            return;
        }

    }


    //kiểm tra hoàn thành nhiệm vụ khi giết được quái
    public void checkDoneTaskKillMob(Player player, Mob mob) {
        if (!player.isBoss && !player.isPet && !player.isBot) {
            switch (mob.tempId) {
                case ConstMob.MOC_NHAN:
                    doneTask(player, ConstTask.TASK_1_0);
                    break;
                case ConstMob.KHUNG_LONG_ME:
                case ConstMob.LON_LOI_ME:
                case ConstMob.QUY_DAT_ME:
                    doneTask(player, ConstTask.TASK_4_0);
                    break;
                case ConstMob.THAN_LAN_BAY:
                case ConstMob.PHI_LONG:
                case ConstMob.QUY_BAY:
                    doneTask(player, ConstTask.TASK_7_1);
                    break;
                case ConstMob.OC_MUON_HON:
                case ConstMob.OC_SEN:
                case ConstMob.HEO_XAYDA_ME:
                    doneTask(player, ConstTask.TASK_10_1);
                    break;
                case ConstMob.HEO_RUNG:
                case ConstMob.HEO_DA_XANH:
                case ConstMob.HEO_XAYDA:
                    if (player.clan != null) {
                        List<Player> list = new ArrayList<>();
                        List<Player> playersMap = player.zone.getPlayers();
                        synchronized (playersMap) {
                            for (Player pl : playersMap) {
                                if (pl != null && pl.clan != null && pl.clan.equals(player.clan)) {
                                    list.add(pl);
                                }
                            }
                        }
                        if (list.size() >= NMEMBER_DO_TASK_TOGETHER) {
                            for (Player pl : list) {
                                switch (mob.tempId) {
                                    case ConstMob.HEO_RUNG:
                                        doneTask(pl, ConstTask.TASK_14_0);
                                        break;
                                    case ConstMob.HEO_DA_XANH:
                                        doneTask(pl, ConstTask.TASK_14_1);
                                        break;
                                    case ConstMob.HEO_XAYDA:
                                        doneTask(pl, ConstTask.TASK_14_2);
                                        break;
                                }
                            }
                        }
                    }
                    break;
                case ConstMob.BULON:
                case ConstMob.UKULELE:
                case ConstMob.QUY_MAP:
                    if (player.clan != null) {
                        List<Player> list = new ArrayList<>();
                        List<Player> playersMap = player.zone.getPlayers();
                        synchronized (playersMap) {
                            for (Player pl : playersMap) {
                                if (pl != null && pl.clan != null && pl.clan.equals(player.clan)) {
                                    list.add(pl);
                                }
                            }
                        }
                        if (list.size() >= NMEMBER_DO_TASK_TOGETHER) {
                            for (Player pl : list) {
                                switch (mob.tempId) {
                                    case ConstMob.BULON:
                                        doneTask(pl, ConstTask.TASK_15_0);
                                        break;
                                    case ConstMob.UKULELE:
                                        doneTask(pl, ConstTask.TASK_15_1);
                                        break;
                                    case ConstMob.QUY_MAP:
                                        doneTask(pl, ConstTask.TASK_15_2);
                                        break;
                                }
                            }
                        }
                    }
                    break;
                case ConstMob.TAMBOURINE:
                    doneTask(player, ConstTask.TASK_16_0);
                    break;
                case ConstMob.DRUM:
                    doneTask(player, ConstTask.TASK_16_1);
                    break;
                case ConstMob.AKKUMAN:
                    doneTask(player, ConstTask.TASK_16_2);
                    break;
                case ConstMob.NAPPA:
                    doneTask(player, ConstTask.TASK_18_0);
                    break;
                case ConstMob.SOLDIER:
                    doneTask(player, ConstTask.TASK_18_1);
                    break;
                case ConstMob.APPULE:
                    doneTask(player, ConstTask.TASK_18_2);
                    break;
                case ConstMob.RASPBERRY:
                    doneTask(player, ConstTask.TASK_18_3);
                    break;
                case ConstMob.THAN_LAN_XANH:
                    doneTask(player, ConstTask.TASK_18_4);
                    break;
                case ConstMob.XEN_CON_CAP_1:
                    doneTask(player, ConstTask.TASK_25_0);
                    break;
                case ConstMob.XEN_CON_CAP_2:
                    doneTask(player, ConstTask.TASK_25_1);
                    break;
                case ConstMob.XEN_CON_CAP_3:
                    doneTask(player, ConstTask.TASK_25_2);
                    break;
                case ConstMob.XEN_CON_CAP_4:
                    doneTask(player, ConstTask.TASK_25_3);
                    break;
                case ConstMob.XEN_CON_CAP_5:
                    doneTask(player, ConstTask.TASK_25_4);
                    break;
                //-------------------------------------   
                case ConstMob.XEN_CON_CAP_6:
                    doneTask(player, ConstTask.TASK_26_0);
                    break;
                case ConstMob.XEN_CON_CAP_7:
                    doneTask(player, ConstTask.TASK_26_1);
                    break;
                case ConstMob.XEN_CON_CAP_8:
                    doneTask(player, ConstTask.TASK_26_2);
                    break;
                //------------------
                case ConstMob.TAI_TIM:
                    doneTask(player, ConstTask.TASK_29_0);
                    break;
                case ConstMob.KADO:
                    doneTask(player, ConstTask.TASK_29_1);
                    break;
                case ConstMob.ABO:
                    doneTask(player, ConstTask.TASK_29_2);
                    break;
                case ConstMob.DA_XANH:
                    doneTask(player, ConstTask.TASK_29_3);
                    break;
                case ConstMob.CABIRA:
                    doneTask(player, ConstTask.TASK_33_1);
                    doneTask(player, ConstTask.TASK_33_3);
                    break;
                case ConstMob.TOBI:
                    doneTask(player, ConstTask.TASK_33_2);
                    doneTask(player, ConstTask.TASK_33_3);
                    break;
            }
            if (MobService.gI().isMonterFly(mob.tempId)) {
//                player.playerTask.achivements.get(ConstAchive.THO_SAN_THIEN_XA).count++;
            }
        }
    }

    //xong nhiệm vụ nào đó
    public boolean doneTask(Player player, int idTaskCustom) {
        if(player.isBot) return false;
        if (TaskService.gI().isCurrentTask(player, idTaskCustom)) {
            this.addDoneSubTask(player, 1);
            switch (idTaskCustom) {
                case ConstTask.TASK_0_0:
                    NpcService.gI().createTutorial(player, -1, transformName(player, "Làm tốt lắm..\n"
                            + "Bây giờ bạn hãy vào nhà ông %2 bên phải để nhận nhiệm vụ mới nhé"));
                    break;
                case ConstTask.TASK_0_1:
                    NpcService.gI().createTutorial(player, -1, transformName(player, "Ông %2 đang đứng đợi kìa\n"
                            + "Hãy nhấn 2 lần vào để nói chuyện"));
                    break;
                case ConstTask.TASK_0_2:
                    npcSay(player, ConstTask.NPC_NHA,
                            "Con vừa đi đâu về đó?\n"
                            + "Con hãy đến rương đồ để lấy rađa..\n"
                            + "..sau đó thu hoạch hết đậu trên cây đậu thần đằng kia!");
                    break;
                case ConstTask.TASK_0_3:
                    break;
                case ConstTask.TASK_0_4:
                    break;
                case ConstTask.TASK_0_5:
                    npcSay(player, ConstTask.NPC_NHA,
                            "Tốt lắm, rađa sẽ giúp con thấy được lượng máu và thể lực ở bên góc trái\n"
                            + "Bây giờ con hãy đi luyện tập\n"
                            + "Con hãy ra %1, ở đó có những con mộc nhân cho con luyện tập dó\n"
                            + "Hãy đốn ngã 5 con mộc nhân cho ông");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_1_0:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.getInstance().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount + " mộc nhân");
                    }
                    break;
                case ConstTask.TASK_1_1:
                    npcSay(player, ConstTask.NPC_NHA,
                            "Thể lực của con cũng khá tốt\n"
                            + "Con à, dạo gần đây dân làng của chúng ta gặp phải vài chuyện\n"
                            + "Bên cạnh làng ta đột nhiên xuất hiện lũ quái vật\n"
                            + "Nó tàn sát dân làng và phá hoại nông sản làng ta\n"
                            + "Con hãy tìm đánh chúng và đem về đây 10 cái đùi gà, 2 ông cháu mình sẽ để dành ăn dần\n"
                            + "Đây là tấm bản đồ của vùng này, con hãy xem để tìm đến %3\n"
                            + "Con có thể sử dụng đậu thần khi hết HP hoặc KI, bằng cách nhấn vào nút có hình trái tim "
                            + "bên góc phải dưới màn hình\n"
                            + "Nhanh lên, ông đói lắm rồi");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_2_0:
                    break;
                case ConstTask.TASK_2_1:
                    InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBagByTemp(player, 73), 10);
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().dropItemMapForMe(player, player.zone.getItemMapByTempId(74));
                    npcSay(player, ConstTask.NPC_NHA,
                            "Tốt lắm, đùi gà đây rồi, haha. Ông sẽ nướng tại đống lửa gần kia con có thể ăn bất cứ lúc nào nếu muốn\n"
                            + "À cháu này, vừa nãy ông có nghe thấy 1 tiếng động lớn, hình như có 1 vật thể rơi tại %5, con hãy đến kiểm tra xem\n"
                            + "Con cũng có thể dùng tiềm năng bản thân để nâng HP, KI hoặc sức đánh");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_3_0:
                    break;
                case ConstTask.TASK_3_1:
                    break;
                case ConstTask.TASK_3_2:
                    InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBagByTemp(player, 78), 1);
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendFlagBag(player);
                    npcSay(player, ConstTask.NPC_NHA,
                            "Có em bé trong phi thuyền rơi xuống à, ông cứ tưởng là sao băng chứ\n"
                            + "Ông sẽ đặt tên cho em nó là Goku, từ giờ nó sẽ là thành viên trong gia đình ta\n"
                            + "Nãy ông mới nhận được tin có bầy mãnh thú xuất hiện tại Trạm phi thuyền\n"
                            + "Bọn chúng vừa đổ bộ xuống trái đất để trả thù việc con sát hại con chúng\n"
                            + "Con hãy đi tiêu diệt chúng để giúp dân làng tại đó luôn nhé");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_4_0:
                    if (isCurrentTask(player, idTaskCustom)) {
                        Service.getInstance().sendThongBao(player, "Bạn đánh được "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count + "/"
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount
                                + transformName(player, " %4 mẹ"));
                    }
                    break;
                case ConstTask.TASK_4_1:
                    npcSay(player, ConstTask.NPC_NHA,
                            "Ông rất tự hào về con\n"
                            + "Ông cho con cuốn bí kíp này để nâng cao võ học\n"
                            + "Hãy dùng sức mạnh của mình trừ gian diệt ác bảo vệ dân lành con nhé\n"
                            + "Bây giờ con hãy đi tập luyện đi, khi nào mạnh hơn thì quay về đây ông giao cho nhiệm vụ mới\n"
                            + "Đi đi..");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_5_0:
                    break;
                case ConstTask.TASK_5_1:
                    break;
                case ConstTask.TASK_5_2:
                    break;
                case ConstTask.TASK_5_3:
                    npcSay(player, ConstTask.NPC_NHA,
                            "Con bây giờ là người khỏe nhất vùng này rồi. Con có thể tới trạm tàu vũ trụ "
                            + "gặp %7, cậu ấy có thể đưa con tới bất cứ nơi nào\n"
                            + "Con hãy tới chào hỏi cậu đi");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_6_0:
                    break;
                case ConstTask.TASK_6_1:
                    npcSay(player, ConstTask.NPC_TTVT,
                            "Ôi, tôi chào cậu " + player.name + ", tôi nghe danh cậu đã lâu trong vùng này mà "
                            + "tới bây giờ mới có thể gặp\n"
                            + "Cậu giúp tôi điều này với, đứa nhỏ nhà tôi nó lên đường đi tìm cái thứ "
                            + "gọi là ngọc rồng gì đó,..\n"
                            + ".. hồi nãy có người báo tôi rằng trên đường bé gặp chuyện không may, bé nó bị bọn %9 bắt\n"
                            + "Cậu hãy đi cứu nó giúp tôi với, bé nó tên là %8.. rất cám ơn cậu và hứa "
                            + "sẽ tặng cậu 1 thứ xứng đáng");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_7_0:
                    break;
                case ConstTask.TASK_7_1:
                    break;
                case ConstTask.TASK_7_2:
                    npcSay(player, ConstTask.NPC_TTVT,
                            "Ôi, tôi thật sự cám ơn cậu, " + player.name + ", con bé đã bình an trở về\n"
                            + "Để báo đáp ơn này, tôi sẽ miễn phí cho cậu đi phi thuyền của tôi, cậu có thể tới bất kỳ nơi nào cậu muốn\n"
                            + "Cứ lúc nào cần hãy đến đây nhé..\n"
                            + "..à mà bé nhà tôi nó cũng đang đứng ở trước %1 đó, nó có bán vài vật phẩm ở đó\n"
                            + "Cậu hãy tới trò chuyện với bé nó nhé..");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_8_0:
                    break;
                case ConstTask.TASK_8_1:
                    Item capsule = ItemService.gI().createNewItem((short) 193, 30);
                    InventoryService.gI().addItemBag(player, capsule, 0);
                    npcSay(player, ConstTask.NPC_SHOP_LANG,
                            "Hiện tại em vẫn khỏe anh ạ, hơi bị trầy xước tí thôi nhưng không sao\n"
                            + "Em thực sự cảm ơn anh đã cứu em, nếu không có anh thì giờ này cũng không biết em sẽ thế nào nữa\n"
                            + "À em có cái món này, tuy nó không quá giá trị nhưng em mong anh nhận cho em vui");
                    break;
                case ConstTask.TASK_8_2:
                    npcSay(player, ConstTask.NPC_NHA,
                            "Cháu trai của ông, con làm ông tự hào lắm. Con đã biết dùng sức mạnh của mình để giúp kẻ yếu\n"
                            + "Bây giờ con đã trưởng thành thực sự rồi, ông sẽ bàn giao con lại cho %10 - người "
                            + "bạn lâu ngày không gặp của ông\n"
                            + "Con hãy tìm đường tới %11 và gửi lời chào của ông tới lão ấy nhé\n"
                            + "Đi đi con...");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_9_0:
                    break;
                case ConstTask.TASK_9_1:
                    npcSay(player, ConstTask.NPC_QUY_LAO,
                            "Chào cậu bé, cháu có phải cháu nội ông %2 phải không?\n"
                            + "Ta cũng đã gặp cháu 1 lần hồi cháu còn bé xíu à\n"
                            + "Bây giờ cháu muốn ta nhận cháu làm đệ tử à? Ta cũng không biết thực lực của cháu hiện tại như nào nữa\n"
                            + "Cháu bé hãy đi đánh mấy con %12 ở quanh đây thể hiện tài năng và ta sẽ coi như đó là học phí nhé");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_10_0:
                    break;
                case ConstTask.TASK_10_1:
                    break;
                case ConstTask.TASK_10_2:
                    Item skill2 = ItemService.gI().createNewItem((short) (player.gender == 0 ? 94 : player.gender == 1 ? 101 : 108), 1);
                    InventoryService.gI().addItemBag(player, skill2, 0);
                    npcSay(player, ConstTask.NPC_QUY_LAO,
                            "Tốt lắm, bây giờ con đã chính thức trở thành đệ tử của ta\n"
                            + "Ta sẽ dạy con 1 tuyệt chiêu đặc biệt của ta\n"
                            + "Bây giờ con hãy đi kết bạn với những người xung quanh đây đi, thêm 1 người bạn bớt 1 kẻ thù mà con\n"
                            + "Mà lưu ý là tránh kết bạn với những người có bang hội nhé, họ không là kẻ thù cũng không nên là bạn");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_11_0:
                    break;
                case ConstTask.TASK_11_1:
                    break;
                case ConstTask.TASK_11_2:
                    break;
                case ConstTask.TASK_11_3:
                    npcSay(player, ConstTask.NPC_QUY_LAO,
                            "Giờ đây xã giao của con đã tiến bộ hơn rất nhiều rồi\n"
                            + "Bây giờ con hãy về nhà xin ông %2 rằng con sẽ vào bang hội nhé\n"
                            + "Ta sợ lão ấy không đồng ý lại quay sang trách móc cái thân già này..\n"
                            + "Đi đi con, nói khéo lão ấy nhé.");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_12_0:
                    break;
                case ConstTask.TASK_12_1:
                    npcSay(player, ConstTask.NPC_NHA,
                            "Con muốn tham gia vào bang hội á? Haizz, cái lão già này lại dạy hư cháu ông rồi\n"
                            + "Con muốn thì cũng được thôi, nhưng con phải biết lựa chọn được bang hội nào tốt đấy nhé..\n"
                            + "..xã hội này có nhiều thành phần lắm, cũng chỉ vì an nguy của con nên ông chỉ biết dặn dò vậy\n"
                            + "Chúc con may mắn trên con đường con chọn, mà luôn nhớ rằng con phải là 1 công dân tốt đấy nhé..");
                    break;
                case ConstTask.TASK_12_2:
                    npcSay(player, ConstTask.NPC_QUY_LAO,
                            "Cuối cùng lão ấy cũng đồng ý rồi à? Tốt lắm\n"
                            + "Bây giờ con hãy cùng những người bạn con vừa kết bạn tạo thành 1 bang hội đi nhé\n"
                            + "Khi nào đủ 5 thành viên bang hãy tới đây ta sẽ giao nhiệm vụ cho tất cả các con");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_13_0:
                    break;
                case ConstTask.TASK_13_1:
                    npcSay(player, ConstTask.NPC_QUY_LAO,
                            "Tốt lắm, con đã có những người đồng đội kề vai sát cánh rồi\n"
                            + "Bây giờ con và 3 người họ hãy thể hiện tinh thần đoàn kết đi nào\n"
                            + "Cách phối hợp nhau làm nhiệm vụ, cách cư xử với nhau đó là hiện thân của tâm tính mỗi người\n"
                            + "Các con hãy đối nhân xử thế với nhau, hãy cùng hợp sức tiêu diệt lũ quái vật nhé");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_14_0:
                    break;
                case ConstTask.TASK_14_1:
                    break;
                case ConstTask.TASK_14_2: //heo rừng
                    break;
                case ConstTask.TASK_14_3:
                    npcSay(player, ConstTask.NPC_QUY_LAO,
                            "Giỏi lắm các con!\n"
                            + "...Hiện tại có vài chủng quái vật mới đổ bộ lên hành tinh chúng ta\n"
                            + "Con hãy cùng 3 người trong bang lên đường tiêu diệt chúng nhé\n"
                            + "Dân chúng đặt niềm tin vào các con hết đấy..\n"
                            + "Đi đi...");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_15_0:
                    break;
                case ConstTask.TASK_15_1: //bulon
                    break;
                case ConstTask.TASK_15_2:
                    break;
                case ConstTask.TASK_15_3:
                    npcSay(player, ConstTask.NPC_QUY_LAO,
                            "Giỏi lắm các con\n"
                            + "Còn 1 vài con quái vật đầu sỏ nữa\n"
                            + "Con hãy tiêu diệt nốt chúng đi nhé..");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_16_0:
                    break;
                case ConstTask.TASK_16_1: //taubourine
                    break;
                case ConstTask.TASK_16_2:
                    break;
                case ConstTask.TASK_16_3:
                    npcSay(player, ConstTask.NPC_QUY_LAO,
                            "Con thực sự làm ta ngạc nhiên đấy, không uổng công ta truyền dạy võ công\n"
                            + "Bên ngoài còn rất nhiều kẻ thù nguy hiểm, nên con phải không ngừng luyện tập nhé\n"
                            + "Lại có chuyện xảy ra rồi, Cui - một người họ hàng xa của họ hàng ta - đang gặp chuyện\n"
                            + "Con hãy tới thành phố Vegeta hỏi thăm tình hình cậu ta nhé! Đi đi con..");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_17_0:
                    break;
                case ConstTask.TASK_17_1:
                    npcSay(player, ConstNpc.CUI,
                            "Chào cậu, cậu là đệ tử của %10 phải không\n"
                            + "Bọn người ngoài hành tinh cầm đầu bởi tên Fide đã và đang đổ bộ vào quê hương của tôi..\n"
                            + "..chúng tàn sát hết dân lành và hủy hoại quê hương chúng tôi\n"
                            + "Cậu hãy giúp tôi 1 tay tiêu diệt bọn chúng nhé"); //need retext
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_18_0:
                    break;
                case ConstTask.TASK_18_1:
                    break;
                case ConstTask.TASK_18_2:
                    break;
                case ConstTask.TASK_18_3:
                    break;
                case ConstTask.TASK_18_4:
                    break;
                case ConstTask.TASK_18_5:
                    npcSay(player, ConstNpc.CUI,
                            "Cảm ơn cậu đã hỗ trợ tôi tiêu diệt bọn lính tay sai Fide\n"
                            + "3 tên cầm đầu chúng đang tức giận lắm, tôi thì không đủ mạnh để chống lại bọn chúng\n"
                            + "...");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_19_0:
                    break;
                case ConstTask.TASK_19_1:
                    break;
                case ConstTask.TASK_19_2:
                    break;
                case ConstTask.TASK_19_3:
                    npcSay(player, ConstNpc.CUI,
                            "Cảm ơn cậu đã tiêu diệt giúp tôi lũ đệ tử của Fide\n"
                            + "Dưới trướng Fide còn có 1 đội gồm 5 thành viên được chúng gọi là Tiều Đội Sát Thủ\n"
                            + "Chúng rất mạnh và rất trung thành với tên Fide\n"
                            + "Bọn chúng vừa được cử tới đi trả thù cho 3 tên đệ tử cậu vừa tiêu diệt\n"
                            + "Hãy chống lại bọn chúng giúp tôi nhé....");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_20_0:
                    break;
                case ConstTask.TASK_20_1:
                    break;
                case ConstTask.TASK_20_2:
                    break;
                case ConstTask.TASK_20_3:
                    break;
                case ConstTask.TASK_20_4:
                    break;
                case ConstTask.TASK_20_5:
                    break;
                case ConstTask.TASK_20_6:
                    npcSay(player, ConstNpc.CUI,
                            "Tốt lắm cậu..\n"
                            + "Không ổn rồi, tên Fide đại ca đã đích thân tới..\n"
                            + "Cậu hãy tới núi khỉ vàng tiêu diệt hắn giúp tôi nhé\n"
                            + "Dân làng sẽ biết ơn cậu rất nhiều đấy...");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_21_0:
                    break;
                case ConstTask.TASK_21_1:
                    break;
                case ConstTask.TASK_21_2:
                    break;
                case ConstTask.TASK_21_3:
                    break;
                case ConstTask.TASK_21_4:
                    npcSay(player, ConstNpc.CUI,
                            "Chúc mừng bạn đã hoàn thành nhiệm vụ\n"
                            + "Cùng nhau lên đường đến Tương lai giải cứu Calick nào\n"
                            + "Đến Rừng Bamboo gặp Calick để đến Tương lai thôi nào");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_22_0:
                    npcSay(player, ConstNpc.BUNMA_TL,
                            "Cuối cùng bạn cũng đã đến giúp tôi giải cứu Tương lai đen tối\n"
                            + "Hãy đến thành phố phía Nam\n"
                            + "đảo Balê hoặc Cao nguyên tìm gặp Rôbốt sát thủ để tiêu diệt");
                    break;
                case ConstTask.TASK_22_1:
                    break;
                case ConstTask.TASK_22_2:
                    break;
                case ConstTask.TASK_22_3:
                    break;
                case ConstTask.TASK_22_4:
                    npcSay(player, ConstNpc.BUNMA_TL,
                            "Tiếp tục tiêu diệt bọn Người máy nào\n"
                            + "Hãy đến thành phố phía Bắc\n"
                            + "Ngọn núi phía Bắc hoặc thung lũng phía Bắc tìm diệt bọn Pic, Poc, KingKong");
                //--------------------------------------------------------------
                case ConstTask.TASK_23_0:
                    break;
                case ConstTask.TASK_23_1:
                    break;
                case ConstTask.TASK_23_2:
                    break;
                case ConstTask.TASK_23_3:
                    break;
                case ConstTask.TASK_23_4:
                    npcSay(player, ConstNpc.BUNMA_TL,
                            "Bạn thật tuyệt vời\n"
                            + "Lên đường tới thị trấn Ginder\n"
                            + "Tiêu diệt bọn Xên bọ hung để nhận được sự công nhận của tôi");
                    break;
                //--------------------------------------------------------------
                case ConstTask.TASK_24_0:
                    break;
                case ConstTask.TASK_24_1:
                    break;
                case ConstTask.TASK_24_2:
                    break;
                case ConstTask.TASK_24_3:
                    break;
                case ConstTask.TASK_24_4:
                    npcSay(player, ConstNpc.BUNMA_TL,
                            "Cuối cùng cũng có thể an nhàn 1 chút rồi\n"
                            + "Bây giờ là thời điểm tích lũy kinh nghiệm của bạn\n"
                            + "Hãy tiêu diệt cái quái Sên để tích lũy kinh nghiệm nào");
                    break;
                //---------------------------   
                case ConstTask.TASK_25_0:
                    break;
                case ConstTask.TASK_25_1:
                    break;
                case ConstTask.TASK_25_2:
                    break;
                case ConstTask.TASK_25_3:
                    break;
                case ConstTask.TASK_25_4:
                    break;
                case ConstTask.TASK_25_5:
                    npcSay(player, ConstNpc.BUNMA_TL,
                            "Tiếp tục tiêu diệt Sên cấp cao hơn\n"
                            + "Capsule kì bí có rất nhiều Item thú vị đang chờ bạn khám phá đó");
                    break;
                //-----------------
                case ConstTask.TASK_26_0:
                    break;
                case ConstTask.TASK_26_1:
                    break;
                case ConstTask.TASK_26_2:
                    break;
                case ConstTask.TASK_26_3:
                    break;
                case ConstTask.TASK_26_4:
                    npcSay(player, ConstNpc.BUNMA_TL,
                            "Hãy mở rộng mối quan hệ bằng việc kết bạn với những người bạn mới nào\n"
                            + "Tiếp theo đó chùng ta sẽ tìm kiếm Ngọc rồng 2 sao tại Võ đài Xên\n"
                            + "Cùng nhau tiêu diệt các Boss Sên ở Võ đài và thu thập Ngọc rồng 2 Sao nào");
                    break;
                //---------------------------------
                case ConstTask.TASK_27_0:
                    break;
                case ConstTask.TASK_27_1:
                    break;
                case ConstTask.TASK_27_2:
                    break;
                case ConstTask.TASK_27_3:
                    break;
                case ConstTask.TASK_27_4:
                    npcSay(player, ConstNpc.BUNMA_TL,
                            "Tiếp tục hành trình giải cứu Tương lai nào\n"
                            + "Bọn Siêu bọ hung đã sinh ra rất nhiều Sên con\n"
                            + "Hãy đến Võ đài Xên để tiêu diệt tận gốc bọn chúng nào");
                    break;
                //----
                case ConstTask.TASK_28_0:
                    break;
                case ConstTask.TASK_28_1:
                    break;
                case ConstTask.TASK_28_2:
                    break;
                case ConstTask.TASK_28_3:
                    npcSay(player, ConstNpc.BUNMA_TL,
                            "Tốt quá rồi, mọi thứ ở đây yên bình rồi. Cảm ơn bạn rất nhiều\n"
                            + "Bạn hãy Đến Thành phố Vegeta gặp Cui để đến Hành tinh Cold\n"
                            + "Thực hiện các nhiệm vụ tại đó để trở nên mạnh mẽ hơn nào");
                    break;
                //-------------------------------
                case ConstTask.TASK_29_0:
                    break;
                case ConstTask.TASK_29_1:
                    break;
                case ConstTask.TASK_29_2:
                    break;
                case ConstTask.TASK_29_3:
                    break;
                case ConstTask.TASK_29_4:
                    npcSay(player, ConstNpc.BUNMA_TL,
                            "Đáng ghét thật!!\n"
                            + "Sao bây giờ lại xuất hiện một tên Black Goku nữa vậy\n"
                            + "Hắn quá mạnh tôi không thể đánh bại hắn, bạn hãy tìm hắn và tiêu diệt giúp tôi nhé");
                    break;
                case ConstTask.TASK_30_0:
                    break;
                case ConstTask.TASK_30_1:
                    npcSay(player, ConstNpc.BUNMA_TL,
                            "Bạn quá mạnh rồi\n"
                            + "Hãy tìm kiếm Nhẫn thời không bằng việc Giết Black Goku để đến vùng đất mới nào");
                    break;
                //-----------------
                case ConstTask.TASK_31_0:
                    break;
                case ConstTask.TASK_31_1:
                    break;
                case ConstTask.TASK_31_2:
                    break;
                case ConstTask.TASK_31_3:
                    break;
                case ConstTask.TASK_31_4:
                    npcSay(player, ConstNpc.BUNMA_TL,
                            "Sử dụng Nhẫn thời không du hành đến khu vực mới nào !!");
                    break;
                case ConstTask.TASK_32_1:
                    npcSay(player, ConstNpc.BARDOCK,
                            "Chào mừng bạn đến Hành tình Nguyên thủy\n"
                                    + "Hãy thu thập giỏ thức ăn và tiếp tục nhiệm vụ nào !!");
                    break;
                case ConstTask.TASK_32_2:
                    break;
                case ConstTask.TASK_32_3:
                    npcSay(player, ConstNpc.BERRY,
                            "Cảm ơn bạn đã cho tôi thức ăn quý giá này!!!\n"
                                    + "Hãy tiếp tục cố gắng hoàn thành các nhiệm vụ tiếp theo nào");
                    break;
                case ConstTask.TASK_33_0:
                    npcSay(player, ConstNpc.BARDOCK,
                            "Quá tuyệt vời!!!\n"
                                    + "Tiếp theo đây sẽ là nhiệm vụ cực kì khó khăn\n"
                                    + "Bạn cần cố gắng thật chăm chỉ mới có thể vượt qua nó");
                    break;
            }
            InventoryService.gI().sendItemBags(player);
            return true;
        }
        return false;
    }

    private void npcSay(Player player, int npcId, String text) {
        npcId = transformNpcId(player, npcId);
        text = transformName(player, text);
        int avatar = NpcService.gI().getAvatar(npcId);
        NpcService.gI().createTutorial(player, avatar, text);
    }

    //Thưởng nhiệm vụ
    private void rewardDoneTask(Player player) {
        short idvatpham = ID_THUONG_NHIEMVU;
        int soLuong = 0;
        Item qua = ItemService.gI().createNewItem(idvatpham);
        switch (player.playerTask.taskMain.id) {
            case 0:
                Service.getInstance().addSMTN(player, (byte) 0, 5000000, false);
                Service.getInstance().addSMTN(player, (byte) 1, 5000000, false);
                break;
            case 1:
                Service.getInstance().addSMTN(player, (byte) 0, 5000000, false);
                Service.getInstance().addSMTN(player, (byte) 1, 5000000, false);
                break;
            case 2:
                Service.getInstance().addSMTN(player, (byte) 0, 50000000, false);
                Service.getInstance().addSMTN(player, (byte) 1, 50000000, false);
                break;
            case 3:
                Service.getInstance().addSMTN(player, (byte) 0, 50000000, false);
                Service.getInstance().addSMTN(player, (byte) 1, 50000000, false);
                break;
            case 4:
                Service.getInstance().addSMTN(player, (byte) 0, 50000000, false);
                Service.getInstance().addSMTN(player, (byte) 1, 50000000, false);
                break;
            case 5:
                Service.getInstance().addSMTN(player, (byte) 0, 50000000, false);
                Service.getInstance().addSMTN(player, (byte) 1, 50000000, false);
                break;
            case 15:
                soLuong += THUONG_NV_15;
                break;
            case 16:
                soLuong += THUONG_NV_16;
                break;
            case 17:
                soLuong += THUONG_NV_17;
                break;
            case 18:
                soLuong += THUONG_NV_18;
                break;
            case 19:
                soLuong += THUONG_NV_19;
                break;
            case 20:
                soLuong += THUONG_NV_20;
                break;
            case 21:
                soLuong += THUONG_NV_21;
                break;
            case 22:
                soLuong += THUONG_NV_22;
                break;
            case 23:
                soLuong += THUONG_NV_23;
                break;
            case 24:
                soLuong += THUONG_NV_24;
                break;
            case 25:
                soLuong += THUONG_NV_25;
                break;
            case 26:
                soLuong += THUONG_NV_26;
                break;
            case 27:
                soLuong += THUONG_NV_27;
                break;
            case 28:
                soLuong += THUONG_NV_28;
                break;
            case 29:
                soLuong += THUONG_NV_29;
                break;
            case 30:
                soLuong += THUONG_NV_30;
                break;
            case 31:
                soLuong += THUONG_NV_31;
                break;
            case 32:
                soLuong += THUONG_NV_32;
                break;
            case 33:
                soLuong += THUONG_NV_33;
                break;
            case 34:
                soLuong += THUONG_NV_34;
                break;
            case 35:
                soLuong += THUONG_NV_35;
                break;
            case 36:
                soLuong += THUONG_NV_36;
                break;
            case 37:
                soLuong += THUONG_NV_37;
                break;
            case 38:
                soLuong += THUONG_NV_38;
                break;
            case 39:
                soLuong += THUONG_NV_39;
                break;
            case 40:
                soLuong += THUONG_NV_40;
                break;
        }
        qua.quantity = soLuong;
        InventoryService.gI().addItemBag(player, qua, 99999999);
        InventoryService.gI().sendItemBags(player);
        Service.getInstance().sendThongBaoOK(player, "Phần thưởng từ nv: " + soLuong + " " + qua.getName());
    }

    // vd: pem đc 1 mộc nhân -> +1 mộc nhân vào nv hiện tại
    private void addDoneSubTask(Player player, int numDone) {
        player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count += numDone;
        if (player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count
                >= player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).maxCount) {
            player.playerTask.taskMain.index++;
            if (player.playerTask.taskMain.index >= player.playerTask.taskMain.subTasks.size()) {
                this.sendNextTaskMain(player);
            } else {
                this.sendNextSubTask(player);
            }
        } else {
            this.sendUpdateCountSubTask(player);
        }
    }

    private int transformMapId(Player player, int id) {
        if (id == ConstTask.MAP_NHA) {
            return (short) (player.gender + 21);
        } else if (id == ConstTask.MAP_200) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 1 : (player.gender == ConstPlayer.NAMEC
                            ? 8 : 15);
        } else if (id == ConstTask.MAP_VACH_NUI) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 39 : (player.gender == ConstPlayer.NAMEC
                            ? 40 : 41);
        } else if (id == ConstTask.MAP_200) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 2 : (player.gender == ConstPlayer.NAMEC
                            ? 9 : 16);
        } else if (id == ConstTask.MAP_TTVT) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 24 : (player.gender == ConstPlayer.NAMEC
                            ? 25 : 26);
        } else if (id == ConstTask.MAP_QUAI_BAY_600) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 3 : (player.gender == ConstPlayer.NAMEC
                            ? 11 : 17);
        } else if (id == ConstTask.MAP_LANG) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 0 : (player.gender == ConstPlayer.NAMEC
                            ? 7 : 14);
        } else if (id == ConstTask.MAP_QUY_LAO) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? 5 : (player.gender == ConstPlayer.NAMEC
                            ? 13 : 20);
        }
        return id;
    }

    private int transformNpcId(Player player, int id) {
        if (id == ConstTask.NPC_NHA) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? ConstNpc.ONG_GOHAN : (player.gender == ConstPlayer.NAMEC
                            ? ConstNpc.ONG_MOORI : ConstNpc.ONG_PARAGUS);
        } else if (id == ConstTask.NPC_TTVT) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? ConstNpc.DR_DRIEF : (player.gender == ConstPlayer.NAMEC
                            ? ConstNpc.CARGO : ConstNpc.CUI);
        } else if (id == ConstTask.NPC_SHOP_LANG) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? ConstNpc.BUNMA : (player.gender == ConstPlayer.NAMEC
                            ? ConstNpc.DENDE : ConstNpc.APPULE);
        } else if (id == ConstTask.NPC_QUY_LAO) {
            return player.gender == ConstPlayer.TRAI_DAT
                    ? ConstNpc.QUY_LAO_KAME : (player.gender == ConstPlayer.NAMEC
                            ? ConstNpc.TRUONG_LAO_GURU : ConstNpc.VUA_VEGETA);
        }
        return id;
    }

    //replate %1 %2 -> chữ
    private String transformName(Player player, String text) {
        byte gender = player.gender;

        text = text.replaceAll(ConstTask.TEN_NPC_QUY_LAO, player.gender == ConstPlayer.TRAI_DAT
                ? "Quy Lão Kame" : (player.gender == ConstPlayer.NAMEC
                        ? "Trưởng lão Guru" : "Vua Vegeta"));
        text = text.replaceAll(ConstTask.TEN_MAP_QUY_LAO, player.gender == ConstPlayer.TRAI_DAT
                ? "Đảo Kamê" : (player.gender == ConstPlayer.NAMEC
                        ? "Đảo Guru" : "Vách núi đen"));
        text = text.replaceAll(ConstTask.TEN_QUAI_3000, player.gender == ConstPlayer.TRAI_DAT
                ? "ốc mượn hồn" : (player.gender == ConstPlayer.NAMEC
                        ? "ốc sên" : "heo Xayda mẹ"));
        //----------------------------------------------------------------------
        text = text.replaceAll(ConstTask.TEN_LANG, player.gender == ConstPlayer.TRAI_DAT
                ? "Làng Aru" : (player.gender == ConstPlayer.NAMEC
                        ? "Làng Mori" : "Làng Kakarot"));
        text = text.replaceAll(ConstTask.TEN_NPC_NHA, player.gender == ConstPlayer.TRAI_DAT
                ? "Ông Gôhan" : (player.gender == ConstPlayer.NAMEC
                        ? "Ông Moori" : "Ông Paragus"));
        text = text.replaceAll(ConstTask.TEN_QUAI_200, player.gender == ConstPlayer.TRAI_DAT
                ? "khủng long" : (player.gender == ConstPlayer.NAMEC
                        ? "lợn lòi" : "quỷ đất"));
        text = text.replaceAll(ConstTask.TEN_MAP_200, player.gender == ConstPlayer.TRAI_DAT
                ? "Đồi hoa cúc" : (player.gender == ConstPlayer.NAMEC
                        ? "Đồi nấm tím" : "Đồi hoang"));
        text = text.replaceAll(ConstTask.TEN_VACH_NUI, player.gender == ConstPlayer.TRAI_DAT
                ? "Vách núi Aru" : (player.gender == ConstPlayer.NAMEC
                        ? "Vách núi Moori" : "Vách núi Kakarot"));
        text = text.replaceAll(ConstTask.TEN_MAP_500, player.gender == ConstPlayer.TRAI_DAT
                ? "Thung lũng tre" : (player.gender == ConstPlayer.NAMEC
                        ? "Thị trấn Moori" : "Làng Plane"));
        text = text.replaceAll(ConstTask.TEN_NPC_TTVT, player.gender == ConstPlayer.TRAI_DAT
                ? "Dr. Brief" : (player.gender == ConstPlayer.NAMEC
                        ? "Cargo" : "Cui"));
        text = text.replaceAll(ConstTask.TEN_QUAI_BAY_600, player.gender == ConstPlayer.TRAI_DAT
                ? "thằn lằn bay" : (player.gender == ConstPlayer.NAMEC
                        ? "phi long" : "quỷ bay"));
        text = text.replaceAll(ConstTask.TEN_NPC_SHOP_LANG, player.gender == ConstPlayer.TRAI_DAT
                ? "Bunma" : (player.gender == ConstPlayer.NAMEC
                        ? "Dende" : "Appule"));
        return text;
    }

    public boolean isCurrentTask(Player player, int idTaskCustom) {
        switch (idTaskCustom) {
            case ConstTask.TASK_0_0:
                return player.playerTask.taskMain.id == 0 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_0_1:
                return player.playerTask.taskMain.id == 0 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_0_2:
                return player.playerTask.taskMain.id == 0 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_0_3:
                return player.playerTask.taskMain.id == 0 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_0_4:
                return player.playerTask.taskMain.id == 0 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_0_5:
                return player.playerTask.taskMain.id == 0 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_0_6:
                return player.playerTask.taskMain.id == 0 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_1_0:
                return player.playerTask.taskMain.id == 1 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_1_1:
                return player.playerTask.taskMain.id == 1 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_1_2:
                return player.playerTask.taskMain.id == 1 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_1_3:
                return player.playerTask.taskMain.id == 1 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_1_4:
                return player.playerTask.taskMain.id == 1 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_1_5:
                return player.playerTask.taskMain.id == 1 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_1_6:
                return player.playerTask.taskMain.id == 1 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_2_0:
                return player.playerTask.taskMain.id == 2 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_2_1:
                return player.playerTask.taskMain.id == 2 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_2_2:
                return player.playerTask.taskMain.id == 2 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_2_3:
                return player.playerTask.taskMain.id == 2 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_2_4:
                return player.playerTask.taskMain.id == 2 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_2_5:
                return player.playerTask.taskMain.id == 2 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_2_6:
                return player.playerTask.taskMain.id == 2 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_3_0:
                return player.playerTask.taskMain.id == 3 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_3_1:
                return player.playerTask.taskMain.id == 3 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_3_2:
                return player.playerTask.taskMain.id == 3 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_3_3:
                return player.playerTask.taskMain.id == 3 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_3_4:
                return player.playerTask.taskMain.id == 3 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_3_5:
                return player.playerTask.taskMain.id == 3 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_3_6:
                return player.playerTask.taskMain.id == 3 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_4_0:
                return player.playerTask.taskMain.id == 4 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_4_1:
                return player.playerTask.taskMain.id == 4 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_4_2:
                return player.playerTask.taskMain.id == 4 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_4_3:
                return player.playerTask.taskMain.id == 4 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_4_4:
                return player.playerTask.taskMain.id == 4 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_4_5:
                return player.playerTask.taskMain.id == 4 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_4_6:
                return player.playerTask.taskMain.id == 4 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_5_0:
                return player.playerTask.taskMain.id == 5 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_5_1:
                return player.playerTask.taskMain.id == 5 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_5_2:
                return player.playerTask.taskMain.id == 5 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_5_3:
                return player.playerTask.taskMain.id == 5 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_5_4:
                return player.playerTask.taskMain.id == 5 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_5_5:
                return player.playerTask.taskMain.id == 5 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_5_6:
                return player.playerTask.taskMain.id == 5 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_6_0:
                return player.playerTask.taskMain.id == 6 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_6_1:
                return player.playerTask.taskMain.id == 6 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_6_2:
                return player.playerTask.taskMain.id == 6 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_6_3:
                return player.playerTask.taskMain.id == 6 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_6_4:
                return player.playerTask.taskMain.id == 6 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_6_5:
                return player.playerTask.taskMain.id == 6 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_6_6:
                return player.playerTask.taskMain.id == 6 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_7_0:
                return player.playerTask.taskMain.id == 7 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_7_1:
                return player.playerTask.taskMain.id == 7 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_7_2:
                return player.playerTask.taskMain.id == 7 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_7_3:
                return player.playerTask.taskMain.id == 7 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_7_4:
                return player.playerTask.taskMain.id == 7 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_7_5:
                return player.playerTask.taskMain.id == 7 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_7_6:
                return player.playerTask.taskMain.id == 7 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_8_0:
                return player.playerTask.taskMain.id == 8 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_8_1:
                return player.playerTask.taskMain.id == 8 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_8_2:
                return player.playerTask.taskMain.id == 8 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_8_3:
                return player.playerTask.taskMain.id == 8 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_8_4:
                return player.playerTask.taskMain.id == 8 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_8_5:
                return player.playerTask.taskMain.id == 8 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_8_6:
                return player.playerTask.taskMain.id == 8 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_9_0:
                return player.playerTask.taskMain.id == 9 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_9_1:
                return player.playerTask.taskMain.id == 9 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_9_2:
                return player.playerTask.taskMain.id == 9 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_9_3:
                return player.playerTask.taskMain.id == 9 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_9_4:
                return player.playerTask.taskMain.id == 9 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_9_5:
                return player.playerTask.taskMain.id == 9 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_9_6:
                return player.playerTask.taskMain.id == 9 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_10_0:
                return player.playerTask.taskMain.id == 10 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_10_1:
                return player.playerTask.taskMain.id == 10 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_10_2:
                return player.playerTask.taskMain.id == 10 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_10_3:
                return player.playerTask.taskMain.id == 10 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_10_4:
                return player.playerTask.taskMain.id == 10 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_10_5:
                return player.playerTask.taskMain.id == 10 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_10_6:
                return player.playerTask.taskMain.id == 10 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_11_0:
                return player.playerTask.taskMain.id == 11 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_11_1:
                return player.playerTask.taskMain.id == 11 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_11_2:
                return player.playerTask.taskMain.id == 11 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_11_3:
                return player.playerTask.taskMain.id == 11 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_11_4:
                return player.playerTask.taskMain.id == 11 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_11_5:
                return player.playerTask.taskMain.id == 11 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_11_6:
                return player.playerTask.taskMain.id == 11 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_12_0:
                return player.playerTask.taskMain.id == 12 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_12_1:
                return player.playerTask.taskMain.id == 12 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_12_2:
                return player.playerTask.taskMain.id == 12 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_12_3:
                return player.playerTask.taskMain.id == 12 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_12_4:
                return player.playerTask.taskMain.id == 12 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_12_5:
                return player.playerTask.taskMain.id == 12 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_12_6:
                return player.playerTask.taskMain.id == 12 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_13_0:
                return player.playerTask.taskMain.id == 13 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_13_1:
                return player.playerTask.taskMain.id == 13 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_13_2:
                return player.playerTask.taskMain.id == 13 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_13_3:
                return player.playerTask.taskMain.id == 13 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_13_4:
                return player.playerTask.taskMain.id == 13 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_13_5:
                return player.playerTask.taskMain.id == 13 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_13_6:
                return player.playerTask.taskMain.id == 13 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_14_0:
                return player.playerTask.taskMain.id == 14 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_14_1:
                return player.playerTask.taskMain.id == 14 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_14_2:
                return player.playerTask.taskMain.id == 14 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_14_3:
                return player.playerTask.taskMain.id == 14 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_14_4:
                return player.playerTask.taskMain.id == 14 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_14_5:
                return player.playerTask.taskMain.id == 14 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_14_6:
                return player.playerTask.taskMain.id == 14 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_15_0:
                return player.playerTask.taskMain.id == 15 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_15_1:
                return player.playerTask.taskMain.id == 15 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_15_2:
                return player.playerTask.taskMain.id == 15 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_15_3:
                return player.playerTask.taskMain.id == 15 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_15_4:
                return player.playerTask.taskMain.id == 15 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_15_5:
                return player.playerTask.taskMain.id == 15 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_15_6:
                return player.playerTask.taskMain.id == 15 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_16_0:
                return player.playerTask.taskMain.id == 16 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_16_1:
                return player.playerTask.taskMain.id == 16 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_16_2:
                return player.playerTask.taskMain.id == 16 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_16_3:
                return player.playerTask.taskMain.id == 16 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_16_4:
                return player.playerTask.taskMain.id == 16 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_16_5:
                return player.playerTask.taskMain.id == 16 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_16_6:
                return player.playerTask.taskMain.id == 16 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_17_0:
                return player.playerTask.taskMain.id == 17 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_17_1:
                return player.playerTask.taskMain.id == 17 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_17_2:
                return player.playerTask.taskMain.id == 17 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_17_3:
                return player.playerTask.taskMain.id == 17 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_17_4:
                return player.playerTask.taskMain.id == 17 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_17_5:
                return player.playerTask.taskMain.id == 17 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_17_6:
                return player.playerTask.taskMain.id == 17 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_18_0:
                return player.playerTask.taskMain.id == 18 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_18_1:
                return player.playerTask.taskMain.id == 18 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_18_2:
                return player.playerTask.taskMain.id == 18 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_18_3:
                return player.playerTask.taskMain.id == 18 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_18_4:
                return player.playerTask.taskMain.id == 18 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_18_5:
                return player.playerTask.taskMain.id == 18 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_18_6:
                return player.playerTask.taskMain.id == 18 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_19_0:
                return player.playerTask.taskMain.id == 19 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_19_1:
                return player.playerTask.taskMain.id == 19 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_19_2:
                return player.playerTask.taskMain.id == 19 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_19_3:
                return player.playerTask.taskMain.id == 19 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_19_4:
                return player.playerTask.taskMain.id == 19 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_19_5:
                return player.playerTask.taskMain.id == 19 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_19_6:
                return player.playerTask.taskMain.id == 19 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_20_0:
                return player.playerTask.taskMain.id == 20 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_20_1:
                return player.playerTask.taskMain.id == 20 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_20_2:
                return player.playerTask.taskMain.id == 20 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_20_3:
                return player.playerTask.taskMain.id == 20 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_20_4:
                return player.playerTask.taskMain.id == 20 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_20_5:
                return player.playerTask.taskMain.id == 20 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_20_6:
                return player.playerTask.taskMain.id == 20 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_21_0:
                return player.playerTask.taskMain.id == 21 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_21_1:
                return player.playerTask.taskMain.id == 21 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_21_2:
                return player.playerTask.taskMain.id == 21 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_21_3:
                return player.playerTask.taskMain.id == 21 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_21_4:
                return player.playerTask.taskMain.id == 21 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_21_5:
                return player.playerTask.taskMain.id == 21 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_21_6:
                return player.playerTask.taskMain.id == 21 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_22_0:
                return player.playerTask.taskMain.id == 22 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_22_1:
                return player.playerTask.taskMain.id == 22 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_22_2:
                return player.playerTask.taskMain.id == 22 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_22_3:
                return player.playerTask.taskMain.id == 22 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_22_4:
                return player.playerTask.taskMain.id == 22 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_22_5:
                return player.playerTask.taskMain.id == 22 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_22_6:
                return player.playerTask.taskMain.id == 22 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_23_0:
                return player.playerTask.taskMain.id == 23 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_23_1:
                return player.playerTask.taskMain.id == 23 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_23_2:
                return player.playerTask.taskMain.id == 23 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_23_3:
                return player.playerTask.taskMain.id == 23 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_23_4:
                return player.playerTask.taskMain.id == 23 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_23_5:
                return player.playerTask.taskMain.id == 23 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_23_6:
                return player.playerTask.taskMain.id == 23 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_24_0:
                return player.playerTask.taskMain.id == 24 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_24_1:
                return player.playerTask.taskMain.id == 24 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_24_2:
                return player.playerTask.taskMain.id == 24 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_24_3:
                return player.playerTask.taskMain.id == 24 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_24_4:
                return player.playerTask.taskMain.id == 24 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_24_5:
                return player.playerTask.taskMain.id == 24 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_24_6:
                return player.playerTask.taskMain.id == 24 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_25_0:
                return player.playerTask.taskMain.id == 25 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_25_1:
                return player.playerTask.taskMain.id == 25 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_25_2:
                return player.playerTask.taskMain.id == 25 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_25_3:
                return player.playerTask.taskMain.id == 25 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_25_4:
                return player.playerTask.taskMain.id == 25 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_25_5:
                return player.playerTask.taskMain.id == 25 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_25_6:
                return player.playerTask.taskMain.id == 25 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_26_0:
                return player.playerTask.taskMain.id == 26 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_26_1:
                return player.playerTask.taskMain.id == 26 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_26_2:
                return player.playerTask.taskMain.id == 26 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_26_3:
                return player.playerTask.taskMain.id == 26 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_26_4:
                return player.playerTask.taskMain.id == 26 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_26_5:
                return player.playerTask.taskMain.id == 26 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_26_6:
                return player.playerTask.taskMain.id == 26 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_27_0:
                return player.playerTask.taskMain.id == 27 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_27_1:
                return player.playerTask.taskMain.id == 27 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_27_2:
                return player.playerTask.taskMain.id == 27 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_27_3:
                return player.playerTask.taskMain.id == 27 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_27_4:
                return player.playerTask.taskMain.id == 27 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_27_5:
                return player.playerTask.taskMain.id == 27 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_27_6:
                return player.playerTask.taskMain.id == 27 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_28_0:
                return player.playerTask.taskMain.id == 28 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_28_1:
                return player.playerTask.taskMain.id == 28 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_28_2:
                return player.playerTask.taskMain.id == 28 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_28_3:
                return player.playerTask.taskMain.id == 28 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_28_4:
                return player.playerTask.taskMain.id == 28 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_28_5:
                return player.playerTask.taskMain.id == 28 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_28_6:
                return player.playerTask.taskMain.id == 28 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_29_0:
                return player.playerTask.taskMain.id == 29 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_29_1:
                return player.playerTask.taskMain.id == 29 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_29_2:
                return player.playerTask.taskMain.id == 29 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_29_3:
                return player.playerTask.taskMain.id == 29 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_29_4:
                return player.playerTask.taskMain.id == 29 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_29_5:
                return player.playerTask.taskMain.id == 29 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_29_6:
                return player.playerTask.taskMain.id == 29 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_30_0:
                return player.playerTask.taskMain.id == 30 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_30_1:
                return player.playerTask.taskMain.id == 30 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_30_2:
                return player.playerTask.taskMain.id == 30 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_30_3:
                return player.playerTask.taskMain.id == 30 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_30_4:
                return player.playerTask.taskMain.id == 30 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_30_5:
                return player.playerTask.taskMain.id == 30 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_30_6:
                return player.playerTask.taskMain.id == 30 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_31_0:
                return player.playerTask.taskMain.id == 31 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_31_1:
                return player.playerTask.taskMain.id == 31 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_31_2:
                return player.playerTask.taskMain.id == 31 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_31_3:
                return player.playerTask.taskMain.id == 31 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_31_4:
                return player.playerTask.taskMain.id == 31 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_31_5:
                return player.playerTask.taskMain.id == 31 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_31_6:
                return player.playerTask.taskMain.id == 31 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_32_0:
                return player.playerTask.taskMain.id == 32 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_32_1:
                return player.playerTask.taskMain.id == 32 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_32_2:
                return player.playerTask.taskMain.id == 32 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_32_3:
                return player.playerTask.taskMain.id == 32 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_32_4:
                return player.playerTask.taskMain.id == 32 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_32_5:
                return player.playerTask.taskMain.id == 32 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_32_6:
                return player.playerTask.taskMain.id == 32 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_33_0:
                return player.playerTask.taskMain.id == 33 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_33_1:
                return player.playerTask.taskMain.id == 33 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_33_2:
                return player.playerTask.taskMain.id == 33 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_33_3:
                return player.playerTask.taskMain.id == 33 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_33_4:
                return player.playerTask.taskMain.id == 33 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_33_5:
                return player.playerTask.taskMain.id == 33 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_33_6:
                return player.playerTask.taskMain.id == 33 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_34_0:
                return player.playerTask.taskMain.id == 34 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_34_1:
                return player.playerTask.taskMain.id == 34 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_34_2:
                return player.playerTask.taskMain.id == 34 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_34_3:
                return player.playerTask.taskMain.id == 34 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_34_4:
                return player.playerTask.taskMain.id == 34 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_34_5:
                return player.playerTask.taskMain.id == 34 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_34_6:
                return player.playerTask.taskMain.id == 34 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_35_0:
                return player.playerTask.taskMain.id == 35 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_35_1:
                return player.playerTask.taskMain.id == 35 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_35_2:
                return player.playerTask.taskMain.id == 35 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_35_3:
                return player.playerTask.taskMain.id == 35 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_35_4:
                return player.playerTask.taskMain.id == 35 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_35_5:
                return player.playerTask.taskMain.id == 35 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_35_6:
                return player.playerTask.taskMain.id == 35 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_36_0:
                return player.playerTask.taskMain.id == 36 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_36_1:
                return player.playerTask.taskMain.id == 36 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_36_2:
                return player.playerTask.taskMain.id == 36 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_36_3:
                return player.playerTask.taskMain.id == 36 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_36_4:
                return player.playerTask.taskMain.id == 36 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_36_5:
                return player.playerTask.taskMain.id == 36 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_36_6:
                return player.playerTask.taskMain.id == 36 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_37_0:
                return player.playerTask.taskMain.id == 37 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_37_1:
                return player.playerTask.taskMain.id == 37 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_37_2:
                return player.playerTask.taskMain.id == 37 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_37_3:
                return player.playerTask.taskMain.id == 37 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_37_4:
                return player.playerTask.taskMain.id == 37 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_37_5:
                return player.playerTask.taskMain.id == 37 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_37_6:
                return player.playerTask.taskMain.id == 37 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_38_0:
                return player.playerTask.taskMain.id == 38 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_38_1:
                return player.playerTask.taskMain.id == 38 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_38_2:
                return player.playerTask.taskMain.id == 38 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_38_3:
                return player.playerTask.taskMain.id == 38 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_38_4:
                return player.playerTask.taskMain.id == 38 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_38_5:
                return player.playerTask.taskMain.id == 38 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_38_6:
                return player.playerTask.taskMain.id == 38 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_39_0:
                return player.playerTask.taskMain.id == 39 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_39_1:
                return player.playerTask.taskMain.id == 39 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_39_2:
                return player.playerTask.taskMain.id == 39 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_39_3:
                return player.playerTask.taskMain.id == 39 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_39_4:
                return player.playerTask.taskMain.id == 39 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_39_5:
                return player.playerTask.taskMain.id == 39 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_39_6:
                return player.playerTask.taskMain.id == 39 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_40_0:
                return player.playerTask.taskMain.id == 40 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_40_1:
                return player.playerTask.taskMain.id == 40 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_40_2:
                return player.playerTask.taskMain.id == 40 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_40_3:
                return player.playerTask.taskMain.id == 40 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_40_4:
                return player.playerTask.taskMain.id == 40 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_40_5:
                return player.playerTask.taskMain.id == 40 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_40_6:
                return player.playerTask.taskMain.id == 40 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_41_0:
                return player.playerTask.taskMain.id == 41 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_41_1:
                return player.playerTask.taskMain.id == 41 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_41_2:
                return player.playerTask.taskMain.id == 41 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_41_3:
                return player.playerTask.taskMain.id == 41 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_41_4:
                return player.playerTask.taskMain.id == 41 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_41_5:
                return player.playerTask.taskMain.id == 41 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_41_6:
                return player.playerTask.taskMain.id == 41 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_42_0:
                return player.playerTask.taskMain.id == 42 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_42_1:
                return player.playerTask.taskMain.id == 42 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_42_2:
                return player.playerTask.taskMain.id == 42 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_42_3:
                return player.playerTask.taskMain.id == 42 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_42_4:
                return player.playerTask.taskMain.id == 42 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_42_5:
                return player.playerTask.taskMain.id == 42 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_42_6:
                return player.playerTask.taskMain.id == 42 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_43_0:
                return player.playerTask.taskMain.id == 43 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_43_1:
                return player.playerTask.taskMain.id == 43 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_43_2:
                return player.playerTask.taskMain.id == 43 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_43_3:
                return player.playerTask.taskMain.id == 43 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_43_4:
                return player.playerTask.taskMain.id == 43 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_43_5:
                return player.playerTask.taskMain.id == 43 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_43_6:
                return player.playerTask.taskMain.id == 43 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_44_0:
                return player.playerTask.taskMain.id == 44 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_44_1:
                return player.playerTask.taskMain.id == 44 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_44_2:
                return player.playerTask.taskMain.id == 44 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_44_3:
                return player.playerTask.taskMain.id == 44 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_44_4:
                return player.playerTask.taskMain.id == 44 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_44_5:
                return player.playerTask.taskMain.id == 44 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_44_6:
                return player.playerTask.taskMain.id == 44 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_45_0:
                return player.playerTask.taskMain.id == 45 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_45_1:
                return player.playerTask.taskMain.id == 45 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_45_2:
                return player.playerTask.taskMain.id == 45 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_45_3:
                return player.playerTask.taskMain.id == 45 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_45_4:
                return player.playerTask.taskMain.id == 45 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_45_5:
                return player.playerTask.taskMain.id == 45 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_45_6:
                return player.playerTask.taskMain.id == 45 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_46_0:
                return player.playerTask.taskMain.id == 46 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_46_1:
                return player.playerTask.taskMain.id == 46 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_46_2:
                return player.playerTask.taskMain.id == 46 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_46_3:
                return player.playerTask.taskMain.id == 46 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_46_4:
                return player.playerTask.taskMain.id == 46 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_46_5:
                return player.playerTask.taskMain.id == 46 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_46_6:
                return player.playerTask.taskMain.id == 46 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_47_0:
                return player.playerTask.taskMain.id == 47 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_47_1:
                return player.playerTask.taskMain.id == 47 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_47_2:
                return player.playerTask.taskMain.id == 47 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_47_3:
                return player.playerTask.taskMain.id == 47 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_47_4:
                return player.playerTask.taskMain.id == 47 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_47_5:
                return player.playerTask.taskMain.id == 47 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_47_6:
                return player.playerTask.taskMain.id == 47 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_48_0:
                return player.playerTask.taskMain.id == 48 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_48_1:
                return player.playerTask.taskMain.id == 48 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_48_2:
                return player.playerTask.taskMain.id == 48 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_48_3:
                return player.playerTask.taskMain.id == 48 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_48_4:
                return player.playerTask.taskMain.id == 48 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_48_5:
                return player.playerTask.taskMain.id == 48 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_48_6:
                return player.playerTask.taskMain.id == 48 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_49_0:
                return player.playerTask.taskMain.id == 49 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_49_1:
                return player.playerTask.taskMain.id == 49 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_49_2:
                return player.playerTask.taskMain.id == 49 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_49_3:
                return player.playerTask.taskMain.id == 49 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_49_4:
                return player.playerTask.taskMain.id == 49 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_49_5:
                return player.playerTask.taskMain.id == 49 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_49_6:
                return player.playerTask.taskMain.id == 49 && player.playerTask.taskMain.index == 6;
            case ConstTask.TASK_50_0:
                return player.playerTask.taskMain.id == 50 && player.playerTask.taskMain.index == 0;
            case ConstTask.TASK_50_1:
                return player.playerTask.taskMain.id == 50 && player.playerTask.taskMain.index == 1;
            case ConstTask.TASK_50_2:
                return player.playerTask.taskMain.id == 50 && player.playerTask.taskMain.index == 2;
            case ConstTask.TASK_50_3:
                return player.playerTask.taskMain.id == 50 && player.playerTask.taskMain.index == 3;
            case ConstTask.TASK_50_4:
                return player.playerTask.taskMain.id == 50 && player.playerTask.taskMain.index == 4;
            case ConstTask.TASK_50_5:
                return player.playerTask.taskMain.id == 50 && player.playerTask.taskMain.index == 5;
            case ConstTask.TASK_50_6:
                return player.playerTask.taskMain.id == 50 && player.playerTask.taskMain.index == 6;
        }
        return false;
    }

    public int getIdTask(Player player) {
        if (player.isPet || player.isBoss || player.playerTask == null || player.playerTask.taskMain == null) {
            return -1;
        } else if (player.playerTask.taskMain.id == 0 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_0_0;
        } else if (player.playerTask.taskMain.id == 0 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_0_1;
        } else if (player.playerTask.taskMain.id == 0 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_0_2;
        } else if (player.playerTask.taskMain.id == 0 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_0_3;
        } else if (player.playerTask.taskMain.id == 0 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_0_4;
        } else if (player.playerTask.taskMain.id == 0 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_0_5;
        } else if (player.playerTask.taskMain.id == 0 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_0_6;
        } else if (player.playerTask.taskMain.id == 1 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_1_0;
        } else if (player.playerTask.taskMain.id == 1 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_1_1;
        } else if (player.playerTask.taskMain.id == 1 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_1_2;
        } else if (player.playerTask.taskMain.id == 1 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_1_3;
        } else if (player.playerTask.taskMain.id == 1 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_1_4;
        } else if (player.playerTask.taskMain.id == 1 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_1_5;
        } else if (player.playerTask.taskMain.id == 1 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_1_6;
        } else if (player.playerTask.taskMain.id == 2 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_2_0;
        } else if (player.playerTask.taskMain.id == 2 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_2_1;
        } else if (player.playerTask.taskMain.id == 2 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_2_2;
        } else if (player.playerTask.taskMain.id == 2 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_2_3;
        } else if (player.playerTask.taskMain.id == 2 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_2_4;
        } else if (player.playerTask.taskMain.id == 2 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_2_5;
        } else if (player.playerTask.taskMain.id == 2 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_2_6;
        } else if (player.playerTask.taskMain.id == 3 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_3_0;
        } else if (player.playerTask.taskMain.id == 3 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_3_1;
        } else if (player.playerTask.taskMain.id == 3 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_3_2;
        } else if (player.playerTask.taskMain.id == 3 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_3_3;
        } else if (player.playerTask.taskMain.id == 3 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_3_4;
        } else if (player.playerTask.taskMain.id == 3 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_3_5;
        } else if (player.playerTask.taskMain.id == 3 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_3_6;
        } else if (player.playerTask.taskMain.id == 4 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_4_0;
        } else if (player.playerTask.taskMain.id == 4 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_4_1;
        } else if (player.playerTask.taskMain.id == 4 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_4_2;
        } else if (player.playerTask.taskMain.id == 4 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_4_3;
        } else if (player.playerTask.taskMain.id == 4 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_4_4;
        } else if (player.playerTask.taskMain.id == 4 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_4_5;
        } else if (player.playerTask.taskMain.id == 4 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_4_6;
        } else if (player.playerTask.taskMain.id == 5 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_5_0;
        } else if (player.playerTask.taskMain.id == 5 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_5_1;
        } else if (player.playerTask.taskMain.id == 5 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_5_2;
        } else if (player.playerTask.taskMain.id == 5 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_5_3;
        } else if (player.playerTask.taskMain.id == 5 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_5_4;
        } else if (player.playerTask.taskMain.id == 5 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_5_5;
        } else if (player.playerTask.taskMain.id == 5 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_5_6;
        } else if (player.playerTask.taskMain.id == 6 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_6_0;
        } else if (player.playerTask.taskMain.id == 6 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_6_1;
        } else if (player.playerTask.taskMain.id == 6 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_6_2;
        } else if (player.playerTask.taskMain.id == 6 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_6_3;
        } else if (player.playerTask.taskMain.id == 6 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_6_4;
        } else if (player.playerTask.taskMain.id == 6 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_6_5;
        } else if (player.playerTask.taskMain.id == 6 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_6_6;
        } else if (player.playerTask.taskMain.id == 7 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_7_0;
        } else if (player.playerTask.taskMain.id == 7 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_7_1;
        } else if (player.playerTask.taskMain.id == 7 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_7_2;
        } else if (player.playerTask.taskMain.id == 7 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_7_3;
        } else if (player.playerTask.taskMain.id == 7 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_7_4;
        } else if (player.playerTask.taskMain.id == 7 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_7_5;
        } else if (player.playerTask.taskMain.id == 7 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_7_6;
        } else if (player.playerTask.taskMain.id == 8 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_8_0;
        } else if (player.playerTask.taskMain.id == 8 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_8_1;
        } else if (player.playerTask.taskMain.id == 8 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_8_2;
        } else if (player.playerTask.taskMain.id == 8 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_8_3;
        } else if (player.playerTask.taskMain.id == 8 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_8_4;
        } else if (player.playerTask.taskMain.id == 8 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_8_5;
        } else if (player.playerTask.taskMain.id == 8 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_8_6;
        } else if (player.playerTask.taskMain.id == 9 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_9_0;
        } else if (player.playerTask.taskMain.id == 9 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_9_1;
        } else if (player.playerTask.taskMain.id == 9 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_9_2;
        } else if (player.playerTask.taskMain.id == 9 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_9_3;
        } else if (player.playerTask.taskMain.id == 9 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_9_4;
        } else if (player.playerTask.taskMain.id == 9 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_9_5;
        } else if (player.playerTask.taskMain.id == 9 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_9_6;
        } else if (player.playerTask.taskMain.id == 10 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_10_0;
        } else if (player.playerTask.taskMain.id == 10 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_10_1;
        } else if (player.playerTask.taskMain.id == 10 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_10_2;
        } else if (player.playerTask.taskMain.id == 10 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_10_3;
        } else if (player.playerTask.taskMain.id == 10 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_10_4;
        } else if (player.playerTask.taskMain.id == 10 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_10_5;
        } else if (player.playerTask.taskMain.id == 10 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_10_6;
        } else if (player.playerTask.taskMain.id == 11 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_11_0;
        } else if (player.playerTask.taskMain.id == 11 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_11_1;
        } else if (player.playerTask.taskMain.id == 11 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_11_2;
        } else if (player.playerTask.taskMain.id == 11 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_11_3;
        } else if (player.playerTask.taskMain.id == 11 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_11_4;
        } else if (player.playerTask.taskMain.id == 11 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_11_5;
        } else if (player.playerTask.taskMain.id == 11 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_11_6;
        } else if (player.playerTask.taskMain.id == 12 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_12_0;
        } else if (player.playerTask.taskMain.id == 12 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_12_1;
        } else if (player.playerTask.taskMain.id == 12 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_12_2;
        } else if (player.playerTask.taskMain.id == 12 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_12_3;
        } else if (player.playerTask.taskMain.id == 12 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_12_4;
        } else if (player.playerTask.taskMain.id == 12 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_12_5;
        } else if (player.playerTask.taskMain.id == 12 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_12_6;
        } else if (player.playerTask.taskMain.id == 13 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_13_0;
        } else if (player.playerTask.taskMain.id == 13 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_13_1;
        } else if (player.playerTask.taskMain.id == 13 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_13_2;
        } else if (player.playerTask.taskMain.id == 13 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_13_3;
        } else if (player.playerTask.taskMain.id == 13 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_13_4;
        } else if (player.playerTask.taskMain.id == 13 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_13_5;
        } else if (player.playerTask.taskMain.id == 13 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_13_6;
        } else if (player.playerTask.taskMain.id == 14 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_14_0;
        } else if (player.playerTask.taskMain.id == 14 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_14_1;
        } else if (player.playerTask.taskMain.id == 14 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_14_2;
        } else if (player.playerTask.taskMain.id == 14 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_14_3;
        } else if (player.playerTask.taskMain.id == 14 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_14_4;
        } else if (player.playerTask.taskMain.id == 14 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_14_5;
        } else if (player.playerTask.taskMain.id == 14 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_14_6;
        } else if (player.playerTask.taskMain.id == 15 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_15_0;
        } else if (player.playerTask.taskMain.id == 15 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_15_1;
        } else if (player.playerTask.taskMain.id == 15 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_15_2;
        } else if (player.playerTask.taskMain.id == 15 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_15_3;
        } else if (player.playerTask.taskMain.id == 15 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_15_4;
        } else if (player.playerTask.taskMain.id == 15 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_15_5;
        } else if (player.playerTask.taskMain.id == 15 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_15_6;
        } else if (player.playerTask.taskMain.id == 16 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_16_0;
        } else if (player.playerTask.taskMain.id == 16 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_16_1;
        } else if (player.playerTask.taskMain.id == 16 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_16_2;
        } else if (player.playerTask.taskMain.id == 16 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_16_3;
        } else if (player.playerTask.taskMain.id == 16 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_16_4;
        } else if (player.playerTask.taskMain.id == 16 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_16_5;
        } else if (player.playerTask.taskMain.id == 16 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_16_6;
        } else if (player.playerTask.taskMain.id == 17 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_17_0;
        } else if (player.playerTask.taskMain.id == 17 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_17_1;
        } else if (player.playerTask.taskMain.id == 17 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_17_2;
        } else if (player.playerTask.taskMain.id == 17 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_17_3;
        } else if (player.playerTask.taskMain.id == 17 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_17_4;
        } else if (player.playerTask.taskMain.id == 17 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_17_5;
        } else if (player.playerTask.taskMain.id == 17 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_17_6;
        } else if (player.playerTask.taskMain.id == 18 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_18_0;
        } else if (player.playerTask.taskMain.id == 18 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_18_1;
        } else if (player.playerTask.taskMain.id == 18 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_18_2;
        } else if (player.playerTask.taskMain.id == 18 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_18_3;
        } else if (player.playerTask.taskMain.id == 18 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_18_4;
        } else if (player.playerTask.taskMain.id == 18 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_18_5;
        } else if (player.playerTask.taskMain.id == 18 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_18_6;
        } else if (player.playerTask.taskMain.id == 19 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_19_0;
        } else if (player.playerTask.taskMain.id == 19 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_19_1;
        } else if (player.playerTask.taskMain.id == 19 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_19_2;
        } else if (player.playerTask.taskMain.id == 19 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_19_3;
        } else if (player.playerTask.taskMain.id == 19 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_19_4;
        } else if (player.playerTask.taskMain.id == 19 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_19_5;
        } else if (player.playerTask.taskMain.id == 19 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_19_6;
        } else if (player.playerTask.taskMain.id == 20 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_20_0;
        } else if (player.playerTask.taskMain.id == 20 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_20_1;
        } else if (player.playerTask.taskMain.id == 20 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_20_2;
        } else if (player.playerTask.taskMain.id == 20 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_20_3;
        } else if (player.playerTask.taskMain.id == 20 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_20_4;
        } else if (player.playerTask.taskMain.id == 20 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_20_5;
        } else if (player.playerTask.taskMain.id == 20 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_20_6;
        } else if (player.playerTask.taskMain.id == 21 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_21_0;
        } else if (player.playerTask.taskMain.id == 21 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_21_1;
        } else if (player.playerTask.taskMain.id == 21 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_21_2;
        } else if (player.playerTask.taskMain.id == 21 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_21_3;
        } else if (player.playerTask.taskMain.id == 21 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_21_4;
        } else if (player.playerTask.taskMain.id == 21 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_21_5;
        } else if (player.playerTask.taskMain.id == 21 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_21_6;
        } else if (player.playerTask.taskMain.id == 22 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_22_0;
        } else if (player.playerTask.taskMain.id == 22 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_22_1;
        } else if (player.playerTask.taskMain.id == 22 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_22_2;
        } else if (player.playerTask.taskMain.id == 22 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_22_3;
        } else if (player.playerTask.taskMain.id == 22 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_22_4;
        } else if (player.playerTask.taskMain.id == 22 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_22_5;
        } else if (player.playerTask.taskMain.id == 22 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_22_6;
        } else if (player.playerTask.taskMain.id == 23 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_23_0;
        } else if (player.playerTask.taskMain.id == 23 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_23_1;
        } else if (player.playerTask.taskMain.id == 23 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_23_2;
        } else if (player.playerTask.taskMain.id == 23 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_23_3;
        } else if (player.playerTask.taskMain.id == 23 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_23_4;
        } else if (player.playerTask.taskMain.id == 23 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_23_5;
        } else if (player.playerTask.taskMain.id == 23 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_23_6;
        } else if (player.playerTask.taskMain.id == 24 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_24_0;
        } else if (player.playerTask.taskMain.id == 24 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_24_1;
        } else if (player.playerTask.taskMain.id == 24 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_24_2;
        } else if (player.playerTask.taskMain.id == 24 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_24_3;
        } else if (player.playerTask.taskMain.id == 24 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_24_4;
        } else if (player.playerTask.taskMain.id == 24 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_24_5;
        } else if (player.playerTask.taskMain.id == 24 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_24_6;
        } else if (player.playerTask.taskMain.id == 25 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_25_0;
        } else if (player.playerTask.taskMain.id == 25 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_25_1;
        } else if (player.playerTask.taskMain.id == 25 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_25_2;
        } else if (player.playerTask.taskMain.id == 25 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_25_3;
        } else if (player.playerTask.taskMain.id == 25 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_25_4;
        } else if (player.playerTask.taskMain.id == 25 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_25_5;
        } else if (player.playerTask.taskMain.id == 25 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_25_6;
        } else if (player.playerTask.taskMain.id == 26 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_26_0;
        } else if (player.playerTask.taskMain.id == 26 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_26_1;
        } else if (player.playerTask.taskMain.id == 26 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_26_2;
        } else if (player.playerTask.taskMain.id == 26 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_26_3;
        } else if (player.playerTask.taskMain.id == 26 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_26_4;
        } else if (player.playerTask.taskMain.id == 26 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_26_5;
        } else if (player.playerTask.taskMain.id == 26 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_26_6;
        } else if (player.playerTask.taskMain.id == 27 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_27_0;
        } else if (player.playerTask.taskMain.id == 27 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_27_1;
        } else if (player.playerTask.taskMain.id == 27 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_27_2;
        } else if (player.playerTask.taskMain.id == 27 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_27_3;
        } else if (player.playerTask.taskMain.id == 27 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_27_4;
        } else if (player.playerTask.taskMain.id == 27 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_27_5;
        } else if (player.playerTask.taskMain.id == 27 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_27_6;
        } else if (player.playerTask.taskMain.id == 28 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_28_0;
        } else if (player.playerTask.taskMain.id == 28 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_28_1;
        } else if (player.playerTask.taskMain.id == 28 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_28_2;
        } else if (player.playerTask.taskMain.id == 28 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_28_3;
        } else if (player.playerTask.taskMain.id == 28 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_28_4;
        } else if (player.playerTask.taskMain.id == 28 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_28_5;
        } else if (player.playerTask.taskMain.id == 28 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_28_6;
        } else if (player.playerTask.taskMain.id == 29 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_29_0;
        } else if (player.playerTask.taskMain.id == 29 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_29_1;
        } else if (player.playerTask.taskMain.id == 29 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_29_2;
        } else if (player.playerTask.taskMain.id == 29 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_29_3;
        } else if (player.playerTask.taskMain.id == 29 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_29_4;
        } else if (player.playerTask.taskMain.id == 29 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_29_5;
        } else if (player.playerTask.taskMain.id == 29 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_29_6;
        } else if (player.playerTask.taskMain.id == 30 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_30_0;
        } else if (player.playerTask.taskMain.id == 30 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_30_1;
        } else if (player.playerTask.taskMain.id == 30 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_30_2;
        } else if (player.playerTask.taskMain.id == 30 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_30_3;
        } else if (player.playerTask.taskMain.id == 30 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_30_4;
        } else if (player.playerTask.taskMain.id == 30 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_30_5;
        } else if (player.playerTask.taskMain.id == 30 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_30_6;
        } else if (player.playerTask.taskMain.id == 31 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_31_0;
        } else if (player.playerTask.taskMain.id == 31 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_31_1;
        } else if (player.playerTask.taskMain.id == 31 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_31_2;
        } else if (player.playerTask.taskMain.id == 31 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_31_3;
        } else if (player.playerTask.taskMain.id == 31 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_31_4;
        } else if (player.playerTask.taskMain.id == 31 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_31_5;
        } else if (player.playerTask.taskMain.id == 31 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_31_6;
        } else if (player.playerTask.taskMain.id == 32 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_32_0;
        } else if (player.playerTask.taskMain.id == 32 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_32_1;
        } else if (player.playerTask.taskMain.id == 32 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_32_2;
        } else if (player.playerTask.taskMain.id == 32 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_32_3;
        } else if (player.playerTask.taskMain.id == 32 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_32_4;
        } else if (player.playerTask.taskMain.id == 32 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_32_5;
        } else if (player.playerTask.taskMain.id == 32 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_32_6;
        } else if (player.playerTask.taskMain.id == 33 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_33_0;
        } else if (player.playerTask.taskMain.id == 33 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_33_1;
        } else if (player.playerTask.taskMain.id == 33 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_33_2;
        } else if (player.playerTask.taskMain.id == 33 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_33_3;
        } else if (player.playerTask.taskMain.id == 33 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_33_4;
        } else if (player.playerTask.taskMain.id == 33 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_33_5;
        } else if (player.playerTask.taskMain.id == 33 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_33_6;
        } else if (player.playerTask.taskMain.id == 34 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_34_0;
        } else if (player.playerTask.taskMain.id == 34 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_34_1;
        } else if (player.playerTask.taskMain.id == 34 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_34_2;
        } else if (player.playerTask.taskMain.id == 34 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_34_3;
        } else if (player.playerTask.taskMain.id == 34 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_34_4;
        } else if (player.playerTask.taskMain.id == 34 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_34_5;
        } else if (player.playerTask.taskMain.id == 34 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_34_6;
        } else if (player.playerTask.taskMain.id == 35 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_35_0;
        } else if (player.playerTask.taskMain.id == 35 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_35_1;
        } else if (player.playerTask.taskMain.id == 35 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_35_2;
        } else if (player.playerTask.taskMain.id == 35 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_35_3;
        } else if (player.playerTask.taskMain.id == 35 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_35_4;
        } else if (player.playerTask.taskMain.id == 35 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_35_5;
        } else if (player.playerTask.taskMain.id == 35 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_35_6;
        } else if (player.playerTask.taskMain.id == 36 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_36_0;
        } else if (player.playerTask.taskMain.id == 36 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_36_1;
        } else if (player.playerTask.taskMain.id == 36 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_36_2;
        } else if (player.playerTask.taskMain.id == 36 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_36_3;
        } else if (player.playerTask.taskMain.id == 36 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_36_4;
        } else if (player.playerTask.taskMain.id == 36 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_36_5;
        } else if (player.playerTask.taskMain.id == 36 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_36_6;
        } else if (player.playerTask.taskMain.id == 37 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_37_0;
        } else if (player.playerTask.taskMain.id == 37 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_37_1;
        } else if (player.playerTask.taskMain.id == 37 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_37_2;
        } else if (player.playerTask.taskMain.id == 37 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_37_3;
        } else if (player.playerTask.taskMain.id == 37 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_37_4;
        } else if (player.playerTask.taskMain.id == 37 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_37_5;
        } else if (player.playerTask.taskMain.id == 37 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_37_6;
        } else if (player.playerTask.taskMain.id == 38 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_38_0;
        } else if (player.playerTask.taskMain.id == 38 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_38_1;
        } else if (player.playerTask.taskMain.id == 38 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_38_2;
        } else if (player.playerTask.taskMain.id == 38 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_38_3;
        } else if (player.playerTask.taskMain.id == 38 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_38_4;
        } else if (player.playerTask.taskMain.id == 38 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_38_5;
        } else if (player.playerTask.taskMain.id == 38 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_38_6;
        } else if (player.playerTask.taskMain.id == 39 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_39_0;
        } else if (player.playerTask.taskMain.id == 39 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_39_1;
        } else if (player.playerTask.taskMain.id == 39 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_39_2;
        } else if (player.playerTask.taskMain.id == 39 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_39_3;
        } else if (player.playerTask.taskMain.id == 39 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_39_4;
        } else if (player.playerTask.taskMain.id == 39 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_39_5;
        } else if (player.playerTask.taskMain.id == 39 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_39_6;
        } else if (player.playerTask.taskMain.id == 40 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_40_0;
        } else if (player.playerTask.taskMain.id == 40 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_40_1;
        } else if (player.playerTask.taskMain.id == 40 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_40_2;
        } else if (player.playerTask.taskMain.id == 40 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_40_3;
        } else if (player.playerTask.taskMain.id == 40 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_40_4;
        } else if (player.playerTask.taskMain.id == 40 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_40_5;
        } else if (player.playerTask.taskMain.id == 40 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_40_6;
        } else if (player.playerTask.taskMain.id == 41 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_41_0;
        } else if (player.playerTask.taskMain.id == 41 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_41_1;
        } else if (player.playerTask.taskMain.id == 41 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_41_2;
        } else if (player.playerTask.taskMain.id == 41 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_41_3;
        } else if (player.playerTask.taskMain.id == 41 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_41_4;
        } else if (player.playerTask.taskMain.id == 41 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_41_5;
        } else if (player.playerTask.taskMain.id == 41 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_41_6;
        } else if (player.playerTask.taskMain.id == 42 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_42_0;
        } else if (player.playerTask.taskMain.id == 42 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_42_1;
        } else if (player.playerTask.taskMain.id == 42 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_42_2;
        } else if (player.playerTask.taskMain.id == 42 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_42_3;
        } else if (player.playerTask.taskMain.id == 42 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_42_4;
        } else if (player.playerTask.taskMain.id == 42 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_42_5;
        } else if (player.playerTask.taskMain.id == 42 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_42_6;
        } else if (player.playerTask.taskMain.id == 43 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_43_0;
        } else if (player.playerTask.taskMain.id == 43 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_43_1;
        } else if (player.playerTask.taskMain.id == 43 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_43_2;
        } else if (player.playerTask.taskMain.id == 43 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_43_3;
        } else if (player.playerTask.taskMain.id == 43 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_43_4;
        } else if (player.playerTask.taskMain.id == 43 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_43_5;
        } else if (player.playerTask.taskMain.id == 43 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_43_6;
        } else if (player.playerTask.taskMain.id == 44 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_44_0;
        } else if (player.playerTask.taskMain.id == 44 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_44_1;
        } else if (player.playerTask.taskMain.id == 44 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_44_2;
        } else if (player.playerTask.taskMain.id == 44 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_44_3;
        } else if (player.playerTask.taskMain.id == 44 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_44_4;
        } else if (player.playerTask.taskMain.id == 44 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_44_5;
        } else if (player.playerTask.taskMain.id == 44 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_44_6;
        } else if (player.playerTask.taskMain.id == 45 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_45_0;
        } else if (player.playerTask.taskMain.id == 45 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_45_1;
        } else if (player.playerTask.taskMain.id == 45 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_45_2;
        } else if (player.playerTask.taskMain.id == 45 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_45_3;
        } else if (player.playerTask.taskMain.id == 45 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_45_4;
        } else if (player.playerTask.taskMain.id == 45 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_45_5;
        } else if (player.playerTask.taskMain.id == 45 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_45_6;
        } else if (player.playerTask.taskMain.id == 46 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_46_0;
        } else if (player.playerTask.taskMain.id == 46 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_46_1;
        } else if (player.playerTask.taskMain.id == 46 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_46_2;
        } else if (player.playerTask.taskMain.id == 46 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_46_3;
        } else if (player.playerTask.taskMain.id == 46 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_46_4;
        } else if (player.playerTask.taskMain.id == 46 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_46_5;
        } else if (player.playerTask.taskMain.id == 46 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_46_6;
        } else if (player.playerTask.taskMain.id == 47 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_47_0;
        } else if (player.playerTask.taskMain.id == 47 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_47_1;
        } else if (player.playerTask.taskMain.id == 47 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_47_2;
        } else if (player.playerTask.taskMain.id == 47 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_47_3;
        } else if (player.playerTask.taskMain.id == 47 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_47_4;
        } else if (player.playerTask.taskMain.id == 47 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_47_5;
        } else if (player.playerTask.taskMain.id == 47 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_47_6;
        } else if (player.playerTask.taskMain.id == 48 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_48_0;
        } else if (player.playerTask.taskMain.id == 48 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_48_1;
        } else if (player.playerTask.taskMain.id == 48 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_48_2;
        } else if (player.playerTask.taskMain.id == 48 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_48_3;
        } else if (player.playerTask.taskMain.id == 48 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_48_4;
        } else if (player.playerTask.taskMain.id == 48 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_48_5;
        } else if (player.playerTask.taskMain.id == 48 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_48_6;
        } else if (player.playerTask.taskMain.id == 49 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_49_0;
        } else if (player.playerTask.taskMain.id == 49 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_49_1;
        } else if (player.playerTask.taskMain.id == 49 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_49_2;
        } else if (player.playerTask.taskMain.id == 49 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_49_3;
        } else if (player.playerTask.taskMain.id == 49 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_49_4;
        } else if (player.playerTask.taskMain.id == 49 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_49_5;
        } else if (player.playerTask.taskMain.id == 49 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_49_6;
        } else if (player.playerTask.taskMain.id == 50 && player.playerTask.taskMain.index == 0) {
            return ConstTask.TASK_50_0;
        } else if (player.playerTask.taskMain.id == 50 && player.playerTask.taskMain.index == 1) {
            return ConstTask.TASK_50_1;
        } else if (player.playerTask.taskMain.id == 50 && player.playerTask.taskMain.index == 2) {
            return ConstTask.TASK_50_2;
        } else if (player.playerTask.taskMain.id == 50 && player.playerTask.taskMain.index == 3) {
            return ConstTask.TASK_50_3;
        } else if (player.playerTask.taskMain.id == 50 && player.playerTask.taskMain.index == 4) {
            return ConstTask.TASK_50_4;
        } else if (player.playerTask.taskMain.id == 50 && player.playerTask.taskMain.index == 5) {
            return ConstTask.TASK_50_5;
        } else if (player.playerTask.taskMain.id == 50 && player.playerTask.taskMain.index == 6) {
            return ConstTask.TASK_50_6;
        } else {
            return -1;
        }
    }

    //--------------------------------------------------------------------------
    public SideTaskTemplate getSideTaskTemplateById(int id) {
        if (id != -1) {
            return Manager.SIDE_TASKS_TEMPLATE.get(id);
        }
        return null;
    }

    public void changeSideTask(Player player, byte level) {
        if (player.playerTask.sideTask.leftTask > 0) {
            player.playerTask.sideTask.reset();
            SideTaskTemplate temp = Manager.SIDE_TASKS_TEMPLATE.get(Util.nextInt(0, Manager.SIDE_TASKS_TEMPLATE.size() - 1));
            player.playerTask.sideTask.template = temp;
            player.playerTask.sideTask.maxCount = Util.nextInt(temp.count[level][0], temp.count[level][1]);
            player.playerTask.sideTask.leftTask--;
            player.playerTask.sideTask.level = level;
            player.playerTask.sideTask.receivedTime = System.currentTimeMillis();
            Service.getInstance().sendThongBao(player, "Bạn nhận được nhiệm vụ: " + player.playerTask.sideTask.getName());
        } else {
            Service.getInstance().sendThongBao(player,
                    "Bạn đã nhận hết nhiệm vụ hôm nay. Hãy chờ tới ngày mai rồi nhận tiếp");
        }
    }

    public void removeSideTask(Player player) {
        Service.getInstance().sendThongBao(player, "Bạn vừa hủy bỏ nhiệm vụ " + player.playerTask.sideTask.getName());
        player.playerTask.sideTask.reset();
    }

    public void paySideTask(Player player) {
        if (player.playerTask.sideTask.template != null) {
            if (player.playerTask.sideTask.isDone()) {
                int goldReward = 0;
                int ruby = 0;
                switch (player.playerTask.sideTask.level) {
                    case ConstTask.EASY:
                        goldReward = ConstTask.GOLD_EASY;
//                        ruby = ConstTask.RUBY_HELL;
                        break;
                    case ConstTask.NORMAL:
                        goldReward = ConstTask.GOLD_NORMAL;
//                        ruby = ConstTask.RUBY_HELL;
                        break;
                    case ConstTask.HARD:
                        goldReward = ConstTask.GOLD_HARD;
//                        ruby = ConstTask.RUBY_HELL;
                        break;
                    case ConstTask.VERY_HARD:
                        goldReward = ConstTask.GOLD_VERY_HARD;
//                        ruby = ConstTask.RUBY_HELL;
                        break;
                    case ConstTask.HELL:
                        goldReward = ConstTask.GOLD_HELL;
//                        ruby = ConstTask.RUBY_HELL;
                        break;
                }
//                player.inventory.ruby += ruby;
                Item thoivang = ItemService.gI().createNewItem((short) 457);
                thoivang.quantity = goldReward;
                thoivang.itemOptions.add(new ItemOption(30, 1));
                InventoryService.gI().addItemBag(player, thoivang, 999);
                InventoryService.gI().sendItemBags(player);
                Service.getInstance().sendMoney(player);
                Service.getInstance().sendThongBao(player, "Bạn nhận được "
                        + goldReward + " Thỏi vàng(khóa)");//và " + ruby + " Hồng ngọc"
                player.playerTask.sideTask.reset();
            } else {
                Service.getInstance().sendThongBao(player, "Bạn chưa hoàn thành nhiệm vụ");
            }
        }
    }

    public void checkDoneSideTaskKillMob(Player player, Mob mob) {
        if (player.playerTask.sideTask.template != null) {
            if ((player.playerTask.sideTask.template.id == 0 && mob.tempId == ConstMob.KHUNG_LONG)
                    || (player.playerTask.sideTask.template.id == 1 && mob.tempId == ConstMob.LON_LOI)
                    || (player.playerTask.sideTask.template.id == 2 && mob.tempId == ConstMob.QUY_DAT)
                    || (player.playerTask.sideTask.template.id == 3 && mob.tempId == ConstMob.KHUNG_LONG_ME)
                    || (player.playerTask.sideTask.template.id == 4 && mob.tempId == ConstMob.LON_LOI_ME)
                    || (player.playerTask.sideTask.template.id == 5 && mob.tempId == ConstMob.QUY_DAT_ME)
                    || (player.playerTask.sideTask.template.id == 6 && mob.tempId == ConstMob.THAN_LAN_BAY)
                    || (player.playerTask.sideTask.template.id == 7 && mob.tempId == ConstMob.PHI_LONG)
                    || (player.playerTask.sideTask.template.id == 8 && mob.tempId == ConstMob.QUY_BAY)
                    || (player.playerTask.sideTask.template.id == 9 && mob.tempId == ConstMob.THAN_LAN_ME)
                    || (player.playerTask.sideTask.template.id == 10 && mob.tempId == ConstMob.PHI_LONG_ME)
                    || (player.playerTask.sideTask.template.id == 11 && mob.tempId == ConstMob.QUY_BAY_ME)
                    || (player.playerTask.sideTask.template.id == 12 && mob.tempId == ConstMob.HEO_RUNG)
                    || (player.playerTask.sideTask.template.id == 13 && mob.tempId == ConstMob.HEO_DA_XANH)
                    || (player.playerTask.sideTask.template.id == 14 && mob.tempId == ConstMob.HEO_XAYDA)
                    || (player.playerTask.sideTask.template.id == 15 && mob.tempId == ConstMob.OC_MUON_HON)
                    || (player.playerTask.sideTask.template.id == 16 && mob.tempId == ConstMob.OC_SEN)
                    || (player.playerTask.sideTask.template.id == 17 && mob.tempId == ConstMob.HEO_XAYDA_ME)
                    || (player.playerTask.sideTask.template.id == 18 && mob.tempId == ConstMob.KHONG_TAC)
                    || (player.playerTask.sideTask.template.id == 19 && mob.tempId == ConstMob.QUY_DAU_TO)
                    || (player.playerTask.sideTask.template.id == 20 && mob.tempId == ConstMob.QUY_DIA_NGUC)
                    || (player.playerTask.sideTask.template.id == 21 && mob.tempId == ConstMob.HEO_RUNG_ME)
                    || (player.playerTask.sideTask.template.id == 22 && mob.tempId == ConstMob.HEO_XANH_ME)
                    || (player.playerTask.sideTask.template.id == 23 && mob.tempId == ConstMob.ALIEN)
                    || (player.playerTask.sideTask.template.id == 24 && mob.tempId == ConstMob.TAMBOURINE)
                    || (player.playerTask.sideTask.template.id == 25 && mob.tempId == ConstMob.DRUM)
                    || (player.playerTask.sideTask.template.id == 26 && mob.tempId == ConstMob.AKKUMAN)
                    || (player.playerTask.sideTask.template.id == 27 && mob.tempId == ConstMob.NAPPA)
                    || (player.playerTask.sideTask.template.id == 28 && mob.tempId == ConstMob.SOLDIER)
                    || (player.playerTask.sideTask.template.id == 29 && mob.tempId == ConstMob.APPULE)
                    || (player.playerTask.sideTask.template.id == 30 && mob.tempId == ConstMob.RASPBERRY)
                    || (player.playerTask.sideTask.template.id == 31 && mob.tempId == ConstMob.THAN_LAN_XANH)
                    || (player.playerTask.sideTask.template.id == 32 && mob.tempId == ConstMob.QUY_DAU_NHON)
                    || (player.playerTask.sideTask.template.id == 33 && mob.tempId == ConstMob.QUY_DAU_VANG)
                    || (player.playerTask.sideTask.template.id == 34 && mob.tempId == ConstMob.QUY_DA_TIM)
                    || (player.playerTask.sideTask.template.id == 35 && mob.tempId == ConstMob.QUY_GIA)
                    || (player.playerTask.sideTask.template.id == 36 && mob.tempId == ConstMob.CA_SAU)
                    || (player.playerTask.sideTask.template.id == 37 && mob.tempId == ConstMob.DOI_DA_XANH)
                    || (player.playerTask.sideTask.template.id == 38 && mob.tempId == ConstMob.QUY_CHIM)
                    || (player.playerTask.sideTask.template.id == 39 && mob.tempId == ConstMob.LINH_DAU_TROC)
                    || (player.playerTask.sideTask.template.id == 40 && mob.tempId == ConstMob.LINH_TAI_DAI)
                    || (player.playerTask.sideTask.template.id == 41 && mob.tempId == ConstMob.LINH_VU_TRU)
                    || (player.playerTask.sideTask.template.id == 42 && mob.tempId == ConstMob.KHI_LONG_DEN)
                    || (player.playerTask.sideTask.template.id == 43 && mob.tempId == ConstMob.KHI_GIAP_SAT)
                    || (player.playerTask.sideTask.template.id == 44 && mob.tempId == ConstMob.KHI_LONG_DO)
                    || (player.playerTask.sideTask.template.id == 45 && mob.tempId == ConstMob.KHI_LONG_VANG)
                    || (player.playerTask.sideTask.template.id == 46 && mob.tempId == ConstMob.XEN_CON_CAP_1)
                    || (player.playerTask.sideTask.template.id == 47 && mob.tempId == ConstMob.XEN_CON_CAP_2)
                    || (player.playerTask.sideTask.template.id == 48 && mob.tempId == ConstMob.XEN_CON_CAP_3)
                    || (player.playerTask.sideTask.template.id == 49 && mob.tempId == ConstMob.XEN_CON_CAP_4)
                    || (player.playerTask.sideTask.template.id == 50 && mob.tempId == ConstMob.XEN_CON_CAP_5)
                    || (player.playerTask.sideTask.template.id == 51 && mob.tempId == ConstMob.XEN_CON_CAP_6)
                    || (player.playerTask.sideTask.template.id == 52 && mob.tempId == ConstMob.XEN_CON_CAP_7)
                    || (player.playerTask.sideTask.template.id == 53 && mob.tempId == ConstMob.XEN_CON_CAP_8)
                    || (player.playerTask.sideTask.template.id == 54 && mob.tempId == ConstMob.TAI_TIM)
                    || (player.playerTask.sideTask.template.id == 55 && mob.tempId == ConstMob.ABO)
                    || (player.playerTask.sideTask.template.id == 56 && mob.tempId == ConstMob.KADO)
                    || (player.playerTask.sideTask.template.id == 57 && mob.tempId == ConstMob.DA_XANH)) {
                player.playerTask.sideTask.count++;
                notifyProcessSideTask(player);
            }
        }
    }

    public void checkDoneSideTaskPickItem(Player player, ItemMap item) {
        if (player.playerTask.sideTask.template != null) {
            if ((player.playerTask.sideTask.template.id == 58 && item.itemTemplate.type == 9)) {
                player.playerTask.sideTask.count += item.quantity;
                notifyProcessSideTask(player);
            }
        }
    }

    private void notifyProcessSideTask(Player player) {
        int percentDone = player.playerTask.sideTask.getPercentProcess();
        boolean notify = false;
        if (percentDone != 100) {
            if (!player.playerTask.sideTask.notify90 && percentDone >= 90) {
                player.playerTask.sideTask.notify90 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify80 && percentDone >= 80) {
                player.playerTask.sideTask.notify80 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify70 && percentDone >= 70) {
                player.playerTask.sideTask.notify70 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify60 && percentDone >= 60) {
                player.playerTask.sideTask.notify60 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify50 && percentDone >= 50) {
                player.playerTask.sideTask.notify50 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify40 && percentDone >= 40) {
                player.playerTask.sideTask.notify40 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify30 && percentDone >= 30) {
                player.playerTask.sideTask.notify30 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify20 && percentDone >= 20) {
                player.playerTask.sideTask.notify20 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify10 && percentDone >= 10) {
                player.playerTask.sideTask.notify10 = true;
                notify = true;
            } else if (!player.playerTask.sideTask.notify0 && percentDone >= 0) {
                player.playerTask.sideTask.notify0 = true;
                notify = true;
            }
            if (notify) {
                Service.getInstance().sendThongBao(player, "Nhiệm vụ: "
                        + player.playerTask.sideTask.getName() + " đã hoàn thành: "
                        + player.playerTask.sideTask.count + "/" + player.playerTask.sideTask.maxCount + " ("
                        + percentDone + "%)");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Chúc mừng bạn đã hoàn thành nhiệm vụ, "
                    + "bây giờ hãy quay về Bò Mộng trả nhiệm vụ.");
        }
    }

    

    public void sendAchivement(Player player) {
        List<Achivement> achivements = player.playerTask.achivements;
        Message m = new Message(Cmd.ACHIEVEMENT);
        DataOutputStream ds = m.writer();
        try {
            ds.writeByte(0);
            ds.writeByte(achivements.size());
            for (Achivement a : achivements) {
                String detail = String.format(a.getDetail(), a.getCount(), a.getMaxCount());
                ds.writeUTF(a.getName());
                ds.writeUTF(detail);
                ds.writeShort(a.getMoney());
                ds.writeBoolean(a.isFinish());
                ds.writeBoolean(a.isReceive());
            }
            ds.flush();
            player.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {

        }
    }

    public void rewardAchivement(Player player, byte id) {
        Achivement achivement = player.playerTask.achivements.get(id);
        if (achivement.isFinish()) {
            short idvatpham = ID_THUONG_THANHTUU;
            int soLuong = achivement.getMoney();
            Item qua = ItemService.gI().createNewItem(idvatpham);
            qua.quantity = soLuong;
            InventoryService.gI().addItemBag(player, qua, 999999);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);
            achivement.setReceive(true);
            sendAchivement(player);
            Service.getInstance().sendThongBao(player, "Bạn nhận được " + soLuong + " " + qua.getName());
        }
    }

    public void checkDoneAchivements(Player player) {
        List<Achivement> list = player.playerTask.achivements;
        for (Achivement achivement : list) {
            if (achivement.isDone()) {
                achivement.setFinish(true);
            }
        }
    }
    
    public boolean TaskNext(Player player){
        int idTask = getIdTask(player);
        return idTask == ConstTask.TASK_11_0 || idTask == ConstTask.TASK_11_1 || idTask == ConstTask.TASK_11_2
                || idTask == ConstTask.TASK_13_0 || idTask == ConstTask.TASK_27_0;
    }
}
