package nro.data;

import java.util.Arrays;
import java.util.List;
import nro.models.item.ItemOptionTemplate;
import nro.models.item.ItemTemplate;
import nro.server.Manager;
import nro.server.io.Message;
import nro.server.io.Session;

public class ItemData {

    public static List<Integer> list_thuc_an = Arrays.asList(663, 664, 665, 666, 667);

    public static List<Integer> list_dapdo = Arrays.asList(1107, 1140, 1196, 1197, 1198, 1221,
             1222, 1223, 1133, 1180, 1181, 1229, 1230, 1326, 1462, 1415, 1524, 1502);

    public static List<Integer> phieu = Arrays.asList(459);

    public static List<Integer> IdMiniPet = Arrays.asList(
            942, 943, 944, 1196, 1197, 1198, 1221, 1222, 1223, 1229, 1230,
            1356, 1415, 1462, 1497, 1498, 1524, 1602, 1644, 1645, 1646, 1647,
            1648, 1683, 1684, 1685, 1750, 1751, 1752, 1753, 1754, 1755, 1781,
            1782, 1783, 1784, 2017, 2018, 2027, 2028, 2029, 2030, 2034, 2035, 2036
            ,999,1000,1001,1002,1003,1004,1005,1006,1863,1864,1865,1866,1867,1868
    );
    
//    public static List<Integer> OptionSPL = Arrays.asList(102, 50, 77, 103, 80, 81, 94, 108, 95, 96, 97, 98, 99, 100, 101, 49, 210, 211, 212, 213, 214, 215, 217, 218, 219, 220, 221, 222, 223);
    public static List<Integer> OptionSPL = Arrays.asList(102, 49, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223);

    //------------------------------------------------------ start update client
    public static void updateItem(Session session) {
        updateItemOptionItemplate(session);
        updateTocBay(session);
//        int count = 500;
//        updateItemTemplate(session, count);
//        updateItemTemplate(session, count, Manager.ITEM_TEMPLATES.size());

        updateItemTemplate(session, 930);
        updateItemTemplate(session, 930, Manager.ITEM_TEMPLATES.size());
//        updateItemTemplate(session, 1500, Manager.ITEM_TEMPLATES.size());
    }
    private static final short[][] head3Htinh = {
        {1723, 1724, 1725}, {1728, 1728, 1729}, {1571, 1572, 1573},{1576, 1577, 1578},{1584, 1585, 1586},{1560, 1561, 1562},{1597, 1598, 1599},{1605, 1606, 1607},//Trái đất, {1737, 1737, 1738}1571
        {1732, 1732, 1733}, {1750, 1751, 1752},{2042, 2043, 2044},{2053, 2054, 2055},//Namec {1738, 1738, 1739},
        {1710, 1711, 1712}, {1715, 1716, 1717}, {1730, 1730, 1731}, {1758, 1759, 1760}//Xayda, {1736, 1736, 1737}
    };
    
    private static void updateTocBay(Session session) {
        Message msg;
        try {
            msg = new Message(-28);
            msg.writer().writeByte(8);
            msg.writer().writeByte(DataGame.vsItem);//vcitem
            msg.writer().writeByte(100); //type NroItem100
            msg.writer().writeShort(head3Htinh.length);//tổng list lặp head
            for (int i = 0; i < head3Htinh.length; i++) {
                msg.writer().writeByte(head3Htinh[i].length);//chiều dài 1 chuỗi
                for (int j = 0; j < head3Htinh[i].length; j++) {
                    msg.writer().writeShort(head3Htinh[i][j]);
                }
            }
            session.doSendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private static void updateItemOptionItemplate(Session session) {
        Message msg;
        try {
            msg = new Message(-28);
            msg.writer().writeByte(8);
            msg.writer().writeByte(DataGame.vsItem); //vcitem
            msg.writer().writeByte(0); //update option
            msg.writer().writeByte(Manager.ITEM_OPTION_TEMPLATES.size());
            for (ItemOptionTemplate io : Manager.ITEM_OPTION_TEMPLATES) {
                msg.writer().writeUTF(io.name);
                msg.writer().writeByte(io.type);
            }
            session.doSendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    private static void updateItemTemplate(Session session, int count) {
        Message msg;
        try {
            msg = new Message(-28);
            msg.writer().writeByte(8);

            msg.writer().writeByte(DataGame.vsItem); //vcitem
            msg.writer().writeByte(1); //reload itemtemplate
            msg.writer().writeShort(count);
            for (int i = 0; i < count; i++) {
                ItemTemplate itemTemplate = Manager.ITEM_TEMPLATES.get(i);
                msg.writer().writeByte(itemTemplate.type);
                msg.writer().writeByte(itemTemplate.gender);
                msg.writer().writeUTF(itemTemplate.name);
                msg.writer().writeUTF(itemTemplate.description);
                msg.writer().writeByte(itemTemplate.level);
                msg.writer().writeInt(itemTemplate.strRequire);
                msg.writer().writeShort(itemTemplate.iconID);
                msg.writer().writeShort(itemTemplate.part);
                msg.writer().writeBoolean(itemTemplate.isUpToUp);
            }
            session.doSendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateItemTemplate(Session session, int start, int end) {
        Message msg;
        try {
            msg = new Message(-28);
            msg.writer().writeByte(8);

            msg.writer().writeByte(DataGame.vsItem); //vcitem
            msg.writer().writeByte(2); //add itemtemplate
            msg.writer().writeShort(start);
            msg.writer().writeShort(end);
            for (int i = start; i < end; i++) {
//                System.out.println("start: " + start + " -> " + end + " id " + Manager.ITEM_TEMPLATES.get(i).id);
                msg.writer().writeByte(Manager.ITEM_TEMPLATES.get(i).type);
                msg.writer().writeByte(Manager.ITEM_TEMPLATES.get(i).gender);
                msg.writer().writeUTF(Manager.ITEM_TEMPLATES.get(i).name);
                msg.writer().writeUTF(Manager.ITEM_TEMPLATES.get(i).description);
                msg.writer().writeByte(Manager.ITEM_TEMPLATES.get(i).level);
                msg.writer().writeInt(Manager.ITEM_TEMPLATES.get(i).strRequire);
                msg.writer().writeShort(Manager.ITEM_TEMPLATES.get(i).iconID);
                msg.writer().writeShort(Manager.ITEM_TEMPLATES.get(i).part);
                msg.writer().writeBoolean(Manager.ITEM_TEMPLATES.get(i).isUpToUp);
            }
            session.doSendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //-------------------------------------------------------- end update client
}
