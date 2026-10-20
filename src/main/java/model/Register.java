package main.java.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


public class Register {
    private final Map<String, Card> cards;
    private int idCounter;

    public Register() {
        cards = new LinkedHashMap<>();
    }

    public Card issueTermCard(CardCategory cardCategory, TermPeriod termPeriod, LocalDate issueDate) {
        String id = createId("TE");
        Card card = new TermCard(id, cardCategory, issueDate, termPeriod);
        cards.put(id, card);
        return card;
    }

    public Card issueTripCard(CardCategory cardCategory, int tripsLeft) {
        String id = createId("TR");
        Card card = new TripCard(id, cardCategory, tripsLeft);
        cards.put(id, card);
        return card;
    }

    public Card issueStoredValueCard(CardCategory cardCategory, BigDecimal balance) {
        String id = createId("ST");
        Card card = new StoredValueCard(id, cardCategory, balance);
        cards.put(id, card);
        return card;
    }

    public void topUp(StoredValueCard card, BigDecimal amount) {
        if (card == null) {
            throw new IllegalArgumentException("Card did not exist");
        }

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must not be negative");
        }

        card.topUp(amount);
    }

    public Card findById(String id) {
        return cards.get(id);
    }

    public ArrayList<Card> getAllCards() {
        return new ArrayList<>(cards.values());
    }

    private String createId(String type) {
        idCounter++;
        return type + "-" + String.format("%06d", idCounter);
    }

}
