import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Singly linked list for Plaid transactions.
 */
public class TransactionLinkedList implements Iterable<TransactionLinkedList.Transaction> {
    public static class Transaction {
        private final String transactionId;
        private final String accountId;
        private final String name;
        private final String date;
        private final double amount;

        public Transaction(String transactionId, String accountId, String name, String date, double amount) {
            this.transactionId = transactionId;
            this.accountId = accountId;
            this.name = name;
            this.date = date;
            this.amount = amount;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getAccountId() {
            return accountId;
        }

        public String getName() {
            return name;
        }

        public String getDate() {
            return date;
        }

        public double getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return "Transaction{" +
                    "transactionId='" + transactionId + '\'' +
                    ", accountId='" + accountId + '\'' +
                    ", name='" + name + '\'' +
                    ", date='" + date + '\'' +
                    ", amount=" + amount +
                    '}';
        }
    }

    private static class Node {
        Transaction value;
        Node next;

        Node(Transaction value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public void add(Transaction transaction) {
        Node n = new Node(transaction);
        if (head == null) {
            head = n;
            tail = n;
        } else {
            tail.next = n;
            tail = n;
        }
        size++;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public List<Transaction> toList() {
        List<Transaction> result = new ArrayList<>();
        for (Transaction tx : this) {
            result.add(tx);
        }
        return result;
    }

    @Override
    public Iterator<Transaction> iterator() {
        return new Iterator<>() {
            private Node cursor = head;

            @Override
            public boolean hasNext() {
                return cursor != null;
            }

            @Override
            public Transaction next() {
                Transaction current = cursor.value;
                cursor = cursor.next;
                return current;
            }
        };
    }
}
