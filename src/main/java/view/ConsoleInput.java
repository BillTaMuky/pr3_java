package main.java.view;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleInput {

    private final Scanner scanner = new Scanner(System.in);

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Введіть ціле число");
            }
        }
    }

    public int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);

            if (value >= min && value <= max) {
                return value;
            }

            System.out.printf("Введіть число від %d до %d%n", min, max);
        }
    }

    public String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Рядок не може бути порожнім");
        }
    }

    public BigDecimal readMoney(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().replace(',', '.');

            try {
                BigDecimal amount = new BigDecimal(input);

                if (amount.signum() < 0) {
                    System.out.println("Сума не може бути від'ємною");
                    continue;
                }

                return amount;
            } catch (NumberFormatException e) {
                System.out.println("Введіть коректну грошову суму");
            }
        }
    }
}