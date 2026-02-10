## Plaid Transactions Linked-List App

This repository now includes a Java CLI app that:

1. Connects to Plaid.
2. Fetches the user's bank accounts.
3. Fetches **all transactions** via `/transactions/sync` pagination.
4. Stores each transaction in a **custom singly linked list**.

## Files

- `src/PlaidApp.java` – entrypoint CLI app.
- `src/PlaidClient.java` – minimal Plaid API client (HTTP + JSON field extraction).
- `src/TransactionLinkedList.java` – custom linked list implementation for transactions.

## Required environment variables

- `PLAID_CLIENT_ID`
- `PLAID_SECRET`
- `PLAID_ACCESS_TOKEN`

Optional:

- `PLAID_ENV` (`sandbox`, `development`, or `production`; defaults to `sandbox`)

## Compile

```bash
javac src/TransactionLinkedList.java src/PlaidClient.java src/PlaidApp.java
```

## Run

```bash
PLAID_CLIENT_ID=... \
PLAID_SECRET=... \
PLAID_ACCESS_TOKEN=... \
PLAID_ENV=sandbox \
java -cp src PlaidApp
```

## Notes

- The app prints account summaries.
- The app loads all `added` transactions from `/transactions/sync` pages into the linked list.
- The app prints a preview of up to the first 10 linked-list transactions.
