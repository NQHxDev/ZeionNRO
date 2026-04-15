package nro.models.player;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstNpc;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class IDMark {

    private int typeChangeMap; //capsule, ngọc rồng đen...
    private int indexMenu; //menu npc
    private int typeInput; //input
    private int shopId; //shop ope
    private byte typeLuckyRound; //type lucky round
    private byte isTranhNgoc = -1;
    @Getter
    @Setter
    private short idItemUpToTop;
    public boolean isUseTuiBaoVeNangCap;

    public byte getTranhNgoc() {
        return isTranhNgoc;
    }

    public void setTranhNgoc(byte tn) {
        this.isTranhNgoc = tn;
    }

    public void setTypeLuckyRound(byte type) {
        this.typeLuckyRound = type;
    }

    public byte getTypeLuckyRound() {
        return this.typeLuckyRound;
    }

    public int getIndexMenu() {
        return indexMenu;
    }

    public void setIndexMenu(int indexMenu) {
        this.indexMenu = indexMenu;
    }

    public boolean isBaseMenu() {
        return this.indexMenu == ConstNpc.BASE_MENU;
    }

    public void setTypeInput(int typeInput) {
        this.typeInput = typeInput;
    }

    public int getTypeInput() {
        return this.typeInput;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getShopId() {
        return this.shopId;
    }

    public int getTypeChangeMap() {
        return typeChangeMap;
    }

    public void setTypeChangeMap(int typeChangeMap) {
        this.typeChangeMap = typeChangeMap;
    }
}
