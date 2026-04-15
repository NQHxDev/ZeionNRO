package nro.models.shop;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class Shop {

    public int id;

    public byte npcId;

    public byte shopOrder;

    public List<TabShop> tabShops;

    public Shop() {
        this.tabShops = new ArrayList<>();
    }

    public Shop(Shop shop, int gender) {
        this.tabShops = new ArrayList<>();
        this.id = shop.id;
        this.npcId = shop.npcId;
        this.shopOrder = shop.shopOrder;
        for (TabShop tabShop : shop.tabShops) {
            this.tabShops.add(new TabShop(tabShop, gender));
        }
    }

    public Shop(Shop shop) {
        this.id = shop.id;
        this.npcId = shop.npcId;
        this.shopOrder = shop.shopOrder;
        this.tabShops = new ArrayList<>();
        for (TabShop tabShop : shop.tabShops) {
            this.tabShops.add(new TabShop(tabShop));
        }
    }

}
