package bg.sofia.uni.fkst.pik.minioutlook;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public record Mail(Account sender, Set<String> recipients, String subject, String body, LocalDateTime received) {
    final static int UNUSED_SPACE_AFTER_DOT = 2;
    final static int METADATA_ROWS = 4;

    public Mail {
        if (sender == null) {
            throw new IllegalArgumentException("Account sender can't be null");
        }
        if (recipients == null || recipients.isEmpty()) {
            throw new IllegalArgumentException("The list of recipients can't be null or empty can't be null");
        }
        if (subject == null || subject.isEmpty() || subject.isBlank()) {
            throw new IllegalArgumentException("The subject can't be null, blank or empty");
        }
        if (body == null) {
            throw new IllegalArgumentException("Body can't be null");
        }
        if (received == null) {
            throw new IllegalArgumentException("Time of receive can't be null");
        }
    }

    public static Mail decodeMetadata(Map<String, Account> accountsHashMap, String mailMetadata, String mailContent) {
        int[] newLineIndexArray = new int[METADATA_ROWS];
        int[] doubleDotIndexArray = new int[METADATA_ROWS];
        int i = 0;
        int ROW = 0;

        while (i < METADATA_ROWS) {
            if (i == 0) {
                newLineIndexArray[i] = mailMetadata.indexOf("\n");
                doubleDotIndexArray[i] = mailMetadata.indexOf(":");
                i++;
                continue;
            }
            newLineIndexArray[i] = mailMetadata.indexOf("\n", newLineIndexArray[i - 1] + 1);
            doubleDotIndexArray[i] = mailMetadata.indexOf(":", doubleDotIndexArray[i - 1] + 1);
            i++;
        }

        String sender = mailMetadata.substring(doubleDotIndexArray[ROW] + UNUSED_SPACE_AFTER_DOT, newLineIndexArray[ROW]);
        sender = sender.trim();
        Account accountSender = null;
        for (Map.Entry<String, Account> entry : accountsHashMap.entrySet()) {
            if (entry.getValue().emailAddress().equals(sender)) {
                accountSender = entry.getValue();
            }
        }
        ROW++;

        String subject = mailMetadata.substring(doubleDotIndexArray[ROW] + UNUSED_SPACE_AFTER_DOT, newLineIndexArray[ROW]);
        ROW++;

        String[] recipientsStringArray = (mailMetadata.substring(doubleDotIndexArray[ROW] + UNUSED_SPACE_AFTER_DOT, newLineIndexArray[ROW]))
                .split(",");
        Set<String> recipients = new LinkedHashSet<>();
        for (int j = 0; j < recipientsStringArray.length; j++) {
            recipientsStringArray[j] = recipientsStringArray[j].trim();

            if (!(recipientsStringArray[j].isBlank() || recipientsStringArray[j].isEmpty())) {
                recipients.add(recipientsStringArray[j]);
            }
        }
        ROW++;

        String timeReceivedString = mailMetadata.substring(doubleDotIndexArray[ROW] + UNUSED_SPACE_AFTER_DOT);
        LocalDateTime receivedAt = LocalDateTime.parse(timeReceivedString.replace(" ", "T"));

        return new Mail(accountSender, recipients, subject, mailContent, receivedAt);
    }
}
