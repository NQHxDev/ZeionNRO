package nro.card;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class CollectionBook {

   @Getter
   @Setter
   public List<Card> cards;

   public void init() {
      List<CardTemplate> cardTemplates = CardManager.getInstance().getCardTemplates();
      if (cards.size() < cardTemplates.size()) {
         for (CardTemplate cardT : cardTemplates) {
            Card card = find(cardT.id);
            if (card == null) {
               Card cardNew = new Card();
               cardNew.setId(cardT.id);
               cards.add(cardNew);
            }
         }
      }
      for (Card card : cards) {
         card.setTemplate();
      }
   }

   public void add(Card card) {
      cards.add(card);
   }

   public void remove(Card card) {
      cards.remove(card);
   }

   public Card find(int id) {
      for (Card card : cards) {
         if (card.id == id) {
            return card;
         }
      }

      return null;
   }

   public Card findWithItemID(int id) {
      for (Card card : cards) {
         if (card.getCardTemplate().getItemID() == id) {
            return card;
         }
      }

      return null;
   }

   public Card findWithMobID(int id) {
      for (Card card : cards) {
         if (card.getCardTemplate().getMobID() == id) {
            return card;
         }
      }

      return null;
   }
}
