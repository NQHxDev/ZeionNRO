package nro.models.skill;

/**
 *
 * @author Văn Tuấn - 0337766460
 * @copyright 💖 Ytb: @Tuan_To_Mo 💖
 *
 */
public class Skill {

    public static final int RANGE_ATTACK_CHIEU_DAM = 50;
    public static final int RANGE_ATTACK_CHIEU_CHUONG = 200;

    public static final byte DEMON = 2;
    public static final byte MASENKO = 3;
    public static final byte TRI_THUONG = 7;
    public static final byte MAKANKOSAPPO = 11;
    public static final byte DE_TRUNG = 12;
    public static final byte LIEN_HOAN = 17;
    public static final byte SOCOLA = 18;

    public static final byte GALICK = 4;
    public static final byte ANTOMIC = 5;
    public static final byte TAI_TAO_NANG_LUONG = 8;
    public static final byte BIEN_KHI = 13;
    public static final byte TU_SAT = 14;
    public static final byte HUYT_SAO = 21;
    public static final byte TROI = 23;

    public static final byte DRAGON = 0;
    public static final byte KAMEJOKO = 1;
    public static final byte THAI_DUONG_HA_SAN = 6;
    public static final byte KAIOKEN = 9;
    public static final byte QUA_CAU_KENH_KHI = 10;
    public static final byte DICH_CHUYEN_TUC_THOI = 20;
    public static final byte THOI_MIEN = 22;

    public static final byte KHIEN_NANG_LUONG = 19;
    public static final byte SUPER_KAME = 24;
    public static final byte LIEN_HOAN_CHUONG = 25;
    public static final byte MA_PHONG_BA = 26;
    public static final byte BIEN_HINH_TD = 27;
    public static final byte BIEN_HINH_NM = 28;
    public static final byte BIEN_HINH_XD = 29;
    public static final byte PHAN_THAN = 30;

    public SkillTemplate template;

    public short skillId;

    public int point;

    public long powRequire;

    public int coolDown;

    public long lastTimeUseThisSkill;

    public int dx;

    public int dy;

    public int maxFight;
    
    public short currLevel;
    
    public int manaUse;

    public boolean paintCanNotUseSkill;

    public short damage;

    public String moreInfo;

    public short price;

    public Skill() {

    }

    public Skill(Skill skill) {
        this.skillId = skill.skillId;
        this.point = skill.point;
        this.powRequire = skill.powRequire;
        if (skill.skillId == 1) {
            this.coolDown = 5000;
        } else {
            this.coolDown = skill.coolDown;
        }
        this.lastTimeUseThisSkill = skill.lastTimeUseThisSkill;
        this.dx = skill.dx;
        this.dy = skill.dy;
        this.maxFight = skill.maxFight;
        this.manaUse = skill.manaUse;
        this.paintCanNotUseSkill = skill.paintCanNotUseSkill;
        this.damage = skill.damage;
        this.moreInfo = new String(skill.moreInfo);
        this.price = skill.price;
        this.template = skill.template;
    }
}
