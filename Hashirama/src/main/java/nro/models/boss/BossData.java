package nro.models.boss;

import nro.consts.ConstPlayer;
import nro.models.skill.Skill;
import lombok.Builder;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 */
public class BossData {

    public static final int _0_GIAY = 0;
    public static final int _1_GIAY = 1;
    public static final int _5_GIAY = 5;
    public static final int _10_GIAY = 10;
    public static final int _30_GIAY = 30;
    public static final int _1_PHUT = 60;
    public static final int _3_PHUT = 180;
    public static final int _5_PHUT = 300;
    public static final int _10_PHUT = 600;
    public static final int _15_PHUT = 900;
    public static final int _30_PHUT = 1800;
    public static final int _45_PHUT = 2700;
    public static final int _1_GIO = 3600;
    public static final int _1_GIO20 = 4800;
    public static final int _2_GIO = 7200;

    //--------------------------------------------------------------------------
    public String name;

    public byte gender;

    public byte typeDame;

    public byte typeHp;

    public double dame;

    public double[][] hp;

    public short[] outfit;

    public short[] mapJoin;

    public int[][] skillTemp;

    public int secondsRest;

    public boolean joinMapIdle;

    public int timeDelayLeaveMap = -1;

    @Builder
    public BossData(String name, byte gender, byte typeDame, byte typeHp, long dame, double[][] hp,
            short[] outfit, short[] mapJoin, int[][] skillTemp, int secondsRest) {
        this.name = name;
        this.gender = gender;
        this.typeDame = typeDame;
        this.typeHp = typeHp;
        this.dame = dame;
        this.hp = hp;
        this.outfit = outfit;
        this.mapJoin = mapJoin;
        this.skillTemp = skillTemp;
        this.secondsRest = secondsRest;
    }

    public BossData(String name, byte gender, byte typeDame, byte typeHp, long dame, double[][] hp,
            short[] outfit, short[] mapJoin, int[][] skillTemp, int secondsRest, boolean joinMapIdle) {
        this.name = name;
        this.gender = gender;
        this.typeDame = typeDame;
        this.typeHp = typeHp;
        this.dame = dame;
        this.hp = hp;
        this.outfit = outfit;
        this.mapJoin = mapJoin;
        this.skillTemp = skillTemp;
        this.secondsRest = secondsRest;
        this.joinMapIdle = joinMapIdle;
    }

    public BossData(String name, byte gender, byte typeDame, byte typeHp, long dame, double[][] hp,
            short[] outfit, short[] mapJoin, int[][] skillTemp, int secondsRest, int timeDelayLeaveMap) {
        this.name = name;
        this.gender = gender;
        this.typeDame = typeDame;
        this.typeHp = typeHp;
        this.dame = dame;
        this.hp = hp;
        this.outfit = outfit;
        this.mapJoin = mapJoin;
        this.skillTemp = skillTemp;
        this.secondsRest = secondsRest;
        this.timeDelayLeaveMap = timeDelayLeaveMap;
    }

    public BossData(String name, byte gender, byte typeDame, byte typeHp, long dame, double[][] hp,
            short[] outfit, short[] mapJoin, int[][] skillTemp, int secondsRest, boolean joinMapIdle, int timeDelayLeaveMap) {
        this.name = name;
        this.gender = gender;
        this.typeDame = typeDame;
        this.typeHp = typeHp;
        this.dame = dame;
        this.hp = hp;
        this.outfit = outfit;
        this.mapJoin = mapJoin;
        this.skillTemp = skillTemp;
        this.secondsRest = secondsRest;
        this.joinMapIdle = joinMapIdle;
        this.timeDelayLeaveMap = timeDelayLeaveMap;
    }

    //--------------------------------------------------------------------------Broly
    public static final BossData BROLY = new BossData(
            "Broly", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_PERCENT_HP_HUND, //type dame
            Boss.HP_NORMAL, //type hp
            1, //dame
            new double[][]{{100, 1000}, {1000, 100000}, {100000, 1000000}, {1000000, 2000000}}, //hp
            new short[]{291, 292, 293}, //outfit
            new short[]{5, 6, 27, 28, 29, 30, 13, 10, 31, 32, 33, 34, 20, 19, 35, 36, 37, 38}, //map join
            new int[][]{ //skill
                {Skill.DEMON, 3, 450}, {Skill.DEMON, 6, 400}, {Skill.DRAGON, 7, 650}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
                {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
                {Skill.TAI_TAO_NANG_LUONG, 1, 15000}
            },
            _5_GIAY//số giây nghỉ
    );

    public static final BossData SUPER_BROLY = new BossData(
            "Super Broly(Trứng đệ mabư)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            3456789, //dame
            new double[][]{{10_000_000D,20_000_000D}}, //hp
            new short[]{294, 295, 296}, //outfit
            new short[]{5, 6, 27, 28, 29, 30, 13, 10, 31, 32, 33, 34, 20, 19, 35, 36, 37, 38}, //map join
            new int[][]{ //skill
                {Skill.DEMON, 3, 450}, {Skill.DEMON, 6, 400}, {Skill.DRAGON, 7, 650}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
                {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
                {Skill.ANTOMIC, 3, 1200}, {Skill.ANTOMIC, 5, 1700}, {Skill.ANTOMIC, 7, 2000},
                {Skill.MASENKO, 1, 800}, {Skill.MASENKO, 5, 1300}, {Skill.MASENKO, 6, 1500},
                {Skill.TAI_TAO_NANG_LUONG, 1, 15000}
            },
            _5_PHUT
    );

    public static final BossData SUPER_BROLY_RED = new BossData(
            "Super Broly Love", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            3456789, //dame
            new double[][]{{600_000_000D}}, //hp
            new short[]{294, 295, 296}, //outfit
            new short[]{5, 6, 27, 28, 29, 30, 13, 10, 31, 32, 33, 34, 20, 19, 35, 36, 37, 38}, //map join
            //            new short[]{14}, //map join
            new int[][]{ //skill
                {Skill.DEMON, 3, 450}, {Skill.DEMON, 6, 400}, {Skill.DRAGON, 7, 650}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
                {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
                {Skill.ANTOMIC, 3, 1200}, {Skill.ANTOMIC, 5, 1700}, {Skill.ANTOMIC, 7, 2000},
                {Skill.MASENKO, 1, 800}, {Skill.MASENKO, 5, 1300}, {Skill.MASENKO, 6, 1500},
                {Skill.TAI_TAO_NANG_LUONG, 1, 15000}
            },
            _5_PHUT
    );
    //--------------------------------------------------------------------------Boss hải tặc

    public static final BossData LUFFY = new BossData(
            "Luffy", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_PERCENT_HP_THOU, //type dame
            Boss.HP_NORMAL, //type hp
            500000, //dame
            new double[][]{{8000000}}, //hp
            new short[]{594, 595, 596}, //outfit
            new short[]{136}, //map join
            new int[][]{ //skill
                {Skill.DEMON, 7, 1000}, {Skill.DEMON, 6, 1000}, {Skill.DEMON, 5, 1000}, {Skill.DEMON, 4, 1000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.ANTOMIC, 7, 5000}
            },
            _30_PHUT, true
    );

    public static final BossData LUFFY_NEW = new BossData(
            "Thích Thịt Chó(Xu bạc)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            3456789, //dame
            new double[][]{{1_000_000_000_000D,5_000_000_000_000D}}, //hp
            new short[]{582, 583, 584}, //outfit
            new short[]{5, 13, 33, 34, 30, 29}, //map join
            new int[][]{ //skill
                {Skill.DEMON, 7, 1000}, {Skill.DEMON, 6, 1000}, {Skill.DEMON, 5, 1000}, {Skill.DEMON, 4, 1000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000}
            },
            _15_PHUT
    );

    public static final BossData ZORO_NEW = new BossData(
            "Thích Đi Lạc(Xu bạc)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            3456789, //dame
            new double[][]{{1_000_000_000_000D,5_000_000_000_000D}}, //hp
            new short[]{585, 586, 587}, //outfit
            new short[]{5, 13, 33, 34, 30, 29}, //map join
            new int[][]{ //skill
                {Skill.DEMON, 7, 1000}, {Skill.DEMON, 6, 1000}, {Skill.DEMON, 5, 1000}, {Skill.DEMON, 4, 1000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000}
            },
            _15_PHUT
    );
    public static final BossData SANJI_NEW = new BossData(
            "Thích Gái Gú(Xu bạc)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            3456789, //dame
            new double[][]{{1_000_000_000_000D,5_000_000_000_000D}}, //hp
            new short[]{588, 589, 590}, //outfit
            new short[]{5, 13, 33, 34, 30, 29}, //map join
            new int[][]{ //skill
                {Skill.DEMON, 7, 1000}, {Skill.DEMON, 6, 1000}, {Skill.DEMON, 5, 1000}, {Skill.DEMON, 4, 1000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000}
            },
            _15_PHUT
    );
    public static final BossData BROOK_NEW = new BossData(
            "Thích Xi Líp(Xu bạc)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            3456789, //dame
            new double[][]{{1_000_000_000_000D,5_000_000_000_000D}}, //hp
            new short[]{591, 592, 593}, //outfit
            new short[]{5, 13, 33, 34, 30, 29}, //map join
            new int[][]{ //skill
                {Skill.DEMON, 7, 1000}, {Skill.DEMON, 6, 1000}, {Skill.DEMON, 5, 1000}, {Skill.DEMON, 4, 1000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000}
            },
            _5_PHUT
    );

    public static final BossData NAMI_NEW = new BossData(
            "Nami vếu to(Xu bạc)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            3456789, //dame
            new double[][]{{1_000_000_000_000D,5_000_000_000_000D}}, //hp
            new short[]{600, 601, 602}, //outfit
            new short[]{5, 13, 33, 34, 30, 29}, //map join
            new int[][]{ //skill
                {Skill.DEMON, 7, 1000}, {Skill.DEMON, 6, 1000}, {Skill.DEMON, 5, 1000}, {Skill.DEMON, 4, 1000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000}
            },
            _5_PHUT
    );
    public static final BossData ROBIN_NEW = new BossData(
            "Robin chân dài(Xu bạc)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            3456789, //dame
            new double[][]{{1_000_000_000_000D,5_000_000_000_000D}}, //hp
            new short[]{603, 604, 605}, //outfit
            new short[]{5, 13, 33, 34, 30, 29}, //map join
            new int[][]{ //skill
                {Skill.DEMON, 7, 1000}, {Skill.DEMON, 6, 1000}, {Skill.DEMON, 5, 1000}, {Skill.DEMON, 4, 1000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000}
            },
            _15_PHUT
    );

    public static final BossData TRUNG_UY_XANH_LO_2 = new BossData(
            "Trung uý Xanh Lơ(xu bạc - spl 5pt sd)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_PERCENT_HP_THOU, //type dame
            Boss.HP_NORMAL, //type hp
            10, //dame
            new double[][]{{1000000000L}}, //hp
            new short[]{135, 136, 137}, //outfit
            new short[]{62}, //map join
            new int[][]{ //skill
                {Skill.DEMON, 1, 520}, {Skill.DEMON, 2, 500}, {Skill.DEMON, 3, 480}, {Skill.DEMON, 4, 460}, {Skill.DEMON, 5, 440}, {Skill.DEMON, 6, 420}, {Skill.DEMON, 7, 400},
                {Skill.KAMEJOKO, 2, 1500},
                {Skill.THAI_DUONG_HA_SAN, 3, 15000}, {Skill.THAI_DUONG_HA_SAN, 7, 30000}
            },
            _30_GIAY
    );

    //--------------------------------------------------------------------------Boss doanh trại
    public static final BossData TRUNG_UY_TRANG = new BossData(
            "Trung uý Trắng(bdkb-xu bạc)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_TIME_PLAYER_WITH_HIGHEST_HP_IN_CLAN, //type dame
            Boss.HP_TIME_PLAYER_WITH_HIGHEST_DAME_IN_CLAN, //type hp
            999999999, //dame
            new double[][]{{250}}, //hp
            new short[]{141, 142, 143}, //outfit
            new short[]{59}, //map join
            new int[][]{ //skill
                {Skill.DEMON, 1, 520}, {Skill.DEMON, 2, 500}, {Skill.DEMON, 3, 480}, {Skill.DEMON, 4, 460}, {Skill.DEMON, 5, 440}, {Skill.DEMON, 6, 420}, {Skill.DEMON, 7, 400}
            },
            _30_GIAY
    );

    public static final BossData TRUNG_UY_XANH_LO = new BossData(
            "Trung uý Xanh Lơ(bdkb-xu bạc)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_TIME_PLAYER_WITH_HIGHEST_HP_IN_CLAN, //type dame
            Boss.HP_TIME_PLAYER_WITH_HIGHEST_DAME_IN_CLAN, //type hp
            20, //dame
            new double[][]{{150}}, //hp
            new short[]{135, 136, 137}, //outfit
            new short[]{62}, //map join
            new int[][]{ //skill
                {Skill.DEMON, 1, 520}, {Skill.DEMON, 2, 500}, {Skill.DEMON, 3, 480}, {Skill.DEMON, 4, 460}, {Skill.DEMON, 5, 440}, {Skill.DEMON, 6, 420}, {Skill.DEMON, 7, 400},
                {Skill.KAMEJOKO, 2, 1500},
                {Skill.THAI_DUONG_HA_SAN, 3, 15000}, {Skill.THAI_DUONG_HA_SAN, 7, 30000}
            },
            _30_GIAY
    );

    public static final BossData TRUNG_UY_THEP = new BossData(
            "Trung uý Thép(bdkb-xu bạc)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_TIME_PLAYER_WITH_HIGHEST_HP_IN_CLAN, //type dame
            Boss.HP_TIME_PLAYER_WITH_HIGHEST_DAME_IN_CLAN, //type hp
            999999999, //dame
            new double[][]{{350}}, //hp
            new short[]{129, 130, 131}, //outfit
            new short[]{55}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 300}, {Skill.DRAGON, 3, 500},
                {Skill.DEMON, 1, 100}, {Skill.DEMON, 2, 300}, {Skill.DEMON, 3, 500},
                {Skill.GALICK, 1, 100},
                {Skill.MASENKO, 1, 100}, {Skill.MASENKO, 2, 100}
            },
            _30_GIAY
    );

    public static final BossData NINJA_AO_TIM = new BossData(
            "Ninja áo tím(bdkb-xu bạc)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_TIME_PLAYER_WITH_HIGHEST_HP_IN_CLAN, //type dame
            Boss.HP_TIME_PLAYER_WITH_HIGHEST_DAME_IN_CLAN, //type hp
            1000, //dame
            new double[][]{{550}}, //hp
            new short[]{123, 124, 125}, //outfit
            new short[]{54}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 100},
                {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
                {Skill.GALICK, 1, 100}
            },
            _30_GIAY
    );

    public static final BossData NINJA_AO_TIM_FAKE = new BossData(
            "Ninja áo tím(bdkb-xu bạc)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_TIME_PLAYER_WITH_HIGHEST_HP_IN_CLAN, //type dame
            Boss.HP_TIME_PLAYER_WITH_HIGHEST_DAME_IN_CLAN, //type hp
            1000, //dame
            new double[][]{{500}}, //hp
            new short[]{123, 124, 125}, //outfit
            new short[]{54}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 100},
                {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
                {Skill.GALICK, 1, 100}
            },
            _30_GIAY
    );

    public static final BossData ROBOT_VE_SI = new BossData(
            "Rôbốt Vệ Sĩ(bdkb-xu bạc)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_TIME_PLAYER_WITH_HIGHEST_HP_IN_CLAN, //type dame
            Boss.HP_TIME_PLAYER_WITH_HIGHEST_DAME_IN_CLAN, //type hp
            999999999, //dame
            new double[][]{{520}}, //hp
            new short[]{138, 139, 140}, //outfit
            new short[]{57}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
                {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
                {Skill.GALICK, 1, 100}
            },
            _30_GIAY
    );

    //--------------------------------------------------------------------------Boss xên ginder
    public static final BossData XEN_BO_HUNG_1 = new BossData(
            "Xên bọ hung 1(Đồ thần - nro 2s 3s)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            120000, //dame
            new double[][]{{500_000_000D}}, //hp
            new short[]{228, 229, 230}, //outfit
            new short[]{100}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
                {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
                {Skill.GALICK, 1, 100}
            },
            _15_PHUT
    );

    public static final BossData XEN_BO_HUNG_2 = new BossData(
            "Xên bọ hung 2(Đồ thần - nro 2s 3s)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            160000, //dame
            new double[][]{{550_000_000D}}, //hp
            new short[]{231, 232, 233}, //outfit
            new short[]{100}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
                {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
                {Skill.GALICK, 1, 100}
            },
            _30_GIAY
    );

    public static final BossData XEN_BO_HUNG_HOAN_THIEN = new BossData(
            "Xên hoàn thiện(Đồ thần - nro 2s 3s)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            200000, //dame
            new double[][]{{600_000_000D}}, //hp
            new short[]{234, 235, 236}, //outfit
            new short[]{100}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
                {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
                {Skill.GALICK, 1, 100}
            },
            _30_GIAY
    );

    //--------------------------------------------------------------------------Boss xên võ đài
    public static final BossData XEN_BO_HUNG = new BossData(
            "Xên bọ hung(Đồ thần - nro 2s 3s)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            200000, //dame
            new double[][]{{700_000_000D}}, //hp
            new short[]{234, 235, 236}, //outfit
            new short[]{103}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
                {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
                {Skill.GALICK, 1, 100},
                {Skill.THAI_DUONG_HA_SAN, 5, 45000},
                {Skill.TU_SAT, 7, 100}
            },
            _1_PHUT, true
    );

    public static final BossData XEN_CON = new BossData(
            "Xên con(Đồ thần - nro 2s 3s)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{500_000_000D}}, //hp
            new short[]{264, 265, 266}, //outfit
            new short[]{103}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
                {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
                {Skill.GALICK, 1, 100}
            },
            _1_PHUT
    );

    public static final BossData SIEU_BO_HUNG = new BossData(
            "Siêu bọ hung(Đồ thần - nro 2s 3s)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            250000, //dame
            new double[][]{{800_000_000D}}, //hp
            new short[]{234, 235, 236}, //outfit
            new short[]{103}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
                {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
                {Skill.GALICK, 1, 100}
            },
            _5_PHUT
    );

    //--------------------------------------------------------------------------Boss nappa
    public static final BossData KUKU = new BossData(
            "Kuku(tv - item c2)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            10000, //dame
            new double[][]{{10_000_000D}}, //hp
            new short[]{159, 160, 161}, //outfit
            new short[]{68, 69, 70, 71, 72}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _5_PHUT
    );
    
    public static final BossData MAP_DAU_DINH = new BossData(
            "Mập đầu đinh(tv - item c2)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            15000, //dame
            new double[][]{{20_000_000D}}, //hp
            new short[]{165, 166, 167}, //outfit
            new short[]{64, 65, 63, 66, 67}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _5_PHUT
    );
    
    public static final BossData RAMBO = new BossData(
            "Rambo(tv - item c2)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            20000, //dame
            new double[][]{{30_000_000D}}, //hp
            new short[]{162, 163, 164}, //outfit
            new short[]{73, 74, 75, 76, 77}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _10_PHUT
    );

    //--------------------------------------------------------------------------Boss cold
    public static final BossData COOLER = new BossData(
            "Cooler(Đồ hd - tv)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{5_000_000_000_000D}}, //hp
            new short[]{317, 318, 319}, //outfit
            new short[]{110}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},},
            _15_PHUT
    );

    public static final BossData COOLER2 = new BossData(
            "Cooler 2(Đồ hd - tv)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{5_000_000_000_000D}}, //hp
            new short[]{320, 321, 322}, //outfit
            new short[]{110}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},},
            _5_GIAY
    );

    //--------------------------------------------------------------------------Tiểu đội sát thủ
    public static final BossData SO4 = new BossData(
            "Số 4", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{40_000_000D}}, //hp
            new short[]{168, 169, 170}, //outfit
            new short[]{82, 83, 79}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _10_GIAY
    );
    public static final BossData SO3 = new BossData(
            "Số 3", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{50_000_000D}}, //hp
            new short[]{174, 175, 176}, //outfit
            new short[]{82, 83, 79}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _10_GIAY, true
    );
    public static final BossData SO2 = new BossData(
            "Số 2", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{60_000_000D}}, //hp
            new short[]{171, 172, 173}, //outfit
            new short[]{82, 83, 79}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _10_GIAY, true
    );
    public static final BossData SO1 = new BossData(
            "Số 1", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{70_000_000D}}, //hp
            new short[]{177, 178, 179}, //outfit
            new short[]{82, 83, 79}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _10_GIAY, true
    );
    public static final BossData TIEU_DOI_TRUONG = new BossData(
            "Tiểu đội trưởng", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{100_000_000D}}, //hp
            new short[]{180, 181, 182}, //outfit
            new short[]{82, 83, 79}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _15_PHUT, true
    );

    //--------------------------------------------------------------------------Fide đại ca
    public static final BossData FIDE_DAI_CA_1 = new BossData(
            "Fide đại ca 1", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{150_000_000D}}, //hp
            new short[]{183, 184, 185}, //outfit
            new short[]{80}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _15_PHUT
    );

    public static final BossData FIDE_DAI_CA_2 = new BossData(
            "Fide đại ca 2", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{200_000_000D}}, //hp
            new short[]{186, 187, 188}, //outfit
            new short[]{80}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _10_GIAY
    );

    public static final BossData FIDE_DAI_CA_3 = new BossData(
            "Fide đại ca 3", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{250_000_000D}}, //hp
            new short[]{189, 190, 191}, //outfit
            new short[]{80}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _10_GIAY
    );

    //--------------------------------------------------------------------------
    public static final BossData ANDROID_19 = new BossData(
            "Android 19(tv - item c2)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{300_000_000D}}, //hp
            new short[]{249, 250, 251}, //outfit
            new short[]{93, 94, 96}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _10_GIAY
    );

    public static final BossData ANDROID_20 = new BossData(
            "Dr.Kôrê(tv - item c2)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{350_000_000D}}, //hp
            new short[]{255, 256, 257}, //outfit
            new short[]{93, 94, 96}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _15_PHUT,
            true
    );
    public static final BossData ANDROID_13 = new BossData(
            "Android 13", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{110_000_000_000D}}, //hp
            new short[]{252, 253, 254}, //outfit
            new short[]{82, 83, 79}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _15_PHUT, true
    );

    public static final BossData ANDROID_14 = new BossData(
            "Android 14", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{120_000_000_000D}}, //hp
            new short[]{246, 247, 248}, //outfit
            new short[]{82, 83, 79}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _15_PHUT, true
    );
    public static final BossData ANDROID_15 = new BossData(
            "Android 15", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{130_000_000_000D}}, //hp
            new short[]{261, 262, 263}, //outfit
            new short[]{82, 83, 79}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _1_PHUT, true
    );
    public static final BossData PIC = new BossData(
            "Pic(Đồ thần - nro 3 4s)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{450_000_000D}}, //hp
            new short[]{237, 238, 239}, //outfit
            new short[]{82, 83, 79}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _1_PHUT, true
    );
    public static final BossData POC = new BossData(
            "Poc(Đồ thần - nro 3 4s)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{400_000_000D}}, //hp
            new short[]{240, 241, 242}, //outfit
            new short[]{82, 83, 79}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _1_PHUT
    );

    public static final BossData KINGKONG = new BossData(
            "King Kong(Đồ thần - nro 3 4s)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{500_000_000D}}, //hp
            new short[]{243, 244, 245}, //outfit
            new short[]{97, 98, 99}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _15_PHUT, true
    );

    //--------------------------------------------------------------------------Boss berus
    public static final BossData WHIS = new BossData(
            "Thần Thiên Sứ(Đồ hd - dcv)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{100_000_000_000D}}, //hp
            new short[]{838, 839, 840}, //outfit
            new short[]{154}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _5_PHUT
    );

    public static final BossData BILL = new BossData(
            "Thần Hủy Diệt(Đồ hd - dcv)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{100_000_000_000D}}, //hp
            new short[]{508, 509, 510}, //outfit
            new short[]{154}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},},
            _10_GIAY
    );
    //--------------------------------------------------------------------------Boss berus
    public static final BossData VADOS = new BossData(
            "Thần Thiên Sứ Vados(Đồ hd - dcv)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{100_000_000_000D}}, //hp
            new short[]{530, 531, 532}, //outfit
            new short[]{166}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}
            },
            _5_PHUT
    );

    public static final BossData CHAMPA = new BossData(
            "Thần Hủy Diệt Champa(Đồ hd - dcv)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{100_000_000_000D}}, //hp
            new short[]{511, 512, 513}, //outfit
            new short[]{166}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},},
            _10_GIAY
    );

    //--------------------------------------------------------------------------Boss CHILLED
    public static final BossData CHILL = new BossData(
            "Chilled(Đồ hd)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{30_000_000_000D}}, //hp
            new short[]{1024, 1025, 1026}, //outfit
            new short[]{163}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},},
            _5_PHUT
    );

    public static final BossData CHILL2 = new BossData(
            "Chilled 2(Đồ hd)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{50_000_000_000D}}, //hp
            new short[]{1021, 1022, 1023}, //outfit
            new short[]{163}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},},
            _10_GIAY
    );

    public static final BossData BULMA = new BossData(
            "Thỏ Hồng Bunma(Nro 3 4 5s)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{100_000_000D,200_000_000D}}, //hp
            new short[]{1095, 1096, 1097}, //outfit
            new short[]{7}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},},
            _15_PHUT
    );

    public static final BossData POCTHO = new BossData(
            "POC Thỏ Đen(Nro 3 4 5s)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{100_000_000D,200_000_000D}}, //hp
            new short[]{1101, 1102, 1103}, //outfit
            new short[]{14}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},},
            _15_PHUT
    );

    public static final BossData CHICHITHO = new BossData(
            "ChiChi Thỏ Đỏ(Nro 3 4 5s)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{100_000_000D,200_000_000D}}, //hp
            new short[]{1098, 1099, 1100}, //outfit
            new short[]{0}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.GALICK, 1, 1000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},},
            _15_PHUT
    );

    public static final BossData SUPER_BLACK_ROSE = new BossData(
            "Super Black Rose(vpnv - đồ thần)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{2_500_000_000D}}, //hp
            new short[]{553, 880, 881}, //outfit
            new short[]{105, 106, 107, 108, 109, 110}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 7, 5000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.ANTOMIC, 7, 2000},
                {Skill.GALICK, 1, 1000}
            },
            _5_PHUT
    );
    
    public static final BossData ZAMAS_TOI_THUONG = new BossData(
            "Thần Zamas Tối Thượng(vpnv - đồ thần)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{2_000_000_000D}}, //hp
            new short[]{903, 904, 905}, //outfit
            new short[]{105, 106, 107, 108, 109, 110}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 7, 3000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.ANTOMIC, 7, 1000},
                {Skill.GALICK, 1, 1000}
            },
            _5_PHUT
    );

    public static final BossData BONG_BANG = new BossData(
            "Bông Băng Vàng(Ngọc rồng băng)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{200_000_000_000D}}, //hp
            new short[]{1451, 1452, 1453}, //outfit
            new short[]{211}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 7, 3000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.ANTOMIC, 7, 1000},
                {Skill.GALICK, 1, 1000}
            },
            _5_PHUT
    );

    public static final BossData SOI_BASIL = new BossData(
            "Sói Basil(Cải trang - nro vip)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{500_000_000_000D}}, //hp
            new short[]{745, 746, 747}, //outfit
            new short[]{212}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.ANTOMIC, 7, 1000},
                {Skill.GALICK, 1, 1000}
            },
            _5_PHUT
    );
    public static final BossData MABU_TUONG_LAI = new BossData(
            "Mabu(20% Rơi Trứng đệ mabu)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1500000, //dame
            new double[][]{{1_000_000_000D}}, //hp
            new short[]{950,951,952}, //outfit
            new short[]{92,93,94,95,96,97,98,99}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.ANTOMIC, 7, 1000},
                {Skill.GALICK, 1, 1000}
            },
            _15_PHUT
    );
            
    public static final BossData ITACHI = new BossData(
            "Itachi(5% Rơi Trứng đệ itachi)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{10_000_000_000_000D}}, //hp
            new short[]{1472, 1473, 1474}, //outfit
            new short[]{213}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.ANTOMIC, 7, 1000},
                {Skill.GALICK, 1, 1000}
            },
            _1_GIO
    );

    public static final BossData WHIS_DETU = new BossData(
            "Whis(15% Rơi Trứng đệ berus)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{400_000_000_000D}}, //hp
            new short[]{838, 839, 840}, //outfit
            new short[]{92, 93, 94, 96, 97, 98, 99, 100}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                {Skill.ANTOMIC, 1, 1000}, {Skill.ANTOMIC, 2, 1200}, {Skill.ANTOMIC, 4, 1500}, {Skill.ANTOMIC, 5, 1700},
                {Skill.MASENKO, 1, 1000}, {Skill.MASENKO, 2, 1200}, {Skill.MASENKO, 4, 1500}, {Skill.MASENKO, 5, 1700},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.GALICK, 1, 1000}
            },
            _5_PHUT
    );
    
    public static final BossData ZENO = new BossData(
            "Thần Zeno(10% Rơi Trứng đệ Zeno)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{1_000_000_000_000D}}, //hp
            new short[]{1213, 1214, 1215}, //outfit
            new short[]{105, 106, 107, 108, 109, 110}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 6, 300},
                {Skill.ANTOMIC, 5, 1700},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.MASENKO, 5, 1700}
            },
            _5_PHUT
    );
    public static final BossData RONG_DEN = new BossData(
            "Rồng Đen(Ngọc rồng vip 4pt)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{1_690_000_000_000D}}, //hp
            new short[]{1978, 1979, 1980}, //outfit
            new short[]{92, 93, 94}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 3, 500}, {Skill.LIEN_HOAN, 4, 1000}, {Skill.LIEN_HOAN, 5, 1500},
                {Skill.KAMEJOKO, 4, 3000}, {Skill.KAMEJOKO, 5, 4000}, {Skill.KAMEJOKO, 7, 7000},
                {Skill.DICH_CHUYEN_TUC_THOI, 4, 10000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},},
            _5_PHUT
    );
    public static final BossData GOKU_SUPER = new BossData(
            "Goku Super Saiyan 4(Đá Pháp Sư)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{1_000_000_000_000_000D}}, //hp
            new short[]{1074, 1075, 1076}, //outfit
            new short[]{155}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 7, 500},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.ANTOMIC, 7, 1000},
                {Skill.GALICK, 1, 1000}
            },
            _10_PHUT
    );

    public static final BossData KAIDO = new BossData(
            "Hải tặc Kaido(5% Rơi Trứng đệ kaido)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{50_000_000_000_000D}}, //hp
            new short[]{1409, 1410, 1411}, //outfit
            new short[]{218}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _30_PHUT
    );

    public static final BossData TIEN_HAC_AM = new BossData(
            "Tiên hắc ám(5% Rơi Trứng đệ tiên)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{200_000_000_000_000D}}, //hp
            new short[]{1469, 1470, 1471}, //outfit
            new short[]{217}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _30_PHUT
    );
//
//    public static final BossData NHAT_VI = new BossData(
//            "Nhất Vĩ(Rơi thịt, Tv)", //name
//            ConstPlayer.TRAI_DAT, //gender
//            Boss.DAME_NORMAL, //type dame
//            Boss.HP_NORMAL, //type hp
//            150000, //dame
//            new double[][]{{1_000_000_000D,2_000_000_000D}}, //hp
//            new short[]{1625, 1626, 1627}, //outfit
//            new short[]{0,7,14,20}, //map join
//            new int[][]{ //skill
//                {Skill.LIEN_HOAN, 5, 300},
//                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
//                {Skill.KAMEJOKO, 7, 1000}
//            },
//            _30_PHUT
//    );
//    public static final BossData NHI_VI = new BossData(
//            "Nhị vĩ(Rơi thịt, Tv)", //name
//            ConstPlayer.TRAI_DAT, //gender
//            Boss.DAME_NORMAL, //type dame
//            Boss.HP_NORMAL, //type hp
//            150000, //dame
//            new double[][]{{1_000_000_000D,2_000_000_000D}}, //hp
//            new short[]{1628, 1629, 1630}, //outfit
//            new short[]{0,7,14,20}, //map join
//            new int[][]{ //skill
//                {Skill.LIEN_HOAN, 5, 300},
//                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
//                {Skill.KAMEJOKO, 7, 1000}
//            },
//            _10_GIAY
//    );
//    public static final BossData TAM_VI = new BossData(
//            "Tam vĩ(Rơi thịt, Tv)", //name
//            ConstPlayer.TRAI_DAT, //gender
//            Boss.DAME_NORMAL, //type dame
//            Boss.HP_NORMAL, //type hp
//            150000, //dame
//            new double[][]{{1_000_000_000D,2_000_000_000D}}, //hp
//            new short[]{1631, 1632, 1633}, //outfit
//            new short[]{0,7,14,20}, //map join
//            new int[][]{ //skill
//                {Skill.LIEN_HOAN, 5, 300},
//                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
//                {Skill.KAMEJOKO, 7, 1000}
//            },
//            _10_GIAY
//    );
//    public static final BossData TU_VI = new BossData(
//            "Tứ vĩ(Rơi thịt, Tv)", //name
//            ConstPlayer.TRAI_DAT, //gender
//            Boss.DAME_NORMAL, //type dame
//            Boss.HP_NORMAL, //type hp
//            150000, //dame
//            new double[][]{{1_000_000_000D,2_000_000_000D}}, //hp
//            new short[]{1634, 1635, 1636}, //outfit
//            new short[]{0,7,14,20}, //map join
//            new int[][]{ //skill
//                {Skill.LIEN_HOAN, 5, 300},
//                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
//                {Skill.KAMEJOKO, 7, 1000}
//            },
//            _10_GIAY
//    );
//    public static final BossData NGU_VI = new BossData(
//            "Ngũ vĩ(Rơi thịt, Tv)", //name
//            ConstPlayer.TRAI_DAT, //gender
//            Boss.DAME_NORMAL, //type dame
//            Boss.HP_NORMAL, //type hp
//            150000, //dame
//            new double[][]{{1_000_000_000D,2_000_000_000D}}, //hp
//            new short[]{1637, 1638, 1639}, //outfit
//            new short[]{0,7,14,20}, //map join
//            new int[][]{ //skill
//                {Skill.LIEN_HOAN, 5, 300},
//                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
//                {Skill.KAMEJOKO, 7, 1000}
//            },
//            _10_GIAY
//    );
//    public static final BossData LUC_VI = new BossData(
//            "Lục vĩ(Rơi thịt, Tv)", //name
//            ConstPlayer.TRAI_DAT, //gender
//            Boss.DAME_NORMAL, //type dame
//            Boss.HP_NORMAL, //type hp
//            150000, //dame
//            new double[][]{{1_000_000_000D,2_000_000_000D}}, //hp
//            new short[]{1640, 1641, 1642}, //outfit
//            new short[]{0,7,14,20}, //map join
//            new int[][]{ //skill
//                {Skill.LIEN_HOAN, 5, 300},
//                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
//                {Skill.KAMEJOKO, 7, 1000}
//            },
//            _10_GIAY
//    );
//    public static final BossData THAT_VI = new BossData(
//            "Thất vĩ(Rơi thịt, Tv)", //name
//            ConstPlayer.TRAI_DAT, //gender
//            Boss.DAME_NORMAL, //type dame
//            Boss.HP_NORMAL, //type hp
//            150000, //dame
//            new double[][]{{1_000_000_000D,2_000_000_000D}}, //hp
//            new short[]{1643, 1644, 1645}, //outfit
//            new short[]{0,7,14,20}, //map join
//            new int[][]{ //skill
//                {Skill.LIEN_HOAN, 5, 300},
//                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
//                {Skill.KAMEJOKO, 7, 1000}
//            },
//            _10_GIAY
//    );
//    public static final BossData BAT_VI = new BossData(
//            "Bát vĩ(Rơi thịt, Tv)", //name
//            ConstPlayer.TRAI_DAT, //gender
//            Boss.DAME_NORMAL, //type dame
//            Boss.HP_NORMAL, //type hp
//            150000, //dame
//            new double[][]{{1_000_000_000D,2_000_000_000D}}, //hp
//            new short[]{1646, 1647, 1648}, //outfit
//            new short[]{0,7,14,20}, //map join
//            new int[][]{ //skill
//                {Skill.LIEN_HOAN, 5, 300},
//                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
//                {Skill.KAMEJOKO, 7, 1000}
//            },
//            _10_GIAY
//    );
//
//    public static final BossData CUU_VI = new BossData(
//            "Cửu vĩ(Rơi thịt, Tv)", //name
//            ConstPlayer.TRAI_DAT, //gender
//            Boss.DAME_NORMAL, //type dame
//            Boss.HP_NORMAL, //type hp
//            150000, //dame
//            new double[][]{{1_000_000_000D,2_000_000_000D}}, //hp
//            new short[]{1544, 1545, 1546}, //outfit
//            new short[]{0,7,14,20}, //map join
//            new int[][]{ //skill
//                {Skill.LIEN_HOAN, 5, 300},
//                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
//                {Skill.KAMEJOKO, 7, 1000}
//            },
//            _10_GIAY
//    );
//    
//    public static final BossData THAP_VI = new BossData(
//            "Thập vĩ(Rơi thịt, Tv)", //name
//            ConstPlayer.TRAI_DAT, //gender
//            Boss.DAME_NORMAL, //type dame
//            Boss.HP_NORMAL, //type hp
//            150000, //dame
//            new double[][]{{1_000_000_000D,2_000_000_000D}}, //hp
//            new short[]{1649, 1650, 1651}, //outfit
//            new short[]{0,7,14,20}, //map join
//            new int[][]{ //skill
//                {Skill.LIEN_HOAN, 5, 300},
//                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
//                {Skill.KAMEJOKO, 7, 1000}
//            },
//            _10_GIAY
//    );
    public static final BossData ALONG = new BossData(
            "Along(Đồ thiên sứ)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{1_000_000_000D,2_000_000_000D}}, //hp
            new short[]{1418, 1419, 1420}, //outfit
            new short[]{214, 215}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );

    public static final BossData MIHALK = new BossData(
            "Mihalk(Đồ thiên sứ)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{1_500_000_000_000D}}, //hp
            new short[]{1421, 1422, 1423}, //outfit
            new short[]{214, 215}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );

    public static final BossData LUFFY_THAN_NIKA = new BossData(
            "Lufy Thần Nika(Đồ thiên sứ)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{2_000_000_000_000D}}, //hp
            new short[]{891, 892, 893}, //outfit
            new short[]{214, 215}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );

    public static final BossData BLACKGOKU = new BossData(
            "Black Goku(vpnv - đồ thần)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{1_000_000_000D}}, //hp
            new short[]{550, 551, 552}, //outfit
            new short[]{92, 93, 94}, //map join
            new int[][]{ //skill
                {Skill.DEMON, 3, 450}, {Skill.DEMON, 6, 400}, {Skill.DRAGON, 7, 650}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
                {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
                {Skill.ANTOMIC, 3, 1200}, {Skill.ANTOMIC, 5, 1700}, {Skill.ANTOMIC, 7, 2000},
                {Skill.MASENKO, 1, 800}, {Skill.MASENKO, 5, 1300}, {Skill.MASENKO, 6, 1500},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}
            },
            _5_PHUT
    );

    public static final BossData SUPERBLACKGOKU = new BossData(
            "SBlack Goku(vpnv - đồ thần)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{1_500_000_000D}}, //hp
            new short[]{553, 551, 552}, //outfit
            new short[]{92, 93, 94}, //map join
            new int[][]{ //skill
                {Skill.DEMON, 3, 450}, {Skill.DEMON, 6, 400}, {Skill.DRAGON, 7, 650}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
                {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
                {Skill.ANTOMIC, 3, 1200}, {Skill.ANTOMIC, 5, 1700}, {Skill.ANTOMIC, 7, 2000},
                {Skill.MASENKO, 1, 800}, {Skill.MASENKO, 5, 1300}, {Skill.MASENKO, 6, 1500},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}
            },
            _1_PHUT
    );

    public static final BossData HOA_HONG = BossData.builder()
            .name("Hoa Hồng")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(0)
            .hp(new double[][]{{100}})
            .outfit(new short[]{706, 707, 708})
            .mapJoin(new short[]{})
            .skillTemp(new int[][]{})
            .secondsRest(_1_PHUT)
            .build();

    public static final BossData SANTA_CLAUS = BossData.builder()
            .name("Ông già Nôen")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(0)
            .hp(new double[][]{{66_666_666_666L}})
            .outfit(new short[]{763, 764, 765})
            .mapJoin(new short[]{104})
            .skillTemp(new int[][]{
        {Skill.THAI_DUONG_HA_SAN, 5, 20000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}
    })
            .secondsRest(_30_PHUT)
            .build();

    public static final BossData QILIN = BossData.builder()
            .name("Lân thần tài")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(0)
            .hp(new double[][]{{66_666_666_666L}})
            .outfit(new short[]{763, 764, 765})
            .mapJoin(new short[]{104})
            .skillTemp(new int[][]{
        {Skill.THAI_DUONG_HA_SAN, 5, 20000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}
    })
            .secondsRest(_30_PHUT)
            .build();

    public static final BossData MABU_MAP = BossData.builder()
            .name("Mabư")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(750000)
            .hp(new double[][]{{50_000_000_000_000D}})
            .outfit(new short[]{297, 298, 299})
            .mapJoin(new short[]{})
            .skillTemp(new int[][]{
        {Skill.DEMON, 3, 450}, {Skill.DEMON, 6, 400}, {Skill.DRAGON, 7, 650}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
        {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
        {Skill.ANTOMIC, 3, 1200}, {Skill.ANTOMIC, 5, 1700}, {Skill.ANTOMIC, 7, 2000},
        {Skill.MASENKO, 1, 800}, {Skill.MASENKO, 5, 1300}, {Skill.MASENKO, 6, 1500},
        {Skill.THAI_DUONG_HA_SAN, 5, 20000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}
    })
            .secondsRest(_5_PHUT)
            .build();

    public static final BossData MABU_MAP2 = BossData.builder()
            .name("Bư Mập")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(750000)
            .hp(new double[][]{{75_000_000_000_000D}})
            .outfit(new short[]{297, 298, 299})
            .mapJoin(new short[]{})
            .skillTemp(new int[][]{
        {Skill.DEMON, 3, 450}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
        {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
        {Skill.ANTOMIC, 3, 1200},
        {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}
    })
            .secondsRest(_10_GIAY)
            .build();

    public static final BossData SUPER_BU = BossData.builder()
            .name("Super Bư")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(0)
            .hp(new double[][]{{100_000_000_000_000D}})
            .outfit(new short[]{427, 428, 429})
            .mapJoin(new short[]{127})
            .skillTemp(new int[][]{
        {Skill.DEMON, 7, 3000},
        {Skill.THAI_DUONG_HA_SAN, 5, 20000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}
    })
            .secondsRest(_10_GIAY)
            .build();

    public static final BossData KID_BU = BossData.builder()
            .name("Kid Bư")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(1200000)
            .hp(new double[][]{{125_000_000_000_000D}})
            .outfit(new short[]{439, 440, 441})
            .mapJoin(new short[]{})
            .skillTemp(new int[][]{
        {Skill.DEMON, 3, 450}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
        {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
        {Skill.ANTOMIC, 3, 1200},
        {Skill.THAI_DUONG_HA_SAN, 5, 20000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}
    })
            .secondsRest(_10_GIAY)
            .build();

    public static final BossData BU_TENK = BossData.builder()
            .name("Bư Tênk")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(0)
            .hp(new double[][]{{150_000_000_000_000D}})
            .outfit(new short[]{439, 440, 441})
            .mapJoin(new short[]{127})
            .skillTemp(new int[][]{
        {Skill.DEMON, 7, 3000},
        {Skill.THAI_DUONG_HA_SAN, 5, 20000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}
    })
            .secondsRest(_10_GIAY)
            .build();

    public static final BossData BU_HAN = BossData.builder()
            .name("Bư Han")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(1200000)
            .hp(new double[][]{{200_000_000_000_000D}})
            .outfit(new short[]{427, 428, 429})
            .mapJoin(new short[]{})
            .skillTemp(new int[][]{
        {Skill.DEMON, 3, 450}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
        {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
        {Skill.ANTOMIC, 3, 1200},
        {Skill.QUA_CAU_KENH_KHI, 7, 1200},
        {Skill.THAI_DUONG_HA_SAN, 5, 20000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}
    })
            .secondsRest(_10_GIAY)
            .build();

    public static final BossData DRABULA_TANG1 = BossData.builder()
            .name("Drabula")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(250000)
            .hp(new double[][]{{10_000_000_000_000D}})
            .outfit(new short[]{418, 419, 420})
            .mapJoin(new short[]{114})
            .skillTemp(new int[][]{
        {Skill.DEMON, 3, 450}, {Skill.DEMON, 6, 400}, {Skill.DRAGON, 7, 650}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
        {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
        {Skill.ANTOMIC, 3, 1200}, {Skill.ANTOMIC, 5, 1700}, {Skill.ANTOMIC, 7, 2000},
        {Skill.MASENKO, 1, 800}, {Skill.MASENKO, 5, 1300}, {Skill.MASENKO, 6, 1500},
        {Skill.THAI_DUONG_HA_SAN, 5, 20000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}
    })
            .secondsRest(_3_PHUT)
            .build();

    public static final BossData DRABULA_TANG5 = BossData.builder()
            .name("Drabula")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(500000)
            .hp(new double[][]{{50_000_000_000_000D}})
            .outfit(new short[]{418, 419, 420})
            .mapJoin(new short[]{119})
            .skillTemp(new int[][]{
        {Skill.DEMON, 3, 450}, {Skill.DEMON, 6, 400}, {Skill.DRAGON, 7, 650}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
        {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
        {Skill.ANTOMIC, 3, 1200}, {Skill.ANTOMIC, 5, 1700}, {Skill.ANTOMIC, 7, 2000},
        {Skill.MASENKO, 1, 800}, {Skill.MASENKO, 5, 1300}, {Skill.MASENKO, 6, 1500},
        {Skill.THAI_DUONG_HA_SAN, 5, 20000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}
    })
            .secondsRest(_3_PHUT)
            .build();

    public static final BossData DRABULA_TANG6 = BossData.builder()
            .name("Drabula")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(500000)
            .hp(new double[][]{{100_000_000_000_000D}})
            .outfit(new short[]{418, 419, 420})
            .mapJoin(new short[]{120})
            .skillTemp(new int[][]{
        {Skill.DEMON, 3, 450}, {Skill.DEMON, 6, 400}, {Skill.DRAGON, 7, 650}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
        {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
        {Skill.ANTOMIC, 3, 1200}, {Skill.ANTOMIC, 5, 1700}, {Skill.ANTOMIC, 7, 2000},
        {Skill.MASENKO, 1, 800}, {Skill.MASENKO, 5, 1300}, {Skill.MASENKO, 6, 1500},
        {Skill.THAI_DUONG_HA_SAN, 5, 20000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}
    })
            .secondsRest(_3_PHUT)
            .build();

    public static final BossData BUIBUI_TANG2 = BossData.builder()
            .name("BuiBui")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(250000)
            .hp(new double[][]{{20_000_000_000_000D}})
            .outfit(new short[]{451, 452, 453})
            .mapJoin(new short[]{115})
            .skillTemp(new int[][]{
        {Skill.DEMON, 3, 450}, {Skill.DEMON, 6, 400}, {Skill.DRAGON, 7, 650}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
        {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
        {Skill.ANTOMIC, 3, 1200}, {Skill.ANTOMIC, 5, 1700}, {Skill.ANTOMIC, 7, 2000},
        {Skill.MASENKO, 1, 800}, {Skill.MASENKO, 5, 1300}, {Skill.MASENKO, 6, 1500},
        {Skill.THAI_DUONG_HA_SAN, 5, 20000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}
    })
            .secondsRest(_3_PHUT)
            .build();

    public static final BossData BUIBUI_TANG3 = BossData.builder()
            .name("BuiBui")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(250000)
            .hp(new double[][]{{30_000_000_000_000D}})
            .outfit(new short[]{451, 452, 453})
            .mapJoin(new short[]{117})
            .skillTemp(new int[][]{
        {Skill.DEMON, 3, 450}, {Skill.DEMON, 6, 400}, {Skill.DRAGON, 7, 650}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
        {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
        {Skill.ANTOMIC, 3, 1200}, {Skill.ANTOMIC, 5, 1700}, {Skill.ANTOMIC, 7, 2000},
        {Skill.MASENKO, 1, 800}, {Skill.MASENKO, 5, 1300}, {Skill.MASENKO, 6, 1500},
        {Skill.THAI_DUONG_HA_SAN, 5, 20000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}
    })
            .secondsRest(_3_PHUT)
            .build();

    public static final BossData CALICH_TANG5 = BossData.builder()
            .name("Ca Đíc")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(350000)
            .hp(new double[][]{{50_000_000_000_000D}})
            .outfit(new short[]{103, 16, 17})
            .mapJoin(new short[]{119})
            .skillTemp(new int[][]{
        {Skill.DEMON, 3, 450}, {Skill.DEMON, 6, 400}, {Skill.DRAGON, 7, 650}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
        {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
        {Skill.ANTOMIC, 3, 1200}, {Skill.ANTOMIC, 5, 1700}, {Skill.ANTOMIC, 7, 2000},
        {Skill.MASENKO, 1, 800}, {Skill.MASENKO, 5, 1300}, {Skill.MASENKO, 6, 1500},
        {Skill.THAI_DUONG_HA_SAN, 5, 20000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}
    })
            .secondsRest(_3_PHUT)
            .build();

    public static final BossData GOKU_TANG5 = BossData.builder()
            .name("Gôku")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(350000)
            .hp(new double[][]{{50_000_000_000_000D}})
            .outfit(new short[]{101, 1, 2})
            .mapJoin(new short[]{119})
            .skillTemp(new int[][]{
        {Skill.DEMON, 3, 450}, {Skill.DEMON, 6, 400}, {Skill.DRAGON, 7, 650}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
        {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
        {Skill.ANTOMIC, 3, 1200}, {Skill.ANTOMIC, 5, 1700}, {Skill.ANTOMIC, 7, 2000},
        {Skill.MASENKO, 1, 800}, {Skill.MASENKO, 5, 1300}, {Skill.MASENKO, 6, 1500},
        {Skill.THAI_DUONG_HA_SAN, 5, 20000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}
    })
            .secondsRest(_3_PHUT)
            .build();

    public static final BossData YACON_TANG4 = BossData.builder()
            .name("Yacôn")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(350000)
            .hp(new double[][]{{40_000_000_000_000D}})
            .outfit(new short[]{415, 416, 417})
            .mapJoin(new short[]{118})
            .skillTemp(new int[][]{
        {Skill.DEMON, 3, 450}, {Skill.DEMON, 6, 400}, {Skill.DRAGON, 7, 650}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
        {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
        {Skill.ANTOMIC, 3, 1200}, {Skill.ANTOMIC, 5, 1700}, {Skill.ANTOMIC, 7, 2000},
        {Skill.MASENKO, 1, 800}, {Skill.MASENKO, 5, 1300}, {Skill.MASENKO, 6, 1500},
        {Skill.THAI_DUONG_HA_SAN, 5, 20000}, {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}
    })
            .secondsRest(_3_PHUT)
            .build();

    public static final BossData XEN_MAX = BossData.builder()
            .name("Xên Max")
            .gender(ConstPlayer.XAYDA)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(350000)
            .hp(new double[][]{{750_000_000_000D}})
            .outfit(new short[]{2000, 2001, 2002})
            .mapJoin(new short[]{99})
            .skillTemp(new int[][]{
        {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
        {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
        {Skill.GALICK, 1, 100}
    })
            .secondsRest(_15_PHUT)
            .build();
    public static final BossData XEN_MAX1 = new BossData(
            "Xên Max", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            5000000, //dame
            new double[][]{{750_000_000_000D}}, //hp
            new short[]{2000, 2001, 2002}, //outfit
            new short[]{99}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
                {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
                {Skill.GALICK, 1, 100}
            },
            _15_PHUT
    );

    public static final BossData SOI_HEC_QUYN = BossData.builder()
            .name("Sói Hẹc Quyn")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(40000)
            .hp(new double[][]{{100_000_000_000D}})
            .outfit(new short[]{394, 395, 396})
            .mapJoin(new short[]{129})
            .skillTemp(new int[][]{
        {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
        {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
        {Skill.GALICK, 1, 100}
    })
            .secondsRest(_1_PHUT)
            .build();

    public static final BossData O_DO = BossData.builder()
            .name("Ở Dơ")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(50000)
            .hp(new double[][]{{200_000_000_000D}})
            .outfit(new short[]{400, 401, 402})
            .mapJoin(new short[]{129})
            .skillTemp(new int[][]{
        {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
        {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
        {Skill.GALICK, 1, 100}
    })
            .secondsRest(_1_PHUT)
            .build();

    public static final BossData XINBATO = BossData.builder()
            .name("Xinbatô")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(60000)
            .hp(new double[][]{{300_000_000_000D}})
            .outfit(new short[]{359, 360, 361})
            .mapJoin(new short[]{129})
            .skillTemp(new int[][]{
        {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
        {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
        {Skill.GALICK, 1, 100}
    })
            .secondsRest(_1_PHUT)
            .build();

    public static final BossData CHA_PA = BossData.builder()
            .name("Cha pa")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(65000)
            .hp(new double[][]{{400_000_000_000D}})
            .outfit(new short[]{362, 363, 364})
            .mapJoin(new short[]{129})
            .skillTemp(new int[][]{
        {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
        {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
        {Skill.GALICK, 1, 100}
    })
            .secondsRest(_1_PHUT)
            .build();

    public static final BossData PON_PUT = BossData.builder()
            .name("Pon put")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(70000)
            .hp(new double[][]{{500_000_000_000D}})
            .outfit(new short[]{365, 366, 367})
            .mapJoin(new short[]{129})
            .skillTemp(new int[][]{
        {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
        {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
        {Skill.GALICK, 1, 100}
    })
            .secondsRest(_1_PHUT)
            .build();

    public static final BossData CHAN_XU = BossData.builder()
            .name("Chan xư")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(75000)
            .hp(new double[][]{{600_000_000_000D}})
            .outfit(new short[]{371, 372, 373})
            .mapJoin(new short[]{129})
            .skillTemp(new int[][]{
        {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
        {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
        {Skill.GALICK, 1, 100}
    })
            .secondsRest(_1_PHUT)
            .build();

    public static final BossData TAU_PAY_PAY = BossData.builder()
            .name("Tàu Pảy Pảy")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(80000)
            .hp(new double[][]{{800_000_000_000D}})
            .outfit(new short[]{92, 93, 94})
            .mapJoin(new short[]{129})
            .skillTemp(new int[][]{
        {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
        {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
        {Skill.GALICK, 1, 100}
    })
            .secondsRest(_1_PHUT)
            .build();

    public static final BossData YAMCHA = BossData.builder()
            .name("Yamcha")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(90000)
            .hp(new double[][]{{1_000_000_000_000D}})
            .outfit(new short[]{374, 375, 376})
            .mapJoin(new short[]{129})
            .skillTemp(new int[][]{
        {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
        {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
        {Skill.GALICK, 1, 100}
    })
            .secondsRest(_1_PHUT)
            .build();

    public static final BossData JACKY_CHUN = BossData.builder()
            .name("Jacky Chun")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(100000)
            .hp(new double[][]{{5_000_000_000_000D}})
            .outfit(new short[]{356, 357, 358})
            .mapJoin(new short[]{129})
            .skillTemp(new int[][]{
        {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
        {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
        {Skill.GALICK, 1, 100}
    })
            .secondsRest(_1_PHUT)
            .build();

    public static final BossData THIEN_XIN_HANG = BossData.builder()
            .name("Thiên Xin Hăng")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(150000)
            .hp(new double[][]{{10_000_000_000_000D}})
            .outfit(new short[]{368, 369, 370})
            .mapJoin(new short[]{129})
            .skillTemp(new int[][]{
        {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
        {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
        {Skill.THAI_DUONG_HA_SAN, 1, 15000}
    })
            .secondsRest(_1_PHUT)
            .build();

    public static final BossData THIEN_XIN_HANG_CLONE = BossData.builder()
            .name("Thiên Xin Hăng")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(75000)
            .hp(new double[][]{{2_000_000_000_000D}})
            .outfit(new short[]{368, 369, 370})
            .mapJoin(new short[]{129})
            .skillTemp(new int[][]{
        {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
        {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
        {Skill.THAI_DUONG_HA_SAN, 1, 15000}
    })
            .secondsRest(_1_PHUT)
            .build();
    public static final BossData LIU_LIU = BossData.builder()
            .name("Liu Liu")
            .gender(ConstPlayer.TRAI_DAT)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(250000)
            .hp(new double[][]{{15_000_000_000_000D}})
            .outfit(new short[]{397, 398, 399})
            .mapJoin(new short[]{129})
            .skillTemp(new int[][]{
        {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
        {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
        {Skill.GALICK, 1, 100}
    })
            .secondsRest(_1_PHUT)
            .build();

    public static final BossData NGO_KHONG = new BossData(
            "Tôn Ngộ Không(1pt trứng đệ - ct đệ)", //name
            ConstPlayer.XAYDA, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            50000, //dame
            new double[][]{{1_500_000_000D}}, //hp
            new short[]{462, 463, 464}, //outfit
            new short[]{124}, //map join
            new int[][]{ //skill
                {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
                {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
                {Skill.GALICK, 1, 100}
            },
            _15_PHUT
    );

    public static final BossData BAT_GIOI = BossData.builder()
            .name("Chư Bát Giới(ct đệ ngon)")
            .gender(ConstPlayer.XAYDA)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(50000)
            .hp(new double[][]{{500_000_000D}})
            .outfit(new short[]{465, 466, 464})
            .mapJoin(new short[]{124})
            .skillTemp(new int[][]{
        {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
        {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
        {Skill.GALICK, 1, 100}
    })
            .secondsRest(_15_PHUT)
            .build();

    public static final BossData FIDEGOLD = BossData.builder()
            .name("Fide Vàng")
            .gender(ConstPlayer.XAYDA)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(500000)
            .hp(new double[][]{{690_000_000_000D}})
            .outfit(new short[]{502, 503, 504})
            .mapJoin(new short[]{6})
            .skillTemp(new int[][]{
        {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
        {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
        {Skill.GALICK, 1, 100}
    })
            .secondsRest(_15_PHUT)
            .build();

    public static final BossData CUMBER = BossData.builder()
            .name("Cumber(Mảnh tinh ấn)")
            .gender(ConstPlayer.XAYDA)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(500000)
            .hp(new double[][]{{500_000_000_000_000D}})
            .outfit(new short[]{2024, 2025, 2026})
            .mapJoin(new short[]{155})
            .skillTemp(new int[][]{
        {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
        {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
        {Skill.GALICK, 1, 100}
    })
            .secondsRest(_10_PHUT)
            .build();

    public static final BossData CUMBER2 = BossData.builder()
            .name("Super Cumber(Mảnh tinh ấn)")
            .gender(ConstPlayer.XAYDA)
            .typeDame(Boss.DAME_NORMAL)
            .typeHp(Boss.HP_NORMAL)
            .dame(500000)
            .hp(new double[][]{{700_000_000_000_000D}})
            .outfit(new short[]{2027, 2028, 2029})
            .mapJoin(new short[]{155})
            .skillTemp(new int[][]{
        {Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700},
        {Skill.KAMEJOKO, 1, 1000}, {Skill.KAMEJOKO, 2, 1200}, {Skill.KAMEJOKO, 5, 1500}, {Skill.KAMEJOKO, 7, 1700},
        {Skill.GALICK, 1, 100}
    })
            .secondsRest(_30_GIAY)
            .build();

    public static final BossData SOI_3_DAU = new BossData(
            "Sói Địa Ngục", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000, //dame
            new double[][]{{99_000_000_000_000_000D}}, //hp
            new short[]{1723, 1726, 1727}, //outfit
            new short[]{211}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000},{Skill.THOI_MIEN, 7, 10000},{Skill.THOI_MIEN, 6, 15000},{Skill.THOI_MIEN, 4, 15000},{Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );

    public static final BossData DAI_THANH = new BossData(
            "Thân Xác Đại Thánh", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{99_000_000_000_000_000D}}, //hp
            new short[]{1877, 1879, 1879}, //outfit
            new short[]{212}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000},{Skill.THOI_MIEN, 7, 10000},{Skill.THOI_MIEN, 6, 15000},{Skill.THOI_MIEN, 4, 15000},{Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _10_PHUT
    );

    public static final BossData GOGETA = new BossData(
            "GOGETA SSJ", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            100000000, //dame
            new double[][]{{6_960_000_000_000_000D}}, //hp
            new short[]{1758,1761,1762}, //outfit
            new short[]{157,158,159,160}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000},{Skill.THOI_MIEN, 7, 10000},{Skill.THOI_MIEN, 6, 15000},{Skill.THOI_MIEN, 4, 15000},{Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );

    public static final BossData GIAM_NGUC = new BossData(
            "Giám Ngục", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            100000000, //dame
            new double[][]{{99_000_000_000_000_000D}}, //hp
            new short[]{1831,1832,1833}, //outfit
            new short[]{211,212,213,166}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000},{Skill.THOI_MIEN, 7, 10000},{Skill.THOI_MIEN, 6, 15000},{Skill.THOI_MIEN, 4, 15000},{Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );

    public static final BossData ROBOT_HUYDIET = new BossData(
            "Robot Huỷ Diệt", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            100000000, //dame
            new double[][]{{99_000_000_000_000_000D}}, //hp
            new short[]{1720,1721,1722}, //outfit
            new short[]{92,93,94,96,97,98,99}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000},{Skill.THOI_MIEN, 7, 10000},{Skill.THOI_MIEN, 6, 15000},{Skill.THOI_MIEN, 4, 15000},{Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );

    public static final BossData VOI9NGA = new BossData(
            "Vòi voi sào lăn", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            100000, //dame
            new double[][]{{99_000_000_000_000_000D}}, //hp
            new short[]{1333,1334,1335}, //outfit
            new short[]{3,4,6,10,19,20}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _5_PHUT
    );

    public static final BossData GA9CUA = new BossData(
            "Gà cháy tỏi", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            100000, //dame
            new double[][]{{99_000_000_000_000_000D}}, //hp
            new short[]{1339,1340,1341}, //outfit
            new short[]{3,4,6,10,19,20}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _5_PHUT
    );

    public static final BossData NGUA9LMAO = new BossData(
            "Thắng cố", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            100000, //dame
            new double[][]{{99_000_000_000_000_000D}}, //hp
            new short[]{1336,1337,1338}, //outfit
            new short[]{3,4,6,10,19,20}, 
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _5_PHUT
    );


//================================================= BOSS KHỦNG LONG RƠI NGỌC RỒNG CÁC LOẠI =================================
    public static final BossData KHUNG_LONG = new BossData(
            "Khủng Long Bạo Chúa (Rơi bộ ngọc rồng 1s)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            100000, //dame
            new double[][]{{10_000_000_000D,100_000_000_000D}}, //hp
            new short[]{1691,1692,1693}, //outfit
            new short[]{3, 4, 6, 10, 19, 20}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );

    public static final BossData KHUNG_LONG_1SAO = new BossData(
            "Khủng Long 1 Sao (Rơi ngọc rồng 1s)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            100000, //dame
            new double[][]{{30_000_000_000D,50_000_000_000D}}, //hp
            new short[]{1688,1689,1690}, //outfit
            new short[]{3, 4, 6, 10, 19, 20}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );

    public static final BossData KHUNG_LONG_2SAO = new BossData(
            "Khủng Long 2 Sao (Rơi ngọc rồng)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            100000, //dame
            new double[][]{{15_000_000_000D,30_000_000_000D}}, //hp
            new short[]{1685,1686,1687}, //outfit
            new short[]{3, 4, 6, 10, 19, 20}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );

    public static final BossData KHUNG_LONG_3SAO = new BossData(
            "Khủng Long 3 Sao (Rơi ngọc rồng)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            100000, //dame
            new double[][]{{5_000_000_000D,15_000_000_000D}}, //hp
            new short[]{1682,1683,1684}, //outfit
            new short[]{3, 4, 6, 10, 19, 20}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );

    public static final BossData KHUNG_LONG_4SAO = new BossData(
            "Khủng Long 4 Sao (Rơi ngọc rồng 3-7s)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            100000, //dame
            new double[][]{{3_000_000_000D,5_000_000_000D}}, //hp
            new short[]{1679,1680,1681}, //outfit
            new short[]{3, 4, 6, 10, 19, 20}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );

    public static final BossData KHUNG_LONG_5SAO = new BossData(
            "Khủng Long 5 Sao (Rơi ngọc rồng 3-7s)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            100000, //dame
            new double[][]{{3_000_000_000D,5_000_000_000D}}, //hp
            new short[]{1676,1677,1678}, //outfit
            new short[]{3, 4, 6, 10, 19, 20}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );

    public static final BossData KHUNG_LONG_6SAO = new BossData(
            "Khủng Long 6 Sao (Rơi ngọc rồng 3-7s)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            100000, //dame
            new double[][]{{3_000_000_000D,5_000_000_000D}}, //hp
            new short[]{1673,1674,1675}, //outfit
            new short[]{3, 4, 6, 10, 19, 20}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );

    public static final BossData KHUNG_LONG_7SAO = new BossData(
            "Khủng Long 7 Sao (Rơi ngọc rồng 3-7s)", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            100000, //dame
            new double[][]{{3_000_000_000D,5_000_000_000D}}, //hp
            new short[]{1670,1671,1672}, //outfit
            new short[]{3, 4, 6, 10, 19, 20}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );

    public static final BossData SON_TINH = new BossData(
            "Sơn Tinh", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            100000, //dame
            new double[][]{{6_696_000_000_000_000D}}, //hp
            new short[]{314,315,316}, //outfit
            new short[]{3,4,6,10,19,20}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );

    public static final BossData THUY_TINH = new BossData(
            "Thuỷ Tinh", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            100000, //dame
            new double[][]{{6_696_000_000_000_000D}}, //hp
            new short[]{311,312,313}, //outfit
            new short[]{3,4,6,10,19,20}, //map join
            new int[][]{ //skill
                {Skill.LIEN_HOAN, 5, 300},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );
    
// ================ Boss mới ======================
    public static final BossData THONG_CHE_KILO = new BossData(
            "Thống Chế Kilo", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{99_000_000_000_000_000D,99_000_000_000_000_000D}}, //hp
            new short[]{712,713,714}, //outfit
            new short[]{156,157,158,159,160,161,162,163}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000},{Skill.THOI_MIEN, 7, 10000},{Skill.THOI_MIEN, 6, 15000},{Skill.THOI_MIEN, 4, 15000},{Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );
    
    public static final BossData HEART_GOLD = new BossData(
            "Heart Gold", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{99_000_000_000_000_000D}}, //hp
            new short[]{1315,1316,1317}, //outfit
            new short[]{211}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000},{Skill.THOI_MIEN, 7, 10000},{Skill.THOI_MIEN, 6, 15000},{Skill.THOI_MIEN, 4, 15000},{Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );
    
    public static final BossData HACHIYACK = new BossData(
            "Hatchiyack", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000, //dame
            new double[][]{{99_000_000_000_000_000D}}, //hp
            new short[]{639,640,641}, //outfit
            new short[]{156,157,158,159,160,161,162,163}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000},{Skill.THOI_MIEN, 7, 10000},{Skill.THOI_MIEN, 6, 15000},{Skill.THOI_MIEN, 4, 15000},{Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );
    
    
    public static final BossData SUPER_ZAMASU = new BossData(
            "Super Zamasu", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{99_000_000_000_000_000D}}, //hp
            new short[]{1312,1313,1314}, //outfit
            new short[]{156,157,158,159,160,161,162,163}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000},{Skill.THOI_MIEN, 7, 10000},{Skill.THOI_MIEN, 6, 15000},{Skill.THOI_MIEN, 4, 15000},{Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );
    
    public static final BossData COOLER_GOLD = new BossData(
            "Cooler Gold", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{99_000_000_000_000_000D}}, //hp
            new short[]{709,710,711}, //outfit
            new short[]{211}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000},{Skill.THOI_MIEN, 7, 10000},{Skill.THOI_MIEN, 6, 15000},{Skill.THOI_MIEN, 4, 15000},{Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );
    
    public static final BossData BILL_BI_NGO = new BossData(
            "Bill Bí Ngô", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{99_000_000_000_000_000D}}, //hp
            new short[]{709,710,711}, //outfit
            new short[]{156,157,158,159,160,161,162,163}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000},{Skill.THOI_MIEN, 7, 10000},{Skill.THOI_MIEN, 6, 15000},{Skill.THOI_MIEN, 4, 15000},{Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );
    
    
    
 //=============== Boss brojack ===============================
    public static final BossData KOGU = new BossData(
            "Kogu", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{1_700_000_000D,1_800_000_000D}}, //hp
            new short[]{329,330,331}, //outfit
            new short[]{17,18,19,20,3,4,6,10,11,12,27,28,29,30,31,32,33,34,35,36,37,38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000},{Skill.THOI_MIEN, 7, 10000},{Skill.THOI_MIEN, 6, 15000},{Skill.THOI_MIEN, 4, 15000},{Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );
    
    public static final BossData BIDO = new BossData(
            "Bido", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{1_100_000_000D,1_200_000_000D}}, //hp
            new short[]{335,336,337}, //outfit
            new short[]{17,18,19,20,3,4,6,10,11,12,27,28,29,30,31,32,33,34,35,36,37,38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000},{Skill.THOI_MIEN, 7, 10000},{Skill.THOI_MIEN, 6, 15000},{Skill.THOI_MIEN, 4, 15000},{Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );
    
    public static final BossData ZANGYA = new BossData(
            "Zangya", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{1_300_000_000D,1_400_000_000D}}, //hp
            new short[]{332,333,334}, //outfit
            new short[]{17,18,19,20,3,4,6,10,11,12,27,28,29,30,31,32,33,34,35,36,37,38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000},{Skill.THOI_MIEN, 7, 10000},{Skill.THOI_MIEN, 6, 15000},{Skill.THOI_MIEN, 4, 15000},{Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );
    
    public static final BossData BUJIN = new BossData(
            "Bujin", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{50_000_000_000_000_000_000_000D,55_000_000_000_000_000_000_000D}}, //hp
            new short[]{341,342,343}, //outfit
            new short[]{17,18,19,20,3,4,6,10,11,12,27,28,29,30,31,32,33,34,35,36,37,38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000},{Skill.THOI_MIEN, 7, 10000},{Skill.THOI_MIEN, 6, 15000},{Skill.THOI_MIEN, 4, 15000},{Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );
    
    public static final BossData BROJACK = new BossData(
            "Brojack", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{50_000_000_000_000_000_000_000D,55_000_000_000_000_000_000_000D}}, //hp
            new short[]{323,324,325}, //outfit
            new short[]{17,18,19,20,3,4,6,10,11,12,27,28,29,30,31,32,33,34,35,36,37,38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000},{Skill.THOI_MIEN, 7, 10000},{Skill.THOI_MIEN, 6, 15000},{Skill.THOI_MIEN, 4, 15000},{Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );
     
    public static final BossData SUPER_BROJACK = new BossData(
            "Brojack", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{1_800_000_000D,2_000_000_000D}}, //hp
            new short[]{326,327,328}, //outfit
            new short[]{17,18,19,20,3,4,6,10,11,12,27,28,29,30,31,32,33,34,35,36,37,38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000},{Skill.THOI_MIEN, 7, 10000},{Skill.THOI_MIEN, 6, 15000},{Skill.THOI_MIEN, 4, 15000},{Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _15_PHUT
    );

    public static final BossData BOSS_VIP_1 = new BossData(
            "Drabura Frost", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            150000, //dame
            new double[][]{{90_000_000_000_000_000_000_000_000_000D, 95_000_000_000_000_000_000_000_000_000D}}, //hp
            new short[]{1309, 1310, 1311}, //outfit
            new short[]{17, 18, 19, 20, 3, 4, 6, 10, 11, 12, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000}, {Skill.THOI_MIEN, 7, 10000}, {Skill.THOI_MIEN, 6, 15000}, {Skill.THOI_MIEN, 4, 15000}, {Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
           _5_PHUT
    );
    
    public static final BossData BOSS_VIP_2 = new BossData(
            "Super Zamasu White", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{90_000_000_000_000_000_000_000_000_000D, 95_000_000_000_000_000_000_000_000_000D}}, //hp
            new short[]{1312, 1313, 1314}, //outfit
            new short[]{17, 18, 19, 20, 3, 4, 6, 10, 11, 12, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000}, {Skill.THOI_MIEN, 7, 10000}, {Skill.THOI_MIEN, 6, 15000}, {Skill.THOI_MIEN, 4, 15000}, {Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _5_PHUT
    );
    
    public static final BossData BOSS_VIP_3 = new BossData(
            "Hearts Gold", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{90_000_000_000_000_000_000_000_000_000D, 95_000_000_000_000_000_000_000_000_000D}}, //hp
            new short[]{1315, 1316, 1317}, //outfit
            new short[]{17, 18, 19, 20, 3, 4, 6, 10, 11, 12, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000}, {Skill.THOI_MIEN, 7, 10000}, {Skill.THOI_MIEN, 6, 15000}, {Skill.THOI_MIEN, 4, 15000}, {Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _5_PHUT
    );
    
    public static final BossData BOSS_VIP_4 = new BossData(
            "Broly Huyền Thoại", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{90_000_000_000_000_000_000_000_000_000D, 95_000_000_000_000_000_000_000_000_000D}}, //hp
            new short[]{1318, 1319, 1320}, //outfit
            new short[]{17, 18, 19, 20, 3, 4, 6, 10, 11, 12, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000}, {Skill.THOI_MIEN, 7, 10000}, {Skill.THOI_MIEN, 6, 15000}, {Skill.THOI_MIEN, 4, 15000}, {Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
           _5_PHUT
    );
    
    public static final BossData BOSS_VIP_5 = new BossData(
            "Baby Vegeta", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{90_000_000_000_000_000_000_000_000_000D, 95_000_000_000_000_000_000_000_000_000D}}, //hp
            new short[]{1324, 1325, 1326}, //outfit
            new short[]{17, 18, 19, 20, 3, 4, 6, 10, 11, 12, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000}, {Skill.THOI_MIEN, 7, 10000}, {Skill.THOI_MIEN, 6, 15000}, {Skill.THOI_MIEN, 4, 15000}, {Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _5_PHUT
    );
    
    public static final BossData BOSS_VIP_6 = new BossData(
            "Evil Buu", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{90_000_000_000_000_000_000_000_000_000D, 95_000_000_000_000_000_000_000_000_000D}}, //hp
            new short[]{1384, 1385, 1386}, //outfit
            new short[]{17, 18, 19, 20, 3, 4, 6, 10, 11, 12, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000}, {Skill.THOI_MIEN, 7, 10000}, {Skill.THOI_MIEN, 6, 15000}, {Skill.THOI_MIEN, 4, 15000}, {Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _5_PHUT
    );
    
    public static final BossData BOSS_VIP_7 = new BossData(
            "Jiren Cuồng Nộ", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{90_000_000_000_000_000_000_000_000_000D, 95_000_000_000_000_000_000_000_000_000D}}, //hp
            new short[]{1402, 1403, 1404}, //outfit
            new short[]{17, 18, 19, 20, 3, 4, 6, 10, 11, 12, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000}, {Skill.THOI_MIEN, 7, 10000}, {Skill.THOI_MIEN, 6, 15000}, {Skill.THOI_MIEN, 4, 15000}, {Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _5_PHUT
    );
    
    public static final BossData BOSS_VIP_8 = new BossData(
            "Vegeta Hakai", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{90_000_000_000_000_000_000_000_000_000D, 95_000_000_000_000_000_000_000_000_000D}}, //hp
            new short[]{1405, 660, 661}, //outfit
            new short[]{17, 18, 19, 20, 3, 4, 6, 10, 11, 12, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000}, {Skill.THOI_MIEN, 7, 10000}, {Skill.THOI_MIEN, 6, 15000}, {Skill.THOI_MIEN, 4, 15000}, {Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _5_PHUT
    );
    
    public static final BossData BOSS_VIP_9 = new BossData(
            "Gohan Zombie", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{90_000_000_000_000_000_000_000_000_000D, 95_000_000_000_000_000_000_000_000_000D}}, //hp
            new short[]{1406, 1407, 1408}, //outfit
            new short[]{17, 18, 19, 20, 3, 4, 6, 10, 11, 12, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000}, {Skill.THOI_MIEN, 7, 10000}, {Skill.THOI_MIEN, 6, 15000}, {Skill.THOI_MIEN, 4, 15000}, {Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _5_PHUT
    );
    
    public static final BossData BOSS_VIP_10 = new BossData(
            "Toppo GOD", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{90_000_000_000_000_000_000_000_000_000D, 95_000_000_000_000_000_000_000_000_000D}}, //hp
            new short[]{1412, 1413, 1414}, //outfit
            new short[]{17, 18, 19, 20, 3, 4, 6, 10, 11, 12, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000}, {Skill.THOI_MIEN, 7, 10000}, {Skill.THOI_MIEN, 6, 15000}, {Skill.THOI_MIEN, 4, 15000}, {Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _5_PHUT
    );
    
    public static final BossData BOSS_VIP_11 = new BossData(
            "Saiyan God Trunks", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{90_000_000_000_000_000_000_000_000_000D, 95_000_000_000_000_000_000_000_000_000D}}, //hp
            new short[]{1415, 1416, 1417}, //outfit
            new short[]{17, 18, 19, 20, 3, 4, 6, 10, 11, 12, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000}, {Skill.THOI_MIEN, 7, 10000}, {Skill.THOI_MIEN, 6, 15000}, {Skill.THOI_MIEN, 4, 15000}, {Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _5_PHUT
    );
    
    public static final BossData BOSS_VIP_12 = new BossData(
            "Rồng đen 1 sao", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{90_000_000_000_000_000_000_000_000_000D, 95_000_000_000_000_000_000_000_000_000D}}, //hp
            new short[]{1050, 1051, 1052}, //outfit
            new short[]{17, 18, 19, 20, 3, 4, 6, 10, 11, 12, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000}, {Skill.THOI_MIEN, 7, 10000}, {Skill.THOI_MIEN, 6, 15000}, {Skill.THOI_MIEN, 4, 15000}, {Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _5_PHUT
    );
    
    public static final BossData BOSS_VIP_13 = new BossData(
            "Goku Super Saiyan", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{90_000_000_000_000_000_000_000_000_000D, 95_000_000_000_000_000_000_000_000_000D}}, //hp
            new short[]{1074, 1075, 1076}, //outfit
            new short[]{17, 18, 19, 20, 3, 4, 6, 10, 11, 12, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000}, {Skill.THOI_MIEN, 7, 10000}, {Skill.THOI_MIEN, 6, 15000}, {Skill.THOI_MIEN, 4, 15000}, {Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _5_PHUT
    );
    
    public static final BossData BOSS_VIP_14 = new BossData(
            "Cadic Super Saiyan", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{90_000_000_000_000_000_000_000_000_000D, 95_000_000_000_000_000_000_000_000_000D}}, //hp
            new short[]{1089, 1090, 1091}, //outfit
            new short[]{17, 18, 19, 20, 3, 4, 6, 10, 11, 12, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000}, {Skill.THOI_MIEN, 7, 10000}, {Skill.THOI_MIEN, 6, 15000}, {Skill.THOI_MIEN, 4, 15000}, {Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _5_PHUT
    );
    
    public static final BossData BOSS_VIP_15 = new BossData(
            "Zamasu Zombie", //name
            ConstPlayer.TRAI_DAT, //gender
            Boss.DAME_NORMAL, //type dame
            Boss.HP_NORMAL, //type hp
            1000000000, //dame
            new double[][]{{90_000_000_000_000_000_000_000_000_000D, 95_000_000_000_000_000_000_000_000_000D}}, //hp
            new short[]{1466, 1467, 1468}, //outfit
            new short[]{17, 18, 19, 20, 3, 4, 6, 10, 11, 12, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38}, //map join
            new int[][]{ //skill
                {Skill.THOI_MIEN, 5, 5000}, {Skill.THOI_MIEN, 7, 10000}, {Skill.THOI_MIEN, 6, 15000}, {Skill.THOI_MIEN, 4, 15000}, {Skill.THOI_MIEN, 3, 15000},
                {Skill.THAI_DUONG_HA_SAN, 6, 30000}, {Skill.THAI_DUONG_HA_SAN, 7, 40000}, {Skill.THAI_DUONG_HA_SAN, 5, 30000},
                {Skill.KAMEJOKO, 7, 1000}
            },
            _5_PHUT
    );
}
