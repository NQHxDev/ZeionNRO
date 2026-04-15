package nro.models.map;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
@Getter
@Builder
public class EffectEventTemplate {
    private int mapId;
    private int eventId;
    private int effId;
    private int layer;
    private int x;
    private int y;
    private int loop;
    private int delay;
}
