package nro.models.map.challenge;

import nro.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
public class MartialCongressManager {

    private static MartialCongressManager i;
    private long lastUpdate;
    private static List<MartialCongress> list = new ArrayList<>();
    private static List<MartialCongress> toRemove = new ArrayList<>();

    public static MartialCongressManager gI() {
        if (i == null) {
            i = new MartialCongressManager();
        }
        return i;
    }

    public void update() {
        if (Util.canDoWithTime(lastUpdate, 1000)) {
            lastUpdate = System.currentTimeMillis();
            synchronized (list) {
                for (MartialCongress mc : list) {
                    try {
                        mc.update();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                list.removeAll(toRemove);
            }
        }
    }

    public void add(MartialCongress mc) {
        synchronized (list) {
            list.add(mc);
        }
    }

    public void remove(MartialCongress mc) {
        synchronized (toRemove) {
            toRemove.add(mc);
        }
    }
}
