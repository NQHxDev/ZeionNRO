package nro.jdbc.daos.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import nro.attr.Attribute;
import nro.attr.AttributeManager;
import nro.attr.AttributeTemplate;
import nro.attr.AttributeTemplateManager;
import nro.card.CardManager;
import nro.card.CardTemplate;
import nro.manager.AchiveManager;
import nro.manager.MiniPetManager;
import nro.manager.PetFollowManager;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.item.MinipetTemplate;
import nro.models.map.EffectEventManager;
import nro.models.map.EffectEventTemplate;
import nro.models.player.PetFollow;
import nro.models.task.AchivementTemplate;
import nro.notification.Alert;
import nro.notification.NotiManager;
import nro.notification.Notification;
import nro.power.Caption;
import nro.power.CaptionManager;
import nro.power.PowerLimit;
import nro.power.PowerLimitManager;
import nro.services.BangTin;
import nro.services.ItemService;
import nro.services.KhamNgoc;
import nro.services.KhamNgocTemplate;
import nro.services.PhongThiNghiem;
import nro.services.PhongThiNghiem_Template;
import nro.services.PhucLoi;
import nro.services.PhucLoiManager;
import nro.services.RuongSuuTam;
import nro.services.RuongSuuTamTemplate;
import nro.services.TamBao;
import nro.services.TamBao_Item;
import nro.server.ServerManager;
import nro.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceDataDAO {

   private static final Gson gson = new Gson();

   public static void loadTemplates(Connection con) {
      loadBangTin(con);
      loadPhucLoi(con);
      loadPhucLoiTab(con);
      loadTamBao(con);
      loadMocTamBao(con);
      loadKhamNgoc(con);
      loadRuongSuuTam(con);
      loadPhongThiNghiem(con);
      loadCardTemplates(con);
      loadPowerLimits(con);
      loadCaptions(con);
      loadAttributeTemplates(con);
      loadAttributeServer(con);
      loadEffectEvents(con);
      loadNotifications(con);
      loadAlert(con);
      loadAchievements(con);
      loadMiniPets(con);
      loadPetFollows(con);
      loadConsignmentItems(con);
   }

   public static void loadBangTin(Connection con) {
      String sql = "SELECT * FROM bang_tin";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         BangTin.BANGTIN_MANAGER.clear();
         while (rs.next()) {
            BangTin bt = new BangTin();
            bt.setId(rs.getInt("id"));
            bt.setTieude(rs.getString("tieu_de"));
            bt.setInfo(rs.getString("info"));
            BangTin.BANGTIN_MANAGER.add(bt);
         }
         Log.success("Bulletin Board (BangTin) loaded successfully (" + BangTin.BANGTIN_MANAGER.size() + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading bulletin board");
      }
   }

   public static void loadPhucLoi(Connection con) {
      String sql = "SELECT * FROM phuc_loi";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         PhucLoi.PHUCLOI_MANAGER.clear();
         while (rs.next()) {
            PhucLoiManager m = new PhucLoiManager();
            m.tab_name = rs.getString("name");
            m.max_tab = rs.getInt("max_tab");
            m.id_tab = rs.getInt("id_tab");
            m.info_phucloi = rs.getString("info_phucloi");
            m.action = rs.getInt("action");
            m.tichLuy = rs.getString("tich_luy");
            PhucLoi.PHUCLOI_MANAGER.add(m);
         }
         Log.success("PhucLoi Managers loaded successfully (" + PhucLoi.PHUCLOI_MANAGER.size() + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading PhucLoi");
      }
   }

   public static void loadPhucLoiTab(Connection con) {
      String sql = "SELECT * FROM phuc_loi_tab";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         PhucLoi.PHUCLOI_TEMPLATES.clear();
         while (rs.next()) {
            PhucLoi pl = new PhucLoi();
            pl.setId(rs.getInt("id"));
            pl.setTab_id(rs.getInt("tab_id"));
            pl.setName(rs.getString("name"));
            pl.setMax_count(rs.getInt("max_count"));
            pl.setActive(rs.getByte("active"));

            String listItemJson = rs.getString("list_item");
            List<ItemData> itemsData = gson.fromJson(listItemJson, new TypeToken<List<ItemData>>() {
            }.getType());
            if (itemsData != null) {
               for (ItemData d : itemsData) {
                  Item it = ItemService.gI().createNewItem((short) d.id, d.quantity);

                  // Sử dụng Robust Parser cho options của item
                  if (d.rawOptions != null) {
                     it.itemOptions.addAll(parseItemOptions(d.rawOptions.toString()));
                  }

                  pl.PHUCLOI_LIST_ITEM.add(it);
               }
            }
            PhucLoi.PHUCLOI_TEMPLATES.add(pl);
         }
         Log.success("PhucLoi Tabs loaded successfully (" + PhucLoi.PHUCLOI_TEMPLATES.size() + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading PhucLoi tabs");
      }
   }

   public static void loadTamBao(Connection con) {
      TamBao.gI().clear();
      String sql = "SELECT * FROM tambao_items WHERE enabled = 1 AND (start_at IS NULL OR start_at <= NOW()) AND (end_at IS NULL OR end_at >= NOW()) ORDER BY id ASC";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         int count = 0;
         while (rs.next()) {
            int keyId = rs.getInt("key_item_id");
            int itemId = rs.getInt("item_id");
            int quantity = rs.getInt("quantity");
            String optionsCompact = rs.getString("item_options");
            int tile = rs.getInt("tile_trung_thuong");

            Item it = ItemService.gI().createNewItem((short) itemId, quantity);
            if (optionsCompact != null && !optionsCompact.isEmpty()) {
               String[] parts = optionsCompact.split(",");
               for (String p : parts) {
                  String[] kv = p.trim().split("-");
                  if (kv.length >= 2) {
                     it.itemOptions.add(new ItemOption(Integer.parseInt(kv[0]), Integer.parseInt(kv[1])));
                  }
               }
            }

            TamBao.gI().getPOOLS().computeIfAbsent(keyId, k -> new ArrayList<>()).add(it);
            TamBao.gI().getPOOL_TILE().computeIfAbsent(keyId, k -> new ArrayList<>())
                  .add(Math.max(0, Math.min(100, tile)));
            TamBao.gI().getPOOL_VIP_FLAGS().computeIfAbsent(keyId, k -> new ArrayList<>()).add(0);
            count++;
         }
         if (!TamBao.gI().getPOOLS().isEmpty()) {
            TamBao.gI().setDEFAULT_KEY_ITEM_ID(TamBao.gI().getPOOLS().keySet().iterator().next());
         }
         Log.success("TamBao Items loaded successfully (" + count + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading TamBao items");
      }
   }

   public static void loadMocTamBao(Connection con) {
      TamBao.MOC_TAMBAO.clear();
      String sql = "SELECT * FROM moc_vong_quay";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            TamBao_Item item = new TamBao_Item();
            item.id_moc = rs.getInt("id");
            int itemId = rs.getInt("item_id");
            item.template = ItemService.gI().getTemplate(itemId);
            item.quantity = rs.getInt("quantity");
            item.max_value = rs.getInt("max_value");
            item.createTime = System.currentTimeMillis();

            String optionsJson = rs.getString("item_options");
            List<ItemOption> options = parseItemOptions(optionsJson);
            item.itemOptions.addAll(options);
            TamBao.MOC_TAMBAO.add(item);
         }
         Log.success("TamBao Milestones loaded successfully (" + TamBao.MOC_TAMBAO.size() + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading TamBao milestones");
      }
   }

   public static void loadKhamNgoc(Connection con) {
      KhamNgoc.KHAM_NGOC.clear();
      String sql = "SELECT * FROM kham_ngoc";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            KhamNgoc kn = new KhamNgoc();
            kn.id = rs.getInt("id");
            String optionsJson = rs.getString("options");
            List<KhamNgocData> list = gson.fromJson(optionsJson, new TypeToken<List<KhamNgocData>>() {
            }.getType());
            if (list != null) {
               for (KhamNgocData d : list) {
                  KhamNgocTemplate detail = new KhamNgocTemplate();
                  detail.level = d.level;
                  detail.tempId = d.tempid;
                  detail.max_value = d.max_value;
                  detail.options = new ItemOption(d.id, d.param);
                  kn.khamNgocTemplates.add(detail);
               }
            }
            KhamNgoc.KHAM_NGOC.add(kn);
         }
         Log.success("KhamNgoc templates loaded successfully (" + KhamNgoc.KHAM_NGOC.size() + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading KhamNgoc");
      }
   }

   public static void loadRuongSuuTam(Connection con) {
      RuongSuuTam.listRuong.clear();
      RuongSuuTam.listCaiTrang.clear();
      RuongSuuTam.listPhuKien.clear();
      RuongSuuTam.listPet.clear();
      RuongSuuTam.listLinhThu.clear();
      RuongSuuTam.listThuCuoi.clear();

      String sql = "SELECT * FROM ruong_suu_tam";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            RuongSuuTamTemplate ruong = new RuongSuuTamTemplate();
            ruong.id = rs.getInt("id");
            ruong.type = rs.getByte("type");
            ruong.id_item = rs.getInt("id_item");
            ruong.option_id = rs.getInt("option_id");
            ruong.param = rs.getInt("param");

            Item it = ItemService.gI().createNewItem((short) ruong.id_item, 1);
            it.itemOptions.add(new ItemOption(ruong.option_id, ruong.param));

            switch (ruong.type) {
               case 0 -> RuongSuuTam.listCaiTrang.add(it);
               case 1 -> RuongSuuTam.listPhuKien.add(it);
               case 2 -> RuongSuuTam.listPet.add(it);
               case 3 -> RuongSuuTam.listLinhThu.add(it);
               case 4 -> RuongSuuTam.listThuCuoi.add(it);
            }
            RuongSuuTam.listRuong.add(ruong);
         }
         Log.success("Collection Book (RuongSuuTam) loaded successfully (" + RuongSuuTam.listRuong.size() + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading RuongSuuTam");
      }
   }

   private static void loadPhongThiNghiem(Connection con) {
      PhongThiNghiem.PHONG_THI_NGHIEM.clear();
      String sql = "SELECT * FROM phong_thi_nghiem";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            PhongThiNghiem ptn = new PhongThiNghiem();
            ptn.id = rs.getInt("id");
            ptn.name_tab = rs.getString("name_tab");
            ptn.name_binh = rs.getString("name_binh");
            ptn.thoi_gian = rs.getInt("thoi_gian");
            ptn.idItem_Nhan = rs.getInt("item_nhan");
            ptn.info = rs.getString("info");
            ptn.color = rs.getByte("color");

            String itemsJson = rs.getString("items");
            List<PtnItemData> itemsData = gson.fromJson(itemsJson, new TypeToken<List<PtnItemData>>() {
            }.getType());
            if (itemsData != null) {
               for (PtnItemData d : itemsData) {
                  PhongThiNghiem_Template t = new PhongThiNghiem_Template();
                  t.tempId = d.tempid;
                  t.quantity = d.quantity;
                  ptn.items.add(t);
               }
            }
            PhongThiNghiem.PHONG_THI_NGHIEM.add(ptn);
         }
         Log.success(
               "Laboratory (PhongThiNghiem) loaded successfully (" + PhongThiNghiem.PHONG_THI_NGHIEM.size() + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading PhongThiNghiem");
      }
   }

   private static void loadCardTemplates(Connection con) {
      CardManager.getInstance().getCardTemplates().clear();
      String sql = "SELECT * FROM collection_book";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            CardTemplate card = CardTemplate.builder()
                  .id(rs.getShort("id"))
                  .itemID(rs.getShort("item_id"))
                  .name(rs.getString("name"))
                  .info(rs.getString("info"))
                  .maxAmount(rs.getByte("max_amount"))
                  .icon(rs.getShort("icon"))
                  .rank(rs.getByte("rank"))
                  .type(rs.getByte("type"))
                  .mobID(rs.getShort("mob_id"))
                  .head(rs.getShort("head"))
                  .body(rs.getShort("body"))
                  .leg(rs.getShort("leg"))
                  .bag(rs.getShort("bag"))
                  .aura(rs.getShort("aura"))
                  .options(new ArrayList<>())
                  .build();

            String optionsJson = rs.getString("options");
            List<CardOptionData> optionsDatas = gson.fromJson(optionsJson, new TypeToken<List<CardOptionData>>() {
            }.getType());
            if (optionsDatas != null) {
               for (CardOptionData d : optionsDatas) {
                  ItemOption io = new ItemOption(d.id, d.param);
                  io.activeCard = (byte) d.active_card;
                  card.getOptions().add(io);
               }
            }
            CardManager.getInstance().getCardTemplates().add(card);
         }
         Log.success(
               "Card Templates loaded successfully (" + CardManager.getInstance().getCardTemplates().size() + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading card templates");
      }
   }

   private static void loadPowerLimits(Connection con) {
      PowerLimitManager.getInstance().getPowers().clear();
      String sql = "SELECT * FROM power_limit";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            PowerLimit pl = PowerLimit.builder()
                  .id(rs.getShort("id"))
                  .power(rs.getLong("power"))
                  .hp(rs.getInt("hp"))
                  .mp(rs.getInt("mp"))
                  .damage(rs.getInt("damage"))
                  .defense(rs.getInt("defense"))
                  .critical(rs.getInt("critical"))
                  .build();
            PowerLimitManager.getInstance().getPowers().add(pl);
         }
         Log.success("Power Limits loaded successfully (" + PowerLimitManager.getInstance().getPowers().size() + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading power limits");
      }
   }

   private static void loadCaptions(Connection con) {
      CaptionManager.getInstance().getCaptions().clear();
      String sql = "SELECT * FROM caption";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            Caption c = Caption.builder()
                  .id(rs.getShort("id"))
                  .earth(rs.getString("earth"))
                  .saiya(rs.getString("saiya"))
                  .namek(rs.getString("namek"))
                  .power(rs.getLong("power"))
                  .build();
            CaptionManager.getInstance().getCaptions().add(c);
         }
         Log.success("Captions loaded successfully (" + CaptionManager.getInstance().getCaptions().size() + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading captions");
      }
   }

   private static void loadAttributeTemplates(Connection con) {
      AttributeTemplateManager.getInstance().getList().clear();
      String sql = "SELECT * FROM attribute_template";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            AttributeTemplate at = AttributeTemplate.builder()
                  .id(rs.getInt("id"))
                  .name(rs.getString("name"))
                  .build();
            AttributeTemplateManager.getInstance().getList().add(at);
         }
         Log.success("Attribute Templates loaded successfully ("
               + AttributeTemplateManager.getInstance().getList().size() + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading attribute templates");
      }
   }

   public static void loadAttributeServer(Connection con) {
      AttributeManager am = new AttributeManager();
      String sql = "SELECT * FROM attribute_server";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            int id = rs.getInt("id");
            int templateId = rs.getInt("attribute_template_id");
            int value = rs.getInt("value");
            int time = rs.getInt("time");
            AttributeTemplate template = AttributeTemplateManager.getInstance().find(templateId);
            if (template != null) {
               Attribute at = Attribute.builder()
                     .id(id)
                     .templateID(templateId)
                     .value(value)
                     .time(time)
                     .build();
               am.add(at);
            }
         }
         ServerManager.gI().setAttributeManager(am);
         Log.success("Attribute Server loaded successfully (" + am.getAttributes().size() + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading attribute server");
      }
   }

   private static void loadEffectEvents(Connection con) {
      EffectEventManager.gI().getTemplates().clear();
      String sql = "SELECT id, eff_event FROM map_template WHERE eff_event IS NOT NULL AND eff_event != '' AND eff_event != '[]'";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            int mapId = rs.getInt("id");
            String effEventJson = rs.getString("eff_event");
            List<EffEventData> effs = gson.fromJson(effEventJson, new TypeToken<List<EffEventData>>() {
            }.getType());
            if (effs != null) {
               for (EffEventData d : effs) {
                  EffectEventTemplate ee = EffectEventTemplate.builder()
                        .mapId(mapId)
                        .eventId(d.event_id)
                        .effId(d.eff_id)
                        .layer(d.layer)
                        .x(d.x)
                        .y(d.y)
                        .loop(d.loop)
                        .delay(d.delay)
                        .build();
                  EffectEventManager.gI().getTemplates().add(ee);
               }
            }
         }
         Log.success("Effect Events loaded successfully (" + EffectEventManager.gI().getTemplates().size() + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading effect events");
      }
   }

   private static void loadNotifications(Connection con) {
      NotiManager.getInstance().getNotifications().clear();
      String sql = "SELECT * FROM notifications";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            Notification n = new Notification();
            n.setId(rs.getInt("id"));
            n.setTitle(rs.getString("title"));
            n.setContent(rs.getString("content"));
            NotiManager.getInstance().getNotifications().add(n);
         }
         Log.success("Notifications loaded successfully (" + NotiManager.getInstance().getNotifications().size() + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading notifications");
      }
   }

   private static void loadAlert(Connection con) {
      String sql = "SELECT * FROM alert LIMIT 1";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         if (rs.next()) {
            Alert a = new Alert();
            a.content = rs.getString("content");
            NotiManager.setAlert(a);
         }
         Log.success("System Alert loaded successfully");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading alert");
      }
   }

   private static void loadAchievements(Connection con) {
      AchiveManager.getInstance().getList().clear();
      String sql = "SELECT * FROM achivements";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            AchivementTemplate at = new AchivementTemplate(
                  rs.getInt("id"),
                  rs.getString("name"),
                  rs.getString("detail"),
                  rs.getInt("money"),
                  rs.getInt("max_count"));
            AchiveManager.getInstance().getList().add(at);
         }
         Log.success("Achievements loaded successfully (" + AchiveManager.getInstance().getList().size() + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading achievements");
      }
   }

   private static void loadMiniPets(Connection con) {
      MiniPetManager.gI().getList().clear();
      String sql = "SELECT * FROM mini_pet";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            MinipetTemplate t = new MinipetTemplate(
                  rs.getInt("id_temp"),
                  rs.getShort("head"),
                  rs.getShort("body"),
                  rs.getShort("leg"));
            MiniPetManager.gI().getList().add(t);
         }
         Log.success("Mini Pets loaded successfully (" + MiniPetManager.gI().getList().size() + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading mini pets");
      }
   }

   public static void loadConsignmentItems(Connection con) {
      String sql = "SELECT * FROM consignment_shop";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         nro.models.consignment.ConsignmentShop shop = nro.models.consignment.ConsignmentShop.getInstance();
         shop.getList().clear();
         int count = 0;
         while (rs.next()) {
            short itemId = rs.getShort("item_id");
            int quantity = rs.getInt("quantity");
            nro.models.consignment.ConsignmentItem item = ItemService.gI().createNewConsignmentItem(itemId, quantity);
            item.setConsignorID(rs.getLong("consignor_id"));
            item.setTab(rs.getByte("tab"));
            item.setPriceGold(rs.getInt("gold"));
            item.setPriceGem(rs.getInt("gem"));
            item.setUpTop(rs.getBoolean("up_top"));
            item.setSold(rs.getBoolean("sold"));
            item.createTime = rs.getLong("time_consign");

            String optionsJson = rs.getString("item_options");
            item.itemOptions.addAll(parseItemOptions(optionsJson));

            int daysExpired = shop.getDaysExpried(item.createTime);
            if (daysExpired > 3 && daysExpired < 6) {
               shop.addExpiredItem(item);
            }
            if (daysExpired < 6) {
               shop.addItem(item);
            }
            count++;
         }
         Log.success("Consignment Items loaded successfully (" + count + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading consignment items");
      }
   }

   public static void saveConsignmentItems(Connection con) {
      try (PreparedStatement truncateStatement = con.prepareStatement("TRUNCATE consignment_shop");
            PreparedStatement insertStatement = con.prepareStatement(
                  "INSERT INTO `consignment_shop`(`id`, `consignor_id`, `tab`, `item_id`, `gold`, `gem`, `quantity`, `item_options`, `up_top`, `sold`,`time_consign`) VALUES (? ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
         truncateStatement.executeUpdate();

         List<nro.models.consignment.ConsignmentItem> list = nro.models.consignment.ConsignmentShop.getInstance()
               .getList();
         int id = 0;
         for (nro.models.consignment.ConsignmentItem it : list) {
            if (it != null) {
               insertStatement.setInt(1, id++);
               insertStatement.setLong(2, it.getConsignorID());
               insertStatement.setInt(3, it.getTab());
               insertStatement.setShort(4, it.template.id);
               insertStatement.setInt(5, it.getPriceGold());
               insertStatement.setInt(6, it.getPriceGem());
               insertStatement.setInt(7, it.quantity);

               // Chuyển sang JSON GSON chuẩn: [{"id":..., "param":...}, ...]
               insertStatement.setString(8, gson.toJson(it.itemOptions));

               insertStatement.setBoolean(9, it.isUpTop());
               insertStatement.setBoolean(10, it.isSold());
               insertStatement.setLong(11, it.createTime);
               insertStatement.addBatch();
            }
         }
         insertStatement.executeBatch();
         Log.log("Consignment items saved successfully (" + list.size() + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error saving consignment items");
      }
   }

   private static void loadPetFollows(Connection con) {
      PetFollowManager.gI().getList().clear();
      String sql = "SELECT * FROM pet_follow";
      try (PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
         while (rs.next()) {
            PetFollow f = new PetFollow(
                  rs.getInt("id_temp"),
                  rs.getInt("icon"),
                  rs.getInt("width"),
                  rs.getInt("height"),
                  rs.getByte("frame"));
            PetFollowManager.gI().getList().add(f);
         }
         Log.success("Pet Follows loaded successfully (" + PetFollowManager.gI().getList().size() + ")");
      } catch (SQLException e) {
         Log.error(ServiceDataDAO.class, e, "Error loading pet follows");
      }
   }

   /**
    * Robust Parser: Tự động nhận diện và xử lý cả hai định dạng JSON Option (Mảng
    * và Object)
    * 
    * @param json Chuỗi JSON từ Database
    * @return Danh sách ItemOption đã parse
    */
   public static List<ItemOption> parseItemOptions(String json) {
      List<ItemOption> options = new ArrayList<>();
      if (json == null || json.isEmpty() || json.equals("[]") || json.equals("null")) {
         return options;
      }

      try {
         JsonElement element = gson.fromJson(json, JsonElement.class);
         if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement e : array) {
               if (e.isJsonArray()) {
                  // Định dạng mảng cũ: [[id, param], ...]
                  JsonArray inner = e.getAsJsonArray();
                  if (inner.size() >= 2) {
                     options.add(new ItemOption(inner.get(0).getAsInt(), inner.get(1).getAsInt()));
                  }
               } else if (e.isJsonObject()) {
                  // Định dạng Object chuẩn: [{"id":..., "param":...}, ...]
                  OptionData data = gson.fromJson(e, OptionData.class);
                  options.add(new ItemOption(data.id, data.param));
               }
            }
         }
      } catch (Exception e) {
         Log.error(ServiceDataDAO.class, e, "Lỗi khi parse Item Options (GSON): " + json);
      }
      return options;
   }

   // Helper classes cho GSON parsing
   private static class ItemData {
      int id;
      int quantity;

      @SerializedName(value = "options", alternate = { "list_option", "item_options" })
      JsonElement rawOptions;
   }

   private static class OptionData {
      int id;
      int param;
   }

   private static class KhamNgocData {
      int level;
      int tempid;
      int max_value;
      int id;
      int param;
   }

   private static class PtnItemData {
      int tempid;
      int quantity;
   }

   private static class CardOptionData {
      int id;
      int param;
      int active_card;
   }

   private static class EffEventData {
      int event_id;
      int eff_id;
      int layer;
      int x;
      int y;
      int loop;
      int delay;
   }
}
