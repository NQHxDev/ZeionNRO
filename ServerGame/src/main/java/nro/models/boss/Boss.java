package nro.models.boss;

import java.io.IOException;
import nro.consts.ConstItem;
import nro.consts.ConstMap;
import nro.consts.ConstPlayer;
import nro.consts.ConstRatio;
import nro.event.Event;
import nro.lib.RandomCollection;
import nro.models.boss.cdrd.CBoss;
import nro.models.boss.iboss.BossInterface;
import nro.models.boss.mabu_war.BossMabuWar;
import nro.models.boss.nappa.Kuku;
import nro.models.boss.nappa.MapDauDinh;
import nro.models.boss.nappa.Rambo;
import nro.models.boss.BossNew.*;
import nro.models.boss.BossMoi.*;
import nro.models.boss.KhungLong.*;
//import nro.models.boss.Boss_Vy_Thu.*;
import nro.models.boss.NguHanhSon.*;
import nro.models.boss.NgucTu.*;
import nro.models.boss.bill.*;
import nro.models.boss.boss_ban_do_kho_bau.*;
import nro.models.boss.boss_doanh_trai.*;
import nro.models.boss.bosstuonglai.*;
import nro.models.boss.broly.*;
import nro.models.boss.cdrd.*;
import nro.models.boss.cell.*;
import nro.models.boss.chill.*;
import nro.models.boss.cold.*;
import nro.models.boss.fide.*;
import nro.models.boss.mabu_war.*;
import nro.models.boss.robotsatthu.*;
import nro.models.boss.tieudoisatthu.*;
import nro.models.boss.traidat.*;
import nro.models.boss.tramtau.*;
import nro.models.item.Item;
import nro.models.map.ItemMap;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.server.ServerNotify;
import nro.services.*;
import nro.services.*;
import nro.services.func.ChangeMapService;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import nro.consts.ConstAchive;
import nro.jdbc.daos.PlayerDAO;
import static nro.models.boss.BossManager.BOSSES_IN_GAME;
import static nro.models.boss.BossNew.Along.ratiItemThienSu;
import nro.models.boss.boss_ban_do_kho_bau.BossBanDoKhoBau;
import nro.models.boss.boss_doanh_trai.BossDoanhTrai;
import nro.models.boss.dhvt.BossDHVT;
import static nro.models.boss.traidat.POCTHO.ratiItemHuyDiet;
import nro.models.item.ItemOption;
import nro.models.map.mabu.MabuWar14h;
import nro.server.io.Message;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 */
public abstract class Boss extends Player implements BossInterface {

    //type dame
    public static final byte DAME_NORMAL = 0;
    public static final byte DAME_PERCENT_HP_HUND = 1;
    public static final byte DAME_PERCENT_MP_HUND = 2;
    public static final byte DAME_PERCENT_HP_THOU = 3;
    public static final byte DAME_PERCENT_MP_THOU = 4;
    public static final byte DAME_TIME_PLAYER_WITH_HIGHEST_HP_IN_CLAN = 5;

    //type hp
    public static final byte HP_NORMAL = 0;
    public static final byte HP_TIME_PLAYER_WITH_HIGHEST_DAME_IN_CLAN = 1;

    protected static final byte DO_NOTHING = 71;
    protected static final byte RESPAWN = 77;
    protected static final byte JUST_RESPAWN = 75; // khởi tạo lại, rồi chuyển sang nghỉ
    protected static final byte REST = 0; //boss chưa xuất hiện
    public static final byte JUST_JOIN_MAP = 1; // vào map rồi chuyển sang nói chuyện lúc đầu
    protected static final byte TALK_BEFORE = 2; //chào hỏi chuyển sang trạng thái khác
    public static final byte ATTACK = 3;
    protected static final byte IDLE = 4;
    protected static final byte DIE = 5;
    protected static final byte TALK_AFTER = 6;
    protected static final byte LEAVE_MAP = 7;

    //--------------------------------------------------------------------------
    public BossData data;
    @Setter
    protected byte status;
    protected short[] outfit;
    protected byte typeDame;
    protected byte typeHp;
//    protected int percentDame;
    protected double percentDame;
    protected short[] mapJoin;

    protected byte indexTalkBefore;
    protected String[] textTalkBefore;
    protected byte indexTalkAfter;
    protected String[] textTalkAfter;
    protected String[] textTalkMidle;

    protected long lastTimeTalk;
    protected int timeTalk;
    protected byte indexTalk;
    protected boolean doneTalkBefore;
    protected boolean doneTalkAffter;

    private long lastTimeRest;
    //thời gian nghỉ chuẩn bị đợt xuất hiện sau
    protected int secondTimeRestToNextTimeAppear = 1800;

    protected int maxIdle;
    protected int countIdle;

    private final List<Skill> skillsAttack;
    private final List<Skill> skillsSpecial;

    protected Player plAttack;
    protected int targetCountChangePlayerAttack;
    protected int countChangePlayerAttack;

    private long lastTimeStartLeaveMap;
    private int timeDelayLeaveMap = 2000;

    protected boolean joinMapIdle;

    private int timeAppear = 0;
    private long lastTimeUpdate;
    private int TIME_RESEND_LOCATION = 15;

    public long timeStartDie;
    public boolean startDie = true;
    public boolean isMabuBoss;
    public int zoneHold;
    public boolean isUseSpeacialSkill;
    public long lastTimeUseSpeacialSkill;

    public void changeStatus(byte status) {
        this.status = status;
    }

    public Boss(int id, BossData data) {
        super();
        this.id = id;
        this.skillsAttack = new ArrayList<>();
        this.skillsSpecial = new ArrayList<>();
        this.data = data;
        this.isBoss = true;
        this.initTalk();
        this.respawn();
        setJustRest();
        if (!(this instanceof CBoss)) {
            BossManager.gI().addBoss(this);
        }
    }

    @Override
    public void init() {
        this.name = data.name.replaceAll("%1", String.valueOf(Util.nextInt(0, 100)));
        this.gender = data.gender;
        this.typeDame = data.typeDame;
        this.typeHp = data.typeHp;
        this.nPoint.power = 1;
        this.nPoint.mpg = 752002;

        if (data.secondsRest != -1) {
            this.secondTimeRestToNextTimeAppear = data.secondsRest;
        }

        // ===== chọn HP cho boss như bạn đang làm =====
        double baseHp;
        double[] arrHp = data.hp[Util.nextInt(0, data.hp.length - 1)];
        baseHp = (arrHp.length == 1) ? arrHp[0] : Util.nextdameDouble(arrHp[0], arrHp[1]);

        if (this.typeHp == HP_NORMAL) {
            // >>> chỉ boss dùng đường double
            this.nPoint.useDoubleHp = true;
            this.nPoint.hpgD = baseHp;
        }

        // ===== tính đồ như bình thường (calPoint sẽ gọi setPointWhenWearClothes + helper boss) =====
        this.nPoint.calPoint();

        // ===== sau khi đã có hp (theo hpgD), giờ mới set dame % nếu cần =====
        double dane = data.dame;
        switch (this.typeDame) {
            case DAME_NORMAL:
                this.nPoint.dameg = (long) Math.min(dane, Long.MAX_VALUE);
                break;
            case DAME_PERCENT_HP_HUND:
                this.percentDame = dane;
                this.nPoint.dameg = clampToLong(this.nPoint.hp * dane / 100.0);
                break;
            case DAME_PERCENT_MP_HUND:
                this.percentDame = dane;
                this.nPoint.dameg = clampToLong(this.nPoint.mpg * dane / 100.0);
                break;
            case DAME_PERCENT_HP_THOU:
                this.percentDame = dane;
                this.nPoint.dameg = clampToLong(this.nPoint.hp * dane / 1000.0);
                break;
            case DAME_PERCENT_MP_THOU:
                this.percentDame = dane;
                this.nPoint.dameg = clampToLong(this.nPoint.mpg * dane / 1000.0);
                break;
            default:
                break;
        }

        this.nPoint.calPoint(); // nếu hệ thống bạn thường gọi lại sau khi thay đổi dameg

        this.outfit = data.outfit;
        this.mapJoin = data.mapJoin;
        if (data.timeDelayLeaveMap != -1) {
            this.timeDelayLeaveMap = data.timeDelayLeaveMap;
        }
        this.joinMapIdle = data.joinMapIdle;
        initSkill();
    }

    @Override
    public int version() {
        return 214;
    }

    protected void initSkill() {
        this.playerSkill.skills.clear();
        this.skillsAttack.clear();
        this.skillsSpecial.clear();
        int[][] skillTemp = data.skillTemp;
        for (int i = 0; i < skillTemp.length; i++) {
            Skill skill = SkillUtil.createSkill(skillTemp[i][0], skillTemp[i][1]);
            skill.coolDown = skillTemp[i][2];
            this.playerSkill.skills.add(skill);
            switch (skillTemp[i][0]) {
                case Skill.DRAGON:
                case Skill.DEMON:
                case Skill.GALICK:
                case Skill.KAMEJOKO:
                case Skill.MASENKO:
                case Skill.ANTOMIC:
                case Skill.LIEN_HOAN:
                case Skill.KAIOKEN:
                    this.skillsAttack.add(skill);
                    break;
                case Skill.TAI_TAO_NANG_LUONG:
                case Skill.THAI_DUONG_HA_SAN:
                case Skill.DICH_CHUYEN_TUC_THOI:
                case Skill.BIEN_KHI:
                case Skill.THOI_MIEN:
                case Skill.TROI:
                case Skill.KHIEN_NANG_LUONG:
                case Skill.SOCOLA:
                case Skill.DE_TRUNG:
                    this.skillsSpecial.add(skill);
                    break;
            }
        }
    }
    public long lastTimeChat;

    private void BaoHpBoss() {
        try {
            Message msg;
            if (this.isBoss && this.nPoint.hp >= 2123456789) {
                if (Util.canDoWithTime(lastTimeChat, 1500)) {
                    String text = "|2|<-" + this.name + "->" + "\n\n"
                            + "|7|Máu Còn lại : " + Util.powerToStringnew(this.nPoint.hp) + "\n"
                            + "|3|< " + Util.format(this.nPoint.hp) + " >";
                    msg = new Message(44);
                    msg.writer().writeInt((int) this.id);
                    msg.writer().writeUTF(text);
                    Service.getInstance().sendMessAllPlayerInMap(this, msg);
                    msg.cleanup();
                    lastTimeChat = System.currentTimeMillis();
                }
            }
        } catch (IOException e) {
            Log.error(SkillService.class, e);
        }
    }

    @Override
    public void update() {
        super.update();
        try {
//            if (!this.effectSkill.isStun) {
//                this.BaoHpBoss();
//            }
            if (!this.effectSkill.isHaveEffectSkill()
                    && !this.effectSkill.isCharging) {
                this.immortalMp();
                switch (this.status) {
                    case RESPAWN:
                        respawn();
                        break;
                    case JUST_RESPAWN:
                        this.changeStatus(REST);
                        break;
                    case REST:
                        if (Util.canDoWithTime(lastTimeRest, secondTimeRestToNextTimeAppear * 1000)) {
                            this.changeStatus(JUST_JOIN_MAP);
                        }
                        break;
                    case JUST_JOIN_MAP:
                        joinMap();
                        if (this.zone != null) {
                            changeStatus(TALK_BEFORE);
                        }
                        break;
                    case TALK_BEFORE:
                        if (talk()) {
                            if (!this.joinMapIdle) {
                                changeToAttack();
                            } else {
                                this.changeStatus(IDLE);
                            }
                        }
                        break;
                    case ATTACK:
                        this.talk();
                        if (this.playerSkill.prepareTuSat || this.playerSkill.prepareLaze
                                || this.playerSkill.prepareQCKK) {
                            break;
                        } else {
                            this.attack();
                        }
                        break;
                    case IDLE:
                        this.idle();
                        break;
                    case DIE:
                        if (this.joinMapIdle) {
                            this.changeToIdle();
                        }
                        if (MabuWar14h.gI().isTimeMabuWar() && this.isMabuBoss && this.zone.map.mapId == 127) {
                            nextMabu(this.isDie());
                            return;
                        }
                        changeStatus(TALK_AFTER);
                        break;
                    case TALK_AFTER:
                        if (talk()) {
                            changeStatus(LEAVE_MAP);
                            this.lastTimeStartLeaveMap = System.currentTimeMillis();
                        }
                        break;
                    case LEAVE_MAP:
                        if (Util.canDoWithTime(lastTimeStartLeaveMap, timeDelayLeaveMap)) {
                            this.leaveMap();
                            this.changeStatus(RESPAWN);
                        }
                        break;
                    case DO_NOTHING:

                        break;
                }
            }
            if (Util.canDoWithTime(lastTimeUpdate, 60000)) {
                if (timeAppear >= TIME_RESEND_LOCATION) {
                    if (this.zone != null && !(this instanceof BossMabuWar)) {
                        ServerNotify.gI().notify("Boss " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName);
                        timeAppear = 0;
                    }
                } else {
                    timeAppear++;
                }
                lastTimeUpdate = System.currentTimeMillis();
            }
        } catch (Exception e) {
            this.leaveMap();
            BossManager.gI().removeBoss(this);
            System.out.println("Loi boss " + this.name);
            Log.error(Boss.class, e);
        }
    }

    public List<Player> getListPlayerAttack(int dis) {
        List<Player> Players = new ArrayList<>();
        for (int i = 0; i < this.zone.getHumanoids().size(); i++) {
            Player pl = this.zone.getHumanoids().get(i);
            if (pl != null && !pl.isDie() && !pl.effectSkill.isHoldMabu && Util.getDistance(this, pl) <= dis) {
                Players.add(pl);
            }
        }
        return Players;
    }

    public void nextMabu(boolean isDie) {
        if ((isDie ? this.isDie() : true) && this.head != 427 && !Util.canDoWithTime(this.timeStartDie, 3200)) {
            if (this.startDie) {
                this.startDie = false;
                Service.getInstance().hsChar(this, -1, -1);
                EffectSkillService.gI().startCharge(this);
            }
            return;
        }
        this.startDie = false;
        EffectSkillService.gI().stopCharge(this);
        int id = (int) this.id;
        switch (id) {
            case BossFactory.MABU_MAP:// boss die là bư mập => Summon Super Bư
                this.leaveMap();
                this.id = BossFactory.SUPER_BU;
                this.data = BossData.SUPER_BU;
                this.changeStatus(RESPAWN);
                break;
            case BossFactory.SUPER_BU:// boss die là Super Bư => Summon Kid Bư
                this.leaveMap();
                this.id = BossFactory.KID_BU;
                this.data = BossData.KID_BU;
                this.changeStatus(RESPAWN);
                break;
            case BossFactory.KID_BU:// boss die là Kid Bư => Summon Bư Tenk nếu nuốt được người trong dạng kid bư
                this.leaveMap();
                this.id = BossFactory.BU_TENK;
                this.data = BossData.BU_TENK;
                this.changeStatus(RESPAWN);
                break;
            case BossFactory.BU_TENK:// boss die là Bư Tenk => Summon bư Han
                this.leaveMap();
                this.id = BossFactory.BU_HAN;
                this.data = BossData.BU_HAN;
                this.changeStatus(RESPAWN);
                break;
            default:
                if (isDie) {
                    this.leaveMap();
                }
                break;
        }
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        double dame = 0;
        if (this.isDie()) {
            return dame;
        } else {
            if (Util.isTrue(1, 100) && plAtt != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.TU_SAT:
                    case Skill.QUA_CAU_KENH_KHI:
                    case Skill.MAKANKOSAPPO:
                        break;
                    default:
                        return 0;
                }
            }
            dame = super.injured(plAtt, damage, piercing, isMobAttack);
            if (this.isDie()) {
                rewards(plAtt);
                generalRewards(plAtt);
                notifyPlayeKill(plAtt);
                die();
            }
            return dame;
        }
    }

    protected void notifyPlayeKill(Player player) {
        if (player != null) {
            ServerNotify.gI().notify(player.name + " vừa tiêu diệt được " + this.name + " mọi người đều ngưỡng mộ");
        }
    }

    public double injuredNotCheckDie(Player plAtt, double damage, boolean piercing) {
        if (this.isDie()) {
            return 0;
        } else {
            double dame = super.injured(plAtt, damage, piercing, false);
            return dame;
        }
    }

    protected Skill getSkillAttack() {
        return skillsAttack.get(Util.nextInt(0, skillsAttack.size() - 1));
    }

    protected Skill getSkillSpecial() {
        return skillsSpecial.get(Util.nextInt(0, skillsSpecial.size() - 1));
    }

    protected Skill getSkillById(int skillId) {
        return SkillUtil.getSkillbyId(this, skillId);
    }

    @Override
    public void die() {
        setJustRest();
        changeStatus(DIE);
    }

    @Override
    public void joinMap() {
        if (this.zone == null) {
            this.zone = getMapCanJoin(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
        }
        if (this.zone != null) {
            ChangeMapService.gI().changeMapBySpaceShip(this, this.zone, ChangeMapService.TENNIS_SPACE_SHIP);
            ServerNotify.gI().notify("Boss " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName);
//            System.out.println("Boss " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName + " khu vực " + this.zone.zoneId);
        }
    }

    public Zone getMapCanJoin(int mapId) {
        Zone map = MapService.gI().getMapWithRandZone(mapId);
        if (map.isBossCanJoin(this)) {
            return map;
        } else {
            return getMapCanJoin(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
        }
    }

    public Zone getMapJoin() {

        int mapId = this.data.mapJoin[Util.nextInt(0, this.data.mapJoin.length - 1)];
        Zone map = MapService.gI().getMapWithRandZone(mapId);
        //to do: check boss in map
        return map;
    }
    
    
    public static void trieuHoiBoss(Player player, int bossId) {
        Boss boss = BossFactory.createBoss(bossId);
        boss.respawn();
        boss.joinZone(player);
        PlayerService.gI().changeAndSendTypePK(boss, ConstPlayer.PK_ALL);
        boss.changeStatus(Boss.ATTACK);
        Service.getInstance().sendThongBao(player, "Triệu hồi boss thành công");
        Service.getInstance().sendThongBaoFromAdmin(player,
                "|2|ADMIN đã gọi boss: " + boss.name + " tại map: "
                + player.zone.map.mapName + " khu " + player.zone.zoneId);
    }

    public void joinZone(Player player) {
        if (player == null || player.zone == null) {
            return;
        }
        Zone target = player.zone;
        this.zone = target;
        this.location.x = player.location.x;
        this.location.y = player.location.y;
        ChangeMapService.gI().changeMapBySpaceShip(this, target, ChangeMapService.TENNIS_SPACE_SHIP);
        ServerNotify.gI().notify("Boss " + this.name + " vừa được triệu hồi tại "
                + target.map.mapName + " khu " + target.zoneId);
    }
    
    @Override
    public void leaveMap() {
        MapService.gI().exitMap(this);
    }

    @Override
    public boolean talk() {
        switch (status) {
            case TALK_BEFORE:
                if (this.textTalkBefore == null || this.textTalkBefore.length == 0) {
                    return true;
                }
                if (Util.canDoWithTime(lastTimeTalk, 5000)) {
                    if (indexTalkBefore < textTalkBefore.length) {
                        this.chat(textTalkBefore[indexTalkBefore++]);
                        if (indexTalkBefore >= textTalkBefore.length) {
                            return true;
                        }
                        lastTimeTalk = System.currentTimeMillis();
                    } else {
                        return true;
                    }
                }
                break;
            case IDLE:
            case ATTACK:
                if (this.textTalkMidle == null || this.textTalkMidle.length == 0 || !Util.isTrue(1, 30)) {
                    return true;
                }
                if (Util.canDoWithTime(lastTimeTalk, Util.nextInt(15000, 20000))) {
                    this.chat(textTalkMidle[Util.nextInt(0, textTalkMidle.length - 1)]);
                    lastTimeTalk = System.currentTimeMillis();
                }
                break;
            case TALK_AFTER:
                if (this.textTalkAfter == null || this.textTalkAfter.length == 0) {
                    return true;
                }
                if (Util.canDoWithTime(lastTimeTalk, 5000)) {
                    this.chat(textTalkAfter[indexTalkAfter++]);
                    if (indexTalkAfter >= textTalkAfter.length) {
                        return true;
                    }
                    if (indexTalkAfter > textTalkAfter.length - 1) {
                        indexTalkAfter = 0;
                    }
                    lastTimeTalk = System.currentTimeMillis();
                }
                break;
        }
        return false;
    }

    @Override
    public void respawn() {
        this.init();
        this.indexTalkBefore = 0;
        this.indexTalkAfter = 0;
        this.nPoint.setFullHpMp();
        this.changeStatus(JUST_RESPAWN);
    }

    protected void goToPlayer(Player pl, boolean isTeleport) {
        goToXY(pl.location.x, pl.location.y, isTeleport);
    }

    protected void goToXY(int x, int y, boolean isTeleport) {
        if (!isTeleport) {
            byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
            byte move = (byte) Util.nextInt(50, 100);
            PlayerService.gI().playerMove(this, this.location.x + (dir == 1 ? move : -move), y);
        } else {
            ChangeMapService.gI().changeMapYardrat(this, this.zone, x, y);
        }
    }

    public int getRangeCanAttackWithSkillSelect() {
        int skillId = this.playerSkill.skillSelect.template.id;
        if (skillId == Skill.KAMEJOKO || skillId == Skill.MASENKO || skillId == Skill.ANTOMIC) {
            return Skill.RANGE_ATTACK_CHIEU_CHUONG;
        } else {
            return Skill.RANGE_ATTACK_CHIEU_DAM;
        }
    }

    @Override
    public Player getPlayerAttack() throws Exception {
        if (countChangePlayerAttack < targetCountChangePlayerAttack
                && plAttack != null && plAttack.zone != null
                && plAttack.zone.equals(this.zone)) {
            if (!plAttack.isDie() && !plAttack.effectSkin.isVoHinh && !plAttack.isMiniPet) {
                this.countChangePlayerAttack++;
                return plAttack;
            } else {
                plAttack = null;
            }
        } else {
            this.targetCountChangePlayerAttack = Util.nextInt(10, 20);
            this.countChangePlayerAttack = 0;
            plAttack = this.zone.getRandomPlayerInMap();
            if (plAttack != null && plAttack.effectSkin.isVoHinh) {
                plAttack = null;
            }
        }
        return plAttack;
    }

    @Override
    public void attack() {
        try {
            Player pl = getPlayerAttack();
            if (pl != null && !pl.isDie() && !pl.isMiniPet) {
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(15, ConstRatio.PER100)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                        } else {
                            goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 30)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                        }
                    }
                    SkillService.gI().useSkill(this, pl, null, null);
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
            }
        } catch (Exception ex) {
            Log.error(Boss.class, ex);
        }
    }

    private void immortalMp() {
        this.nPoint.mp = this.nPoint.mpg;
    }

    protected abstract boolean useSpecialSkill();

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public short getHead() {
        return this.outfit[0];
    }

    @Override
    public short getBody() {
        return this.outfit[1];
    }

    @Override
    public short getLeg() {
        return this.outfit[2];
    }

    //status
    protected void changeIdle() {
        this.changeStatus(IDLE);
    }

    /**
     * Đổi sang trạng thái tấn công
     */
    protected void changeAttack() {
        this.changeStatus(ATTACK);
    }

    public void setJustRest() {
        this.lastTimeRest = System.currentTimeMillis();
    }

    public void setJustRestToFuture() {
        this.lastTimeRest = System.currentTimeMillis() + 8640000000L;
    }

    @Override
    public void dropItemReward(int tempId, int playerId, int... quantity) {
        if (!this.zone.map.isMapOffline && this.zone.map.type == ConstMap.MAP_NORMAL) {
            int x = this.location.x + Util.nextInt(-30, 30);
            if (x < 30) {
                x = 30;
            } else if (x > zone.map.mapWidth - 30) {
                x = zone.map.mapWidth - 30;
            }
            int y = this.location.y;
            if (y > 24) {
                y = this.zone.map.yPhysicInTop(x, y - 24);
            }
            ItemMap itemMap = new ItemMap(this.zone, tempId, (quantity != null && quantity.length == 1) ? quantity[0] : 1, x, y, playerId);
            Service.getInstance().dropItemMap(itemMap.zone, itemMap);
        }
    }

    @Override
    public void generalRewards(Player player) {//Hmmmmm....phẩn thưởng chung (boss nào cũng rớt - boss phó bản)
        if (player != null) {
            int x = this.location.x;
            int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
            //================ SU KIEN ===================
            int[] listDch = {1791,1792,1793,1794,1795,1796,1563,1564,1565,1559,1560,1561,1562,1797};
            int dch1to4 = listDch[Util.nextInt(0, 3)];
            int dch4to9 = listDch[Util.nextInt(3, 7)];
            int dch9to11 = listDch[Util.nextInt(8, 10)];
            if (Util.isTrue(20, 100)) {
                ItemMap dacuonghoa1to4 = new ItemMap(player.zone, dch1to4, 1,
                        this.location.x, y, player.id);
                Service.getInstance().dropItemMap(player.zone, dacuonghoa1to4);
            }
            if (Util.isTrue(1, 100)) {
                ItemMap dacuonghoa4to9 = new ItemMap(player.zone, dch4to9, 1,
                        this.location.x, y, player.id);
                Service.getInstance().dropItemMap(player.zone, dacuonghoa4to9);
            }
            if (Util.isTrue(1, 1000)) {
                ItemMap dacuonghoa9to11 = new ItemMap(player.zone, dch9to11, 1,
                        this.location.x, y, player.id);
                Service.getInstance().dropItemMap(player.zone, dacuonghoa9to11);
            } 
            player.playerTask.achivements.get(ConstAchive.DANH_BAI_1_BOSS).count++;
            player.playerTask.achivements.get(ConstAchive.DANH_BAI_10_BOSS).count++;
            player.playerTask.achivements.get(ConstAchive.DANH_BAI_20_BOSS).count++;
            player.playerTask.achivements.get(ConstAchive.DANH_BAI_30_BOSS).count++;
            player.playerTask.achivements.get(ConstAchive.DANH_BAI_50_BOSS).count++;
            player.playerTask.achivements.get(ConstAchive.DANH_BAI_100_BOSS).count++;
            player.playerTask.achivements.get(ConstAchive.DANH_BAI_200_BOSS).count++;
            player.playerTask.achivements.get(ConstAchive.DANH_BAI_300_BOSS).count++;
            player.playerTask.achivements.get(ConstAchive.DANH_BAI_400_BOSS).count++;
            player.playerTask.achivements.get(ConstAchive.DANH_BAI_500_BOSS).count++;
            player.playerTask.achivements.get(ConstAchive.DANH_BAI_600_BOSS).count++;
            player.playerTask.achivements.get(ConstAchive.DANH_BAI_700_BOSS).count++;
            player.playerTask.achivements.get(ConstAchive.DANH_BAI_800_BOSS).count++;
            player.playerTask.achivements.get(ConstAchive.DANH_BAI_900_BOSS).count++;
            player.playerTask.achivements.get(ConstAchive.DANH_BAI_1000_BOSS).count++;
            player.playerTask.achivements.get(ConstAchive.DANH_BAI_2000_BOSS).count++;
            PlayerDAO.CongKillBoss(player, 1); 
        }
    }

    public void DoXungQuanh(Player pl, int item, int sl, int soluongroi) {
            int a = 0;
            int x = this.location.x;
            int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
            for (int k = 0; k < soluongroi; k++) {
                ItemMap itemMap32 = new ItemMap(pl.zone, item, sl,
                        this.location.x + a, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), pl.id);
                a += 10;
                Service.getInstance().dropItemMap(pl.zone, itemMap32);
            }
    }

    public void itemDropCoTile(Player pl, int item, int sl, int tile) {
            if (Util.isTrue(tile, 100)) {
                ItemMap itemMap33 = new ItemMap(pl.zone, item, sl,
                        this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), pl.id);
                Service.getInstance().dropItemMap(pl.zone, itemMap33);
                TaskService.gI().checkDoneTaskKillBoss(pl, this);// check nhiệm vụ
            }
    }
    public void itemDropCoTile1000(Player pl, int item, int sl, int tile) {
            if (Util.isTrue(tile, 50)) {
                ItemMap itemMap33 = new ItemMap(pl.zone, item, sl,
                        this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), pl.id);
                Service.getInstance().dropItemMap(pl.zone, itemMap33);
                TaskService.gI().checkDoneTaskKillBoss(pl, this);// check nhiệm vụ
            }
    }

    public void tileRoiCT_DeTu_RandomChiSo(Player pl, int caitrang, int tile, int sd1, int sd2, int hpki1, int hpki2, int tnsm1, int tnsm2, int tlvinhvien, int hansudungmax) {
//        ItemMap itemMap1 = null;
        int x = this.location.x;
        int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
        if (Util.isTrue(tile, 100)) {
            int[] set3 = {caitrang};
            ItemMap itemMap1 = new ItemMap(this.zone, set3[Util.nextInt(0, set3.length - 1)], 1, x, y, pl.id);
            itemMap1.options.add(new ItemOption(77, Util.nextInt(sd1, sd2)));
            itemMap1.options.add(new ItemOption(103, Util.nextInt(hpki1, hpki2)));
            itemMap1.options.add(new ItemOption(50, Util.nextInt(hpki1, hpki2)));
            itemMap1.options.add(new ItemOption(101, Util.nextInt(tnsm1, tnsm2)));
            itemMap1.options.add(new ItemOption(30, 1));//Không Thể Giao Dịch
            itemMap1.options.add(new ItemOption(197, 1));//Chỉ Mặc Cho Đệ Tử
            itemMap1.options.add(new ItemOption(199, 1));//Không Thể Gia Hạn
            ///thời hạn cải trang
            if (Util.isTrue(tlvinhvien, 100)) {
                //Tỉ lệ vĩnh viễn
                itemMap1.options.add(new ItemOption(73, 0));
            } else {
                itemMap1.options.add(new ItemOption(93, Util.nextInt(1, hansudungmax)));
            }
            RewardService.gI().initBaseOptionClothes(itemMap1.itemTemplate.id, itemMap1.itemTemplate.type, itemMap1.options);
            Service.getInstance().dropItemMap(zone, itemMap1);
        }
    }

    public void tileRoiCT_SuPhu_RandomChiSo(Player pl, int caitrang, int tile, int sd1, int sd2, int hpki1, int hpki2, int tnsm1, int tnsm2, int tlvinhvien, int hansudungmax) {
//        ItemMap itemMap2 = null;
        int x = this.location.x;
        int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
        if (Util.isTrue(tile, 100)) {
            int[] set3 = {caitrang};
            ItemMap itemMap2 = new ItemMap(this.zone, set3[Util.nextInt(0, set3.length - 1)], 1, x, y, pl.id);
            itemMap2.options.add(new ItemOption(0, Util.nextInt(1111, 6666)));
            itemMap2.options.add(new ItemOption(6, Util.nextInt(5555, 23456)));
            itemMap2.options.add(new ItemOption(7, Util.nextInt(5555, 23456)));
            itemMap2.options.add(new ItemOption(50, Util.nextInt(sd1, sd2)));
            itemMap2.options.add(new ItemOption(77, Util.nextInt(hpki1, hpki2)));
            itemMap2.options.add(new ItemOption(103, Util.nextInt(hpki1, hpki2)));
            itemMap2.options.add(new ItemOption(101, Util.nextInt(tnsm1, tnsm2)));
            itemMap2.options.add(new ItemOption(117, Util.nextInt(20, 45)));//kháng
            itemMap2.options.add(new ItemOption(5, Util.nextInt(20, 69)));//kháng
            itemMap2.options.add(new ItemOption(116, 1));//kháng
            itemMap2.options.add(new ItemOption(106, 1));//kháng
            itemMap2.options.add(new ItemOption(199, 1));//Không Thể Gia Hạn
            ///thời hạn cải trang
            if (Util.isTrue(tlvinhvien, 100)) {
                //Tỉ lệ vĩnh viễn
                itemMap2.options.add(new ItemOption(73, 0));
            } else {
                itemMap2.options.add(new ItemOption(93, Util.nextInt(1, hansudungmax)));
            }
            itemMap2.options.add(new ItemOption(30, 1));//Không Thể Giao Dịch
            RewardService.gI().initBaseOptionClothes(itemMap2.itemTemplate.id, itemMap2.itemTemplate.type, itemMap2.options);
            Service.getInstance().dropItemMap(zone, itemMap2);
        }
    }

    public void tileRoiNhanThanLinh(Player pl, int tile, int tileloSPL, int SoloSPLMax) {
        int x = this.location.x;
        int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
        if (Util.isTrue(tile, 100)) {
            int[] set2 = {555, 556, 563, 557, 558, 565, 559, 567, 560};
            ItemMap itemMap3 = new ItemMap(this.zone, set2[Util.nextInt(0, set2.length - 1)], 1, x, y, pl.id);
            RewardService.gI().initBaseOptionClothes(itemMap3.itemTemplate.id, itemMap3.itemTemplate.type, itemMap3.options);
            RewardService.gI().initStarOption(itemMap3, new RewardService.RatioStar[]{
                new RewardService.RatioStar((byte) Util.nextInt(1, SoloSPLMax), tileloSPL, 100),});
            Service.getInstance().dropItemMap(zone, itemMap3);
        }
    }

    public void tileRoiDoThanLinh(Player pl, int tile, int tileloSPL, int SoloSPLMax) {
        int x = this.location.x;
        int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
        if (Util.isTrue(tile, 100)) {
            int[] set2 = {555, 556, 563, 557, 558, 565, 559, 567, 560};
            ItemMap itemMap4 = new ItemMap(this.zone, set2[Util.nextInt(0, set2.length - 1)], 1, x, y, pl.id);
            RewardService.gI().initBaseOptionClothes(itemMap4.itemTemplate.id, itemMap4.itemTemplate.type, itemMap4.options);
            RewardService.gI().initStarOption(itemMap4, new RewardService.RatioStar[]{
                new RewardService.RatioStar((byte) Util.nextInt(1, SoloSPLMax), tileloSPL, 100),});
            Service.getInstance().dropItemMap(zone, itemMap4);
        }
    }

//    public void tileRoiNhanHD(Player pl,int nhanhd, int tile, int tileloSPL, int SoloSPLMax) {
//            ItemMap itemMap5 = null;
//            if (Util.isTrue(tile, 100)) {
//                itemMap5 = ratiItemHuyDiet(zone, nhanhd, 1, this.location.x - 20, this.location.y, pl.id);
//            }
//            RewardService.gI().initStarOption(itemMap5, new RewardService.RatioStar[]{
//                new RewardService.RatioStar((byte) Util.nextInt(1, SoloSPLMax), tileloSPL, 100),});
//            Service.getInstance().dropItemMap(zone, itemMap5);
//    }

    public void tileRoiDoHD(Player pl,int dohd, int tile, int tileloSPL, int SoloSPLMax) {
            if (Util.isTrue(tile, 100)) {
                ItemMap itemMap6 = ratiItemHuyDiet(zone, dohd, 1, this.location.x - 20, this.location.y, pl.id);RewardService.gI().initStarOption(itemMap6, new RewardService.RatioStar[]{
                new RewardService.RatioStar((byte) Util.nextInt(1, SoloSPLMax), tileloSPL, 100),});
            Service.getInstance().dropItemMap(zone, itemMap6);
            }
            
    }

    public void tileRoiGangNhanThienSu(Player pl, int tile, int tileloSPL, int SoloSPLMax) {
            if (Util.isTrue(tile, 100)) {
                int[] set1 = {1054, 1055, 1056, 1060, 1061, 1062};
                ItemMap itemMap7 = ratiItemThienSu(zone, set1[Util.nextInt(0, set1.length - 1)], 1, this.location.x - 20, this.location.y, pl.id);
                Service.getInstance().dropItemMap(this.zone, itemMap7);
                RewardService.gI().initStarOption(itemMap7, new RewardService.RatioStar[]{
                    new RewardService.RatioStar((byte) Util.nextInt(1, SoloSPLMax), tileloSPL, 100),});
                Service.getInstance().dropItemMap(zone, itemMap7);
            }
    }

    public void tileRoiQuanAoThienSu(Player pl, int tile, int tileloSPL, int SoloSPLMax) {
        if (Util.isTrue(tile, 100)) {
            int[] set1 = {1048, 1049, 1050, 1051, 1052, 1053, 1057, 1058, 1059};
            ItemMap itemMap8 = ratiItemThienSu(zone, set1[Util.nextInt(0, set1.length - 1)], 1, this.location.x - 20, this.location.y, pl.id);
            Service.getInstance().dropItemMap(this.zone, itemMap8);
            RewardService.gI().initStarOption(itemMap8, new RewardService.RatioStar[]{
                new RewardService.RatioStar((byte) Util.nextInt(1, SoloSPLMax), tileloSPL, 100),});
            Service.getInstance().dropItemMap(zone, itemMap8);
        }
    }

    /**
     * Đổi trạng thái máu trắng -> đỏ, chuyển trạng thái tấn công
     */
    public void changeToAttack() {
        PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.PK_ALL);
        changeStatus(ATTACK);
    }

    /**
     * Đổi trạng thái máu đỏ -> trắng, chuyển trạng thái đứng
     */
    public void changeToIdle() {
        PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.NON_PK);
        changeStatus(IDLE);
    }

    protected void chat(String text) {
        Service.getInstance().chat(this, text);
    }

    public static ItemMap ratiItemHuyDiet(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap it = new ItemMap(zone, tempId, quantity, x, y, playerId);
        List<Integer> ao = Arrays.asList(650, 652, 654);
        List<Integer> quan = Arrays.asList(651, 653, 655);
        List<Integer> gang = Arrays.asList(657, 659, 661);
        List<Integer> giay = Arrays.asList(658, 660, 662);
        int nhd = 656;
        if (ao.contains(tempId)) {
            it.options.add(new ItemOption(47, Util.highlightsItem(it.itemTemplate.gender == 2, new Random().nextInt(1001) + 1800))); // áo từ 1800-2800 giáp
        }
        if (quan.contains(tempId)) {
            it.options.add(new ItemOption(22, Util.highlightsItem(it.itemTemplate.gender == 0, new Random().nextInt(16) + 85))); // hp 85-100k
        }
        if (gang.contains(tempId)) {
            it.options.add(new ItemOption(0, Util.highlightsItem(it.itemTemplate.gender == 2, new Random().nextInt(1500) + 8500))); // 8500-10000
        }
        if (giay.contains(tempId)) {
            it.options.add(new ItemOption(23, Util.highlightsItem(it.itemTemplate.gender == 1, new Random().nextInt(11) + 80))); // ki 80-90k
        }
        if (nhd == tempId) {
            it.options.add(new ItemOption(14, new Random().nextInt(3) + 17));
        }
        it.options.add(new ItemOption(21, 80));
        return it;
    }

    public static ItemMap ratiItemThienSu(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap dots = new ItemMap(zone, tempId, quantity, x, y, playerId);
        List<Integer> ao = Arrays.asList(1048, 1049, 1050);
        List<Integer> quan = Arrays.asList(1051, 1052, 1053);
        List<Integer> gang = Arrays.asList(1054, 1055, 1056);
        List<Integer> giay = Arrays.asList(1057, 1058, 1059);
        List<Integer> nhan = Arrays.asList(1060, 1061, 1062);
        //áo
        if (ao.contains(tempId)) {
            dots.options.add(new ItemOption(47, Util.highlightsItem(dots.itemTemplate.gender == 2, new Random().nextInt(1201) + 2800))); // áo từ 2800-4000 giáp
            if (Util.isTrue(30, 100)) {
                dots.options.add(new ItemOption(108, Util.nextInt(3, 10)));
            }
        }
        //quần
        if (Util.isTrue(60, 100)) {
            if (quan.contains(tempId)) {
                dots.options.add(new ItemOption(22, Util.highlightsItem(dots.itemTemplate.gender == 0, new Random().nextInt(11) + 120))); // hp 120k-130k
            }
        } else {
            if (quan.contains(tempId)) {
                dots.options.add(new ItemOption(22, Util.highlightsItem(dots.itemTemplate.gender == 0, new Random().nextInt(51) + 130))); // hp 130-180k 15%
                if (Util.isTrue(30, 100)) {
                    dots.options.add(new ItemOption(77, Util.nextInt(5, 15)));
                }
            }
        }
        //găng
        if (Util.isTrue(60, 100)) {
            if (gang.contains(tempId)) {
                dots.options.add(new ItemOption(0, Util.highlightsItem(dots.itemTemplate.gender == 2, new Random().nextInt(7000) + 12000))); // 11000-18600
            }
        } else {
            if (gang.contains(tempId)) {
                dots.options.add(new ItemOption(0, Util.highlightsItem(dots.itemTemplate.gender == 2, new Random().nextInt(7000) + 12000))); // gang 15% 12-19k -xayda 12k1
                if (Util.isTrue(30, 100)) {
                    dots.options.add(new ItemOption(50, Util.nextInt(3, 10)));
                }
            }
        }
        //giày
        if (Util.isTrue(60, 100)) {
            if (giay.contains(tempId)) {
                dots.options.add(new ItemOption(23, Util.highlightsItem(dots.itemTemplate.gender == 1, new Random().nextInt(21) + 120))); // ki 90-110k
            }
        } else {
            if (giay.contains(tempId)) {
                dots.options.add(new ItemOption(23, Util.highlightsItem(dots.itemTemplate.gender == 1, new Random().nextInt(21) + 130))); // ki 110-130k
                if (Util.isTrue(30, 100)) {
                    dots.options.add(new ItemOption(103, Util.nextInt(5, 15)));
                }
            }
        }

        if (nhan.contains(tempId)) {
            dots.options.add(new ItemOption(14, Util.highlightsItem(dots.itemTemplate.gender == 1, new Random().nextInt(3) + 18))); // nhẫn 18-20%
            if (Util.isTrue(30, 100)) {
                dots.options.add(new ItemOption(117, Util.nextInt(3, 10)));
            }
        }
        dots.options.add(new ItemOption(21, 120));
        return dots;
    }
    
    private static long clampToLong(double v) {
        if (!Double.isFinite(v) || v < 1) {
            return 1;
        }
        if (v >= Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        }
        return (long) v;
    }
    
}
