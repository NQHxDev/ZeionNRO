package nro.card;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class CardManager {

   private static final CardManager instance = new CardManager();

   public static CardManager getInstance() {
      return instance;
   }

   @Getter
   private final List<CardTemplate> cardTemplates = new ArrayList<>();

   public void add(CardTemplate cardTemplate) {
      cardTemplates.add(cardTemplate);
   }

   public void remove(CardTemplate cardTemplate) {
      cardTemplates.add(cardTemplate);
   }

   public CardTemplate find(int id) {
      for (CardTemplate card : cardTemplates) {
         if (card.getId() == id) {
            return card;
         }
      }
      return null;
   }
}
