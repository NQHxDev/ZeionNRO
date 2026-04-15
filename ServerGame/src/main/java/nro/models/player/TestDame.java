package nro.models.player;

import nro.consts.ConstPlayer;
import nro.models.map.Map;
import nro.models.map.Zone;
import nro.server.Manager;
import nro.services.MapService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.utils.Util;

/**
 * Test Dame - Player giả dạng bia test dame (spawn theo kiểu referee/zone-managed)
 * @author Hoàng Việt
 */
public class TestDame extends Player {

    // =========================
    // CẤU HÌNH
    // =========================
    private static final String NAME = "Test Dame";
    private static final int[] MAP_IDS = {42, 43, 44};

    // Tọa độ mặc định theo map (bạn đang dùng)
    private static int getSpawnX(int mapId) {
        switch (mapId) {
            case 42: return 1014;
            case 43: return 1108;
            case 44: return 1168;
            default: return 200;
        }
    }

    private static int getSpawnY(int mapId) {
        switch (mapId) {
            case 42: return 408;
            case 43: return 432;
            case 44: return 432;
            default: return 200;
        }
    }

    // HP cực lớn (giữ kiểu double như bạn đang set)
//    private static final double HP_MAX = 9.0e10;      // ~ 9 * 10^40
    private static final double HP_MAX = 8.9e9;      // ~ 9 * 10^40
    private static final long HP_REGEN = 100_000_000_000L;

    // id âm cho player giả
    private static int NEXT_ID = -1000045;

    // =========================
    // ENTRYPOINT (được Manager gọi)
    // =========================
    public void initTestDame() {
        init();
    }

    // =========================
    // SKIN
    // =========================
    @Override
    public short getHead() {
        return 1720;
    }

    @Override
    public short getBody() {
        return 1721;
    }

    @Override
    public short getLeg() {
        return 1722;
    }

    @Override
    public int version() {
        return 214;
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public void update() {
        // luôn PK ALL để người chơi đánh được
        PlayerService.gI().changeTypePK(this, ConstPlayer.PK_ALL);

        // tự hồi sinh vô hạn để làm bia test
        if (this.isDie()) {
            Service.getInstance().sendMoney(this);

            // hoiSinh() sẽ trừ ngọc/vàng nếu là player thật -> không dùng
            // => hồi trực tiếp
            Service.getInstance().hsChar(this, this.nPoint.hpMax, this.nPoint.mpMax);
            PlayerService.gI().sendInfoHpMp(this);
        }
    }

    // =========================
    // SPAWN LOGIC
    // =========================
    private void init() {
        for (Map m : Manager.MAPS) {
            if (!isTargetMap(m.mapId)) {
                continue;
            }
            for (Zone z : m.zones) {
                spawnOneInZone(z);
            }
        }
    }

    private boolean isTargetMap(int mapId) {
        for (int id : MAP_IDS) {
            if (id == mapId) return true;
        }
        return false;
    }

    private void spawnOneInZone(Zone z) {
        if (z == null || z.map == null) return;

        TestDame pl = new TestDame();

        // ĐÁNH DẤU: player giả -> tránh bị coi như player thường ở vài logic map/zone
        pl.isBot = true;          // quan trọng để hạn chế bị xử lý như player thật
        pl.isPet = false;
        pl.isBoss = false;

        pl.name = NAME;
        pl.gender = 0;
        pl.id = NEXT_ID++;

        // stats
        pl.nPoint.hpMax = HP_MAX;
        pl.nPoint.hpg = HP_REGEN;
        pl.nPoint.hp = HP_MAX;
        pl.nPoint.setFullHpMp();

        // set tọa độ + clamp để không bị out-of-map (hay gây “không thấy”)
        int x = getSpawnX(z.map.mapId);
        int y = getSpawnY(z.map.mapId);

        // clamp X tối thiểu 60, tối đa mapWidth-60 (giống logic bạn hay dùng)
        int maxX = Math.max(120, z.map.mapWidth - 60);
        if (x < 60) x = 60;
        if (x > maxX) x = maxX;

        // clamp Y cơ bản (tránh âm)
        if (y < 0) y = 0;

        pl.location.x = x;
        pl.location.y = y;

        // Cho vào map
        // goToMap sẽ set zone cho pl
        MapService.gI().goToMap(pl, z);

        // Gửi cho người trong khu thấy ngay
        z.load_Me_To_Another(pl);

        // QUAN TRỌNG:
        // Bạn đang dùng cơ chế referee, nên gắn vào zone để zone giữ reference/update
        z.setReferee(pl);
    }
}
