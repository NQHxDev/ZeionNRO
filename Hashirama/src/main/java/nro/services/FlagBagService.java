package nro.services;

import nro.models.item.FlagBag;
import nro.models.player.Player;
import nro.server.Manager;
import nro.server.io.Message;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class FlagBagService {

    private List<FlagBag> flagClan = new ArrayList<>();
    private static FlagBagService i;

    public static FlagBagService gI() {
        if (i == null) {
            i = new FlagBagService();
        }
        return i;
    }

    public void sendIconFlagChoose(Player player, int id) {
        FlagBag fb = getFlagBag(id);
        if (fb != null) {
            Message msg;
            try {
                msg = new Message(-62);
                msg.writer().writeByte(fb.id);
                msg.writer().writeByte(1);
                msg.writer().writeShort(fb.iconId);

                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    public void sendIconEffectFlag(Player player, int id) {
        FlagBag fb = getFlagBag(id);
        if (fb != null) {
            Message msg;
            try {
                msg = new Message(-63);
                msg.writer().writeByte(fb.id);
                msg.writer().writeByte(fb.iconEffect.length);
                for (Short iconId : fb.iconEffect) {
                    msg.writer().writeShort(iconId);
                }
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    public void sendListFlagClan(Player pl) {
        List<FlagBag> list = getFlagsForChooseClan();
        Message msg;
        try {
            msg = new Message(-46);
            msg.writer().writeByte(1); //type
            msg.writer().writeByte(list.size());
            for (FlagBag fb : list) {
                msg.writer().writeByte(fb.id);
                msg.writer().writeUTF(fb.name);
                msg.writer().writeInt(fb.gold);
                msg.writer().writeInt(fb.gem);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public FlagBag getFlagBag(int id) {
        for (FlagBag fb : Manager.FLAGS_BAGS) {
            if (fb.id == id) {
                return fb;
            }
        }
        return null;
    }

    public FlagBag getFlagBagByName(String name) {
        for (FlagBag fb : Manager.FLAGS_BAGS) {
            if (fb.name.equals(name)) {
                return fb;
            }
        }
        return null;
    }

    public List<FlagBag> getFlagsForChooseClan() {
        if (flagClan.isEmpty()) {
            int[] flagsId = {                
//              0, 8, 7, 6, 5, 4, 3, 2, 1, 18, 17, 16, 15, 14, 13,
//              12, 11, 10, 9, 27, 26, 25, 24, 23, 36, 32, 33, 34, 35, 19, 22, 21, 20, 29,                 
//                38,40,41,42,43,72,50,51,//free
//                65,66,67,68,69,62,63,64,//5ktv
//                89,90,91,92,93,94,95,96,97,98,99,//10ktv
                71,70,66,69,67,11,5,6,7//25ktv
//                108,104,105,107,106,74,//50ktv 
//                0,1,2,3,4,5,//50ktv 
//                6,7,8,9,10,11,//50ktv 
//                18,19,20,21,22,23,//50ktv 
//                24,25,26,27,32,33,//50ktv 
//                12,13,14,15,16,17,125,96,114,100//50ktv 
//              ,56,57,58,59,60,61,62,63,64,65,66,67,68
            };
            for (int i = 0; i < flagsId.length; i++) {
                flagClan.add(getFlagBag(flagsId[i]));
            }
        }
        return flagClan;
    }
}
