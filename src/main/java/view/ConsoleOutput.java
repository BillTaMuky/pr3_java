package main.java.view;

import main.java.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConsoleOutput {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void showMenu() {
        System.out.println("Турнікет трамваю");
        System.out.println("1. Випустити картку");
        System.out.println("2. Поповнити накопичувальну картку");
        System.out.println("3. Прикласти картку до турнікета");
        System.out.println("4. Прикласти нечитану картку");
        System.out.println("5. Показати всі картки");
        System.out.println("6. Статистика");
        System.out.println("7. Демонстрація");
        System.out.println("0. Вихід");

    }

    public void showCard(Card card) {
        System.out.print(card.getId().toString());
        System.out.print(" | ");
        System.out.print(typeName(card.getCardType()));
        System.out.print(" | ");

        if (card instanceof TermCard t) {
            System.out.print("діє з " + DATE_FORMAT.format(t.getIssueDate()));
            System.out.println(" до " + DATE_FORMAT.format(t.getExpiryDate()));
        } else if (card instanceof TripCard t){
            System.out.println("залишилось поїздок: " + t.getTripsLeft());
        } else if (card instanceof StoredValueCard s){
            System.out.println("баланс: " + formatMoney(s.getBalance()));
        }
    }

    public void showListOfCards(List<Card> cards) {
        System.out.println("Усі картки (" + cards.size() + ")");
        if  (cards.isEmpty()) {
            System.out.println("Карток немає");
        } else {
            for (Card card : cards) {
                showCard(card);
            }
        }

    }

    public void showPassResult(Card card, PassResult passResult) {
        if (card == null || card.getId() == null || card.getId().isBlank()) {
            System.out.println(resultText(passResult));
        } else {
            System.out.println(card.getId() + " | " + resultText(passResult));
        }
    }

    public void showTotalStatistics(Statistics statistics) {
        System.out.println("Сумарна статистика");
        System.out.println("Усього спроб проходу: " + statistics.getTotal());
        System.out.println("Дозволено: " + statistics.getAllowedTotal());
        System.out.println("Відмовлено:  " + statistics.getDeniedTotal());
        System.out.println("  з них картку не вдалося зчитати: " + statistics.getUnreadableTotal());
    }

    public void showStatisticsByType(Statistics statistics) {
        System.out.println("Статистика за типами карток");

        System.out.printf("%-28s %10s %10s%n",
                "Тип картки", "Дозволено", "Відмовлено");

        for (CardType type : CardType.values()) {
            System.out.printf("%-28s %10d %10d%n",
                    typeName(type),
                    statistics.getAllowedByType().get(type),
                    statistics.getDeniedByType().get(type));
        }

        System.out.printf("%-28s %10d %10d%n",
                "Нечитані картки (без типу)",
                0,
                statistics.getUnreadableTotal());

        System.out.println("-".repeat(50));

        System.out.printf("%-28s %10d %10d%n",
                "Разом",
                statistics.getAllowedTotal(),
                statistics.getDeniedTotal());
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showError(String error) {
        System.out.println("Помилка: " + error);

    }

    private String resultText(PassResult passResult) {
        return switch (passResult) {
            case ALLOWED -> "Прохід дозволено";
            case UNREADABLE -> "ВІДМОВА: картку не вдалося зчитати";
            case EXPIRED -> "ВІДМОВА: термін дії картки закінчився";
            case NO_TRIPS -> "ВІДМОВА: поїздки закінчились";
            case INSUFFICIENT_FUNDS -> "ВІДМОВА: недостатньо коштів на картці";

        };
    }

    private String categoryName(CardCategory cardCategory) {
        return switch (cardCategory) {
            case REGULAR -> "Звичайна";
            case PUPIL -> "Учнівська";
            case STUDENT -> "Студентська";
        };
    }

    private String typeName(CardType cardType) {
        return switch (cardType) {
            case REGULAR_TERM -> "Звичайна, за терміном";
            case STUDENT_TERM -> "Студентська, за терміном";
            case PUPIL_TERM -> "Учнівська, за терміном";
            case REGULAR_TRIPS -> "Звичайна, за поїздками";
            case STUDENT_TRIPS -> "Студентська, за поїздками";
            case PUPIL_TRIPS -> "Учнівська, за поїздками";
            case STORED_VALUE -> "Накопичувальна";
        };
    }

    public void showCardTypeOptions() {
        System.out.println("Оберіть тип картки:");
        System.out.println("1. За терміном");
        System.out.println("2. За кількістю поїздок");
        System.out.println("3. Накопичувальна");
    }

    public void showCategoryOptions() {
        System.out.println("Оберіть категорію картки:");

        CardCategory[] categories = CardCategory.values();

        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categoryName(categories[i]));
        }
    }

    public void showPeriodOptions() {
        System.out.println("Оберіть термін дії картки:");

        TermPeriod[] periods = TermPeriod.values();

        for (int i = 0; i < periods.length; i++) {
            System.out.println((i + 1) + ". " + periodName(periods[i]));
        }
    }

    private String periodName(TermPeriod period) {
        return switch (period) {
            case TEN_DAYS -> "10 днів";
            case MONTH -> "Місяць";
        };
    }

    private String formatMoney(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString() + " грн";
    }
}
