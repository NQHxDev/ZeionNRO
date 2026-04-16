package nro.models.skill;

import java.util.ArrayList;
import java.util.List;

public class SkillTemplate {

   public byte id;

   public int classId;

   public String name;

   public int maxPoint;

   public int manaUseType;

   public int type;

   public int iconId;

   public String description;

   public Skill[] skills;

   public List<Skill> skillss = new ArrayList<>();

   public String damInfo;

}
