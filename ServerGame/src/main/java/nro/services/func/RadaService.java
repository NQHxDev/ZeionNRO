package nro.services.func;

import nro.card.Card;
import nro.card.CardTemplate;
import nro.card.CollectionBook;
import nro.consts.Cmd;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.player.Player;
import nro.network.io.Message;
import nro.services.InventoryService;
import nro.services.Service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class RadaService {

   private static RadaService instance;

   private RadaService() {
   }

   public static RadaService getInstance() {
      if (instance == null) {
         instance = new RadaService();
      }
      return instance;
   }

   public void controller(Player player, Message msg) {
      try {
         byte type = msg.reader().readByte();
         int id = -1;
         if (msg.reader().available() > 0) {
            id = msg.reader().readShort();
         }
         switch (type) {
            case 0:
               viewCollectionBook(player);
               break;
            case 1:
               cardAction(player, id);
               break;
         }
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   public void cardAction(Player player, int id) {
      CollectionBook book = player.getCollectionBook();
      Card c = book.findWithItemID(id);
      if (c != null) {
         if (c.level > 0) {
            if (!c.isUse) {
               long size = book.cards.stream().filter(a -> a.isUse).count();
               if (size >= 3) {
                  return;
               }
            }
            byte auraOld = player.getAura();
            c.isUse = !c.isUse;
            byte auraNew = player.getAura();
            useCard(player, c);
            player.nPoint.calPoint();
            Service.getInstance().point(player);
            if (auraOld != auraNew) {
               setIDAuraEff(player, auraNew);
            }
         }
      }
   }

   public void useCard(Player player, Card card) {
      try {
         Message mss = Message.create(Cmd.RADA_CARD);
         DataOutputStream ds = mss.writer();
         ds.writeByte(1);
         ds.writeShort(card.cardTemplate.itemID);
         ds.writeBoolean(card.isUse);
         ds.flush();
         player.sendMessage(mss);
         mss.cleanup();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   public void viewCollectionBook(Player player) {
      try {
         CollectionBook book = player.getCollectionBook();
         Message mss = Message.create(Cmd.RADA_CARD);
         DataOutputStream ds = mss.writer();
         ds.writeByte(0);
         List<Card> cards = book.cards;
         ds.writeShort(cards.size());
         for (Card card : cards) {
            CardTemplate cardT = card.cardTemplate;
            ds.writeShort(cardT.itemID);
            ds.writeShort(cardT.icon);
            ds.writeByte(cardT.rank);
            ds.writeByte(card.amount);
            ds.writeByte(cardT.maxAmount);
            ds.writeByte(cardT.type);
            if (cardT.type == 0) {
               ds.writeShort(cardT.mobID);
            } else {
               ds.writeShort(cardT.head);
               ds.writeShort(cardT.body);
               ds.writeShort(cardT.leg);
               ds.writeShort(cardT.bag);
            }
            ds.writeUTF(cardT.name);
            ds.writeUTF(cardT.info);
            ds.writeByte(card.level);
            ds.writeBoolean(card.isUse);
            List<ItemOption> options = cardT.options;
            ds.writeByte(options.size());
            for (ItemOption option : options) {
               ds.writeByte(option.optionTemplate.id);
               ds.writeInt(option.param);
               ds.writeByte(option.activeCard);
            }
         }
         ds.flush();
         player.sendMessage(mss);
         mss.cleanup();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   public void useItemCard(Player player, Item item) {
      CollectionBook book = player.getCollectionBook();
      Card card = book.findWithItemID(item.template.id);
      if (card != null) {
         InventoryService.gI().subQuantityItemsBag(player, item, 1);
         int cardLevelOld = card.level;
         card.addAmount(1);
         int cardLevelNew = card.level;
         if (cardLevelOld != cardLevelNew) {
            if (card.isUse) {
               player.nPoint.calPoint();
               Service.getInstance().point(player);
            }
         }
         setCardLevel(player, card);
      }
   }

   public void setCardLevel(Player player, Card card) {
      try {
         Message mss = Message.create(Cmd.RADA_CARD);
         DataOutputStream ds = mss.writer();
         ds.writeByte(2);
         ds.writeShort(card.cardTemplate.itemID);
         ds.writeByte(card.level);
         ds.flush();
         player.sendMessage(mss);
         mss.cleanup();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   public void setIDAuraEff(Player player, byte aura) {
      try {
         Message mss = Message.create(Cmd.RADA_CARD);
         DataOutputStream ds = mss.writer();
         ds.writeByte(4);
         ds.writeInt((int) player.id);
         ds.writeShort(aura);
         ds.flush();
         Service.getInstance().sendMessAllPlayerInMap(player, mss);
         mss.cleanup();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

}
