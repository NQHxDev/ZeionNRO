package nro.models.skill;

import nro.consts.Cmd;
import nro.models.player.Player;
import nro.server.io.Message;
import nro.services.Service;

// đổi Timer -> Scheduler dùng chung
import nro.core.concurrent.GameScheduler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright
 */
public class PlayerSkill {
    // ❌ BỎ: public Timer timer;
    private final List<ScheduledFuture<?>> scheduledTasks = new ArrayList<>();

    private Player player;
    public List<Skill> skills;
    public Skill skillSelect;

    public PlayerSkill(Player player) {
        this.player = player;
        this.skills = new ArrayList<>();
        // ❌ BỎ: timer = new Timer(true);
    }

    /* ================== Helpers dùng Scheduler ================== */

    /** Lập lịch chạy 1 lần sau delayMs; tự lưu future để hủy khi dispose() */
    public ScheduledFuture<?> scheduleOnce(Runnable r, long delayMs) {
        ScheduledFuture<?> f = GameScheduler.SCHED.schedule(safe(r), delayMs, TimeUnit.MILLISECONDS);
        scheduledTasks.add(f);
        return f;
    }

    /** Lập lịch định kỳ; tự lưu future để hủy khi dispose() */
    public ScheduledFuture<?> scheduleFixedRate(Runnable r, long initialDelayMs, long periodMs) {
        ScheduledFuture<?> f = GameScheduler.SCHED.scheduleAtFixedRate(safe(r),
                initialDelayMs, periodMs, TimeUnit.MILLISECONDS);
        scheduledTasks.add(f);
        return f;
    }

    /** Bọc runnable để không làm chết task nếu có exception */
    private Runnable safe(Runnable r) {
        return () -> {
            try { r.run(); } catch (Throwable t) {
                // TODO: thay bằng logger của bạn
                t.printStackTrace();
            }
        };
    }

    /* ================== Logic cũ giữ nguyên ================== */

    public Skill getSkillbyId(int id) {
        for (Skill skill : skills) {
            if (skill.template.id == id) {
                return skill;
            }
        }
        return null;
    }

    public byte[] skillShortCut = new byte[99];

    public void sendSkillShortCut() {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 61);
            msg.writer().writeUTF("KSkill");
            msg.writer().writeInt(skillShortCut.length);
            msg.writer().write(skillShortCut);
            player.sendMessage(msg);
            msg.cleanup();

            msg = Service.getInstance().messageSubCommand((byte) 61);
            msg.writer().writeUTF("OSkill");
            msg.writer().writeInt(skillShortCut.length);
            msg.writer().write(skillShortCut);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception ignored) {}
    }

    public void sendSkillShortCutNew() {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand(Cmd.CHANGE_ONSKILL);
            msg.writer().writeInt(skillShortCut.length);
            msg.writer().write(skillShortCut);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception ignored) {}
    }

    public boolean prepareQCKK;
    public boolean prepareTuSat;
    public boolean prepareLaze;
    public long lastTimeUseQCKK;

    public byte getIndexSkillSelect() {
        switch (skillSelect.template.id) {
            case Skill.DRAGON:
            case Skill.DEMON:
            case Skill.GALICK:
            case Skill.KAIOKEN:
            case Skill.LIEN_HOAN:
                return 1;
            case Skill.KAMEJOKO:
            case Skill.ANTOMIC:
            case Skill.MASENKO:
                return 2;
            default:
                return 3;
        }
    }

    public byte getSizeSkill() {
        byte size = 0;
        for (Skill skill : skills) {
            if (skill.skillId != -1) {
                size++;
            }
        }
        return size;
    }

    public void dispose() {
        // Hủy toàn bộ task đã lập lịch cho player này
        for (ScheduledFuture<?> f : scheduledTasks) {
            try { if (f != null) f.cancel(false); } catch (Exception ignored) {}
        }
        scheduledTasks.clear();

        this.player = null;
        this.skillSelect = null;
        this.skills = null;
    }
}
