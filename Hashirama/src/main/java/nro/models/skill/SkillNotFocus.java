package nro.models.skill;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
@Setter
@Getter
public class SkillNotFocus extends Skill {

    private int timePre;

    private int timeDame;

    private short range;

    public SkillNotFocus(Skill skill) {
        super(skill);
        this.timePre = 2000;
        this.timeDame = 5000;
        this.range = 250;
    }
}
