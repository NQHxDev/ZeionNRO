package nro.models.task;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nro.consts.ConstAchive;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */

@Setter
@Getter
@NoArgsConstructor
public class Achivement {

    private int id;

    private String name;

    private String detail;

    private int money;


    public int count;

    private int maxCount;

    private boolean isFinish;

    private boolean isReceive;

    public boolean isDone() {
        return this.count >= this.maxCount;
    }

    public boolean isDone(int divisor) {
        return this.count / divisor >= this.maxCount / divisor;
    }

    public int getCount() {
        return this.count;
    }

    public int getMaxCount() {
        return this.maxCount;
    }
}
