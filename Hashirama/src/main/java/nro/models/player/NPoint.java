package nro.models.player;

import java.math.BigDecimal;
import java.math.BigInteger;
import nro.attr.Attribute;
import nro.card.Card;
import nro.card.CollectionBook;
import nro.consts.ConstAttribute;
import nro.consts.ConstPlayer;
import nro.consts.ConstRatio;
import nro.models.clan.Buff;
import nro.models.intrinsic.Intrinsic;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.skill.Skill;
import nro.power.PowerLimit;
import nro.power.PowerLimitManager;
import nro.server.Manager;
import nro.server.ServerManager;
import nro.services.*;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;

import java.util.ArrayList;
import java.util.List;
import static nro.services.KhamNgoc.KHAM_NGOC;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 */
public class NPoint {

    public static final byte MAX_LIMIT = 11;

    private Player player;
    public boolean isCrit;
    public boolean isCrit100;

    private Intrinsic intrinsic;
    private int percentDameIntrinsic;
    public int dameAfter;

    /*-----------------------Chỉ số cơ bản------------------------------------*/
    public byte numAttack;
    public short stamina, maxStamina;

    public byte limitPower;
    public double power;
    public double tiemNang;
    public double mpg, hpg;
    public double dameg;

    public double hp, hpMax;
    public double mp, mpMax;
    public double dame;
    public double def;
    public double defg;
    public int crit, critg;
    public byte speed = 5;

    public boolean teleport;

    public boolean khangTDHS;
    
    public boolean useDoubleHp = false;  // chỉ boss bật = true
    public double  hpgD = 0d;            // HP gốc dạng double cho boss

    /**
     * Chỉ số cộng thêm
     */
    public int hpAdd, mpAdd, dameAdd, defAdd, critAdd, hpHoiAdd, mpHoiAdd;

    /**
     * //+#% sức đánh chí mạng
     */
    public List<Integer> tlDameCrit;

    public boolean buffExpSatellite, buffDefenseSatellite;

    /**
     * Tỉ lệ hp, mp cộng thêm
     */
    public List<Integer> tlHp, tlMp;

    /**
     * Điểm tích lũy
     */
    public List<Integer> TichLuy;

    /**
     * Tỉ lệ giáp cộng thêm
     */
    public List<Integer> tlDef;

    /**
     * Tỉ lệ sức đánh/ sức đánh khi đánh quái
     */
    public List<Integer> tlDame, tlDameAttMob;

    /**
     * Lượng hp, mp hồi mỗi 30s, mp hồi cho người khác
     */
    public double hpHoi, mpHoi, mpHoiCute;

    /**
     * Tỉ lệ hp, mp hồi cộng thêm
     */
    public short tlHpHoi, tlMpHoi;

    /**
     * Tỉ lệ hp, mp hồi bản thân và đồng đội cộng thêm
     */
    public short tlHpHoiBanThanVaDongDoi, tlMpHoiBanThanVaDongDoi;

    /**
     * Tỉ lệ hút hp, mp khi đánh, hp khi đánh quái
     */
    public short tlHutHp, tlHutMp, tlHutHpMob;

    /**
     * Tỉ lệ hút hp, mp xung quanh mỗi 5s
     */
    public short tlHutHpMpXQ;

    /**
     * Tỉ lệ phản sát thương
     */
    public short tlPST;

    /**
     * Tỉ lệ tiềm năng sức mạnh
     */
    public List<Integer> tlTNSM;
    public int tlTNSMPet;

    /**
     * Tỉ lệ vàng cộng thêm
     */
    public short tlGold;

    /**
     * Tỉ lệ né đòn
     */
    public short tlNeDon,tlchinhxac;

    /**
     * Tỉ lệ sức đánh đẹp cộng thêm cho bản thân và người xung quanh
     */
    public List<Integer> tlSDDep;

    /**
     * Tỉ lệ sức đánh khi gan thanh vien bang
     */
    public List<Integer> tlSDBang;

    /**
     * Tỉ lệ giảm sức đánh
     */
    public short tlSubSD;
    public List<Integer> tlSpeed;
    public int mstChuong;
    public int tlGiamst;

    /*------------------------Effect skin-------------------------------------*/
    public Item trainArmor;
    public boolean wornTrainArmor;
    public boolean wearingTrainArmor;

    public boolean wearingVoHinh;
    public boolean isKhongLanh;

    public short tlHpGiamODo;

    private PowerLimit powerLimit;
    public boolean wearingDrabula;
    public boolean wearingMabu;
    public boolean wearingBuiBui;

    public boolean wearingNezuko;
    public boolean wearingTanjiro;
    public boolean wearingInosuke;
    public boolean wearingInoHashi;
    public boolean wearingZenitsu;
    public int tlDameChuong;
    public boolean xDameChuong;
    public boolean wearingYacon;
    public boolean wearingRedNoelHat;
    public boolean wearingGrayNoelHat;
    public boolean wearingBlueNoelHat;
    public boolean wearingNoelHat;

    public NPoint(Player player) {
        this.player = player;
        this.tlHp = new ArrayList<>();
        this.tlMp = new ArrayList<>();
        this.tlDef = new ArrayList<>();
        this.tlDame = new ArrayList<>();
        this.tlDameAttMob = new ArrayList<>();
        this.tlSDDep = new ArrayList<>();
        this.tlTNSM = new ArrayList<>();
        this.tlDameCrit = new ArrayList<>();
        this.tlSpeed = new ArrayList<>();
        this.tlSDBang = new ArrayList<>();
    }

    public void initPowerLimit() {
        powerLimit = PowerLimitManager.getInstance().get(limitPower);
    }

    /*-------------------------------------------------------------------------*/
    /**
     * Tính toán mọi chỉ số sau khi có thay đổi
     */
    public void calPoint() {
        try {
            if (this.player.pet != null) {
                this.player.pet.nPoint.setPointWhenWearClothes();
            }
            this.setPointWhenWearClothes();
            
            this.applyBossDoubleHpIfNeeded();
        } catch (Exception e) {
        }
    }

    public void setPoint(ItemOption io) {
        switch (io.optionTemplate.id) {
            case 0, 224 -> //Tấn công +#
                this.dameAdd += io.param;
            case 2 -> {
                //HP, KI+#000
                this.hpAdd += io.param * 1000;
                this.mpAdd += io.param * 1000;
            }
            case 3 -> // vô hiệu vả biến st chưởng thành ki
                this.mstChuong += io.param;
            case 5 -> //+#% sức đánh chí mạng
                this.tlDameCrit.add(io.param);
            case 6, 225 -> //HP+#
                this.hpAdd += io.param;
            case 7, 226 -> //KI+#
                this.mpAdd += io.param;
            case 8 -> //Hút #% HP, KI xung quanh mỗi 5 giây
                this.tlHutHpMpXQ += io.param;
            case 14, 216 -> //Chí mạng+#%
                this.critAdd += io.param;
            case 19 -> //Tấn công+#% khi đánh quái
                this.tlDameAttMob.add(io.param);
            case 22 -> //HP+#K
                this.hpAdd += io.param * 1000;
            case 23 -> //MP+#K
                this.mpAdd += io.param * 1000;
            case 24 ->
                this.wearingBuiBui = true;
            case 25 ->
                this.wearingYacon = true;
            case 26 -> {
                this.wearingDrabula = true;
                this.player.effectSkin.lastTimeDrabula = System.currentTimeMillis();
            }
            case 29 ->
                this.wearingMabu = true;
            case 27 -> //+# HP/30s
                this.hpHoiAdd += io.param;
            case 28 -> //+# KI/30s
                this.mpHoiAdd += io.param;
            case 33 -> //dịch chuyển tức thời
                this.teleport = true;
            case 47 -> //Giáp+#
                this.defAdd += io.param;
            case 48 -> {
                //HP/KI+#
                this.hpAdd += io.param;
                this.mpAdd += io.param;
            }
            case 49, 50 -> //Tấn công+#%
                //Sức đánh+#%
                this.tlDame.add(io.param);
            case 77, 210 -> //HP+#%
                this.tlHp.add(io.param);
            case 80, 212 -> //HP+#%/30s
                this.tlHpHoi += io.param;
            case 81, 213 -> //MP+#%/30s
                this.tlMpHoi += io.param;
            case 88 -> //Cộng #% exp khi đánh quái
                this.tlTNSM.add(io.param);
            case 94, 214 -> //Giáp #%
                this.tlDef.add(io.param);
            case 95, 217 -> //Biến #% tấn công thành HP
                this.tlHutHp += io.param;
            case 96, 218 -> //Biến #% tấn công thành MP
                this.tlHutMp += io.param;
            case 97, 219 -> //Phản #% sát thương
                this.tlPST += io.param;
            case 100, 222 -> //+#% vàng từ quái
                this.tlGold += io.param;
            case 101, 223 -> //+#% TN,SM
                this.tlTNSM.add(io.param);
            case 103, 211 -> //KI +#%
                this.tlMp.add(io.param);
            case 104 -> //Biến #% tấn công quái thành HP
                this.tlHutHpMob += io.param;
            case 105 -> //Vô hình khi không đánh quái và boss
                this.wearingVoHinh = true;
            case 106 -> //Không ảnh hưởng bởi cái lạnh
                this.isKhongLanh = true;
            case 108, 215 -> //#% Né đòn
                this.tlNeDon += io.param;
            case 109 -> //Hôi, giảm #% HP
                this.tlHpGiamODo += io.param;
            case 114 ->
                this.tlSpeed.add(io.param);
            case 116 -> //Kháng thái dương hạ san
                this.khangTDHS = true;
            case 117 -> //Đẹp +#% SĐ cho mình và người xung quanh
                this.tlSDDep.add(io.param);
            case 209 -> //sdanh khi gan thanh vien bang
                this.tlSDBang.add(io.param);
            case 147 -> //+#% sức đánh
                this.tlDame.add(io.param);
            case 156 -> {
                //Giảm 50% sức đánh, HP, KI và +#% SM, TN, vàng từ quái
                this.tlSubSD += 50;
                this.tlTNSM.add(io.param);
                this.tlGold += io.param;
            }
            case 160 ->
                this.tlTNSMPet += io.param;
            case 162 -> //Cute hồi #% KI/s bản thân và xung quanh
                this.mpHoiCute += io.param;
            case 173 -> {
                //Phục hồi #% HP và KI cho đồng đội
                this.tlHpHoiBanThanVaDongDoi += io.param;
                this.tlMpHoiBanThanVaDongDoi += io.param;
            }
            case 189 ->
                this.wearingNezuko = true;
            case 190 ->
                this.wearingTanjiro = true;
            case 191 ->
                this.wearingInoHashi = true;
            case 192 ->
                this.wearingInosuke = true;
            case 193 ->
                this.wearingZenitsu = true;
            case 194 ->
                this.tlDameChuong = 3;
            case 195 ->
                this.tlDameChuong = 4;
            case 229 -> //HP pháp sư +#%
                this.tlHp.add(io.param);
            case 230 -> //KI pháp sư +#%
                this.tlMp.add(io.param);
            case 231 -> //Sức đánh pháp sư +#%
                this.tlDame.add(io.param);
            case 232 -> //Sức đánh chí mạng pháp sư +#%
                this.tlDameCrit.add(io.param);
            case 240 -> //HP pháp sư +#%
                this.TichLuy.add(io.param);
        }
        //Tấn công+#%
    }

    private void setPointWhenWearClothes() {
        resetPoint();
        for (Item item : this.player.inventory.itemsBody) {
            if (item.isNotNullItem()) {
                int tempID = item.template.id;
                if (tempID >= 592 && tempID <= 594) {
                    teleport = true;
                }
                for (ItemOption io : item.itemOptions) {
                    setPoint(io);
                }
            }
        }
        List<Item> itemsBody = player.inventory.itemsBody;
        if (!player.isBoss && !player.isMiniPet) {
            if (player.inventory.itemsBody.get(1).isNotNullItem()) {
                Item pants = itemsBody.get(1);
                if (pants.isNotNullItem() && pants.getId() >= 691 && pants.getId() >= 693) {
                    player.event.setUseQuanHoa(true);
                }
            }
        }
        if (Manager.EVENT_SEVER == 3) {
            if (!this.player.isBoss && !this.player.isMiniPet) {
                if (itemsBody.get(5).isNotNullItem()) {
                    int tempID = itemsBody.get(5).getId();
                    switch (tempID) {
                        case 386, 389, 392 -> {
                            wearingGrayNoelHat = true;
                            wearingNoelHat = true;
                        }
                        case 387, 390, 393 -> {
                            wearingRedNoelHat = true;
                            wearingNoelHat = true;
                        }
                        case 388, 391, 394 -> {
                            wearingBlueNoelHat = true;
                            wearingNoelHat = true;
                        }
                        default -> {
                            wearingRedNoelHat = false;
                            wearingBlueNoelHat = false;
                            wearingGrayNoelHat = false;
                            wearingNoelHat = false;
                        }
                    }
                }
            }
        }
        CollectionBook book = player.getCollectionBook();
        if (book != null) {
            List<Card> cards = book.getCards();
            if (cards != null) {
                for (Card c : cards) {
                    if (c.getLevel() > 0) {
                        int index = 0;
                        for (ItemOption o : c.getCardTemplate().getOptions()) {
                            if ((index == 0 || c.isUse()) && c.getLevel() >= o.activeCard) {
                                setPoint(o);
                            }
                            index++;
                        }
                    }
                }
            }
        }
        setChisoPorata();
        setDameTrainArmor();
        setPointKhamNgoc();
        setPointRuongSuuTam();
        setBasePoint();
    }

    private void setChisoPorata() {
        if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 921) {
                    for (ItemOption io : item.itemOptions) {
                        switch (io.optionTemplate.id) {
                            case 14 -> //Chí mạng+#%
                                this.critAdd += io.param;
                            case 50 -> //Sức đánh+#%
                                this.tlDame.add(io.param);
                            case 209 -> //Sức đánh+#%
                                this.tlDame.add(io.param);
                            case 77 -> //HP+#%
                                this.tlHp.add(io.param);
                            case 80 -> //HP+#%/30s
                                this.tlHpHoi += io.param;
                            case 81 -> //MP+#%/30s
                                this.tlMpHoi += io.param;
                            case 94 -> //Giáp #%
                                this.tlDef.add(io.param);
                            case 103 -> //KI +#%
                                this.tlMp.add(io.param);
//                            case 108 -> //#% Né đòn
//                                this.tlNeDon += io.param;
                        }
                    }
                    break;
                }
            }
        }
        if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1165) {
                    for (ItemOption io : item.itemOptions) {
                        switch (io.optionTemplate.id) {
                            case 14 -> //Chí mạng+#%
                                this.critAdd += io.param;
                            case 50 -> //Sức đánh+#%
                                this.tlDame.add(io.param);
                            case 77 -> //HP+#%
                                this.tlHp.add(io.param);
                            case 80 -> //HP+#%/30s
                                this.tlHpHoi += io.param;
                            case 81 -> //MP+#%/30s
                                this.tlMpHoi += io.param;
                            case 94 -> //Giáp #%
                                this.tlDef.add(io.param);
                            case 103 -> //KI +#%
                                this.tlMp.add(io.param);
//                            case 108 -> //#% Né đòn
//                                this.tlNeDon += io.param;
                        }
                    }
                    break;
                }
            }
        }
        if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1129) {
                    for (ItemOption io : item.itemOptions) {
                        switch (io.optionTemplate.id) {
                            case 14 -> //Chí mạng+#%
                                this.critAdd += io.param;
                            case 50 -> //Sức đánh+#%
                                this.tlDame.add(io.param);
                            case 77 -> //HP+#%
                                this.tlHp.add(io.param);
                            case 80 -> //HP+#%/30s
                                this.tlHpHoi += io.param;
                            case 81 -> //MP+#%/30s
                                this.tlMpHoi += io.param;
                            case 94 -> //Giáp #%
                                this.tlDef.add(io.param);
                            case 103 -> //KI +#%
                                this.tlMp.add(io.param);
//                            case 108 -> //#% Né đòn
//                                this.tlNeDon += io.param;
                        }
                    }
                    break;
                }
            }
        }
        if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 2099) {
                    for (ItemOption io : item.itemOptions) {
                        switch (io.optionTemplate.id) {
                            case 14 -> //Chí mạng+#%
                                this.critAdd += io.param;
                            case 50 -> //Sức đánh+#%
                                this.tlDame.add(io.param);
                            case 77 -> //HP+#%
                                this.tlHp.add(io.param);
                            case 80 -> //HP+#%/30s
                                this.tlHpHoi += io.param;
                            case 81 -> //MP+#%/30s
                                this.tlMpHoi += io.param;
                            case 94 -> //Giáp #%
                                this.tlDef.add(io.param);
                            case 103 -> //KI +#%
                                this.tlMp.add(io.param);
//                            case 108 -> //#% Né đòn
//                                this.tlNeDon += io.param;
                        }
                    }
                    break;
                }
            }
        }
        if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA6) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 2098) {
                    for (ItemOption io : item.itemOptions) {
                        switch (io.optionTemplate.id) {
                            case 14 -> //Chí mạng+#%
                                this.critAdd += io.param;
                            case 50 -> //Sức đánh+#%
                                this.tlDame.add(io.param);
                            case 77 -> //HP+#%
                                this.tlHp.add(io.param);
                            case 80 -> //HP+#%/30s
                                this.tlHpHoi += io.param;
                            case 81 -> //MP+#%/30s
                                this.tlMpHoi += io.param;
                            case 94 -> //Giáp #%
                                this.tlDef.add(io.param);
                            case 103 -> //KI +#%
                                this.tlMp.add(io.param);
                            case 108 -> //#% Né đòn
                                this.tlNeDon += io.param;
                        }
                    }
                    break;
                }
            }
        }
    }

    private void setDameTrainArmor() {
        if (!this.player.isPet && !this.player.isBoss && !this.player.isMiniPet) {
            try {
                Item gtl = this.player.inventory.itemsBody.get(6);
                if (gtl.isNotNullItem()) {
                    this.wearingTrainArmor = true;
                    this.wornTrainArmor = true;
                    this.player.inventory.trainArmor = gtl;
                    this.tlSubSD += ItemService.gI().getPercentTrainArmor(gtl);
                } else {
                    if (this.wornTrainArmor) {
                        this.wearingTrainArmor = false;
                        for (ItemOption io : this.player.inventory.trainArmor.itemOptions) {
                            if (io.optionTemplate.id == 9 && io.param > 0) {
                                this.tlDame.add(ItemService.gI().getPercentTrainArmor(this.player.inventory.trainArmor));
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.error("Lỗi get giáp tập luyện " + this.player.name);
            }
        }
    }

    private void setNeDon() {
        //ngọc rồng đen 6 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[5] > System.currentTimeMillis()) {
            this.tlNeDon += RewardBlackBall.R6S;
        }
    }

    private void setHpHoi() {
        this.hpHoi = calPercent(this.hpMax, 5);
        this.hpHoi += this.hpHoiAdd;
        this.hpHoi += calPercent(this.hpMax, this.tlHpHoi);
        this.hpHoi += calPercent(this.hpMax, this.tlHpHoiBanThanVaDongDoi);
        if (this.player.effectSkin.isNezuko) {
            this.hpHoi += calPercent(this.hpMax, 3);
        }
    }

    private void setMpHoi() {
        this.mpHoi = calPercent(this.mpMax, 5);
        this.mpHoi += this.mpHoiAdd;
        this.mpHoi += calPercent(this.mpMax, this.tlMpHoi);
        this.mpHoi += calPercent(this.mpMax, this.tlMpHoiBanThanVaDongDoi);
        if (this.player.effectSkin.isNezuko) {
            this.mpHoi += calPercent(this.mpMax, 3);
        }
    }

    private void setHpMax() {
        this.hpMax = this.hpg;
        this.hpMax += this.hpAdd;
        //đồ
        for (Integer tl : this.tlHp) {
            this.hpMax += calPercent(this.hpMax, tl);
        }
        //set tinh ấn
        if (this.player.setClothes.tinhan == 5) {
            this.hpMax += calPercent(this.hpMax, 40);
        }
        //set huyết long
        if (this.player.setClothes.huyetlongan == 5) {
            this.hpMax += calPercent(this.hpMax, 100);
        }
        //set nappa
        if (this.player.setClothes.nappa == 5) {
            this.hpMax += calPercent(this.hpMax, 100);
        }
        //thiên tử
        if (this.player.setClothes.ThienTu == 5) {
            this.hpMax *= 2;
        }
        //ngọc rồng đen 2 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[1] > System.currentTimeMillis()) {
            this.hpMax += calPercent(this.hpMax, RewardBlackBall.R2S);
        }
        //khỉ
        if (this.player.effectSkill.isMonkey) {
            if (!this.player.isPet || (this.player.isPet
                    && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = SkillUtil.getPercentHpMonkey(player.effectSkill.levelMonkey);
                this.hpMax += calPercent(this.hpMax, percent);
            }
        }

        //chỉ số pet khi hợp thể
        if (this.player.isPet && (((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA6)) {
            switch (((Pet) this.player).typePet) {
                case 12 -> //mabu
                    this.hpMax += calPercent(this.hpMax, 50);
                case 2 -> //berus
                    this.hpMax += calPercent(this.hpMax, 100);
                case 3 -> //zeno
                    this.hpMax += calPercent(this.hpMax, 200);
                case 4 -> //kaido
                    this.hpMax += calPercent(this.hpMax, 1500);
                case 5 -> //itachi
                    this.hpMax += calPercent(this.hpMax, 1000);
                case 6 -> //tienhacam
                    this.hpMax += calPercent(this.hpMax, 2000);
                case 7 -> //ngộ khỉ
                    this.hpMax += calPercent(this.hpMax, 500);
                case 8 -> //Sói 3 đầu
                    this.hpMax += calPercent(this.hpMax, 5000);
                case 9 -> //Đại thánh
                    this.hpMax += calPercent(this.hpMax, 7500);
                case 10 -> //sơn tinh
                    this.hpMax += calPercent(this.hpMax, 6000);
                case 11 -> //thuỷ tinh
                    this.hpMax += calPercent(this.hpMax, 6000);
                default -> {
                }
            }
        }
        if (this.player.isPet && this.player.PorataVIP == true && (((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA6)) {
            this.hpMax += calPercent(this.hpMax, 50);
        }
        //phù
        if (this.player.zone != null && MapService.gI().isMapBlackBallWar(this.player.zone.map.mapId)) {
            this.hpMax *= this.player.effectSkin.xHPKI;
        }
        //phù mabu 14h
        if (this.player.zone != null && MapService.gI().isMapMabuWar14H(this.player.zone.map.mapId)) {
            this.hpMax += 1000000;
        }
        //+hp đệ
        if (this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            this.hpMax += this.player.pet.nPoint.hpMax;
        }
        //huýt sáo
        if (!this.player.isPet
                || (this.player.isPet
                && ((Pet) this.player).status != Pet.FUSION)) {
            if (this.player.effectSkill.tiLeHPHuytSao != 0) {
                this.hpMax += calPercent(this.hpMax, this.player.effectSkill.tiLeHPHuytSao);
            }
        }
        //bổ huyết
        if (this.player.itemTime != null && this.player.itemTime.isUseBoHuyet) {
            this.hpMax *= 2;
        }
        //bổ huyết 2
        if (this.player.itemTime != null && this.player.itemTime.isUseBoHuyet2) {
            this.hpMax += calPercent(hpMax, 120);
        }
        if (this.player.zone != null && MapService.gI().isMapCold(this.player.zone.map)
                && !this.isKhongLanh) {
            this.hpMax /= 2;
        }
        if (!player.isBoss) {
            Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.HP);
            if (at != null && !at.isExpired()) {
                hpMax += calPercent(hpMax, at.getValue());
            }
        }
        if (this.player.itemTime != null) {
            if (this.player.itemTime.isUseBanhTet) {
                hpMax += calPercent(hpMax, 20);
            }
        }
        if (player.getBuff() == Buff.BUFF_HP) {
            hpMax += calPercent(hpMax, 20);
        }
        //đuôi khỉ
        if (!this.player.isPet && this.player.itemTimesieucap.isDuoikhi
                || this.player.isPet && ((Pet) this.player).master.itemTimesieucap.isDuoikhi) {
            this.hpMax += calPercent(hpMax, 5);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isDaNgucTu) {
            this.hpMax += calPercent(hpMax, 10);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isChoido) {
            this.hpMax += calPercent(hpMax, 20);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseXiMuoi) {
            this.hpMax += calPercent(hpMax, 5);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4042) {
            this.hpMax += calPercent(hpMax, 10);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4043) {
            this.hpMax += calPercent(hpMax, 20);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4125) {
            this.hpMax += calPercent(hpMax, 30);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4126) {
            this.hpMax += calPercent(hpMax, 40);
        }

        int cs = this.player.chuyensinh;

        if (cs > 0) {
            double baseHp = 0;
            int bonusPercent = 0;

            if (cs < 50) {
                baseHp = safeMultiply(5_000_000, cs);
            } else if (cs < 100) {
                baseHp = safeMultiply(10_000_000, cs);
                bonusPercent = 10;
            } else if (cs < 150) {
                baseHp = safeMultiply(40_000_000, cs);
                bonusPercent = 20;
            } else if (cs < 200) {
                baseHp = safeMultiply(80_000_000, cs);
                bonusPercent = 30;
            } else if (cs < 300) {
                baseHp = safeMultiply(120_000_000, cs);
                bonusPercent = 40;
            } else if (cs < 400) {
                baseHp = safeMultiply(160_000_000, cs);
                bonusPercent = 50;
            } else if (cs < 500) {
                baseHp = safeMultiply(200_000_000, cs);
                bonusPercent = 75;
            } else {
                baseHp = safeMultiply(300_000_000, cs);
                bonusPercent = 100;
            }

            // Cộng hp cơ bản
            this.hpMax += baseHp;

            // Cộng phần trăm tăng thêm (dựa trên hpMax hiện tại đã + baseHp)
            if (bonusPercent > 0) {
                this.hpMax += calPercent(this.hpMax, bonusPercent);
            }
        }
        //rồng băng
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isRongBang) {
            this.hpMax += calPercent(hpMax, 15);
        }
        //rồng siêu cấp
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isRongSieuCap) {
            this.hpMax += calPercent(hpMax, 50);
        }
        //Biến hình SC 
        if (this.player.effectSkill.isBienHinhSc) {
            if (!this.player.isPet || (this.player.isPet && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = SkillUtil.getPercentHPBienHinhSc(player.effectSkill.levelBienHinhSc);
                this.hpMax += calPercent(this.hpMax, percent);
            }
        }
        if (this.player.isPl() && this.player.isTitleUse1 == true && this.player.lastTimeTitle1 > 0) {
            this.hpMax += calPercent(this.hpMax, 50);
        }
        if (this.player.isPl() && this.player.isTitleUse2 == true && this.player.lastTimeTitle2 > 0) {
            this.hpMax += calPercent(this.hpMax, 100);
        }
        if (this.player.isPl() && this.player.isTitleUse3 == true && this.player.lastTimeTitle3 > 0) {
            this.hpMax += calPercent(this.hpMax, 200);
        }
    }

    // (hp sư phụ + hp đệ tử ) + 15%
    // (hp sư phụ + 15% +hp đệ tử)
    private void setHp() {
        if (this.hp > this.hpMax) {
            this.hp = this.hpMax;
        }
    }

    private void setMpMax() {
        this.mpMax = this.mpg;
        this.mpMax += this.mpAdd;
        //đồ
        for (Integer tl : this.tlMp) {
            this.mpMax += calPercent(this.mpMax, tl);
        }
        if (this.player.setClothes.picolo == 5) {
            this.mpMax *= 3;
        }
        //set nhật ấn
        if (this.player.setClothes.nhatan == 5) {
            this.mpMax += calPercent(this.mpMax, 40);
        }
        //set thanh long
        if (this.player.setClothes.thanhlongan == 5) {
            this.mpMax += calPercent(this.mpMax, 100);
        }
        if (this.player.setClothes.MaThan == 5) {
            this.mpMax *= 4;
        }
        //ngọc rồng đen 3 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[2] > System.currentTimeMillis()) {
            this.mpMax += calPercent(this.mpMax, RewardBlackBall.R3S);
        }
        //chỉ số pet khi hợp thể
        if (this.player.isPet && (((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA6)) {
            switch (((Pet) this.player).typePet) {
                case 12 -> //mabu
                    this.mpMax += calPercent(this.mpMax, 50);
                case 2 -> //berus
                    this.mpMax += calPercent(this.mpMax, 100);
                case 3 -> //zeno
                    this.mpMax += calPercent(this.mpMax, 200);
                case 4 -> //kaido
                    this.mpMax += calPercent(this.mpMax, 1500);
                case 5 -> //itachi
                    this.mpMax += calPercent(this.mpMax, 1000);
                case 6 -> //tienhacam
                    this.mpMax += calPercent(this.mpMax, 2000);
                case 7 -> //ngộ khỉ
                    this.mpMax += calPercent(this.mpMax, 500);
                case 8 -> //Sói 3 đầu
                    this.mpMax += calPercent(this.mpMax, 5000);
                case 9 -> //Đại thánh
                    this.mpMax += calPercent(this.mpMax, 7500);
                case 10 -> //sơn tinh
                    this.mpMax += calPercent(this.mpMax, 6000);
                case 11 -> //thuỷ tinh
                    this.mpMax += calPercent(this.mpMax, 6000);
                default -> {
                }
            }
        }
        if (this.player.isPet && this.player.PorataVIP == true && (((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA6)) {
            this.mpMax += calPercent(this.mpMax, 50);
        }
        //hợp thể
        if (this.player.fusion.typeFusion != 0) {
            this.mpMax += this.player.pet.nPoint.mpMax;
        }
        //bổ khí
        if (this.player.itemTime != null && this.player.itemTime.isUseBoKhi) {
            this.mpMax *= 2;
        }
        //bổ khí 2
        if (this.player.itemTime != null && this.player.itemTime.isUseBoKhi2) {
            this.mpMax += calPercent(mpMax, 120);
        }
        //phù
        if (this.player.zone != null && MapService.gI().isMapBlackBallWar(this.player.zone.map.mapId)) {
            this.mpMax *= this.player.effectSkin.xHPKI;
        }
        //phù mabu 14h
        if (this.player.zone != null && MapService.gI().isMapMabuWar14H(this.player.zone.map.mapId)) {
            this.mpMax += 1000000;
        }
//        //xiên cá
//        if (this.player.effectFlagBag.useXienCa) {
//            this.mpMax += calPercent(this.mpMax, 15);
//        }
//        //Kiem z
//        if (this.player.effectFlagBag.useKiemz) {
//            this.mpMax += calPercent(this.mpMax, 20);
//        }
//        if (this.player.effectFlagBag.useDieuRong) {
//            this.mpMax += calPercent(this.mpMax, 30);
//        }
//        if (this.player.effectFlagBag.useHoaVang || this.player.effectFlagBag.useHoaHong) {
//            this.mpMax += calPercent(this.mpMax, 20);
//        }
        if (!player.isBoss) {
            Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.KI);
            if (at != null && !at.isExpired()) {
                mpMax += calPercent(mpMax, at.getValue());
            }
        }
        if (this.player.itemTime != null) {
            if (this.player.itemTime.isUseBanhTet) {
                mpMax += calPercent(mpMax, 20);
            }
        }
        if (player.getBuff() == Buff.BUFF_KI) {
            mpMax += calPercent(mpMax, 20);
        }
        //đuôi khỉ
        if (!this.player.isPet && this.player.itemTimesieucap.isDuoikhi
                || this.player.isPet && ((Pet) this.player).master.itemTimesieucap.isDuoikhi) {
            this.mpMax += calPercent(mpMax, 5);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isDaNgucTu) {
            this.mpMax += calPercent(mpMax, 10);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isChoido) {
            this.mpMax += calPercent(mpMax, 20);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseXiMuoi) {
            this.mpMax += calPercent(mpMax, 5);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4042) {
            this.mpMax += calPercent(mpMax, 10);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4043) {
            this.mpMax += calPercent(mpMax, 20);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4125) {
            this.mpMax += calPercent(mpMax, 30);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4126) {
            this.mpMax += calPercent(mpMax, 40);
        }
        
        int cs = this.player.chuyensinh;

        if (cs > 0) {
            double baseMp = 0;
            int bonusPercent = 0;

            if (cs < 50) {
                baseMp = safeMultiply(5_000_000, cs);
            } else if (cs < 100) {
                baseMp = safeMultiply(10_000_000, cs);
                bonusPercent = 10;
            } else if (cs < 150) {
                baseMp = safeMultiply(40_000_000, cs);
                bonusPercent = 20;
            } else if (cs < 200) {
                baseMp = safeMultiply(80_000_000, cs);
                bonusPercent = 30;
            } else if (cs < 300) {
                baseMp = safeMultiply(120_000_000, cs);
                bonusPercent = 40;
            } else if (cs < 400) {
                baseMp = safeMultiply(160_000_000, cs);
                bonusPercent = 50;
            } else if (cs < 500) {
                baseMp = safeMultiply(200_000_000, cs);
                bonusPercent = 75;
            } else {
                baseMp = safeMultiply(300_000_000, cs);
                bonusPercent = 100;
            }

            this.mpMax += baseMp;

            if (bonusPercent > 0) {
                this.mpMax += calPercent(this.mpMax, bonusPercent);
            }
        }
        //rồng băng
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isRongBang) {
            this.mpMax += calPercent(mpMax, 15);
        }
        //rồng siêu cấp
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isRongSieuCap) {
            this.mpMax += calPercent(mpMax, 50);
        }
        //Biến hình SC 
        if (this.player.effectSkill.isBienHinhSc) {
            if (!this.player.isPet || (this.player.isPet && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = SkillUtil.getPercentMPBienHinhSc(player.effectSkill.levelBienHinhSc);
                this.mpMax += calPercent(this.mpMax, percent);
            }
        }

        if (this.player.isPl() && this.player.isTitleUse1 == true && this.player.lastTimeTitle1 > 0) {
            this.mpMax += calPercent(this.mpMax, 50);
        }
        if (this.player.isPl() && this.player.isTitleUse2 == true && this.player.lastTimeTitle2 > 0) {
            this.mpMax += calPercent(this.mpMax, 100);
        }
        if (this.player.isPl() && this.player.isTitleUse3 == true && this.player.lastTimeTitle3 > 0) {
            this.mpMax += calPercent(this.mpMax, 200);
        }
    }

    private void setMp() {
        if (this.mp > this.mpMax) {
            this.mp = this.mpMax;
        }
    }

    private void setDame() {
        this.dame = this.dameg;
        this.dame += this.dameAdd;
        //đồ
        for (Integer tl : this.tlDame) {
            this.dame += calPercent(this.dame, tl);
        }
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
            if (list.size() >= 2) {
                for (Integer tl : this.tlSDBang) {
                    this.dame += calPercent(this.dame * (list.size() - 1), tl);
                }
            }
        }
        for (Integer tl : this.tlSDDep) {
            this.dame += calPercent(this.dame, tl);
        }
        //set nguyệt ấn
        if (this.player.setClothes.nguyetan == 5) {
            this.dame += calPercent(this.dame, 30);
        }
        //set kim long
        if (this.player.setClothes.kimlongan == 5) {
            this.dame += calPercent(this.dame, 80);
        }
        //chỉ số pet khi hợp thể
        if (this.player.isPet && (((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA6)) {
            switch (((Pet) this.player).typePet) {
                case 12 -> //mabu
                    this.dame += calPercent(this.dame, 50);
                case 2 -> //berus
                    this.dame += calPercent(this.dame, 100);
                case 3 -> //zeno
                    this.dame += calPercent(this.dame, 200);
                case 4 -> //kaido
                    this.dame += calPercent(this.dame, 1500);
                case 5 -> //itachi
                    this.dame += calPercent(this.dame, 1000);
                case 6 -> //tienhacam
                    this.dame += calPercent(this.dame, 2000);
                case 7 -> //ngộ khỉ
                    this.dame += calPercent(this.dame, 500);
                case 8 -> //sói 3 đầu
                    this.dame += calPercent(this.dame, 5000);
                case 9 -> //đại thánh
                    this.dame += calPercent(this.dame, 7500);
                case 10 -> //sơn tinh
                    this.dame += calPercent(this.dame, 6000);
                case 11 -> //thuỷ tinh
                    this.dame += calPercent(this.dame, 6000);
                default -> {
                }
            }
        }
        if (this.player.isPet && this.player.PorataVIP == true && (((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5
                || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA6)) {
            this.dame += calPercent(this.dame, 50);
        }
        //thức ăn
        if (!this.player.isPet && this.player.itemTime.isEatMeal
                || this.player.isPet && ((Pet) this.player).master.itemTime.isEatMeal) {
            this.dame += calPercent(this.dame, 10);
        }
        //hợp thể
        if (this.player.fusion.typeFusion != 0) {
            this.dame += this.player.pet.nPoint.dame;
        }
        //cuồng nộ
        if (this.player.itemTime != null && this.player.itemTime.isUseCuongNo) {
            this.dame *= 2;
        }
        //cuồng nộ 2
        if (this.player.itemTime != null && this.player.itemTime.isUseCuongNo2) {
            this.dame += calPercent(dame, 120);
        }
        //phù mabu 14h
        if (this.player.zone != null && MapService.gI().isMapMabuWar14H(this.player.zone.map.mapId)) {
            this.dame += 10000;
        }
        //giảm dame
        this.dame -= calPercent(this.dame, tlSubSD);
        //map cold
        if (this.player.zone != null && MapService.gI().isMapCold(this.player.zone.map)
                && !this.isKhongLanh) {
            this.dame /= 2;
        }
        //ngọc rồng đen 1 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[0] > System.currentTimeMillis()) {
            this.dame += calPercent(this.dame, RewardBlackBall.R1S);//(30%)
        }
        //set nhan hoang
        if (this.player.setClothes.NhanHoang == 5) {
            this.dame += calPercent(this.dame, 30);
            this.tlDameCrit.add(150);
        }
        if (!player.isBoss) {
            Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.SUC_DANH);
            if (at != null && !at.isExpired()) {
                this.dame += calPercent(dame, at.getValue());
            }
        }
        if (this.player.itemTime != null) {
            if (this.player.itemTime.isUseBanhChung) {
                dame += calPercent(dame, 20);
            }
        }
        if (player.getBuff() == Buff.BUFF_ATK) {
            dame += calPercent(dame, 1);
        }
        if (this.player.effectSkin.isNezuko) {
            this.dame += calPercent(this.dame, 15);
        }
        //khỉ
        if (this.player.effectSkill.isMonkey) {
            if (!this.player.isPet || (this.player.isPet
                    && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = SkillUtil.getPercentDameMonkey(player.effectSkill.levelMonkey);
                this.dame += calPercent(dame, percent);
            }
        }

        //thức ăn
        if (!this.player.isPet && this.player.itemTime.isEatMeal
                || this.player.isPet && ((Pet) this.player).master.itemTime.isEatMeal) {
            this.dame += calPercent(dame, 10);
        }

        //đuôi khỉ
        if (!this.player.isPet && this.player.itemTimesieucap.isDuoikhi
                || this.player.isPet && ((Pet) this.player).master.itemTimesieucap.isDuoikhi) {
            this.dame += calPercent(dame, 5);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isDaNgucTu) {
            this.dame += calPercent(dame, 10);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isChoido) {
            this.dame += calPercent(dame, 20);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseXiMuoi) {
            this.dame += calPercent(dame, 5);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4042) {
            this.dame += calPercent(dame, 10);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4043) {
            this.dame += calPercent(dame, 20);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4125) {
            this.dame += calPercent(dame, 30);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4126) {
            this.dame += calPercent(dame, 40);
        }
        int cs = this.player.chuyensinh;

        if (cs > 0) {
            double baseDame = 0;
            int bonusPercent = 0;

            if (cs < 50) {
                baseDame = safeMultiply(2_500_000, cs);
            } else if (cs < 100) {
                baseDame = safeMultiply(5_000_000, cs);
                bonusPercent = 10;
            } else if (cs < 150) {
                baseDame = safeMultiply(10_000_000, cs);
                bonusPercent = 20;
            } else if (cs < 200) {
                baseDame = safeMultiply(20_000_000, cs);
                bonusPercent = 30;
            } else if (cs < 300) {
                baseDame = safeMultiply(30_000_000, cs);
                bonusPercent = 40;
            } else if (cs < 400) {
                baseDame = safeMultiply(40_000_000, cs);
                bonusPercent = 50;
            } else if (cs < 500) {
                baseDame = safeMultiply(50_000_000, cs);
                bonusPercent = 75;
            } else {
                baseDame = safeMultiply(75_000_000, cs);
                bonusPercent = 100;
            }

            this.dame += baseDame;

            if (bonusPercent > 0) {
                this.dame += calPercent(this.dame, bonusPercent);
            }
        }


        //rồng băng
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isRongBang) {
            this.dame += calPercent(dame, 15);
        }
        //rồng siêu cấp
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isRongSieuCap) {
            this.dame += calPercent(dame, 50);
        }
        //Biến hình SC 
        if (this.player.effectSkill.isBienHinhSc) {
            if (!this.player.isPet || (this.player.isPet && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = SkillUtil.getPercentDameBienHinhSc(player.effectSkill.levelBienHinhSc);
                this.dame += calPercent(this.dame, percent);
            }
        }

        if (this.player.isPl() && this.player.isTitleUse3 == true && this.player.lastTimeTitle3 > 0) {
            this.dame += calPercent(this.dame, 200);
        }
        if (this.player.isPl() && this.player.isTitleUse2 == true && this.player.lastTimeTitle2 > 0) {
            this.dame += calPercent(this.dame, 50);
        }
        if (this.player.isPl() && this.player.isTitleUse1 == true && this.player.lastTimeTitle1 > 0) {
            this.dame += calPercent(this.dame, 50);
        }
    }

    private void setDef() {
        this.def = this.defg * 4;
        this.def += this.defAdd;
        //đồ
        for (Integer tl : this.tlDef) {
            this.tlGiamst += tl;
        }
        if (tlGiamst > 60) {
            tlGiamst = 60;
        }
        //ngọc rồng đen 5 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[4] > System.currentTimeMillis()) {
            this.def += calPercent(this.def, RewardBlackBall.R5S);
        }
        if (this.player.effectSkin.isInosuke) {
            this.def += calPercent(this.def, 50);
        }
        if (this.player.effectSkin.isInoHashi) {
            this.def += calPercent(this.def, 60);
        }
    }

    private void setCrit() {
        this.crit = this.critg;
        this.crit += this.critAdd;
        //ngọc rồng đen 4 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[3] > System.currentTimeMillis()) {
            this.crit += RewardBlackBall.R4S;
        }
        //biến khỉ
        if (this.player.effectSkill.isMonkey) {
            this.crit = 110;
        }
        //nhân hoàng
        if (this.player.setClothes.NhanHoang == 5) {
            this.crit += 20;
        }
        if (player.getBuff() == Buff.BUFF_CRIT) {
            crit += 10;
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isChoido) {
            this.crit += 50;
        }
        //nếu crit > 100 thì phần dư sẽ được cộng vào tlDameCrit
        if (this.crit > 100) {
            int overCrit = this.crit - 100;
            this.tlDameCrit.add(overCrit); // cộng phần dư vào tỉ lệ dame chí mạng
            this.crit = 100; // giữ giới hạn crit không vượt quá 100
        }
    }

    private void setCritDame() {
        if (this.player.effectSkin.isTanjiro) {
            this.tlDameCrit.add(30);
        }
        if (this.player.itemTime != null) {
            if (this.player.itemTime.isUseBanhChung) {
                this.tlDameCrit.add(15);
            }
        }  
        if (this.player.isPl() && this.player.isTitleUse3 == true && this.player.lastTimeTitle3 > 0) {
            this.dame += calPercent(this.dame, 100);
        }
        if (this.player.isPl() && this.player.isTitleUse2 == true && this.player.lastTimeTitle2 > 0) {
            this.dame += calPercent(this.dame, 50);
        }
        if (this.player.isPl() && this.player.isTitleUse1 == true && this.player.lastTimeTitle1 > 0) {
            this.dame += calPercent(this.dame, 25);
        }
    }

    private void setSpeed() {
        for (Integer tl : this.tlSpeed) {
            this.speed += calPercent(this.speed, tl);
        }
        if (this.player.effectSkin.isSlow) {
            this.speed = 1;
        }
    }

    private void resetPoint() {
        this.hpAdd = 0;
        this.mpAdd = 0;
        this.dameAdd = 0;
        this.defAdd = 0;
        this.critAdd = 0;
        this.tlHp.clear();
        this.tlMp.clear();
        this.tlDef.clear();
        this.tlDame.clear();
        this.tlDameAttMob.clear();
        this.tlDameCrit.clear();
        this.tlHpHoiBanThanVaDongDoi = 0;
        this.tlMpHoiBanThanVaDongDoi = 0;
        this.hpHoi = 0;
        this.mpHoi = 0;
        this.mpHoiCute = 0;
        this.tlHpHoi = 0;
        this.tlMpHoi = 0;
        this.tlHutHp = 0;
        this.tlHutMp = 0;
        this.tlHutHpMob = 0;
        this.tlHutHpMpXQ = 0;
        this.tlPST = 0;
        this.tlTNSM.clear();
        this.tlDameAttMob.clear();
        this.tlDameCrit.clear();
        this.tlGold = 0;
        this.tlNeDon = 0;
        this.tlchinhxac = 0;
        this.tlSDDep.clear();
        this.tlSubSD = 0;
        this.tlHpGiamODo = 0;
        this.teleport = false;
        this.khangTDHS = false;
        this.tlSpeed.clear();
        this.speed = 5;
        this.mstChuong = 0;
        this.tlGiamst = 0;
        this.tlTNSMPet = 0;
        this.tlSDBang.clear();

        this.wearingVoHinh = false;
        this.isKhongLanh = false;
        this.wearingDrabula = false;
        this.wearingNezuko = false;
        this.wearingZenitsu = false;
        this.wearingInosuke = false;
        this.wearingInoHashi = false;
        this.wearingTanjiro = false;
        this.wearingMabu = false;
        this.wearingBuiBui = false;
        this.xDameChuong = false;
        this.wearingYacon = false;
    }

    public void addHp(double hp) {
        this.hp += hp;
        if (this.hp > this.hpMax) {
            this.hp = this.hpMax;
        }
    }

    public void addMp(double mp) {
        this.mp += mp;
        if (this.mp > this.mpMax) {
            this.mp = this.mpMax;
        }
    }

    public void setHp(double hp) {
        if (hp > this.hpMax) {
            this.hp = this.hpMax;
        } else {
            this.hp = hp;
        }
    }

    public void setMp(double mp) {
        if (mp > this.mpMax) {
            this.mp = this.mpMax;
        } else {
            this.mp = mp;
        }
    }

    private void setIsCrit() {
        if (intrinsic != null && intrinsic.id == 25
                && this.getCurrPercentHP() <= intrinsic.param1) {
            isCrit = true;
        } else if (isCrit100) {
            isCrit100 = false;
            isCrit = true;
        } else {
            isCrit = Util.isTrue(this.crit, ConstRatio.PER100);
        }
    }

    public double getDameAttack(boolean isAttackMob) {
        setIsCrit();
        double dameAttack = this.dame;
        intrinsic = this.player.playerIntrinsic.intrinsic;
        percentDameIntrinsic = 0;
        double percentDameSkill = 0;
        double percentXDame = 0;
        Skill skillSelect = player.playerSkill.skillSelect;
        switch (skillSelect.template.id) {
            case Skill.DRAGON -> {
                if (intrinsic.id == 1) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
            }
            case Skill.KAMEJOKO -> {
                if (intrinsic.id == 2) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.songoku == 5) {
                    percentXDame = 100;
                }
//                if (this.player.effectSkin.xDameChuong) {
//                    percentXDame += tlDameChuong;
//                    this.player.effectSkin.xDameChuong = false;
//                }
            }
            case Skill.GALICK -> {
                if (intrinsic.id == 16) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.kakarot == 5) {
                    percentXDame = 100;
                }
                if (this.player.setClothes.ThienTu == 5) {
                    percentXDame = 150;
                }
            }
            case Skill.ANTOMIC -> {
                if (intrinsic.id == 17) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
//                if (this.player.effectSkin.xDameChuong) {
//                    percentXDame += tlDameChuong;
//                    this.player.effectSkin.xDameChuong = false;
//                }
            }
            case Skill.DEMON -> {
                if (intrinsic.id == 8) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
            }
            case Skill.MASENKO -> {
                if (intrinsic.id == 9) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
//                if (this.player.effectSkin.xDameChuong) {
//                    percentXDame += tlDameChuong * 100;
//                    this.player.effectSkin.xDameChuong = false;
//                }
            }
            case Skill.KAIOKEN -> {
                if (intrinsic.id == 26) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.thienXinHang == 5) {
                    percentXDame = 150;
                }
            }
            case Skill.LIEN_HOAN -> {
                if (intrinsic.id == 13) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.ocTieu == 5) {
                    percentXDame = 100;
                }
                if (this.player.setClothes.MaThan == 5) {
                    percentXDame = 150;
                }
            }
            case Skill.DICH_CHUYEN_TUC_THOI -> {
                dameAttack *= 2;
                dameAttack = Util.nextdameDouble((dameAttack - calPercent(dameAttack, 5)),
                        (dameAttack + calPercent(dameAttack, 5)));
                return dameAttack;
            }
            case Skill.MAKANKOSAPPO -> {
                percentDameSkill = skillSelect.damage;
                double dameSkill = calPercent(this.mpMax, percentDameSkill);
                return dameSkill;
            }
            case Skill.QUA_CAU_KENH_KHI -> {
                double totalHP = 0;
                if (player.zone != null) {
                    totalHP = player.zone.getTotalHP();
                }
                double damage = ((totalHP / 10.0) + (this.dame * 10.0));
                if (this.player.setClothes.kirin == 5) {
                    damage *= 3;
                }
                return damage;
            }
        }
        if (intrinsic.id == 18 && this.player.effectSkill.isMonkey) {
            percentDameIntrinsic = intrinsic.param1;
        }
        if (percentDameSkill != 0) {
            dameAttack = calPercent(dameAttack, percentDameSkill);
        }
        dameAttack += calPercent(dameAttack, percentDameIntrinsic);
        dameAttack += calPercent(dameAttack, dameAfter);

        if (isAttackMob) {
            for (Integer tl : this.tlDameAttMob) {
                dameAttack += calPercent(dameAttack, tl);
            }
        }
        dameAfter = 0;
        if (this.player.isPet && ((Pet) this.player).master.charms.tdDeTu > System.currentTimeMillis()) {
            dameAttack *= 2;
        }
        if (isCrit) {
            dameAttack *= 2;
            for (Integer tl : this.tlDameCrit) {
                dameAttack += calPercent(dameAttack, tl);
            }
        }
        dameAttack += calPercent( dameAttack, percentXDame);
//        System.out.println(dameAttack);
        dameAttack = Util.nextdameDouble((dameAttack - calPercent(dameAttack, 5)), (dameAttack + calPercent(dameAttack, 5)));

        if (player.isPl()) {
            if (player.inventory.haveOption(player.inventory.itemsBody, 5, 159)) {
                if (Util.canDoWithTime(player.lastTimeUseOption, 60000) && (player.playerSkill.skillSelect.template.id == Skill.KAMEJOKO || player.playerSkill.skillSelect.template.id == Skill.ANTOMIC || player.playerSkill.skillSelect.template.id == Skill.MASENKO)) {
                    dameAttack *= player.inventory.getParam(player.inventory.itemsBody.get(5), 159);
                    player.lastTimeUseOption = System.currentTimeMillis();
                    Service.getInstance().sendThongBao(player, "|1|Bạn vừa gây ra x" + player.inventory.getParam(player.inventory.itemsBody.get(5), 159) + " Sát thương Chưởng");
                }
            }
        }

        //check activation set
        return dameAttack;
    }

    public double getDameAttackSkillNotFocus() {
        setIsCrit();
        double dameAttack = this.dame;
        intrinsic = this.player.playerIntrinsic.intrinsic;
        percentDameIntrinsic = 0;
        int percentDameSkill = 0;
        int percentXDame = 0;
        Skill skillSelect = player.playerSkill.skillSelect;
        switch (skillSelect.template.id) {

        }
        if (intrinsic.id == 18 && this.player.effectSkill.isMonkey) {
            percentDameIntrinsic = intrinsic.param1;
        }
        if (percentDameSkill != 0) {
            dameAttack = calPercent(dameAttack, percentDameSkill);
        }
        dameAttack += calPercent(dameAttack, percentDameIntrinsic);
        dameAttack += calPercent(dameAttack, dameAfter);
        dameAfter = 0;
        if (this.player.isPet && ((Pet) this.player).master.charms.tdDeTu > System.currentTimeMillis()) {
            dameAttack *= 2.0;
        }
        if (isCrit) {
            dameAttack *= 2.0;
            for (Integer tl : this.tlDameCrit) {
                dameAttack += calPercent(dameAttack, tl);
            }
        }
        dameAttack += calPercent(dameAttack, percentXDame);
        dameAttack = Util.nextdameDouble((dameAttack - calPercent(dameAttack, 5)), (dameAttack + calPercent(dameAttack, 5)));
        return dameAttack;
    }

    public double getCurrPercentHP() {
        if (this.hpMax == 0) {
            return 100;
        }
        return this.hp * 100 / this.hpMax;
    }

    public double getCurrPercentMP() {
        return this.mp * 100 / this.mpMax;
    }

    public void setFullHpMp() {
        this.hp = this.hpMax;
        this.mp = this.mpMax;
    }

    public void subHP(double sub) {
        this.hp -= sub;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }

    public void subMP(double sub) {
        this.mp -= sub;
        if (this.mp < 0) {
            this.mp = 0;
        }
    }

    public double calSucManhTiemNang(double tiemNang) {
        if (power < getPowerLimit()) {
            for (Integer tl : this.tlTNSM) {
                tiemNang += calPercent(tiemNang, tl);
            }
            if (this.player.cFlag != 0) {
                if (this.player.cFlag == 8) {
                    tiemNang += calPercent(tiemNang, 10);
                } else {
                    tiemNang += calPercent(tiemNang, 5);
                }
            }
            if (buffExpSatellite) {
                tiemNang += calPercent(tiemNang, 20);
            }
            if (player.isPet) {
                Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.TNSM);
                if (at != null && !at.isExpired()) {
                    tiemNang += calPercent(tiemNang, at.getValue());
                }
            }
            if (this.player.isPet) {
                int tltnsm = ((Pet) this.player).master.nPoint.tlTNSMPet;
                if (tltnsm > 0) {
                    tiemNang += calPercent(tiemNang, tltnsm);
                }
            }
            double tn = tiemNang;
            if (this.player.charms.tdTriTue > System.currentTimeMillis()) {
                tiemNang += tn;
            }
            if (this.player.charms.tdTriTue3 > System.currentTimeMillis()) {
                tiemNang += tn * 2.0;
            }
            if (this.player.charms.tdTriTue4 > System.currentTimeMillis()) {
                tiemNang += tn * 3.0;
            }
            if (!this.player.isPet && this.player.itemTimesieucap.isDuoikhi
                    || this.player.isPet && ((Pet) this.player).master.itemTimesieucap.isDuoikhi) {
                tiemNang += tn * 3.0;
            }
            if (!this.player.isPet && this.player.itemTimesieucap.isKeo
                    || this.player.isPet && ((Pet) this.player).master.itemTimesieucap.isKeo) {
                tiemNang += tn * 2.0;
            }
            if (this.intrinsic != null && this.intrinsic.id == 24) {
                tiemNang += calPercent(tiemNang, this.intrinsic.param1);
            }
            if (this.power >= 60000000000L) {
                tiemNang -= calPercent(tiemNang, 50);
            }
            if (this.player.isPet) {
                if (((Pet) this.player).master.charms.tdDeTu > System.currentTimeMillis()) {
                    tiemNang += tn * 2.0;
                }
            }
            tiemNang *= (double)Manager.RATE_EXP_SERVER;
            tiemNang = calSubTNSM(tiemNang);
            if (tiemNang <= 0) {
                tiemNang = 1;
            }
        } else {
            tiemNang = 10;
        }
        return tiemNang;
    }

    public double calSubTNSM(double tiemNang) {
        if (power >= 350000000000L) {
            tiemNang -= calPercent(tiemNang, 80);
        } else if (power >= 210000000000L) {
            tiemNang -= calPercent(tiemNang, 75);
        } else if (power >= 110000000000L) {
            tiemNang -= calPercent(tiemNang, 70);
        } else if (power >= 100000000000L) {
            tiemNang -= calPercent(tiemNang, 65);
        } else if (power >= 90000000000L) {
            tiemNang -= calPercent(tiemNang, 60);
        } else if (power >= 80000000000L) {
            tiemNang -= calPercent(tiemNang, 55);
        }
        if (tiemNang > 20000000000L) {
            tiemNang = 20000000000L;
        }
        return tiemNang;
    }

    public short getTileHutHp(boolean isMob) {
        if (isMob) {
            return (short) (this.tlHutHp + this.tlHutHpMob);
        } else {
            return this.tlHutHp;
        }
    }

    public short getTiLeHutMp() {
        return this.tlHutMp;
    }

    public double subDameInjureWithDeff(double dame) {
        double def = this.def;
        dame -= def;
        if (this.player.itemTime.isUseGiapXen) {
            dame /= 2;
        }
        if (this.player.itemTime.isUseGiapXen2) {
            dame -= calPercent(dame, 60);
        }
        if (dame < 0) {
            dame = 1;
        }
        return dame;
    }

    /*------------------------------------------------------------------------*/
    public boolean canOpenPower() {
        return this.power >= getPowerLimit();
    }

    public long getPowerLimit() {
        if (powerLimit != null) {
            return powerLimit.getPower();
        }
        return 0;
    }

    public double getPowerNextLimit() {
        PowerLimit powerLimit = PowerLimitManager.getInstance().get(limitPower + 1);
        if (powerLimit != null) {
            return powerLimit.getPower();
        }
        return 0;
    }

    //**************************************************************************
    //POWER - TIEM NANG
    public void powerUp(double power) {
        if(power >= 9_000_000_000_000_000L){
        this.power += 9_000_000_000_000_000L;
        }else{
        this.power += power;
        }
        TaskService.gI().checkDoneTaskPower(player, this.power);
    }

    public void tiemNangUp(double tiemNang) {
        this.tiemNang += tiemNang;
    }

    public void increasePoint(byte type, short point) {
        if (powerLimit == null) {
            return;
        }
        if (point <= 0) {
            return;
        }
        boolean updatePoint = false;
        double tiemNangUse = 0;
        if (type == 0) {
            int pointHp = point * 20;
            tiemNangUse = point * (double)(2 * (this.hpg + 1000) + pointHp - 20) / 2.0;
            if ((this.hpg + pointHp) <= powerLimit.getHp()) {
                if (doUseTiemNang(tiemNangUse)) {
                    hpg += pointHp;
                    updatePoint = true;
                }
            } else {
                Service.getInstance().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 1) {
            int pointMp = point * 20;
            tiemNangUse = point * (double)(2 * (this.mpg + 1000) + pointMp - 20) / 2.0;
            if ((this.mpg + pointMp) <= powerLimit.getMp()) {
                if (doUseTiemNang(tiemNangUse)) {
                    mpg += pointMp;
                    updatePoint = true;
                }
            } else {
                Service.getInstance().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 2) {
            tiemNangUse = point * (double)(2 * this.dameg + point - 1) / 2 * 100.0;
            if ((this.dameg + point) <= powerLimit.getDamage()) {
                if (doUseTiemNang(tiemNangUse)) {
                    dameg += point;
                    updatePoint = true;
                }
            } else {
                Service.getInstance().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 3) {
            tiemNangUse = point * 2.0 * (double)(this.defg + 5) / 2.0 * 100000.0;
            if ((this.defg + point) <= powerLimit.getDefense()) {
                if (doUseTiemNang(tiemNangUse)) {
                    defg += point;
                    updatePoint = true;
                }
            } else {
                Service.getInstance().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 4) {
            tiemNangUse = 50000000D;
            for (int i = 0; i < this.critg; i++) {
                tiemNangUse *= 3.0;
            }
            if ((this.critg + point) <= powerLimit.getCritical()) {
                if (doUseTiemNang(tiemNangUse)) {
                    critg += point;
                    updatePoint = true;
                }
            } else {
                Service.getInstance().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (updatePoint) {
            Service.getInstance().point(player);
        }
    }

    public boolean doUseTiemNang(double tiemNang) {
        if (this.tiemNang < tiemNang) {
            Service.getInstance().sendThongBaoOK(player, "Bạn không đủ tiềm năng");
            return false;
        }
        if (this.tiemNang >= tiemNang) {
            this.tiemNang -= tiemNang;
            TaskService.gI().checkDoneTaskUseTiemNang(player);
            return true;
        }
        return false;
    }

    //--------------------------------------------------------------------------
    private long lastTimeHoiPhuc;
    private long lastTimeHoiStamina;

    public void update() {
        if (player != null && player.effectSkill != null) {
            if (player.effectSkill.isCharging && player.effectSkill.countCharging < 10) {
                long tiLeHoiPhuc = SkillUtil.getPercentCharge(player.playerSkill.skillSelect.point);
                if (player.effectSkill.isCharging && !player.isDie() && !player.effectSkill.isHaveEffectSkill()
                        && (hp < hpMax || mp < mpMax)) {
                    PlayerService.gI().hoiPhuc(player, calPercent(hpMax, tiLeHoiPhuc), calPercent(mpMax, tiLeHoiPhuc));
                    if (player.effectSkill.countCharging % 3 == 0) {
                        Service.getInstance().chat(player, "Phục hồi năng lượng " + Util.LongGioihan(getCurrPercentHP()) + "%");
                    }
                } else {
                    EffectSkillService.gI().stopCharge(player);
                }
                if (++player.effectSkill.countCharging >= 10) {
                    EffectSkillService.gI().stopCharge(player);
                }
            }
            if (Util.canDoWithTime(lastTimeHoiPhuc, 30000)) {
                PlayerService.gI().hoiPhuc(this.player, hpHoi, mpHoi);
                this.lastTimeHoiPhuc = System.currentTimeMillis();
            }
            if (Util.canDoWithTime(lastTimeHoiStamina, 60000) && this.stamina < this.maxStamina) {
                this.stamina++;
                this.lastTimeHoiStamina = System.currentTimeMillis();
                if (!this.player.isBoss && !this.player.isPet) {
                    PlayerService.gI().sendCurrentStamina(this.player);
                }
            }
        }
        //hồi phục 30s
        //hồi phục thể lực
    }

    private void setBasePoint() {
        setHpMax();
        setMpMax();
        setDame();
        setDef();
        setCrit();
        setHpHoi();
        setMpHoi();
        setNeDon();
        setCritDame();
        setSpeed();
        setAttributeOverLimit();
    }

    public void setAttributeOverLimit() {
        int max = Integer.MAX_VALUE;
        int min = -100000000;
        if (this.hpMax < 0) {
            if (this.hpMax < min) {
                this.hpMax = max;
            } else {
                this.hpMax = 1;
            }
        }
        if (this.mpMax < 0) {
            if (this.mpMax < min) {
                this.mpMax = max;
            } else {
                this.mpMax = 1;
            }
        }
        if (this.dame < 0) {
            if (this.dame < min) {
                this.dame = max;
            } else {
                this.dame = 1;
            }
        }
        if (this.def < 0) {
            if (this.def < min) {
                this.def = max;
            } else {
                this.def = 1;
            }
        }
        if (this.crit < 0) {
            if (this.crit < min) {
                this.crit = max;
            } else {
                this.crit = 1;
            }
        }
        setHp();
        setMp();
    }

    public void applyBossDoubleHpIfNeeded() {
        if (!useDoubleHp) {
            return; // nhân vật thường bỏ qua
        }
        // Giữ tỉ lệ HP hiện tại (nếu đang hợp lệ) để không bị tụt/đầy bất thường
        double oldMax = (hpMax > 0 && Double.isFinite(hpMax)) ? hpMax : 0d;
        double ratio = 1.0;
        if (oldMax > 0 && hp > 0 && Double.isFinite(hp)) {
            ratio = hp / oldMax;
            if (ratio < 0) {
                ratio = 0;
            }
            if (ratio > 1) {
                ratio = 1;
            }
        }

        // Đặt hpMax theo hpgD (double)
        double base = (Double.isFinite(hpgD) && hpgD > 0) ? hpgD : 0d;
        hpMax = capHP(base);

        // Phục hồi hp theo tỉ lệ, nếu ratio không hợp lệ thì full
        if (!(Double.isFinite(ratio)) || ratio <= 0) {
            hp = hpMax;
        } else {
            hp = Math.min(hpMax, hpMax * ratio);
        }
    }
    // Trần an toàn cho HP để tránh Infinity khi cộng/trừ về sau
    private static double capHP(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v) || v < 0) return 0d;
        final double CAP = 1e300; // < Double.MAX_VALUE, an toàn UI & tính toán
        return (v > CAP) ? CAP : v;
    }
    
    public long calPercent(long param, long percent) {
        return (param / 100) * percent;
    }

//    public static long safeMultiply(long base, long multiplier) {
//        BigInteger result = BigInteger.valueOf(base).multiply(BigInteger.valueOf(multiplier));
//        return result.min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();
//    }

    public static double safeMultiply(double base, double multiplier) {
        BigDecimal result = BigDecimal.valueOf(base).multiply(BigDecimal.valueOf(multiplier));
        // Giới hạn tối đa theo Double.MAX_VALUE
        if (result.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0) {
            return Double.MAX_VALUE;
        }
        return result.doubleValue();
    }

    public static double calPercent(double param, double percent) {
        return (param / 100.0) * percent;
    }

    public void dispose() {
        this.intrinsic = null;
        this.player = null;
        this.tlHp = null;
        this.tlMp = null;
        this.tlDef = null;
        this.tlDame = null;
        this.tlDameAttMob = null;
        this.tlSDDep = null;
        this.tlTNSM = null;
        this.tlDameCrit = null;
        this.tlSpeed = null;
        this.tlSDBang = null;
    }
    
    public void setPointKhamNgoc() {
        if (this.player.active_kham_ngoc == 1) {
            for (int j = 0; j < KHAM_NGOC.size(); j++) {
                KhamNgoc manager = KHAM_NGOC.get(j);
                for (int k = 0; k < manager.khamNgocTemplates.size(); k++) {
                    KhamNgocTemplate template = manager.khamNgocTemplates.get(k);
                    if (this.player.khamNgoc.get(j).levelNro >= template.level) {
                        ItemOption io = template.options;
                        switch (io.optionTemplate.id) {
                            case 0: //Tấn công +#
                                this.player.nPoint.dameAdd += io.param;
                                break;
                            case 2: //HP, KI+#000
                                this.player.nPoint.hpAdd += io.param * 1000;
                                this.player.nPoint.mpAdd += io.param * 1000;
                                break;
                            case 3: // vô hiệu vả biến st chưởng thành ki
                                this.player.nPoint.mstChuong += io.param;
                                break;
                            case 5: //+#% sức đánh chí mạng
                                this.player.nPoint.tlDameCrit.add(io.param);
                                break;
                            case 6: //HP+#
                                this.player.nPoint.hpAdd += io.param;
                                break;
                            case 7: //KI+#
                                this.player.nPoint.mpAdd += io.param;
                                break;
                            case 8: //Hút #% HP, KI xung quanh mỗi 5 giây
                                this.player.nPoint.tlHutHpMpXQ += io.param;
                                break;
                            case 14: //Chí mạng+#%
                                this.player.nPoint.critAdd += io.param;
                                break;
                            case 18: // #% chính xác
                                this.player.nPoint.tlchinhxac += io.param;
                                break;
                            case 19: //Tấn công+#% khi đánh quái
                                this.player.nPoint.tlDameAttMob.add(io.param);
                                break;
                            case 22: //HP+#K
                                this.player.nPoint.hpAdd += io.param * 1000;
                                break;
                            case 23: //MP+#K
                                this.player.nPoint.mpAdd += io.param * 1000;
                                break;
                            case 24:
                                this.player.nPoint.wearingBuiBui = true;
                                break;
                            case 25:
                                this.player.nPoint.wearingYacon = true;
                                break;
                            case 26:
                                this.player.nPoint.wearingDrabula = true;
                                this.player.effectSkin.lastTimeDrabula = System.currentTimeMillis();
                                break;
                            case 29:
                                this.player.nPoint.wearingMabu = true;
                                break;
                            case 27: //+# HP/30s
                                this.player.nPoint.hpHoiAdd += io.param;
                                break;
                            case 28: //+# KI/30s
                                this.player.nPoint.mpHoiAdd += io.param;
                                break;
                            case 33: //dịch chuyển tức thời
                                this.player.nPoint.teleport = true;
                                break;
                            case 47: //Giáp+#
                                this.player.nPoint.defAdd += io.param;
                                break;
                            case 48: //HP/KI+#
                                this.player.nPoint.hpAdd += io.param;
                                this.player.nPoint.mpAdd += io.param;
                                break;
                            case 49: //Tấn công+#%
                            case 50: //Sức đánh+#%
                                this.player.nPoint.tlDame.add(io.param);
                                break;
                            case 77: //HP+#%
                                this.player.nPoint.tlHp.add(io.param);
                                break;
                            case 80: //HP+#%/30s
                                this.player.nPoint.tlHpHoi += io.param;
                                break;
                            case 81: //MP+#%/30s
                                this.player.nPoint.tlMpHoi += io.param;
                                break;
                            case 88: //Cộng #% exp khi đánh quái
                                this.player.nPoint.tlTNSM.add(io.param);
                                break;
                            case 94: //Giáp #%
                                this.player.nPoint.tlDef.add(io.param);
                                break;
                            case 95: //Biến #% tấn công thành HP
                                this.player.nPoint.tlHutHp += io.param;
                                break;
                            case 96: //Biến #% tấn công thành MP
                                this.player.nPoint.tlHutMp += io.param;
                                break;
                            case 97: //Phản #% sát thương
                                this.player.nPoint.tlPST += io.param;
                                break;
                            case 100: //+#% vàng từ quái
                                this.player.nPoint.tlGold += io.param;
                                break;
                            case 101: //+#% TN,SM
                                this.player.nPoint.tlTNSM.add(io.param);
                                break;
                            case 103: //KI +#%
                                this.player.nPoint.tlMp.add(io.param);
                                break;
                            case 104: //Biến #% tấn công quái thành HP
                                this.player.nPoint.tlHutHpMob += io.param;
                                break;
                            case 105: //Vô hình khi không đánh quái và boss
                                this.player.nPoint.wearingVoHinh = true;
                                break;
                            case 106: //Không ảnh hưởng bởi cái lạnh
                                this.player.nPoint.isKhongLanh = true;
                                break;
                            case 108: //#% Né đòn
                                this.player.nPoint.tlNeDon += io.param;
                                break;
                            case 109: //Hôi, giảm #% HP
                                this.player.nPoint.tlHpGiamODo += io.param;
                                break;
                            case 114:
                                this.player.nPoint.tlSpeed.add(io.param);
                                break;
                            case 117: //Đẹp +#% SĐ cho mình và người xung quanh
                                this.player.nPoint.tlSDDep.add(io.param);
                                break;
                            case 147: //+#% sức đánh
                                this.player.nPoint.tlDame.add(io.param);
                                break;
                            case 155: //Giảm 50% sức đánh, HP, KI và +#% SM, TN, vàng từ quái
                                this.player.nPoint.tlSubSD += 50;
                                this.player.nPoint.tlTNSM.add(io.param);
                                this.player.nPoint.tlGold += io.param;
                                break;
                            case 160:
                                this.player.nPoint.tlTNSMPet += io.param;
                                break;
                            case 162: //Cute hồi #% KI/s bản thân và xung quanh
                                this.player.nPoint.mpHoiCute += io.param;
                                break;
                            case 173: //Phục hồi #% HP và KI cho đồng đội
                                this.player.nPoint.tlHpHoiBanThanVaDongDoi += io.param;
                                this.player.nPoint.tlMpHoiBanThanVaDongDoi += io.param;
                                break;
                            case 189:
                                this.player.nPoint.wearingNezuko = true;
                                break;
                            case 190:
                                this.player.nPoint.wearingTanjiro = true;
                                break;
                            case 191:
                                this.player.nPoint.wearingInoHashi = true;
                                break;
                            case 192:
                                this.player.nPoint.wearingInosuke = true;
                                break;
                            case 193:
                                this.player.nPoint.wearingZenitsu = true;
                                break;
                            case 194:
                                this.player.nPoint.tlDameChuong = 3;
                                break;
                            case 195:
                                this.player.nPoint.tlDameChuong = 4;
                                break;
                        }
                    }
                }
            }
        }
    }
    public void setPointRuongSuuTam() {
        setPointRuongCaiTrang();
        setPointRuongPhuKien();
        setPointRuongPet();
        setPointRuongLinhThu();
        setPointRuongThuCuoi();
    }

    public void setPointRuongCaiTrang() {
        if (this.player.active_ruong_suu_tam == 1) {
            for (int i = 0; i < RuongSuuTam.listCaiTrang.size(); i++) {
                for (int k = 0; k < this.player.ruongSuuTam.RuongCaiTrang.size(); k++) {
                    Item listRuong = RuongSuuTam.listCaiTrang.get(i);
                    Item template = this.player.ruongSuuTam.RuongCaiTrang.get(k);
                    if (template.template != null && listRuong.template != null && listRuong.template.id == template.template.id){
                        ItemOption io = listRuong.itemOptions.get(0);
                        switch (io.optionTemplate.id) {
                            case 0: //Tấn công +#
                                this.player.nPoint.dameAdd += io.param;
                                break;
                            case 2: //HP, KI+#000
                                this.player.nPoint.hpAdd += io.param * 1000;
                                this.player.nPoint.mpAdd += io.param * 1000;
                                break;
                            case 3: // vô hiệu vả biến st chưởng thành ki
                                this.player.nPoint.mstChuong += io.param;
                                break;
                            case 5: //+#% sức đánh chí mạng
                                this.player.nPoint.tlDameCrit.add(io.param);
                                break;
                            case 6: //HP+#
                                this.player.nPoint.hpAdd += io.param;
                                break;
                            case 7: //KI+#
                                this.player.nPoint.mpAdd += io.param;
                                break;
                            case 8: //Hút #% HP, KI xung quanh mỗi 5 giây
                                this.player.nPoint.tlHutHpMpXQ += io.param;
                                break;
                            case 14: //Chí mạng+#%
                                this.player.nPoint.critAdd += io.param;
                                break;
                            case 18: // #% chính xác
                                this.player.nPoint.tlchinhxac += io.param;
                                break;
                            case 19: //Tấn công+#% khi đánh quái
                                this.player.nPoint.tlDameAttMob.add(io.param);
                                break;
                            case 22: //HP+#K
                                this.player.nPoint.hpAdd += io.param * 1000;
                                break;
                            case 23: //MP+#K
                                this.player.nPoint.mpAdd += io.param * 1000;
                                break;
                            case 24:
                                this.player.nPoint.wearingBuiBui = true;
                                break;
                            case 25:
                                this.player.nPoint.wearingYacon = true;
                                break;
                            case 26:
                                this.player.nPoint.wearingDrabula = true;
                                this.player.effectSkin.lastTimeDrabula = System.currentTimeMillis();
                                break;
                            case 29:
                                this.player.nPoint.wearingMabu = true;
                                break;
                            case 27: //+# HP/30s
                                this.player.nPoint.hpHoiAdd += io.param;
                                break;
                            case 28: //+# KI/30s
                                this.player.nPoint.mpHoiAdd += io.param;
                                break;
                            case 33: //dịch chuyển tức thời
                                this.player.nPoint.teleport = true;
                                break;
                            case 47: //Giáp+#
                                this.player.nPoint.defAdd += io.param;
                                break;
                            case 48: //HP/KI+#
                                this.player.nPoint.hpAdd += io.param;
                                this.player.nPoint.mpAdd += io.param;
                                break;
                            case 49: //Tấn công+#%
                            case 50: //Sức đánh+#%
                                this.player.nPoint.tlDame.add(io.param);
                                break;
                            case 77: //HP+#%
                                this.player.nPoint.tlHp.add(io.param);
                                break;
                            case 80: //HP+#%/30s
                                this.player.nPoint.tlHpHoi += io.param;
                                break;
                            case 81: //MP+#%/30s
                                this.player.nPoint.tlMpHoi += io.param;
                                break;
                            case 88: //Cộng #% exp khi đánh quái
                                this.player.nPoint.tlTNSM.add(io.param);
                                break;
                            case 94: //Giáp #%
                                this.player.nPoint.tlDef.add(io.param);
                                break;
                            case 95: //Biến #% tấn công thành HP
                                this.player.nPoint.tlHutHp += io.param;
                                break;
                            case 96: //Biến #% tấn công thành MP
                                this.player.nPoint.tlHutMp += io.param;
                                break;
                            case 97: //Phản #% sát thương
                                this.player.nPoint.tlPST += io.param;
                                break;
                            case 100: //+#% vàng từ quái
                                this.player.nPoint.tlGold += io.param;
                                break;
                            case 101: //+#% TN,SM
                                this.player.nPoint.tlTNSM.add(io.param);
                                break;
                            case 103: //KI +#%
                                this.player.nPoint.tlMp.add(io.param);
                                break;
                            case 104: //Biến #% tấn công quái thành HP
                                this.player.nPoint.tlHutHpMob += io.param;
                                break;
                            case 105: //Vô hình khi không đánh quái và boss
                                this.player.nPoint.wearingVoHinh = true;
                                break;
                            case 106: //Không ảnh hưởng bởi cái lạnh
                                this.player.nPoint.isKhongLanh = true;
                                break;
                            case 108: //#% Né đòn
                                this.player.nPoint.tlNeDon += io.param;
                                break;
                            case 109: //Hôi, giảm #% HP
                                this.player.nPoint.tlHpGiamODo += io.param;
                                break;
                            case 114:
                                this.player.nPoint.tlSpeed.add(io.param);
                                break;
                            case 117: //Đẹp +#% SĐ cho mình và người xung quanh
                                this.player.nPoint.tlSDDep.add(io.param);
                                break;
                            case 147: //+#% sức đánh
                                this.player.nPoint.tlDame.add(io.param);
                                break;
                            case 155: //Giảm 50% sức đánh, HP, KI và +#% SM, TN, vàng từ quái
                                this.player.nPoint.tlSubSD += 50;
                                this.player.nPoint.tlTNSM.add(io.param);
                                this.player.nPoint.tlGold += io.param;
                                break;
                            case 160:
                                this.player.nPoint.tlTNSMPet += io.param;
                                break;
                            case 162: //Cute hồi #% KI/s bản thân và xung quanh
                                this.player.nPoint.mpHoiCute += io.param;
                                break;
                            case 173: //Phục hồi #% HP và KI cho đồng đội
                                this.player.nPoint.tlHpHoiBanThanVaDongDoi += io.param;
                                this.player.nPoint.tlMpHoiBanThanVaDongDoi += io.param;
                                break;
                            case 189:
                                this.player.nPoint.wearingNezuko = true;
                                break;
                            case 190:
                                this.player.nPoint.wearingTanjiro = true;
                                break;
                            case 191:
                                this.player.nPoint.wearingInoHashi = true;
                                break;
                            case 192:
                                this.player.nPoint.wearingInosuke = true;
                                break;
                            case 193:
                                this.player.nPoint.wearingZenitsu = true;
                                break;
                            case 194:
                                this.player.nPoint.tlDameChuong = 3;
                                break;
                            case 195:
                                this.player.nPoint.tlDameChuong = 4;
                                break;
                        }
                    }
                }
            }
        }
    }

    public void setPointRuongPhuKien() {
        if (this.player.active_ruong_suu_tam == 1) {
            for (int i = 0; i < RuongSuuTam.listPhuKien.size(); i++) {
                for (int k = 0; k < this.player.ruongSuuTam.RuongPhuKien.size(); k++) {
                    Item listRuong = RuongSuuTam.listPhuKien.get(i);
                    Item template = this.player.ruongSuuTam.RuongPhuKien.get(k);
                    if (template.template != null && listRuong.template != null && listRuong.template.id == template.template.id){
                        ItemOption io = listRuong.itemOptions.get(0);
                        switch (io.optionTemplate.id) {
                            case 0: //Tấn công +#
                                this.player.nPoint.dameAdd += io.param;
                                break;
                            case 2: //HP, KI+#000
                                this.player.nPoint.hpAdd += io.param * 1000;
                                this.player.nPoint.mpAdd += io.param * 1000;
                                break;
                            case 3: // vô hiệu vả biến st chưởng thành ki
                                this.player.nPoint.mstChuong += io.param;
                                break;
                            case 5: //+#% sức đánh chí mạng
                                this.player.nPoint.tlDameCrit.add(io.param);
                                break;
                            case 6: //HP+#
                                this.player.nPoint.hpAdd += io.param;
                                break;
                            case 7: //KI+#
                                this.player.nPoint.mpAdd += io.param;
                                break;
                            case 8: //Hút #% HP, KI xung quanh mỗi 5 giây
                                this.player.nPoint.tlHutHpMpXQ += io.param;
                                break;
                            case 14: //Chí mạng+#%
                                this.player.nPoint.critAdd += io.param;
                                break;
                            case 18: // #% chính xác
                                this.player.nPoint.tlchinhxac += io.param;
                                break;
                            case 19: //Tấn công+#% khi đánh quái
                                this.player.nPoint.tlDameAttMob.add(io.param);
                                break;
                            case 22: //HP+#K
                                this.player.nPoint.hpAdd += io.param * 1000;
                                break;
                            case 23: //MP+#K
                                this.player.nPoint.mpAdd += io.param * 1000;
                                break;
                            case 24:
                                this.player.nPoint.wearingBuiBui = true;
                                break;
                            case 25:
                                this.player.nPoint.wearingYacon = true;
                                break;
                            case 26:
                                this.player.nPoint.wearingDrabula = true;
                                this.player.effectSkin.lastTimeDrabula = System.currentTimeMillis();
                                break;
                            case 29:
                                this.player.nPoint.wearingMabu = true;
                                break;
                            case 27: //+# HP/30s
                                this.player.nPoint.hpHoiAdd += io.param;
                                break;
                            case 28: //+# KI/30s
                                this.player.nPoint.mpHoiAdd += io.param;
                                break;
                            case 33: //dịch chuyển tức thời
                                this.player.nPoint.teleport = true;
                                break;
                            case 47: //Giáp+#
                                this.player.nPoint.defAdd += io.param;
                                break;
                            case 48: //HP/KI+#
                                this.player.nPoint.hpAdd += io.param;
                                this.player.nPoint.mpAdd += io.param;
                                break;
                            case 49: //Tấn công+#%
                            case 50: //Sức đánh+#%
                                this.player.nPoint.tlDame.add(io.param);
                                break;
                            case 77: //HP+#%
                                this.player.nPoint.tlHp.add(io.param);
                                break;
                            case 80: //HP+#%/30s
                                this.player.nPoint.tlHpHoi += io.param;
                                break;
                            case 81: //MP+#%/30s
                                this.player.nPoint.tlMpHoi += io.param;
                                break;
                            case 88: //Cộng #% exp khi đánh quái
                                this.player.nPoint.tlTNSM.add(io.param);
                                break;
                            case 94: //Giáp #%
                                this.player.nPoint.tlDef.add(io.param);
                                break;
                            case 95: //Biến #% tấn công thành HP
                                this.player.nPoint.tlHutHp += io.param;
                                break;
                            case 96: //Biến #% tấn công thành MP
                                this.player.nPoint.tlHutMp += io.param;
                                break;
                            case 97: //Phản #% sát thương
                                this.player.nPoint.tlPST += io.param;
                                break;
                            case 100: //+#% vàng từ quái
                                this.player.nPoint.tlGold += io.param;
                                break;
                            case 101: //+#% TN,SM
                                this.player.nPoint.tlTNSM.add(io.param);
                                break;
                            case 103: //KI +#%
                                this.player.nPoint.tlMp.add(io.param);
                                break;
                            case 104: //Biến #% tấn công quái thành HP
                                this.player.nPoint.tlHutHpMob += io.param;
                                break;
                            case 105: //Vô hình khi không đánh quái và boss
                                this.player.nPoint.wearingVoHinh = true;
                                break;
                            case 106: //Không ảnh hưởng bởi cái lạnh
                                this.player.nPoint.isKhongLanh = true;
                                break;
                            case 108: //#% Né đòn
                                this.player.nPoint.tlNeDon += io.param;
                                break;
                            case 109: //Hôi, giảm #% HP
                                this.player.nPoint.tlHpGiamODo += io.param;
                                break;
                            case 114:
                                this.player.nPoint.tlSpeed.add(io.param);
                                break;
                            case 117: //Đẹp +#% SĐ cho mình và người xung quanh
                                this.player.nPoint.tlSDDep.add(io.param);
                                break;
                            case 147: //+#% sức đánh
                                this.player.nPoint.tlDame.add(io.param);
                                break;
                            case 155: //Giảm 50% sức đánh, HP, KI và +#% SM, TN, vàng từ quái
                                this.player.nPoint.tlSubSD += 50;
                                this.player.nPoint.tlTNSM.add(io.param);
                                this.player.nPoint.tlGold += io.param;
                                break;
                            case 160:
                                this.player.nPoint.tlTNSMPet += io.param;
                                break;
                            case 162: //Cute hồi #% KI/s bản thân và xung quanh
                                this.player.nPoint.mpHoiCute += io.param;
                                break;
                            case 173: //Phục hồi #% HP và KI cho đồng đội
                                this.player.nPoint.tlHpHoiBanThanVaDongDoi += io.param;
                                this.player.nPoint.tlMpHoiBanThanVaDongDoi += io.param;
                                break;
                            case 189:
                                this.player.nPoint.wearingNezuko = true;
                                break;
                            case 190:
                                this.player.nPoint.wearingTanjiro = true;
                                break;
                            case 191:
                                this.player.nPoint.wearingInoHashi = true;
                                break;
                            case 192:
                                this.player.nPoint.wearingInosuke = true;
                                break;
                            case 193:
                                this.player.nPoint.wearingZenitsu = true;
                                break;
                            case 194:
                                this.player.nPoint.tlDameChuong = 3;
                                break;
                            case 195:
                                this.player.nPoint.tlDameChuong = 4;
                                break;
                        }
                    }
                }
            }
        }
    }

    public void setPointRuongPet() {
        if (this.player.active_ruong_suu_tam == 1) {
            for (int i = 0; i < RuongSuuTam.listPet.size(); i++) {
                for (int k = 0; k < this.player.ruongSuuTam.RuongPet.size(); k++) {
                    Item listRuong = RuongSuuTam.listPet.get(i);
                    Item template = this.player.ruongSuuTam.RuongPet.get(k);
                    if (template.template != null && listRuong.template != null && listRuong.template.id == template.template.id){
                        ItemOption io = listRuong.itemOptions.get(0);
                        switch (io.optionTemplate.id) {
                            case 0: //Tấn công +#
                                this.player.nPoint.dameAdd += io.param;
                                break;
                            case 2: //HP, KI+#000
                                this.player.nPoint.hpAdd += io.param * 1000;
                                this.player.nPoint.mpAdd += io.param * 1000;
                                break;
                            case 3: // vô hiệu vả biến st chưởng thành ki
                                this.player.nPoint.mstChuong += io.param;
                                break;
                            case 5: //+#% sức đánh chí mạng
                                this.player.nPoint.tlDameCrit.add(io.param);
                                break;
                            case 6: //HP+#
                                this.player.nPoint.hpAdd += io.param;
                                break;
                            case 7: //KI+#
                                this.player.nPoint.mpAdd += io.param;
                                break;
                            case 8: //Hút #% HP, KI xung quanh mỗi 5 giây
                                this.player.nPoint.tlHutHpMpXQ += io.param;
                                break;
                            case 14: //Chí mạng+#%
                                this.player.nPoint.critAdd += io.param;
                                break;
                            case 18: // #% chính xác
                                this.player.nPoint.tlchinhxac += io.param;
                                break;
                            case 19: //Tấn công+#% khi đánh quái
                                this.player.nPoint.tlDameAttMob.add(io.param);
                                break;
                            case 22: //HP+#K
                                this.player.nPoint.hpAdd += io.param * 1000;
                                break;
                            case 23: //MP+#K
                                this.player.nPoint.mpAdd += io.param * 1000;
                                break;
                            case 24:
                                this.player.nPoint.wearingBuiBui = true;
                                break;
                            case 25:
                                this.player.nPoint.wearingYacon = true;
                                break;
                            case 26:
                                this.player.nPoint.wearingDrabula = true;
                                this.player.effectSkin.lastTimeDrabula = System.currentTimeMillis();
                                break;
                            case 29:
                                this.player.nPoint.wearingMabu = true;
                                break;
                            case 27: //+# HP/30s
                                this.player.nPoint.hpHoiAdd += io.param;
                                break;
                            case 28: //+# KI/30s
                                this.player.nPoint.mpHoiAdd += io.param;
                                break;
                            case 33: //dịch chuyển tức thời
                                this.player.nPoint.teleport = true;
                                break;
                            case 47: //Giáp+#
                                this.player.nPoint.defAdd += io.param;
                                break;
                            case 48: //HP/KI+#
                                this.player.nPoint.hpAdd += io.param;
                                this.player.nPoint.mpAdd += io.param;
                                break;
                            case 49: //Tấn công+#%
                            case 50: //Sức đánh+#%
                                this.player.nPoint.tlDame.add(io.param);
                                break;
                            case 77: //HP+#%
                                this.player.nPoint.tlHp.add(io.param);
                                break;
                            case 80: //HP+#%/30s
                                this.player.nPoint.tlHpHoi += io.param;
                                break;
                            case 81: //MP+#%/30s
                                this.player.nPoint.tlMpHoi += io.param;
                                break;
                            case 88: //Cộng #% exp khi đánh quái
                                this.player.nPoint.tlTNSM.add(io.param);
                                break;
                            case 94: //Giáp #%
                                this.player.nPoint.tlDef.add(io.param);
                                break;
                            case 95: //Biến #% tấn công thành HP
                                this.player.nPoint.tlHutHp += io.param;
                                break;
                            case 96: //Biến #% tấn công thành MP
                                this.player.nPoint.tlHutMp += io.param;
                                break;
                            case 97: //Phản #% sát thương
                                this.player.nPoint.tlPST += io.param;
                                break;
                            case 100: //+#% vàng từ quái
                                this.player.nPoint.tlGold += io.param;
                                break;
                            case 101: //+#% TN,SM
                                this.player.nPoint.tlTNSM.add(io.param);
                                break;
                            case 103: //KI +#%
                                this.player.nPoint.tlMp.add(io.param);
                                break;
                            case 104: //Biến #% tấn công quái thành HP
                                this.player.nPoint.tlHutHpMob += io.param;
                                break;
                            case 105: //Vô hình khi không đánh quái và boss
                                this.player.nPoint.wearingVoHinh = true;
                                break;
                            case 106: //Không ảnh hưởng bởi cái lạnh
                                this.player.nPoint.isKhongLanh = true;
                                break;
                            case 108: //#% Né đòn
                                this.player.nPoint.tlNeDon += io.param;
                                break;
                            case 109: //Hôi, giảm #% HP
                                this.player.nPoint.tlHpGiamODo += io.param;
                                break;
                            case 114:
                                this.player.nPoint.tlSpeed.add(io.param);
                                break;
                            case 117: //Đẹp +#% SĐ cho mình và người xung quanh
                                this.player.nPoint.tlSDDep.add(io.param);
                                break;
                            case 147: //+#% sức đánh
                                this.player.nPoint.tlDame.add(io.param);
                                break;
                            case 155: //Giảm 50% sức đánh, HP, KI và +#% SM, TN, vàng từ quái
                                this.player.nPoint.tlSubSD += 50;
                                this.player.nPoint.tlTNSM.add(io.param);
                                this.player.nPoint.tlGold += io.param;
                                break;
                            case 160:
                                this.player.nPoint.tlTNSMPet += io.param;
                                break;
                            case 162: //Cute hồi #% KI/s bản thân và xung quanh
                                this.player.nPoint.mpHoiCute += io.param;
                                break;
                            case 173: //Phục hồi #% HP và KI cho đồng đội
                                this.player.nPoint.tlHpHoiBanThanVaDongDoi += io.param;
                                this.player.nPoint.tlMpHoiBanThanVaDongDoi += io.param;
                                break;
                            case 189:
                                this.player.nPoint.wearingNezuko = true;
                                break;
                            case 190:
                                this.player.nPoint.wearingTanjiro = true;
                                break;
                            case 191:
                                this.player.nPoint.wearingInoHashi = true;
                                break;
                            case 192:
                                this.player.nPoint.wearingInosuke = true;
                                break;
                            case 193:
                                this.player.nPoint.wearingZenitsu = true;
                                break;
                            case 194:
                                this.player.nPoint.tlDameChuong = 3;
                                break;
                            case 195:
                                this.player.nPoint.tlDameChuong = 4;
                                break;
                        }
                    }
                }
            }
        }
    }

    public void setPointRuongLinhThu() {
        if (this.player.active_ruong_suu_tam == 1) {
            for (int i = 0; i < RuongSuuTam.listLinhThu.size(); i++) {
                for (int k = 0; k < this.player.ruongSuuTam.RuongLinhThu.size(); k++) {
                    Item listRuong = RuongSuuTam.listLinhThu.get(i);
                    Item template = this.player.ruongSuuTam.RuongLinhThu.get(k);
                    if (template.template != null && listRuong.template != null && listRuong.template.id == template.template.id){
                        ItemOption io = listRuong.itemOptions.get(0);
                        switch (io.optionTemplate.id) {
                            case 0: //Tấn công +#
                                this.player.nPoint.dameAdd += io.param;
                                break;
                            case 2: //HP, KI+#000
                                this.player.nPoint.hpAdd += io.param * 1000;
                                this.player.nPoint.mpAdd += io.param * 1000;
                                break;
                            case 3: // vô hiệu vả biến st chưởng thành ki
                                this.player.nPoint.mstChuong += io.param;
                                break;
                            case 5: //+#% sức đánh chí mạng
                                this.player.nPoint.tlDameCrit.add(io.param);
                                break;
                            case 6: //HP+#
                                this.player.nPoint.hpAdd += io.param;
                                break;
                            case 7: //KI+#
                                this.player.nPoint.mpAdd += io.param;
                                break;
                            case 8: //Hút #% HP, KI xung quanh mỗi 5 giây
                                this.player.nPoint.tlHutHpMpXQ += io.param;
                                break;
                            case 14: //Chí mạng+#%
                                this.player.nPoint.critAdd += io.param;
                                break;
                            case 18: // #% chính xác
                                this.player.nPoint.tlchinhxac += io.param;
                                break;
                            case 19: //Tấn công+#% khi đánh quái
                                this.player.nPoint.tlDameAttMob.add(io.param);
                                break;
                            case 22: //HP+#K
                                this.player.nPoint.hpAdd += io.param * 1000;
                                break;
                            case 23: //MP+#K
                                this.player.nPoint.mpAdd += io.param * 1000;
                                break;
                            case 24:
                                this.player.nPoint.wearingBuiBui = true;
                                break;
                            case 25:
                                this.player.nPoint.wearingYacon = true;
                                break;
                            case 26:
                                this.player.nPoint.wearingDrabula = true;
                                this.player.effectSkin.lastTimeDrabula = System.currentTimeMillis();
                                break;
                            case 29:
                                this.player.nPoint.wearingMabu = true;
                                break;
                            case 27: //+# HP/30s
                                this.player.nPoint.hpHoiAdd += io.param;
                                break;
                            case 28: //+# KI/30s
                                this.player.nPoint.mpHoiAdd += io.param;
                                break;
                            case 33: //dịch chuyển tức thời
                                this.player.nPoint.teleport = true;
                                break;
                            case 47: //Giáp+#
                                this.player.nPoint.defAdd += io.param;
                                break;
                            case 48: //HP/KI+#
                                this.player.nPoint.hpAdd += io.param;
                                this.player.nPoint.mpAdd += io.param;
                                break;
                            case 49: //Tấn công+#%
                            case 50: //Sức đánh+#%
                                this.player.nPoint.tlDame.add(io.param);
                                break;
                            case 77: //HP+#%
                                this.player.nPoint.tlHp.add(io.param);
                                break;
                            case 80: //HP+#%/30s
                                this.player.nPoint.tlHpHoi += io.param;
                                break;
                            case 81: //MP+#%/30s
                                this.player.nPoint.tlMpHoi += io.param;
                                break;
                            case 88: //Cộng #% exp khi đánh quái
                                this.player.nPoint.tlTNSM.add(io.param);
                                break;
                            case 94: //Giáp #%
                                this.player.nPoint.tlDef.add(io.param);
                                break;
                            case 95: //Biến #% tấn công thành HP
                                this.player.nPoint.tlHutHp += io.param;
                                break;
                            case 96: //Biến #% tấn công thành MP
                                this.player.nPoint.tlHutMp += io.param;
                                break;
                            case 97: //Phản #% sát thương
                                this.player.nPoint.tlPST += io.param;
                                break;
                            case 100: //+#% vàng từ quái
                                this.player.nPoint.tlGold += io.param;
                                break;
                            case 101: //+#% TN,SM
                                this.player.nPoint.tlTNSM.add(io.param);
                                break;
                            case 103: //KI +#%
                                this.player.nPoint.tlMp.add(io.param);
                                break;
                            case 104: //Biến #% tấn công quái thành HP
                                this.player.nPoint.tlHutHpMob += io.param;
                                break;
                            case 105: //Vô hình khi không đánh quái và boss
                                this.player.nPoint.wearingVoHinh = true;
                                break;
                            case 106: //Không ảnh hưởng bởi cái lạnh
                                this.player.nPoint.isKhongLanh = true;
                                break;
                            case 108: //#% Né đòn
                                this.player.nPoint.tlNeDon += io.param;
                                break;
                            case 109: //Hôi, giảm #% HP
                                this.player.nPoint.tlHpGiamODo += io.param;
                                break;
                            case 114:
                                this.player.nPoint.tlSpeed.add(io.param);
                                break;
                            case 117: //Đẹp +#% SĐ cho mình và người xung quanh
                                this.player.nPoint.tlSDDep.add(io.param);
                                break;
                            case 147: //+#% sức đánh
                                this.player.nPoint.tlDame.add(io.param);
                                break;
                            case 155: //Giảm 50% sức đánh, HP, KI và +#% SM, TN, vàng từ quái
                                this.player.nPoint.tlSubSD += 50;
                                this.player.nPoint.tlTNSM.add(io.param);
                                this.player.nPoint.tlGold += io.param;
                                break;
                            case 160:
                                this.player.nPoint.tlTNSMPet += io.param;
                                break;
                            case 162: //Cute hồi #% KI/s bản thân và xung quanh
                                this.player.nPoint.mpHoiCute += io.param;
                                break;
                            case 173: //Phục hồi #% HP và KI cho đồng đội
                                this.player.nPoint.tlHpHoiBanThanVaDongDoi += io.param;
                                this.player.nPoint.tlMpHoiBanThanVaDongDoi += io.param;
                                break;
                            case 189:
                                this.player.nPoint.wearingNezuko = true;
                                break;
                            case 190:
                                this.player.nPoint.wearingTanjiro = true;
                                break;
                            case 191:
                                this.player.nPoint.wearingInoHashi = true;
                                break;
                            case 192:
                                this.player.nPoint.wearingInosuke = true;
                                break;
                            case 193:
                                this.player.nPoint.wearingZenitsu = true;
                                break;
                            case 194:
                                this.player.nPoint.tlDameChuong = 3;
                                break;
                            case 195:
                                this.player.nPoint.tlDameChuong = 4;
                                break;
                        }
                    }
                }
            }
        }
    }

    public void setPointRuongThuCuoi() {
        if (this.player.active_ruong_suu_tam == 1) {
            for (int i = 0; i < RuongSuuTam.listThuCuoi.size(); i++) {
                for (int k = 0; k < this.player.ruongSuuTam.RuongThuCuoi.size(); k++) {
                    Item listRuong = RuongSuuTam.listThuCuoi.get(i);
                    Item template = this.player.ruongSuuTam.RuongThuCuoi.get(k);
                    if (template.template != null && listRuong.template != null && listRuong.template.id == template.template.id){
                        ItemOption io = listRuong.itemOptions.get(0);
                        switch (io.optionTemplate.id) {
                            case 0: //Tấn công +#
                                this.player.nPoint.dameAdd += io.param;
                                break;
                            case 2: //HP, KI+#000
                                this.player.nPoint.hpAdd += io.param * 1000;
                                this.player.nPoint.mpAdd += io.param * 1000;
                                break;
                            case 3: // vô hiệu vả biến st chưởng thành ki
                                this.player.nPoint.mstChuong += io.param;
                                break;
                            case 5: //+#% sức đánh chí mạng
                                this.player.nPoint.tlDameCrit.add(io.param);
                                break;
                            case 6: //HP+#
                                this.player.nPoint.hpAdd += io.param;
                                break;
                            case 7: //KI+#
                                this.player.nPoint.mpAdd += io.param;
                                break;
                            case 8: //Hút #% HP, KI xung quanh mỗi 5 giây
                                this.player.nPoint.tlHutHpMpXQ += io.param;
                                break;
                            case 14: //Chí mạng+#%
                                this.player.nPoint.critAdd += io.param;
                                break;
                            case 18: // #% chính xác
                                this.player.nPoint.tlchinhxac += io.param;
                                break;
                            case 19: //Tấn công+#% khi đánh quái
                                this.player.nPoint.tlDameAttMob.add(io.param);
                                break;
                            case 22: //HP+#K
                                this.player.nPoint.hpAdd += io.param * 1000;
                                break;
                            case 23: //MP+#K
                                this.player.nPoint.mpAdd += io.param * 1000;
                                break;
                            case 24:
                                this.player.nPoint.wearingBuiBui = true;
                                break;
                            case 25:
                                this.player.nPoint.wearingYacon = true;
                                break;
                            case 26:
                                this.player.nPoint.wearingDrabula = true;
                                this.player.effectSkin.lastTimeDrabula = System.currentTimeMillis();
                                break;
                            case 29:
                                this.player.nPoint.wearingMabu = true;
                                break;
                            case 27: //+# HP/30s
                                this.player.nPoint.hpHoiAdd += io.param;
                                break;
                            case 28: //+# KI/30s
                                this.player.nPoint.mpHoiAdd += io.param;
                                break;
                            case 33: //dịch chuyển tức thời
                                this.player.nPoint.teleport = true;
                                break;
                            case 47: //Giáp+#
                                this.player.nPoint.defAdd += io.param;
                                break;
                            case 48: //HP/KI+#
                                this.player.nPoint.hpAdd += io.param;
                                this.player.nPoint.mpAdd += io.param;
                                break;
                            case 49: //Tấn công+#%
                            case 50: //Sức đánh+#%
                                this.player.nPoint.tlDame.add(io.param);
                                break;
                            case 77: //HP+#%
                                this.player.nPoint.tlHp.add(io.param);
                                break;
                            case 80: //HP+#%/30s
                                this.player.nPoint.tlHpHoi += io.param;
                                break;
                            case 81: //MP+#%/30s
                                this.player.nPoint.tlMpHoi += io.param;
                                break;
                            case 88: //Cộng #% exp khi đánh quái
                                this.player.nPoint.tlTNSM.add(io.param);
                                break;
                            case 94: //Giáp #%
                                this.player.nPoint.tlDef.add(io.param);
                                break;
                            case 95: //Biến #% tấn công thành HP
                                this.player.nPoint.tlHutHp += io.param;
                                break;
                            case 96: //Biến #% tấn công thành MP
                                this.player.nPoint.tlHutMp += io.param;
                                break;
                            case 97: //Phản #% sát thương
                                this.player.nPoint.tlPST += io.param;
                                break;
                            case 100: //+#% vàng từ quái
                                this.player.nPoint.tlGold += io.param;
                                break;
                            case 101: //+#% TN,SM
                                this.player.nPoint.tlTNSM.add(io.param);
                                break;
                            case 103: //KI +#%
                                this.player.nPoint.tlMp.add(io.param);
                                break;
                            case 104: //Biến #% tấn công quái thành HP
                                this.player.nPoint.tlHutHpMob += io.param;
                                break;
                            case 105: //Vô hình khi không đánh quái và boss
                                this.player.nPoint.wearingVoHinh = true;
                                break;
                            case 106: //Không ảnh hưởng bởi cái lạnh
                                this.player.nPoint.isKhongLanh = true;
                                break;
                            case 108: //#% Né đòn
                                this.player.nPoint.tlNeDon += io.param;
                                break;
                            case 109: //Hôi, giảm #% HP
                                this.player.nPoint.tlHpGiamODo += io.param;
                                break;
                            case 114:
                                this.player.nPoint.tlSpeed.add(io.param);
                                break;
                            case 117: //Đẹp +#% SĐ cho mình và người xung quanh
                                this.player.nPoint.tlSDDep.add(io.param);
                                break;
                            case 147: //+#% sức đánh
                                this.player.nPoint.tlDame.add(io.param);
                                break;
                            case 155: //Giảm 50% sức đánh, HP, KI và +#% SM, TN, vàng từ quái
                                this.player.nPoint.tlSubSD += 50;
                                this.player.nPoint.tlTNSM.add(io.param);
                                this.player.nPoint.tlGold += io.param;
                                break;
                            case 160:
                                this.player.nPoint.tlTNSMPet += io.param;
                                break;
                            case 162: //Cute hồi #% KI/s bản thân và xung quanh
                                this.player.nPoint.mpHoiCute += io.param;
                                break;
                            case 173: //Phục hồi #% HP và KI cho đồng đội
                                this.player.nPoint.tlHpHoiBanThanVaDongDoi += io.param;
                                this.player.nPoint.tlMpHoiBanThanVaDongDoi += io.param;
                                break;
                            case 189:
                                this.player.nPoint.wearingNezuko = true;
                                break;
                            case 190:
                                this.player.nPoint.wearingTanjiro = true;
                                break;
                            case 191:
                                this.player.nPoint.wearingInoHashi = true;
                                break;
                            case 192:
                                this.player.nPoint.wearingInosuke = true;
                                break;
                            case 193:
                                this.player.nPoint.wearingZenitsu = true;
                                break;
                            case 194:
                                this.player.nPoint.tlDameChuong = 3;
                                break;
                            case 195:
                                this.player.nPoint.tlDameChuong = 4;
                                break;
                        }
                    }
                }
            }
        }
    }
}
