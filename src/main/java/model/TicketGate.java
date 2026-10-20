package main.java.model;

import java.math.BigDecimal;
import java.time.LocalDate;


public class TicketGate {
    private final BigDecimal fare;
    private final Statistics statistics;

    public TicketGate(BigDecimal fare, Statistics statistics) {
        if (fare == null || fare.signum() <= 0) {
            throw new IllegalArgumentException("Fare must be positive");
        }

        if (statistics == null) {
            throw new IllegalArgumentException("Statistics cannot be null");
        }

        this.fare = fare;
        this.statistics = statistics;
    }

    public PassResult process(Card card, LocalDate today) {
        try {
            read(card);
        } catch (CardReadException e) {
            statistics.recordUnreadable();
            return PassResult.UNREADABLE;
        }

        PassResult result = card.check(today, fare);

        if (result == PassResult.ALLOWED) {
            card.charge(fare);
        }

        statistics.record(card.getCardType(), result);
        return result;
    }

    private void read(Card card) throws CardReadException {
        if (card == null || card.getId() == null || card.getId().isBlank()) {
            throw new CardReadException("Card is unreadable");
        }
    }

    public Statistics getStatistics() {
        return statistics;
    }

}
