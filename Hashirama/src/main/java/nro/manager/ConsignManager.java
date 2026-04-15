package nro.manager;

import nro.jdbc.DBService;
import nro.models.consignment.ConsignmentItem;
import nro.models.consignment.ConsignmentShop;
import nro.models.item.ItemOption;
import nro.services.ItemService;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
public class ConsignManager {

    private static final ConsignManager INSTANCE = new ConsignManager();

    public static ConsignManager getInstance() {
        return INSTANCE;
    }

    public void load() {
        try {
            Connection con = DBService.gI().getConnectionForGame();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM consignment_shop");
            ResultSet rs = ps.executeQuery();
            JSONArray jsonArray = null;
            JSONValue jsonValue = new JSONValue();
            ConsignmentShop consignmentShop = ConsignmentShop.getInstance();
            while (rs.next()) {
                short itemID = rs.getShort("item_id");
                int quantity = rs.getInt("quantity");
                ConsignmentItem item = ItemService.gI().createNewConsignmentItem(itemID, quantity);
                item.setConsignorID(rs.getLong("consignor_id"));
                item.setTab(rs.getByte("tab"));
                item.setPriceGold(rs.getInt("gold"));
                item.setPriceGem(rs.getInt("gem"));
                item.setUpTop(rs.getBoolean("up_top"));
                item.setSold(rs.getBoolean("sold"));
                item.createTime = rs.getLong("time_consign");
                jsonArray = (JSONArray) jsonValue.parse(rs.getString("item_options"));

                for (int j = 0; j < jsonArray.size(); j++) {
                    JSONArray opt = (JSONArray) jsonValue.parse(String.valueOf(jsonArray.get(j)));
                    item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                            Integer.parseInt(String.valueOf(opt.get(1)))));
                }
                int daysExpired = ConsignmentShop.getInstance().getDaysExpried(item.createTime);
                if (daysExpired > 3 && daysExpired < 6) {
                    consignmentShop.addExpiredItem(item);
                }
                if (daysExpired < 6) {
                    consignmentShop.addItem(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try (Connection con = DBService.gI().getConnection();
             PreparedStatement truncateStatement = con.prepareStatement("TRUNCATE consignment_shop");
             PreparedStatement insertStatement = con.prepareStatement("INSERT INTO `consignment_shop`(`id`, `consignor_id`, `tab`, `item_id`, `gold`, `gem`, `quantity`, `item_options`, `up_top`, `sold`,`time_consign`) VALUES (? ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            truncateStatement.executeUpdate();
            int id = 0;
            List<ConsignmentItem> list = ConsignmentShop.getInstance().getList();
            for (ConsignmentItem it : list) {
                if (it != null) {
                    JSONArray options = new JSONArray();
                    for (ItemOption io : it.itemOptions) {
                        JSONArray option = new JSONArray();
                        option.add(io.optionTemplate.id);
                        option.add(io.param);
                        options.add(option);
                    }
                    insertStatement.setInt(1, id++);
                    insertStatement.setLong(2, it.getConsignorID());
                    insertStatement.setInt(3, it.getTab());
                    insertStatement.setShort(4, it.template.id);
                    insertStatement.setInt(5, it.getPriceGold());
                    insertStatement.setInt(6, it.getPriceGem());
                    insertStatement.setInt(7, it.quantity);
                    insertStatement.setString(8, options.toJSONString());
                    insertStatement.setBoolean(9, it.isUpTop());
                    insertStatement.setBoolean(10, it.isSold());
                    insertStatement.setLong(11, it.createTime);
                    insertStatement.addBatch();
                }
            }
            insertStatement.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
