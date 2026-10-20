package main.java.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TermCard extends Card{
    private final TermPeriod termPeriod;
    private final LocalDate issueDate;
    private final LocalDate expiryDate;

    public TermCard(String id, CardCategory cardCategory, LocalDate issueDate, TermPeriod termPeriod){
        super(id, cardCategory);
        this.issueDate = issueDate;
        this.termPeriod = termPeriod;
        this.expiryDate = switch (termPeriod) {
            case MONTH -> issueDate.plusMonths(1);
            case TEN_DAYS ->  issueDate.plusDays(10);
        };
    }


    public TermPeriod getTermPeriod() {
        return termPeriod;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    @Override
    public PassResult check(LocalDate today, BigDecimal fare) {
        if (!today.isBefore(issueDate) && !today.isAfter(expiryDate)) {
            return PassResult.ALLOWED;
        } else {
            return PassResult.EXPIRED;
        }
    }

    //fare не потрібен для картки за терміном дії
    @Override
    public void charge(BigDecimal fare) {}

    @Override
    public CardType getCardType() {
        return switch (getCardCategory()) {
            case REGULAR -> CardType.REGULAR_TERM;
            case PUPIL -> CardType.PUPIL_TERM;
            case STUDENT ->  CardType.STUDENT_TERM;
        };
    }
}
