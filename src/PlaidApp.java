import java.io.IOException;
import java.util.List;

/**
 * CLI application that fetches all Plaid accounts and transactions,
 * and stores transactions in a linked list.
 */
public class PlaidApp {
    public static void main(String[] args) {
        String clientId = System.getenv("PLAID_CLIENT_ID");
        String secret = System.getenv("PLAID_SECRET");
        String accessToken = System.getenv("PLAID_ACCESS_TOKEN");
        String environment = System.getenv("PLAID_ENV");

        if (isBlank(clientId) || isBlank(secret) || isBlank(accessToken)) {
            System.err.println("Missing required environment variables:");
            System.err.println("  - PLAID_CLIENT_ID");
            System.err.println("  - PLAID_SECRET");
            System.err.println("  - PLAID_ACCESS_TOKEN");
            System.err.println("Optional: PLAID_ENV=sandbox|development|production (defaults to sandbox)");
            System.exit(1);
        }

        PlaidClient client = new PlaidClient(clientId, secret, accessToken, environment);

        try {
            List<PlaidClient.Account> accounts = client.fetchAccounts();
            System.out.println("Accounts found: " + accounts.size());
            for (PlaidClient.Account account : accounts) {
                System.out.println("- " + account.accountId() + " | " + account.name() +
                        " | type=" + account.type() + " | subtype=" + account.subtype());
            }

            TransactionLinkedList transactions = client.fetchAllTransactions();
            System.out.println("\nTransactions loaded into linked list: " + transactions.size());

            int previewCount = 0;
            for (TransactionLinkedList.Transaction tx : transactions) {
                System.out.println(tx);
                previewCount++;
                if (previewCount >= 10) {
                    break;
                }
            }

            if (transactions.isEmpty()) {
                System.out.println("No transactions returned by Plaid.");
            } else {
                System.out.println("(Showing first " + previewCount + " transactions)");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to fetch Plaid data: " + e.getMessage());
            System.exit(2);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
