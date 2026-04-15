package nro.models.player;

import nro.models.mob.Mob;
import nro.models.skill.Skill;
import nro.services.SkillService;

import java.util.ArrayList;
import java.util.List;

// ++ Thay Timer bằng ScheduledExecutor
import nro.core.concurrent.GameScheduler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Admin
 */
public class SkillSpecial {

    public static final int TIME_GONG = 2000;
    public static final int TIME_END_24_25 = 3000;
    public static final int TIME_END_26 = 11000;

    private Player player;

    public SkillSpecial(Player player) {
        this.player = player;
        this.playersTaget = new ArrayList<>();
        this.mobsTaget = new ArrayList<>();
    }

    public Skill skillSpecial;

    public byte dir;

    public short _xPlayer;

    public short _yPlayer;

    public short _xObjTaget;

    public short _yObjTaget;

    public List<Player> playersTaget;

    public List<Mob> mobsTaget;

    public boolean isStartSkillSpecial;

    public byte stepSkillSpecial;

    public long lastTimeSkillSpecial;

    // ===== Scheduler state =====
    private ScheduledFuture<?> loop;
    private boolean isActive = false;

    private void update() {
        if (this.isStartSkillSpecial == true) { // giữ nguyên logic cũ
            SkillService.gI().updateSkillSpecial(player);
        }
    }

    public void setSkillSpecial(byte dir, short _xPlayer, short _yPlayer, short _xObjTaget, short _yObjTaget) {
        this.skillSpecial = this.player.playerSkill.skillSelect;
        if (skillSpecial.currLevel < 1000) {
            skillSpecial.currLevel++;
            SkillService.gI().sendCurrLevelSpecial(player, skillSpecial);
        }
        this.dir = dir;
        this._xPlayer = _xPlayer;
        this._yPlayer = _yPlayer;
        this._xObjTaget = _xObjTaget;
        this._yObjTaget = _yObjTaget;
        this._xObjTaget = (short) (skillSpecial.dx + skillSpecial.point * 75); // skill độ dài
        this._yObjTaget = (short) skillSpecial.dy;

        this.isStartSkillSpecial = true;
        this.stepSkillSpecial = 0;
        this.lastTimeSkillSpecial = System.currentTimeMillis();

        this.start(250); // delay tick
    }

    public void closeSkillSpecial() {
        this.isStartSkillSpecial = false;
        this.stepSkillSpecial = 0;
        this.playersTaget.clear();
        this.mobsTaget.clear();
        this.close();
    }

    private void close() {
        try {
            this.isActive = false;
            if (this.loop != null) {
                this.loop.cancel(false);
            }
        } catch (Exception ignore) {
        } finally {
            this.loop = null;
        }
    }

    public void start(int leep) {
        if (!this.isActive) {
            this.isActive = true;
            // Bọc runnable để tránh exception làm dừng chu kỳ
            Runnable safe = () -> {
                try {
                    SkillSpecial.this.update();
                } catch (Throwable t) {
                    t.printStackTrace(); // thay bằng logger nếu có
                }
            };
            // chạy định kỳ: initialDelay = leep, period = leep
            this.loop = GameScheduler.SCHED.scheduleAtFixedRate(
                    safe, leep, leep, TimeUnit.MILLISECONDS);
        }
    }

    public void dispose() {
        // đóng scheduler cho skill này
        close();
        this.player = null;
        this.skillSpecial = null;
    }
}
