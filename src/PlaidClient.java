import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minimal Plaid API client using Java's built-in HTTP support.
 */
public class PlaidClient {
    private final String clientId;
    private final String secret;
    private final String accessToken;
    private final String baseUrl;
    private final HttpClient httpClient;

    public record Account(String accountId, String name, String officialName, String type, String subtype) {
    }

    public PlaidClient(String clientId, String secret, String accessToken, String environment) {
        this.clientId = clientId;
        this.secret = secret;
        this.accessToken = accessToken;
        this.baseUrl = environmentToBaseUrl(environment);
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
    }

    private String environmentToBaseUrl(String env) {
        if (env == null) {
            return "https://sandbox.plaid.com";
        }
        return switch (env.toLowerCase()) {
            case "development" -> "https://development.plaid.com";
            case "production" -> "https://production.plaid.com";
            default -> "https://sandbox.plaid.com";
        };
    }

    public List<Account> fetchAccounts() throws IOException, InterruptedException {
        String body = "{" +
                "\"client_id\":\"" + escapeJson(clientId) + "\"," +
                "\"secret\":\"" + escapeJson(secret) + "\"," +
                "\"access_token\":\"" + escapeJson(accessToken) + "\"" +
                "}";

        String response = postJson("/accounts/get", body);
        List<String> accountObjects = extractArrayObjects(response, "accounts");
        List<Account> accounts = new ArrayList<>();

        for (String obj : accountObjects) {
            accounts.add(new Account(
                    extractJsonString(obj, "account_id"),
                    extractJsonString(obj, "name"),
                    extractJsonString(obj, "official_name"),
                    extractJsonString(obj, "type"),
                    extractJsonString(obj, "subtype")
            ));
        }

        return accounts;
    }

    public TransactionLinkedList fetchAllTransactions() throws IOException, InterruptedException {
        TransactionLinkedList linkedList = new TransactionLinkedList();
        String cursor = null;
        boolean hasMore;

        do {
            String body = "{" +
                    "\"client_id\":\"" + escapeJson(clientId) + "\"," +
                    "\"secret\":\"" + escapeJson(secret) + "\"," +
                    "\"access_token\":\"" + escapeJson(accessToken) + "\"," +
                    "\"count\":500," +
                    "\"cursor\":" + (cursor == null ? "null" : "\"" + escapeJson(cursor) + "\"") +
                    "}";

            String response = postJson("/transactions/sync", body);

            List<String> addedObjects = extractArrayObjects(response, "added");
            for (String obj : addedObjects) {
                String transactionId = extractJsonString(obj, "transaction_id");
                String accountId = extractJsonString(obj, "account_id");
                String name = extractJsonString(obj, "name");
                String date = extractJsonString(obj, "date");
                double amount = extractJsonNumber(obj, "amount");

                linkedList.add(new TransactionLinkedList.Transaction(transactionId, accountId, name, date, amount));
            }

            cursor = extractJsonString(response, "next_cursor");
            hasMore = extractJsonBoolean(response, "has_more");
        } while (hasMore);

        return linkedList;
    }

    private String postJson(String endpoint, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Plaid API request failed. HTTP " + response.statusCode() + ": " + response.body());
        }

        if (response.body().contains("\"error_code\"")) {
            throw new IOException("Plaid API returned error payload: " + response.body());
        }

        return response.body();
    }

    private String extractJsonString(String json, String fieldName) {
        Pattern p = Pattern.compile("\\\"" + Pattern.quote(fieldName) + "\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"])*)\\\"");
        Matcher m = p.matcher(json);
        if (!m.find()) {
            return "";
        }
        return unescapeJson(m.group(1));
    }

    private boolean extractJsonBoolean(String json, String fieldName) {
        Pattern p = Pattern.compile("\\\"" + Pattern.quote(fieldName) + "\\\"\\s*:\\s*(true|false)");
        Matcher m = p.matcher(json);
        return m.find() && Boolean.parseBoolean(m.group(1));
    }

    private double extractJsonNumber(String json, String fieldName) {
        Pattern p = Pattern.compile("\\\"" + Pattern.quote(fieldName) + "\\\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)");
        Matcher m = p.matcher(json);
        if (!m.find()) {
            return 0.0;
        }
        return Double.parseDouble(m.group(1));
    }

    private List<String> extractArrayObjects(String json, String fieldName) {
        List<String> objects = new ArrayList<>();
        int fieldIndex = json.indexOf("\"" + fieldName + "\"");
        if (fieldIndex < 0) {
            return objects;
        }

        int arrayStart = json.indexOf('[', fieldIndex);
        if (arrayStart < 0) {
            return objects;
        }

        int arrayEnd = findMatchingBracket(json, arrayStart, '[', ']');
        if (arrayEnd < 0) {
            return objects;
        }

        String arrayContent = json.substring(arrayStart + 1, arrayEnd);

        int i = 0;
        while (i < arrayContent.length()) {
            int objStart = arrayContent.indexOf('{', i);
            if (objStart < 0) {
                break;
            }
            int objEnd = findMatchingBracket(arrayContent, objStart, '{', '}');
            if (objEnd < 0) {
                break;
            }
            objects.add(arrayContent.substring(objStart, objEnd + 1));
            i = objEnd + 1;
        }

        return objects;
    }

    private int findMatchingBracket(String input, int openingIndex, char open, char close) {
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;

        for (int i = openingIndex; i < input.length(); i++) {
            char c = input.charAt(i);

            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (c == '\\') {
                    escaped = true;
                } else if (c == '"') {
                    inString = false;
                }
                continue;
            }

            if (c == '"') {
                inString = true;
                continue;
            }

            if (c == open) {
                depth++;
            } else if (c == close) {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }

        return -1;
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String unescapeJson(String value) {
        return value
                .replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\\", "\\");
    }
}
