package bg.sofia.uni.fkst.pik.minioutlook;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public class MailTest {
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @DisplayName("Testing the constructor for IllegalArguments Exception")
    void testConstructorForIllegalArgumentsException(String emptyOrBlankString) {
        Account account = new Account("test@abv.bg", "test");
        Set<String> recipients = new TreeSet<>();
        String subject = "test";
        String body = "test";

        assertAll(
                () ->
                        assertThrows(IllegalArgumentException.class, () ->
                                        new Mail(null, recipients, emptyOrBlankString, body, LocalDateTime.now())
                                , "IllegalArgumentException was expected to be thrown for passing null as accountSender, but was never thrown")
                ,
                () ->
                        assertThrows(IllegalArgumentException.class, () ->
                                        new Mail(account, null, subject, emptyOrBlankString, LocalDateTime.now())
                                , "IllegalArgumentException was expected to be thrown, but was never thrown for passing null set of recipients")
                ,
                () ->
                        assertThrows(IllegalArgumentException.class, () ->
                                        new Mail(account, recipients, subject, body, null)
                                , "IllegalArgumentException was expected to be thrown for passing null LocalDateTime, but was never thrown")
        );
    }

    @Test
    @DisplayName("test .decodeMetadata() method functionality")
    void testDecodeMetadataMethodFunctionality() {
        Account account = new Account("gosho@abv.bg", "gosho");
        Outlook outlook = new Outlook();
        outlook.addNewAccount(account.name(), account.emailAddress());
        Set<String> recipients = new LinkedHashSet<>();
        recipients.add(account.emailAddress());
        String subject = "test";
        String mailContent = "Hello World";
        LocalDateTime localDateTime = LocalDateTime.of(2022, 12, 8, 14, 14);
        Mail expectedResult = new Mail(account, recipients, subject, mailContent, localDateTime);

        String metaData =
                "sender: gosho@abv.bg\n" +
                        "subject: test\n" +
                        "recipients: gosho@abv.bg,\n" +
                        "received: 2022-12-08 14:14";
        Mail actualResult = Mail.decodeMetadata(outlook.getAddedAccounts(), metaData, mailContent);

        assertEquals(expectedResult, actualResult, "Expected result is not matching with actual result");
    }

    @Test
    @DisplayName("test .decodeMetadata() method functionality with more recipients")
    void testDecodeMetadataMethodFunctionalityWithMoreRecipients() {
        Account account = new Account("gosho@abv.bg", "gosho");
        Outlook outlook = new Outlook();
        outlook.addNewAccount(account.name(), account.emailAddress());
        Set<String> recipients = new LinkedHashSet<>();
        recipients.add(account.emailAddress());
        recipients.add("pesho@abv.bg");
        String subject = "test";
        String mailContent = "Hello World";
        LocalDateTime localDateTime = LocalDateTime.of(2022, 12, 8, 14, 14);
        Mail expectedResult = new Mail(account, recipients, subject, mailContent, localDateTime);

        String metaData =
                "sender: gosho@abv.bg\n" +
                        "subject: test\n" +
                        "recipients: gosho@abv.bg, pesho@abv.bg, \n" +
                        "received: 2022-12-08 14:14";
        Mail actualResult = Mail.decodeMetadata(outlook.getAddedAccounts(), metaData, mailContent);

        assertEquals(expectedResult, actualResult, "Expected result is not matching with actual result");
    }
}
