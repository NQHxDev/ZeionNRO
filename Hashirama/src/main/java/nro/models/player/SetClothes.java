package nro.models.player;

import nro.models.item.Item;
import nro.models.item.ItemOption;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class SetClothes {

    private Player player;

    public SetClothes(Player player) {
        this.player = player;
    }
    
    public byte NhanHoang;
    public byte MaThan;
    public byte ThienTu;

    public byte songoku;
    public byte thienXinHang;
    public byte kirin;

    public byte ocTieu;
    public byte pikkoroDaimao;
    public byte picolo;

    public byte kakarot;
    public byte cadic;
    public byte nappa;

    public byte tinhan;
    public byte nguyetan;
    public byte nhatan;
    public byte huyetlongan;
    public byte thanhlongan;
    public byte kimlongan;

    public byte setDHD;
    public byte setDTS;
    public byte setDTL;

    public int ctHaiTac = -1;

    public void setup() {
        setDefault();
        setupSKT();
        setupAN();
        setupDTS();
        setupDHD();
        setupDTL();
        setupLongAn();
        Item ct = this.player.inventory.itemsBody.get(5);
        if (ct.isNotNullItem()) {
            switch (ct.template.id) {
                case 618:
                case 619:
                case 620:
                case 621:
                case 622:
                case 623:
                case 624:
                case 626:
                case 627:
                    this.ctHaiTac = ct.template.id;
                    break;
            }
        }
    }

    private void setupSKT() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                for (ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 129:
                        case 141:
                            isActSet = true;
                            songoku++;
                            break;
                        case 127:
                        case 139:
                            isActSet = true;
                            thienXinHang++;
                            break;
                        case 128:
                        case 140:
                            isActSet = true;
                            kirin++;
                            break;
                        case 131:
                        case 143:
                            isActSet = true;
                            ocTieu++;
                            break;
                        case 132:
                        case 144:
                            isActSet = true;
                            pikkoroDaimao++;
                            break;
                        case 130:
                        case 142:
                            isActSet = true;
                            picolo++;
                            break;
                        case 135:
                        case 138:
                            isActSet = true;
                            nappa++;
                            break;
                        case 133:
                        case 136:
                            isActSet = true;
                            kakarot++;
                            break;
                        case 134:
                        case 137:
                            isActSet = true;
                            cadic++;
                            break;
                        case 234:
                        case 237:
                            isActSet = true;
                            NhanHoang++;
                            break;
                        case 235:
                        case 238:
                            isActSet = true;
                            MaThan++;
                            break;
                        case 236:
                        case 239:
                            isActSet = true;
                            ThienTu++;
                            break;
                    }
                    if (isActSet) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void setupAN() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSett = false;
                for (ItemOption chisoan : item.itemOptions) {
                    switch (chisoan.optionTemplate.id) {
                        case 34:
                            isActSett = true;
                            tinhan++;
                            break;
                        case 35:
                            isActSett = true;
                            nguyetan++;
                            break;
                        case 36:
                            isActSett = true;
                            nhatan++;
                            break;
                    }
                    if (isActSett) {
                        break;
                    }

                }
            } else {
                break;
            }
        }
    }
    
    private void setupLongAn() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSettLongAn = false;
                for (ItemOption chisolonghon : item.itemOptions) {
                    switch (chisolonghon.optionTemplate.id) {
                        case 37:
                            isActSettLongAn = true;
                            huyetlongan++;
                            break;
                        case 38:
                            isActSettLongAn = true;
                            thanhlongan++;
                            break;
                        case 39:
                            isActSettLongAn = true;
                            kimlongan++;
                            break;
                    }
                    if (isActSettLongAn) {
                        break;
                    }

                }
            } else {
                break;
            }
        }
    }

    private void setupDTS() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                for (ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 21:
                            if (io.param == 120) {
                                setDTS++;
                            }
                            break;
                    }
                    if (isActSet) {
                        break;
                    }

                }
            } else {
                break;
            }
        }
    }

    private void setupDHD() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                for (ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 21:
                            if (io.param == 80) {
                                setDHD++;
                            }
                            break;
                    }
                    if (isActSet) {
                        break;
                    }

                }
            } else {
                break;
            }
        }
    }

    private void setupDTL() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                    switch (item.template.id) {
                        case 555:
                        case 556:
                        case 557:
                        case 558:
                        case 559:
                        case 560:
                        case 561:
                        case 562:
                        case 563:
                        case 564:
                        case 565:
                        case 566:
                        case 567:
                                setDTL++;
                            break;
                    }
                    if (isActSet) {
                        break;
                    }
            } else {
                break;
            }
        }
    }

    private void setDefault() {
        this.songoku = 0;
        this.thienXinHang = 0;
        this.kirin = 0;
        this.ocTieu = 0;
        this.pikkoroDaimao = 0;
        this.picolo = 0;
        this.kakarot = 0;
        this.cadic = 0;
        this.nappa = 0;
        this.ctHaiTac = -1;

        this.tinhan = 0;
        this.nhatan = 0;
        this.nguyetan = 0;

        this.huyetlongan = 0;
        this.thanhlongan = 0;
        this.kimlongan = 0;
        
        this.setDHD = 0;
        this.setDTS = 0;
        this.setDTL = 0;
        
        this.NhanHoang = 0;
        this.MaThan = 0;
        this.ThienTu = 0;
    }

    public void dispose() {
        this.player = null;
    }
}
