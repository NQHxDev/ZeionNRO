package nro.models.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
@Setter
@Getter
@AllArgsConstructor
public class AchivementTemplate {

    private int id;

    private String name;

    private String detail;

    private int money;

    private int maxCount;
}
