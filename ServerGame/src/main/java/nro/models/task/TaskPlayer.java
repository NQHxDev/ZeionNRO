package nro.models.task;

import java.util.ArrayList;
import java.util.List;

public class TaskPlayer {

   public TaskMain taskMain;

   public SideTask sideTask;
   public List<Achivement> achivements;

   public TaskPlayer() {
      this.sideTask = new SideTask();
      this.achivements = new ArrayList<>();
   }

   public void dispose() {
      this.taskMain = null;
      this.sideTask = null;
   }

}
