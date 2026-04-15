package nro.models.item;

import nro.consts.ConstItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nro.models.player.Player;
import nro.services.InventoryService;
import nro.services.ItemService;

public class Item {

    public ItemTemplate template;

    public String info;

    public String content;

    public int quantity;

    public List<ItemOption> itemOptions;

    public long createTime;

    public boolean isNotNullItem() {
        return this.template != null;
    }

    public Item(short itemId) {
        this.template = ItemService.gI().getTemplate(itemId);
        this.itemOptions = new ArrayList<>();
        this.createTime = System.currentTimeMillis();
    }

    public Item() {
        this.itemOptions = new ArrayList<>();
        this.createTime = System.currentTimeMillis();
    }

    public String getInfo() {
        String strInfo = "";
        for (ItemOption itemOption : itemOptions) {
            strInfo += itemOption.getOptionString() + "\n";
        }
        return strInfo;
    }

    public String getInfoItem() {
        String strInfo = "|1|" + template.name + "\n|0|";
        for (ItemOption itemOption : itemOptions) {
            strInfo += itemOption.getOptionString() + "\n";
        }
        strInfo += "|2|" + template.description;
        return strInfo;
    }

    public List<ItemOption> getDisplayOptions() {
        List<ItemOption> list = new ArrayList<>();
        if (itemOptions.isEmpty()) {
            list.add(new ItemOption(73, 0));
        } else {
            for (ItemOption o : itemOptions) {
                list.add(o.format());
            }
        }
        return list;
    }

    public String getContent() {
        return "Yêu cầu sức mạnh " + this.template.strRequire + " trở lên";
    }

    public boolean canConsign() {
        byte type = template.type;
        for (ItemOption o : itemOptions) {
            int optionId = o.optionTemplate.id;
            if (template.id != ConstItem.THOI_VANG && (optionId != 30) && (type == 12 || type == 33 || type == 29 || type == 27 || type == 99 || type == 11 || type == 5 || (type < 4 && (optionId == 86 || optionId == 87)))) {
                return true;
            }
        }
        return false;
    }

    public boolean isSKH() {
        for (ItemOption itemOption : itemOptions) {
            if (itemOption.optionTemplate.id >= 127 && itemOption.optionTemplate.id <= 135 || this.template.id == 1553 && !this.isTrangBiHSD() || this.template.id == 1772) {
                return true;
            }
        }
        return false;
    }

    public boolean isKimDan() {
        if (this.template.id >= 1798 && this.template.id <= 1801) {
            return true;
        }
        return false;
    }
    
    public boolean isTrangBiNangCap() {
        if (this.template.id >= 1798 && this.template.id <= 1801) {
            return true;
        }
        return false;
    }
    
    public boolean isKhoa() {
        for (ItemOption itemOption : itemOptions) {
            if (itemOption.optionTemplate.id == 30) {
                return true;
            }
        }
        return false;
    }


    public boolean isDaCuongHoa() {
        for (ItemOption itemOption : itemOptions) {
            if (this.template.id == 1791 || this.template.id == 1563 || this.template.id == 1561
                    || this.template.id == 1793 || this.template.id == 1564 || this.template.id == 1562
                    || this.template.id == 1794 || this.template.id == 1565 || this.template.id == 1797
                    || this.template.id == 1795 || this.template.id == 1559 || this.template.id == 1560
                    || this.template.id == 1796 || this.template.id == 1792 || this.template.id >= 1419 && this.template.id <= 1423) {
                return true;
            }
        }
        return false;
    }

    public boolean isDa() {
        if (this.template.id >= 1048 && this.template.id <= 1062) {
            return true;
        }
        return false;
    }

    public boolean isDTS() {
        if (this.template.id >= 1048 && this.template.id <= 1062) {
            return true;
        }
        return false;
    }

    public boolean isDTL() {
        if (this.template.id >= 555 && this.template.id <= 567) {
            return true;
        }
        return false;
    } 
    public boolean isTrangBiThuHoi() {
        if (this.template.id >= 555 && this.template.id <= 567 //đồ thần
                || this.template.id >= 1401 && this.template.id <= 1405 // đồ thánh tôn
                || this.template.id >= 650 && this.template.id <= 662 // đồ huỷ diệt
                || this.template.id >= 1048 && this.template.id <= 1062
                ) {
            return true;
        }
        return false;
    }

    public boolean isThanhTon() {
        if (this.template.id >= 1401 && this.template.id <= 1405) {
            return true;
        }
        return false;
    }
    public boolean isThanLongThach() {
        if (this.template.id == 1749) {
            return true;
        }
        return false;
    }

    public boolean isCongThuc() {
        if (this.template.id >= 1071 && this.template.id <= 1073) {
            return true;
        }
        return false;
    }

    public boolean isSachCoNgu() {
        if (this.template.id >= 1697 && this.template.id <= 1698) {
            return true;
        }
        return false;
    }

    public boolean isDHD() {
        if (this.template.id >= 650 && this.template.id <= 662) {
            return true;
        }
        return false;
    }

    public boolean isManhTS() {
        if (this.template.id >= 1066 && this.template.id <= 1070) {
            return true;
        }
        return false;
    }

    public boolean isNguyenTo() {
        if (this.template.id == 1688) {
            return true;
        }
        return false;
    }

    public boolean haveOption(int idOption) {
        if (this != null && this.isNotNullItem()) {
            return this.itemOptions.stream().anyMatch(op -> op != null && op.optionTemplate.id == idOption);
        }
        return false;
    }

    public int getCapDo() {
        if (this != null && this.isNotNullItem()) {
            return this.itemOptions.stream()
                    .filter(op -> op != null && op.optionTemplate.id == 72)
                    .map(op -> op.param)
                    .findFirst()
                    .orElse(0);
        }
        return 0;
    }

    public boolean isTrangBiHSD() {
        return InventoryService.gI().hasOptionTemplateId(this, 93) || InventoryService.gI().hasOptionTemplateId(this, 199);
    }

    public String typeName() {
        switch (this.template.type) {
            case 0:
                return "Áo";
            case 1:
                return "Quần";
            case 2:
                return "Găng";
            case 3:
                return "Giày";
            case 4:
                return "Rada";
            default:
                return "";
        }
    }

    public String typeOption() {
        switch (this.template.type) {
            case 0:
                return "Giáp";
            case 1:
                return "Hp";
            case 2:
                return "Tấn công";
            case 3:
                return "KI";
            case 4:
                return "Chí mạng";
            default:
                return "";
        }
    }

    public byte typeIdManh() {
        if (!isManhTS()) {
            return -1;
        }
        switch (this.template.id) {
            case 1066:
                return 0;
            case 1067:
                return 1;
            case 1070:
                return 2;
            case 1068:
                return 3;
            case 1069:
                return 4;
            default:
                return -1;
        }
    }

    public String typeNameManh() {
        switch (this.template.id) {
            case 1066:
                return "Áo";
            case 1067:
                return "Quần";
            case 1070:
                return "Găng";
            case 1068:
                return "Giày";
            case 1069:
                return "Nhẫn";
            default:
                return "";
        }
    }

    public void dispose() {
        this.template = null;
        this.info = null;
        this.content = null;
        if (this.itemOptions != null) {
            for (ItemOption io : this.itemOptions) {
                io.dispose();
            }
            this.itemOptions.clear();
        }
        this.itemOptions = null;
    }

    public short getId() {
        return template.id;
    }

    public byte getType() {
        return template.type;
    }

    public String getName() {
        return template.name;
    }

}
