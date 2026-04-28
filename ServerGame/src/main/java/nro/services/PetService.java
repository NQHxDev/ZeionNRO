package nro.services;

import nro.consts.ConstPlayer;
import nro.core.GameScheduler;
import nro.models.player.Pet;
import nro.models.player.Player;

import nro.utils.SkillUtil;
import nro.utils.Util;
import java.util.concurrent.TimeUnit;

public class PetService {

   private static PetService i;

   public static PetService gI() {
      if (i == null) {
         i = new PetService();
      }
      return i;
   }

   public void createNormalPet(Player player, int gender, byte... limitPower) {
      GameScheduler.SCHED.submit(() -> {
         try {
            createNewPet(player, false, false, false, false, false, false, false, false, (byte) gender);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            GameScheduler.SCHED.schedule(() -> {
               try {
                  Service.getInstance().chatJustForMe(player, player.pet, "Xin hãy thu nhận làm đệ tử");
               } catch (Exception e) {
               }
            }, 1, TimeUnit.SECONDS);
         } catch (Exception e) {
         }
      });
   }

   public void createNormalPet(Player player, byte... limitPower) {
      GameScheduler.SCHED.submit(() -> {
         try {
            createNewPet(player, false, false, false, false, false, false, false, false);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            GameScheduler.SCHED.schedule(() -> {
               try {
                  Service.getInstance().chatJustForMe(player, player.pet, "Xin hãy thu nhận làm đệ tử");
               } catch (Exception e) {
               }
            }, 1, TimeUnit.SECONDS);
         } catch (Exception e) {
         }
      });
   }

   public void createMabuPet(Player player, byte... limitPower) {
      GameScheduler.SCHED.submit(() -> {
         try {
            createNewPet(player, true, false, false, false, false, false, false, false);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            GameScheduler.SCHED.schedule(() -> {
               try {
                  Service.getInstance().chatJustForMe(player, player.pet, "Oa oa oa...");
               } catch (Exception e) {
               }
            }, 1, TimeUnit.SECONDS);
         } catch (Exception e) {
         }
      });
   }

   public void createMabuPet(Player player, int gender, byte... limitPower) {
      GameScheduler.SCHED.submit(() -> {
         try {
            createNewPet(player, true, false, false, false, false, false, false, false, (byte) gender);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            GameScheduler.SCHED.schedule(() -> {
               try {
                  Service.getInstance().chatJustForMe(player, player.pet, "Oa oa oa...");
               } catch (Exception e) {
               }
            }, 1, TimeUnit.SECONDS);
         } catch (Exception e) {
         }
      });
   }

   public void createBerusPet(Player player, byte... limitPower) {
      GameScheduler.SCHED.submit(() -> {
         try {
            createNewPet(player, false, true, false, false, false, false, false, false);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            GameScheduler.SCHED.schedule(() -> {
               try {
                  Service.getInstance().chatJustForMe(player, player.pet,
                        "Thần hủy diệt hiện thân tất cả quỳ xuống...");
               } catch (Exception e) {
               }
            }, 1, TimeUnit.SECONDS);
         } catch (Exception e) {
         }
      });
   }

   public void createBerusPet(Player player, int gender, byte... limitPower) {
      GameScheduler.SCHED.submit(() -> {
         try {
            createNewPet(player, false, true, false, false, false, false, false, false, (byte) gender);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            GameScheduler.SCHED.schedule(() -> {
               try {
                  Service.getInstance().chatJustForMe(player, player.pet,
                        "Thần hủy diệt hiện thân tất cả quỳ xuống...");
               } catch (Exception e) {
               }
            }, 1, TimeUnit.SECONDS);
         } catch (Exception e) {
         }
      });
   }

   public void createPicPet(Player player, byte... limitPower) {
      GameScheduler.SCHED.submit(() -> {
         try {
            createNewPet(player, false, false, true, false, false, false, false, false);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet,
                  "Tháº§n cá»§a má»�i Tháº§n, Ta lÃ  Zeno tá»¥i mÃ y quá»³ xuá»‘ng...");
         } catch (Exception e) {
         }
      });
   }

   public void createPicPet(Player player, int gender, byte... limitPower) {
      GameScheduler.SCHED.submit(() -> {
         try {
            createNewPet(player, false, false, true, false, false, false, false, false, (byte) gender);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet,
                  "Tháº§n cá»§a má»�i Tháº§n, Ta lÃ  Zeno tá»¥i mÃ y quá»³ xuá»‘ng...");
         } catch (Exception e) {
         }
      });
   }

   public void createKaidoPet(Player player, byte... limitPower) {
      GameScheduler.SCHED.submit(() -> {
         try {
            createNewPet(player, false, false, false, true, false, false, false, false);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            GameScheduler.SCHED.schedule(() -> {
               try {
                  Service.getInstance().chatJustForMe(player, player.pet, "Lại đây để tao gõ mày một cái");
               } catch (Exception e) {
               }
            }, 1, TimeUnit.SECONDS);
         } catch (Exception e) {
         }
      });
   }

   public void createKaidoPet(Player player, int gender, byte... limitPower) {
      GameScheduler.SCHED.submit(() -> {
         try {
            createNewPet(player, false, false, false, true, false, false, false, false, (byte) gender);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            GameScheduler.SCHED.schedule(() -> {
               try {
                  Service.getInstance().chatJustForMe(player, player.pet, "Lại đây để tao gõ mày một cái");
               } catch (Exception e) {
               }
            }, 1, TimeUnit.SECONDS);
         } catch (Exception e) {
         }
      });
   }/// Nữa nè

   public void createItachiPet(Player player, byte... limitPower) {
      GameScheduler.SCHED.submit(() -> {
         try {
            createNewPet(player, false, false, false, false, true, false, false, false);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "NhÃ¬n gÃ¬, chÃ©m cháº¿t cá»¥ mÃ y giá»�");
         } catch (Exception e) {
         }
      });
   }

   public void createItachiPet(Player player, int gender, byte... limitPower) {
      GameScheduler.SCHED.submit(() -> {
         try {
            createNewPet(player, false, false, false, false, true, false, false, false, (byte) gender);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "NhÃ¬n gÃ¬, chÃ©m cháº¿t cá»¥ mÃ y giá»�");
         } catch (Exception e) {
         }
      });
   }

   public void createAndroidPet(Player player, byte... limitPower) {
      GameScheduler.SCHED.submit(() -> {
         try {
            createNewPet(player, false, false, false, false, false, true, false, false);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "NhÃ¬n gÃ¬, chÃ©m cháº¿t cá»¥ mÃ y giá»�");
         } catch (Exception e) {
         }
      });
   }

   public void createAndroidPet(Player player, int gender, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPet(player, false, false, false, false, false, true, false, false, (byte) gender);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "NhÃ¬n gÃ¬, chÃ©m cháº¿t cá»¥ mÃ y giá»�");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createNgoKoPet(Player player, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPet(player, false, false, false, false, false, false, true, false);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Ä�á»¡ má»™t gáº­y cá»§a lÃ£o tÃ´n");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createNgoKoPet(Player player, int gender, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPet(player, false, false, false, false, false, false, true, false, (byte) gender);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Ä�á»¡ má»™t gáº­y cá»§a lÃ£o tÃ´n");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createSoi3DauPet(Player player, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPet(player, false, false, false, false, false, false, false, true);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Bái Kiến Chủ Nhân");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createSoi3DauPet(Player player, int gender, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPet(player, false, false, false, false, false, false, false, true, (byte) gender);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Bái Kiến Chủ Nhân");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createDaiThanhPet(Player player, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPetNew(player, true, false, false, false, false, false, false, false);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Bái Kiến Chủ Nhân");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createDaiThanhPet(Player player, int gender, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPetNew(player, true, false, false, false, false, false, false, false, (byte) gender);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Bái Kiến Chủ Nhân");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createPetNew2(Player player, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPetNew(player, false, true, false, false, false, false, false, false);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Bái Kiến Chủ Nhân");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createPetNew2(Player player, int gender, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPetNew(player, false, true, false, false, false, false, false, false, (byte) gender);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Bái Kiến Chủ Nhân");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createPetNew3(Player player, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPetNew(player, false, false, true, false, false, false, false, false);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Bái Kiến Chủ Nhân");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createPetNew3(Player player, int gender, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPetNew(player, false, false, true, false, false, false, false, false, (byte) gender);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Bái Kiến Chủ Nhân");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createPetNew4(Player player, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPetNew(player, false, false, false, true, false, false, false, false);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Bái Kiến Chủ Nhân");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createPetNew4(Player player, int gender, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPetNew(player, false, false, false, true, false, false, false, false, (byte) gender);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Bái Kiến Chủ Nhân");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createPetNew5(Player player, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPetNew(player, false, false, false, false, true, false, false, false);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Bái Kiến Chủ Nhân");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createPetNew5(Player player, int gender, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPetNew(player, false, false, false, false, true, false, false, false, (byte) gender);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Bái Kiến Chủ Nhân");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createPetNew6(Player player, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPetNew(player, false, false, false, false, false, true, false, false);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Bái Kiến Chủ Nhân");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createPetNew6(Player player, int gender, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPetNew(player, false, false, false, false, false, true, false, false, (byte) gender);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Bái Kiến Chủ Nhân");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createPetNew7(Player player, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPetNew(player, false, false, false, false, false, false, true, false);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Bái Kiến Chủ Nhân");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createPetNew7(Player player, int gender, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPetNew(player, false, false, false, false, false, false, true, false, (byte) gender);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Bái Kiến Chủ Nhân");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createPetNew8(Player player, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPetNew(player, false, false, false, false, false, false, false, true);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Bái Kiến Chủ Nhân");
         } catch (Exception e) {
         }
      }).start();
   }

   public void createPetNew8(Player player, int gender, byte... limitPower) {
      new Thread(() -> {
         try {
            createNewPetNew(player, false, false, false, false, false, false, false, true, (byte) gender);
            if (limitPower != null && limitPower.length == 1) {
               player.pet.nPoint.limitPower = limitPower[0];
            }
            Thread.sleep(1000);
            Service.getInstance().chatJustForMe(player, player.pet, "Bái Kiến Chủ Nhân");
         } catch (Exception e) {
         }
      }).start();
   }

   public void changeNormalPet(Player player, int gender) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createNormalPet(player, gender, limitPower);
   }

   public void changeNormalPet(Player player) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createNormalPet(player, limitPower);
   }

   public void changeMabuPet(Player player) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createPetNew4(player, limitPower);
   }

   public void changeMabuPet(Player player, int gender) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createPetNew4(player, gender, limitPower);
   }

   public void changeBerusPet(Player player) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createBerusPet(player, limitPower);
   }

   public void changeBerusPet(Player player, int gender) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createBerusPet(player, gender, limitPower);
   }

   public void changePicPet(Player player) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createPicPet(player, limitPower);
   }

   public void changePicPet(Player player, int gender) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createPicPet(player, gender, limitPower);
   }

   public void changeKaidoPet(Player player) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createKaidoPet(player, limitPower);
   }

   public void changeKaidoPet(Player player, int gender) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createKaidoPet(player, gender, limitPower);
   }

   public void changeItachiPet(Player player) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createItachiPet(player, limitPower);
   }

   public void changeItachiPet(Player player, int gender) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createItachiPet(player, gender, limitPower);
   }

   public void changeAndroidPet(Player player) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createAndroidPet(player, limitPower);
   }

   public void changeAndroidPet(Player player, int gender) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createAndroidPet(player, gender, limitPower);
   }

   public void changeNgoKoPet(Player player) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createNgoKoPet(player, limitPower);
   }

   public void changeNgoKoPet(Player player, int gender) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createNgoKoPet(player, gender, limitPower);
   }

   public void changeSoi3DauPet(Player player) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createSoi3DauPet(player, limitPower);
   }

   public void changeSoi3DauPet(Player player, int gender) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createSoi3DauPet(player, gender, limitPower);
   }

   public void changePetDaiThanh(Player player) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createDaiThanhPet(player, limitPower);
   }

   public void changePetDaiThanh(Player player, int gender) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createDaiThanhPet(player, gender, limitPower);
   }

   public void changePetNew2(Player player) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createPetNew2(player, limitPower);
   }

   public void changePetNew2(Player player, int gender) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createPetNew2(player, gender, limitPower);
   }

   public void changePetNew3(Player player) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createPetNew3(player, limitPower);
   }

   public void changePetNew3(Player player, int gender) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createPetNew3(player, gender, limitPower);
   }

   public void changePetNew4(Player player) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createPetNew4(player, limitPower);
   }

   public void changePetNew4(Player player, int gender) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createPetNew4(player, gender, limitPower);
   }

   public void changePetNew5(Player player) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createPetNew5(player, limitPower);
   }

   public void changePetNew5(Player player, int gender) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createPetNew5(player, gender, limitPower);
   }

   public void changePetNew6(Player player) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createPetNew6(player, limitPower);
   }

   public void changePetNew6(Player player, int gender) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createPetNew6(player, gender, limitPower);
   }

   public void changePetNew7(Player player) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createPetNew7(player, limitPower);
   }

   public void changePetNew7(Player player, int gender) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createPetNew7(player, gender, limitPower);
   }

   public void changePetNew8(Player player) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createPetNew8(player, limitPower);
   }

   public void changePetNew8(Player player, int gender) {
      byte limitPower = player.pet.nPoint.limitPower;
      if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
         player.pet.unFusion();
      }
      MapService.gI().exitMap(player.pet);
      player.pet.dispose();
      player.pet = null;
      createPetNew8(player, gender, limitPower);
   }

   public void changeNamePet(Player player, String name) {
      try {
         if (!InventoryService.gI().existItemBag(player, 400)) {
            Service.getInstance().sendThongBao(player, "Bạn cần thẻ đặt tên đệ tử, mua tại Santa");
            return;
         } else if (Util.haveSpecialCharacter(name)) {
            Service.getInstance().sendThongBao(player, "Tên không được chứa ký tự đặc biệt");
            return;
         } else if (name.length() > 10) {
            Service.getInstance().sendThongBao(player, "Tên quá dài");
            return;
         }
         MapService.gI().exitMap(player.pet);
         player.pet.name = "$" + name.toLowerCase().trim();
         InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBagByIndex(player, 400), 1);
         new Thread(() -> {
            try {
               Thread.sleep(1000);
               Service.getInstance().chatJustForMe(player, player.pet,
                     "Cảm ơn sư phụ đã đặt cho con tên " + name);
            } catch (Exception e) {
            }
         }).start();
      } catch (Exception ex) {

      }
   }

   private long[] getDataPetNormal() {
      long[] petData = new long[5];

      petData[0] = Util.nextInt(400, 1050) * 20; // hp
      petData[1] = Util.nextInt(400, 1050) * 20; // mp
      petData[2] = Util.nextInt(200, 450); // dame
      petData[3] = Util.nextInt(90, 500); // def
      petData[4] = Util.nextInt(0, 2); // crit
      return petData;
   }

   private long[] getDataPetMabu() {
      long[] petData = new long[5];
      petData[0] = Util.nextInt(400, 1050) * 20; // hp
      petData[1] = Util.nextInt(400, 1050) * 20; // mp
      petData[2] = Util.nextInt(500, 1200); // dame
      petData[3] = Util.nextInt(90, 500); // def
      petData[4] = Util.nextInt(0, 2); // crit
      return petData;
   }

   private long[] getDataPetBerus() {
      long[] petData = new long[5];
      petData[0] = Util.nextInt(400, 1100) * 20; // hp
      petData[1] = Util.nextInt(400, 1100) * 20; // mp
      petData[2] = Util.nextInt(500, 1300); // dame
      petData[3] = Util.nextInt(90, 500); // def
      petData[4] = Util.nextInt(0, 2); // crit
      return petData;
   }

   private long[] getDataPetZeno() {
      long[] petData = new long[5];
      petData[0] = Util.nextInt(400, 1250) * 20; // hp
      petData[1] = Util.nextInt(400, 1250) * 20; // mp
      petData[2] = Util.nextInt(800, 1600); // dame
      petData[3] = Util.nextInt(100, 600); // def
      petData[4] = Util.nextInt(2, 5); // crit
      return petData;
   }

   private long[] getDataPetKaido() {
      long[] petData = new long[5];
      petData[0] = Util.nextInt(400, 1250) * 20; // hp
      petData[1] = Util.nextInt(400, 1250) * 20; // mp
      petData[2] = Util.nextInt(800, 1600); // dame
      petData[3] = Util.nextInt(100, 600); // def
      petData[4] = Util.nextInt(2, 5); // crit
      return petData;
   }

   private long[] getDataPetItachi() {
      long[] petData = new long[5];
      petData[0] = Util.nextInt(400, 1250) * 20; // hp
      petData[1] = Util.nextInt(400, 1250) * 20; // mp
      petData[2] = Util.nextInt(800, 1600); // dame
      petData[3] = Util.nextInt(100, 600); // def
      petData[4] = Util.nextInt(2, 5); // crit
      return petData;
   }

   private long[] getDataPetAndroid() {
      long[] petData = new long[5];
      petData[0] = Util.nextInt(400, 1250) * 20; // hp
      petData[1] = Util.nextInt(400, 1250) * 20; // mp
      petData[2] = Util.nextInt(800, 1600); // dame
      petData[3] = Util.nextInt(100, 600); // def
      petData[4] = Util.nextInt(2, 5); // crit
      return petData;
   }

   private long[] getDataPetNgoKo() {
      long[] petData = new long[5];
      petData[0] = Util.nextInt(400, 1250) * 20; // hp
      petData[1] = Util.nextInt(400, 1250) * 20; // mp
      petData[2] = Util.nextInt(800, 1600); // dame
      petData[3] = Util.nextInt(100, 300); // def
      petData[4] = Util.nextInt(2, 5); // crit
      return petData;
   }

   private long[] getDataPetSoi3Dau() {
      long[] petData = new long[5];
      petData[0] = Util.nextInt(800, 1250) * 20; // hp
      petData[1] = Util.nextInt(800, 5250) * 20; // mp
      petData[2] = Util.nextInt(800, 5600); // dame
      petData[3] = Util.nextInt(300, 600); // def
      petData[4] = Util.nextInt(20, 15); // crit
      return petData;
   }

   // private long[] getDataPetDaiThanh() {
   // long[] petData = new long[5];
   // petData[0] = Util.nextInt(800, 1250) * 20; // hp
   // petData[1] = Util.nextInt(800, 5250) * 20; // mp
   // petData[2] = Util.nextInt(800, 5600); // dame
   // petData[3] = Util.nextInt(300, 600); // def
   // petData[4] = Util.nextInt(20, 15); // crit
   // return petData;
   // }

   // private long[] getDataPetNew() {
   // long[] petData = new long[5];
   // petData[0] = Util.nextInt(8000, 12500) * 20; // hp
   // petData[1] = Util.nextInt(8000, 52500) * 20; // mp
   // petData[2] = Util.nextInt(8000, 56000); // dame
   // petData[3] = Util.nextInt(3000, 6000); // def
   // petData[4] = Util.nextInt(20, 15); // crit
   // return petData;
   // }

   private long[] getDataPetNew1() {
      long[] petData = new long[5];
      petData[0] = Util.nextInt(8000, 12500) * 20; // hp
      petData[1] = Util.nextInt(8000, 52500) * 20; // mp
      petData[2] = Util.nextInt(8000, 56000); // dame
      petData[3] = Util.nextInt(3000, 6000); // def
      petData[4] = Util.nextInt(20, 15); // crit
      return petData;
   }

   private long[] getDataPetNew2() {
      long[] petData = new long[5];
      petData[0] = Util.nextInt(8000, 12500) * 20; // hp
      petData[1] = Util.nextInt(8000, 52500) * 20; // mp
      petData[2] = Util.nextInt(8000, 56000); // dame
      petData[3] = Util.nextInt(3000, 6000); // def
      petData[4] = Util.nextInt(20, 15); // crit
      return petData;
   }

   private long[] getDataPetNew3() {
      long[] petData = new long[5];
      petData[0] = Util.nextInt(8000, 12500) * 20; // hp
      petData[1] = Util.nextInt(8000, 52500) * 20; // mp
      petData[2] = Util.nextInt(8000, 56000); // dame
      petData[3] = Util.nextInt(3000, 6000); // def
      petData[4] = Util.nextInt(20, 15); // crit
      return petData;
   }

   private long[] getDataPetNew4() {
      long[] petData = new long[5];
      petData[0] = Util.nextInt(8000, 12500) * 20; // hp
      petData[1] = Util.nextInt(8000, 52500) * 20; // mp
      petData[2] = Util.nextInt(8000, 56000); // dame
      petData[3] = Util.nextInt(3000, 6000); // def
      petData[4] = Util.nextInt(20, 15); // crit
      return petData;
   }

   private long[] getDataPetNew5() {
      long[] petData = new long[5];
      petData[0] = Util.nextInt(8000, 12500) * 20; // hp
      petData[1] = Util.nextInt(8000, 52500) * 20; // mp
      petData[2] = Util.nextInt(8000, 56000); // dame
      petData[3] = Util.nextInt(3000, 6000); // def
      petData[4] = Util.nextInt(20, 15); // crit
      return petData;
   }

   private long[] getDataPetNew6() {
      long[] petData = new long[5];
      petData[0] = Util.nextInt(8000, 12500) * 20; // hp
      petData[1] = Util.nextInt(8000, 52500) * 20; // mp
      petData[2] = Util.nextInt(8000, 56000); // dame
      petData[3] = Util.nextInt(3000, 6000); // def
      petData[4] = Util.nextInt(20, 15); // crit
      return petData;
   }

   private long[] getDataPetNew7() {
      long[] petData = new long[5];
      petData[0] = Util.nextInt(8000, 12500) * 20; // hp
      petData[1] = Util.nextInt(8000, 52500) * 20; // mp
      petData[2] = Util.nextInt(8000, 56000); // dame
      petData[3] = Util.nextInt(3000, 6000); // def
      petData[4] = Util.nextInt(20, 15); // crit
      return petData;
   }

   private long[] getDataPetNew8() {
      long[] petData = new long[5];
      petData[0] = Util.nextInt(8000, 12500) * 20; // hp
      petData[1] = Util.nextInt(8000, 52500) * 20; // mp
      petData[2] = Util.nextInt(8000, 56000); // dame
      petData[3] = Util.nextInt(3000, 6000); // def
      petData[4] = Util.nextInt(20, 15); // crit
      return petData;
   }

   public void createNewPet(Player player, boolean isMabu, boolean isBerus, boolean isPic, boolean isKaido,
         boolean isItachi, boolean isAndroid, boolean isNgoKo, boolean isSoiBaDau, byte... gender) {
      long[] data = isMabu ? isBerus ? isPic ? isKaido ? isItachi ? isAndroid ? isNgoKo ? isSoiBaDau ? getDataPetMabu()
            : getDataPetBerus() : getDataPetZeno() : getDataPetKaido() : getDataPetItachi() : getDataPetAndroid()
            : getDataPetNgoKo() : getDataPetSoi3Dau() : getDataPetNormal();
      Pet pet = new Pet(player);
      pet.name = "$" + (isMabu ? "Ma Bư"
            : isBerus ? "Berus"
                  : isPic ? "Thần Zeno"
                        : isKaido ? "Hải tặc Kaido"
                              : isItachi ? "Itachi akatsuki"
                                    : isAndroid ? "Tiên hắc ám"
                                          : isNgoKo ? "Ngộ Không"
                                                : isSoiBaDau ? "Sói Địa Ngục" : "Đệ tử");
      pet.gender = (gender != null && gender.length != 0) ? gender[0] : (byte) Util.nextInt(0, 2);
      pet.id = -player.id;
      pet.nPoint.power = isMabu || isBerus || isPic || isKaido || isItachi || isAndroid || isNgoKo || isSoiBaDau
            ? 1500000
            : 2000;
      pet.typePet = (byte) (isMabu ? 1
            : isBerus ? 2 : isPic ? 3 : isKaido ? 4 : isItachi ? 5 : isAndroid ? 6 : isNgoKo ? 7 : isSoiBaDau ? 8 : 0);
      pet.nPoint.stamina = 1000;
      pet.nPoint.maxStamina = 1000;
      pet.nPoint.hpg = data[0];
      pet.nPoint.mpg = data[1];
      pet.nPoint.dameg = data[2];
      pet.nPoint.defg = data[3];
      pet.nPoint.critg = (int) data[4];
      for (int i = 0; i < 10; i++) {
         pet.inventory.itemsBody.add(ItemService.gI().createItemNull());
      }
      pet.playerSkill.skills.add(SkillUtil.createSkill(Util.nextInt(0, 2) * 2, 1));
      for (int i = 0; i < 3; i++) {
         pet.playerSkill.skills.add(SkillUtil.createEmptySkill());
      }
      pet.nPoint.calPoint();
      pet.nPoint.setFullHpMp();
      player.pet = pet;
   }

   public void createNewPetNew(Player player, boolean isPetNew1, boolean isPetNew2, boolean isPetNew3,
         boolean isPetNew4, boolean isPetNew5, boolean isPetNew6, boolean isPetNew7, boolean isPetNew8,
         byte... gender) {
      long[] data = isPetNew1 ? isPetNew2
            ? isPetNew3 ? isPetNew4 ? isPetNew5 ? isPetNew6 ? isPetNew7 ? isPetNew8 ? getDataPetNew1()
                  : getDataPetNew2() : getDataPetNew3() : getDataPetNew4() : getDataPetNew5() : getDataPetNew6()
                  : getDataPetNew7()
            : getDataPetNew8() : getDataPetNormal();
      Pet pet = new Pet(player);
      pet.name = "$" + (isPetNew1 ? "Đại Thánh"
            : isPetNew2 ? "Sơn Tinh"
                  : isPetNew3 ? "Thủy Tinh"
                        : isPetNew4 ? "Ma Bư"
                              : isPetNew5 ? "Chưa biết3"
                                    : isPetNew6 ? "Chưa biết4"
                                          : isPetNew7 ? "Chưa biết5" : isPetNew8 ? "Chưa biết6" : "Đệ tử");
      pet.gender = (gender != null && gender.length != 0) ? gender[0] : (byte) Util.nextInt(0, 2);
      pet.id = -player.id;
      pet.nPoint.power = isPetNew1 || isPetNew2 || isPetNew3 || isPetNew4 || isPetNew5 || isPetNew6 || isPetNew7
            || isPetNew8 ? 1500000 : 2000;
      pet.typePet = (byte) (isPetNew1 ? 9
            : isPetNew2 ? 10
                  : isPetNew3 ? 11
                        : isPetNew4 ? 12 : isPetNew5 ? 13 : isPetNew6 ? 14 : isPetNew7 ? 15 : isPetNew8 ? 16 : 0);
      pet.nPoint.stamina = 1000;
      pet.nPoint.maxStamina = 1000;
      pet.nPoint.hpg = data[0];
      pet.nPoint.mpg = data[1];
      pet.nPoint.dameg = data[2];
      pet.nPoint.defg = data[3];
      pet.nPoint.critg = (int) data[4];
      for (int i = 0; i < 10; i++) {
         pet.inventory.itemsBody.add(ItemService.gI().createItemNull());
      }
      pet.playerSkill.skills.add(SkillUtil.createSkill(Util.nextInt(0, 2) * 2, 1));
      for (int i = 0; i < 3; i++) {
         pet.playerSkill.skills.add(SkillUtil.createEmptySkill());
      }
      pet.nPoint.calPoint();
      pet.nPoint.setFullHpMp();
      player.pet = pet;
   }

   public void createPetIsBot(Player player, byte type) {
      Pet pet = new Pet(player);
      pet.name = "$" + ((type == 1) ? "Ma Bư"
            : (type == 2) ? "Berus"
                  : (type == 3) ? "Thần Zeno"
                        : (type == 4)
                              ? "Hải tặc kaido"
                              : (type == 5) ? "Itachi Akatsuki"
                                                      : (type == 9) ? "Ä�áº¡i ThÃ¡nh" : "Ä�á»‡ tá»­");
      pet.gender = (byte) Util.nextInt(0, 2);
      pet.id = -player.id;
      pet.nPoint.power = Util.nextInt(1000, 500000);
      pet.typePet = type;
      pet.nPoint.stamina = 1000;
      pet.nPoint.maxStamina = 1000;
      pet.nPoint.hpg = Util.nextInt(1000, 20000);
      pet.nPoint.mpg = Util.nextInt(1000, 20000);
      pet.nPoint.dameg = Util.nextInt(200, 400);
      pet.nPoint.defg = Util.nextInt(1000, 2000);
      pet.nPoint.critg = Util.nextInt(10, 20);
      for (int i = 0; i < 8; i++) {
         pet.inventory.itemsBody.add(ItemService.gI().createItemNull());
      }
      pet.playerSkill.skills.add(SkillUtil.createSkill(Util.nextInt(0, 2) * 2, 1));
      for (int i = 0; i < 3; i++) {
         pet.playerSkill.skills.add(SkillUtil.createEmptySkill());
      }
      pet.nPoint.calPoint();
      pet.nPoint.setFullHpMp();
      player.pet = pet;
   }

   // --------------------------------------------------------------------------
}
