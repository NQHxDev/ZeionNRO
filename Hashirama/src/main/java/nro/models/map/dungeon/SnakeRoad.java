/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nro.models.map.dungeon;

import nro.consts.ConstItem;
import nro.consts.ConstMap;
import nro.consts.ConstPlayer;
import nro.lib.RandomCollection;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.cdrd.CBoss;
import nro.models.boss.cdrd.Cadich;
import nro.models.boss.cdrd.Nadic;
import nro.models.boss.cdrd.Saibamen;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.map.Map;
import nro.models.map.dungeon.zones.ZSnakeRoad;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.services.MapService;
import nro.services.Service;
import nro.utils.Util;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
@Getter
@Setter
public class SnakeRoad extends Dungeon {

    protected final List<CBoss> bosses = new ArrayList<>();

    public SnakeRoad(int level) {
        super(level);
        setType(Dungeon.SNAKE_ROAD);
        setName("Con đường rắn độc");
        setTitle("Con đường rắn độc");
        setCountDown(30 * 60);// 30p
        initBoss();
    }

    @Override
    protected void init() {
        Map map = MapService.gI().getMapById(ConstMap.CON_DUONG_RAN_DOC);
        addZone(new ZSnakeRoad(map, this));
        map = MapService.gI().getMapById(ConstMap.CON_DUONG_RAN_DOC_142);
        addZone(new ZSnakeRoad(map, this));
        map = MapService.gI().getMapById(ConstMap.CON_DUONG_RAN_DOC_143);
        addZone(new ZSnakeRoad(map, this));
        map = MapService.gI().getMapById(ConstMap.THAN_DIEN);
        addZone(new ZSnakeRoad(map, this));
        map = MapService.gI().getMapById(ConstMap.THAP_KARIN);
        addZone(new ZSnakeRoad(map, this));
        map = MapService.gI().getMapById(ConstMap.RUNG_KARIN);
        addZone(new ZSnakeRoad(map, this));
        map = MapService.gI().getMapById(ConstMap.HOANG_MAC);
        addZone(new ZSnakeRoad(map, this));
    }

    public void addBoss(CBoss boss) {
        synchronized (bosses) {
            bosses.add(boss);
        }
    }

    public void removeBoss(CBoss boss) {
        synchronized (bosses) {
            bosses.remove(boss);
        }
    }

    public CBoss getBoss(int index) {
        synchronized (bosses) {
            if (index < 0 || index >= bosses.size()) {
                return null;
            }
            return bosses.get(index);
        }
    }

    public void initBoss() {
        int num = -999;
        for (int i = 0; i < 5; i++) {
            short x = (short) (400 + (24 * i));
            short y = 336;
            BossData data = BossData.builder()
                    .name("Số " + (i + 1))
                    .gender(ConstPlayer.XAYDA)
                    .typeDame(Boss.DAME_NORMAL)
                    .typeHp(Boss.HP_NORMAL)
                    .dame(1000 * level)
                    .hp(new double[][]{{20000 * level}})
                    .outfit(new short[]{642, 643, 644})
                    .skillTemp(new int[][]{{Skill.DRAGON, 1, 100}, {Skill.DRAGON, 2, 200}, {Skill.DRAGON, 3, 300}, {Skill.DRAGON, 7, 700}, {Skill.TU_SAT, 1, 100}})
                    .secondsRest(BossData._0_GIAY)
                    .build();
            data.joinMapIdle = (i != 0);
            Saibamen saibamen = new Saibamen(num++, x, y, this, data);
            addBoss(saibamen);
        }
        BossData nadic = BossData.builder()
                .name("Nađíc")
                .gender(ConstPlayer.XAYDA)
                .typeDame(Boss.DAME_NORMAL)
                .typeHp(Boss.HP_NORMAL)
                .dame(100 * level * level)
                .hp(new double[][]{{1000000 * level * level}})
                .outfit(new short[]{648, 649, 650})
                .skillTemp(new int[][]{{Skill.GALICK, 3, 300}, {Skill.GALICK, 7, 700}, {Skill.ANTOMIC, 5, 500}})
                .secondsRest(BossData._0_GIAY)
                .build();
        nadic.joinMapIdle = true;
        Nadic bNadic = new Nadic(num++, (short) 520, (short) 336, this, nadic);
        addBoss(bNadic);
        BossData cadic = BossData.builder()
                .name("Cađích")
                .gender(ConstPlayer.XAYDA)
                .typeDame(Boss.DAME_NORMAL)
                .typeHp(Boss.HP_NORMAL)
                .dame(150 * level * level)
                .hp(new double[][]{{1500000 * level * level}})
                .outfit(new short[]{645, 646, 647})
                .skillTemp(new int[][]{{Skill.GALICK, 7, 700}, {Skill.ANTOMIC, 7, 1000}, {Skill.TAI_TAO_NANG_LUONG, 1, 20000}, {Skill.BIEN_KHI, 7, 60000}})
                .secondsRest(BossData._0_GIAY)
                .build();
        cadic.joinMapIdle = true;
        Cadich bCadic = new Cadich(num++, (short) 532, (short) 336, this, cadic);
        addBoss(bCadic);
    }

    @Override
    public void update() {
        synchronized (bosses) {
            boolean isAllDead = true;
            List<CBoss> list = bosses.stream().collect(Collectors.toList());
            for (CBoss boss : list) {
                boss.update();
                if (!boss.isDie()) {
                    isAllDead = false;
                }
            }
            if (isAllDead) {
                finish();
            }
        }
        super.update();
    }

    @Override
    public void join(Player player) {
        player.setInteractWithKarin(false);
        ZSnakeRoad road = (ZSnakeRoad) find(ConstMap.CON_DUONG_RAN_DOC_143);
        road.enter(player, 1110, 336);
    }

    @Override
    public void finish() {
        if (!finish) {
            finish = true;
            setTime(60);
            sendNotification("Trận chiến với người Xayda sẽ kết thúc sau 60 giây nữa");
            RandomCollection<Integer> rc = new RandomCollection<>();
            rc.add(300, ConstItem.VANG);
            rc.add(level, ConstItem.THE_GIA_HAN);
            int quantity = level / 10;
            if (quantity < 3) {
                quantity = 3;
            }
            ZSnakeRoad r = (ZSnakeRoad) find(ConstMap.HOANG_MAC);
            for (int i = 0; i < quantity; i++) {
                int itemID = rc.next();
                int q = 1;
                if (itemID == ConstItem.VANG_188) {
                    q = 30000;
                }
                ItemMap itemMap = new ItemMap(r, itemID, q, 350 + (i * 10), 312, -1);
                if (level < 80 && (itemID == ConstItem.THE_GIA_HAN)) {// || itemID == ConstItem.VE_TANG_NGOC_2023
                    itemMap.options.add(new ItemOption(30, 0));
                }
                Service.getInstance().dropItemMap(r, itemMap);
            }
            if (level >= 80) {
                int num = Util.nextInt(2,6);
                for (int i = 0; i < num; i++) {
                    ItemMap itemMap = new ItemMap(r, ConstItem.THOI_VANG, Util.nextInt(1, 5), 250+ (i * 20), 312, -1);
                    Service.getInstance().dropItemMap(r, itemMap);
                }
            }
        }
    }

}
