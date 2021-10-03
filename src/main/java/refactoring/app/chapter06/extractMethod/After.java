package refactoring.app.chapter06.extractMethod;

import java.time.LocalDateTime;

public class After {
    public void printOwing(Invoice invoice) {
        printBanner();

        int outstanding = calculateOutstanding(invoice);

        recordDueDate(invoice);

        printDetails(invoice, outstanding);
    }

    private void printBanner() {
        System.out.println("*****************");
        System.out.println("**** 고객 채무 ****");
        System.out.println("*****************");
    }

    private int calculateOutstanding(Invoice invoice) {
        int result = 0;
        // 미해결 채무 (outstanding) 을 계산한다.
        for (Order o : invoice.getOrders()) {
            result += o.amount;
        }
        return result;
    }


    private void recordDueDate(Invoice invoice) {
        // 마감일(dueDate) 을 기록한다.
        LocalDateTime today = Clock.today();
        invoice.dueDate = today.plusDays(30);
    }

    private void printDetails(Invoice invoice, int outstanding) {
        // 세부 사항을 출력한다.
        System.out.println(String.format("고객명: %s", invoice.customer));
        System.out.println(String.format("채무액: %d", outstanding));
        System.out.println(String.format("마감일: %s", invoice.dueDate));
    }
}
