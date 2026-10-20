package main.java.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StoredValueCard extends Card{

    private BigDecimal balance;

    public StoredValueCard(String id, CardCategory cardCategory, BigDecimal balance) {
        super(id, cardCategory);
        if (cardCategory != CardCategory.REGULAR) {
            throw new IllegalArgumentException("Stored card category only supports regular cards");
        }
        if (balance.signum() < 0) {
            throw new IllegalArgumentException("Balance must be positive");
        }
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void topUp(BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        balance = balance.add(amount);
    }

    @Override
    public PassResult check(LocalDate today, BigDecimal fare) {
        if(balance.compareTo(fare) >= 0){
            return PassResult.ALLOWED;
        } else{
            return PassResult.INSUFFICIENT_FUNDS;
        }
    }

    @Override
    public void charge(BigDecimal fare) {
        if (balance.compareTo(fare) < 0) {
            throw new IllegalStateException("Balance must be equal or grater then fare");
        }
        balance = balance.subtract(fare);
    }

    @Override
    public CardType getCardType() {
        return  CardType.STORED_VALUE;
    }
}
