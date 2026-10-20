
package main.java.controller;

import main.java.model.*;
import main.java.view.ConsoleInput;
import main.java.view.ConsoleOutput;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

public class MainController {

    private static final BigDecimal FARE = new BigDecimal("30.00");

    private final Register register;
    private final TicketGate gate;
    private final ConsoleOutput view;
    private final ConsoleInput input;

    public MainController() {
        Statistics statistics = new Statistics();
        this.register = new Register();
        this.gate = new TicketGate(FARE, statistics);
        this.view = new ConsoleOutput();
        this.input = new ConsoleInput();
    }

    public void run() {
        boolean running = true;

        while (running) {
            view.showMenu();

            int choice = input.readIntInRange("Ваш вибір: ", 0, 7);

            switch (choice) {case 1 -> issueCard();
                case 2 -> topUpCard();
                case 3 -> applyCard();
                case 4 -> applyUnreadable();
                case 5 -> showCards();
                case 6 -> showStatistics();
                case 7 -> runDemo();
                case 0 -> {
                    view.showMessage("Завершення роботи");
                    running = false;
                }
            }
        }
    }

    private void issueCard() {
        view.showCardTypeOptions();

        int type = input.readIntInRange("Ваш вибір: ", 1, 3);

        view.showCategoryOptions();

        int categoryChoice = input.readIntInRange("Оберіть категорію: ", 1, CardCategory.values().length);

        CardCategory category = CardCategory.values()[categoryChoice - 1];

        try {
            Card card;

            switch (type) {
                case 1 -> {
                    view.showPeriodOptions();

                    int periodChoice = input.readIntInRange("Оберіть термін: ", 1, TermPeriod.values().length);

                    TermPeriod period = TermPeriod.values()[periodChoice - 1];

                    card = register.issueTermCard(category, period, LocalDate.now());
                }

                case 2 -> {
                    view.showMessage("1. 5 поїздок, 2. 10 поїздок");

                    int tripsChoice = input.readIntInRange("Оберіть кількість поїздок: ", 1, 2);

                    int trips = tripsChoice == 1 ? 5 : 10;

                    card = register.issueTripCard(category, trips);
                }

                case 3 -> {
                    if (category != CardCategory.REGULAR) {
                        view.showError("Накопичувальна картка доступна лише для звичайної категорії");
                        return;
                    }

                    BigDecimal balance = input.readMoney("Введіть початковий баланс: ");

                    card = register.issueStoredValueCard(category, balance);
                }

                default -> {
                    return;
                }
            }

            view.showMessage("Картку успішно випущено:");
            view.showCard(card);

        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }

    private void topUpCard() {
        view.showListOfCards(register.getAllCards());

        String id = readCardId();
        Card card = register.findById(id);

        if (card == null) {
            view.showError("Картку не знайдено");
            return;
        }

        if (!(card instanceof StoredValueCard storedValueCard)) {
            view.showError("Поповнювати можна лише накопичувальну картку");
            return;
        }

        BigDecimal amount = input.readMoney("Введіть суму поповнення: ");

        try {
            register.topUp(storedValueCard, amount);

            view.showMessage("Баланс успішно поповнено:");
            view.showCard(storedValueCard);

        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }

    private void applyCard() {
        view.showListOfCards(register.getAllCards());

        String id = readCardId();
        Card card = register.findById(id);

        if (card == null) {
            view.showError("Картку не знайдено");
            return;
        }

        PassResult result = gate.process(card, LocalDate.now());

        view.showPassResult(card, result);
        view.showMessage("");
        view.showCard(card);
    }

    private void applyUnreadable() {
        PassResult result = gate.process(null, LocalDate.now());

        view.showPassResult(null, result);
        view.showMessage("");
    }

    private void showCards() {
        view.showListOfCards(register.getAllCards());
    }

    private void showStatistics() {
        Statistics statistics = gate.getStatistics();

        view.showTotalStatistics(statistics);
        view.showStatisticsByType(statistics);
    }

    private void runDemo() {
        view.showMessage("ДЕМОНСТРАЦІЯ РОБОТИ ТУРНІКЕТА");

        LocalDate today = LocalDate.now();
        BigDecimal demoBalance = new BigDecimal("30.00");

        Statistics demoStatistics = new Statistics();
        Register demoRegister = new Register();
        TicketGate demoGate = new TicketGate(FARE, demoStatistics);

        try {
            // Випускаємо картки

            // Дійсна термінова картка
            Card validTermCard = demoRegister.issueTermCard(CardCategory.REGULAR, TermPeriod.MONTH, today);

            // Прострочена термінова картка
            Card expiredTermCard = demoRegister.issueTermCard(CardCategory.REGULAR, TermPeriod.MONTH, today.minusDays(40));

            // Картка на 5 поїздок
            Card tripCard = demoRegister.issueTripCard(CardCategory.REGULAR, 5);

            // Накопичувальна картка з оплатою однієї поїздки
            Card storedValueCard = demoRegister.issueStoredValueCard(CardCategory.REGULAR, demoBalance);

            // Всі випущені картки
            view.showMessage("\nВипущені демонстраційні картки:");
            view.showListOfCards(demoRegister.getAllCards());

            // 12 спроб проходу

            view.showMessage("\nПроходи через турнікет");

            // Дві успішні спроби
            applyDemoCard(demoGate, validTermCard, today, 2);

            // Одна відмова: термін дії закінчився
            applyDemoCard(demoGate, expiredTermCard, today, 1);

            // П'ять успішних проходів і одна відмова:
            // поїздки закінчилися
            applyDemoCard(demoGate, tripCard, today, 6);

            // Один успішний прохід і одна відмова:
            // недостатньо коштів
            applyDemoCard(demoGate, storedValueCard, today, 2);

            // Одна відмова: картку не вдалося зчитати
            PassResult unreadableResult = demoGate.process(null, today);

            view.showPassResult(null, unreadableResult);
            view.showMessage("");

            // Підсумкова статистика
            view.showMessage("\nСтатистика демонстрації");
            view.showTotalStatistics(demoStatistics);
            view.showStatisticsByType(demoStatistics);

        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }

    private void applyDemoCard(
            TicketGate demoGate,
            Card card,
            LocalDate today,
            int attempts
    ) {
        for (int i = 0; i < attempts; i++) {
            PassResult result = demoGate.process(card, today);

            view.showPassResult(card, result);
            view.showMessage("");
            view.showCard(card);
        }
    }

    private String readCardId() {
        return input.readString("Введіть ID картки: ").trim().toUpperCase(Locale.ROOT);
    }
}
