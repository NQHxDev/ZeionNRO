package nro.models.consignment;

import nro.models.item.Item;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
@Setter
@Getter
public class ConsignmentItem extends Item {
    public int consignID;
    public long consignorID;
    public int priceGold;
    public int priceGem;
    public byte tab;
    public boolean sold;
    public boolean upTop;
    private long timeConsign;
}
