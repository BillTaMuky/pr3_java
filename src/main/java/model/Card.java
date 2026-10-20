package main.java.model;


import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class Card {

    private final String id;
    private final CardCategory cardCategory;

    public Card(String id, CardCategory cardCategory) {
        this.id = id;
        this.cardCategory = cardCategory;
    }

    public abstract PassResult check(LocalDate today, BigDecimal fare);

    public abstract void charge(BigDecimal fare);

    public abstract CardType getCardType();

    public String getId() {
        return id;
    }

    public CardCategory getCardCategory() {
        return cardCategory;
    }
}
