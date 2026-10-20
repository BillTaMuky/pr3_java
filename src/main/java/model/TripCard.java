package main.java.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TripCard extends Card {

    private int tripsLeft;

    public TripCard(String id, CardCategory cardCategory, int tripsLeft) {
        super(id, cardCategory);
        if (tripsLeft < 0) {
            throw new IllegalArgumentException("Number of trips cannot be negative");
        }
        this.tripsLeft = tripsLeft;
    }

    public int getTripsLeft() {
        return tripsLeft;
    }


    @Override
    public PassResult check(LocalDate today, BigDecimal fare) {
        if (tripsLeft > 0) {
            return PassResult.ALLOWED;
        } else {
            return PassResult.NO_TRIPS;
        }
    }

    //fare не потрібен для картки за поїздками
    @Override
    public void charge(BigDecimal fare) {
        if (tripsLeft <= 0) {
            throw new IllegalStateException("tripsLeft must be greater than 0");
        }
        tripsLeft--;
    }

    @Override
    public CardType getCardType() {
        return switch (getCardCategory()) {
            case REGULAR -> CardType.REGULAR_TRIPS;
            case PUPIL -> CardType.PUPIL_TRIPS;
            case STUDENT ->  CardType.STUDENT_TRIPS;
        };
    }
}
