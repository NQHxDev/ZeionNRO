package nro.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.player.Player;
import java.util.ArrayList;
import java.util.List;
import nro.jdbc.DBService;
import nro.server.Manager;
import nro.server.io.Message;
import nro.utils.Log;

/**
 *
 * @author Hoàng Việt - 0857853150
 *
 */
public class BangTin {

    private int id;
    private String tieude;
    private String info;
    private static final int START = 1;
    public static final List<BangTin> BANGTIN_MANAGER = new ArrayList<>();

    private static BangTin i;

    public static BangTin gI() {
        if (i == null) {
            i = new BangTin();
        }
        return i;
    }

    public void Send_BangTin(Player pl) {
        Message msg = null;
        try {
            msg = new Message(104);
            msg.writer().writeByte(START);
            msg.writer().writeByte(BANGTIN_MANAGER.size());

            for (int j = 0; j < BANGTIN_MANAGER.size(); j++) {
                BangTin manager = BANGTIN_MANAGER.get(j);
                msg.writer().writeInt(manager.id);
                msg.writer().writeUTF(manager.tieude);
                msg.writer().writeUTF(manager.info);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void load_BangTin() {
        try {
            Connection con = DBService.gI().getConnectionForGame();
            PreparedStatement ps = con.prepareStatement("select * from bang_tin");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BangTin bangtin_manager = new BangTin();
                bangtin_manager.id = rs.getInt("id");
                bangtin_manager.tieude = rs.getString("tieu_de");
                bangtin_manager.info = rs.getString("info");
                BANGTIN_MANAGER.add(bangtin_manager);
            }
            Log.success("Load bảng tin thành công (" + BANGTIN_MANAGER.size() + ")");
        } catch (SQLException e) {
            Log.error(BangTin.class, e, "Lỗi load database");
            System.exit(0);
        }
    }

}
