package bg.sofia.uni.fkst.pik.minioutlook;

import bg.sofia.uni.fkst.pik.minioutlook.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OutlookTest {
    Outlook outlookSetup;
    Account account;

    @BeforeEach
    void setUp() {
        outlookSetup = new Outlook();
        account = new Account("gosho@abv.bg", "gosho");
        outlookSetup.addNewAccount(account.name(), account.emailAddress());
    }

    @Test
    @DisplayName("Tests the returned value of the .addNewAccount() method")
    void testAddNewAccountReturnedValue() {
        Account actualAccount = outlookSetup.addNewAccount("pesho", "pesho@abv.bg");
        Account expectedAccount = new Account("pesho@abv.bg", "pesho");
        assertEquals(expectedAccount, actualAccount, "Expected account is not equal to actual account" +
                " that's being returned");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @DisplayName("IllegalArgumentException being thrown for passing null as parameters for .addNewAccount() method")
    void testAddNewAccountIllegalArgumentException(String emptyOrBlankString) {
        assertAll(() ->
                        assertThrows(IllegalArgumentException.class, () ->
                                        outlookSetup.addNewAccount(emptyOrBlankString, "test@abv.bg")
                                , "IllegalArgumentException was expected for passing null" +
                                        " as accountName param, but never followed")
                , () ->
                        assertThrows(IllegalArgumentException.class, () ->
                                        outlookSetup.addNewAccount("gosho", emptyOrBlankString),
                                "IllegalArgumentException was expected for passing null" +
                                        " as email param, but never followed")
        );
    }

    @Test
    @DisplayName("Functional test of the .addNewAccount() method")
    void testAddNewAccountWorkingCorrectly() {
        Map<String, Account> accounts = outlookSetup.getAddedAccounts();
        assertAll(
                () -> assertTrue(accounts.containsKey(account.name()),
                        "A key for the account that was added is not found"),
                () -> assertTrue(accounts.containsValue(account),
                        "The account that was tried to be added is not present in the map of accounts")
        );
    }

    @Test
    @DisplayName("Test AccountAlreadyExistsException thrown by .addNewAccount() method")
    void testAddNewAccountAccountAlreadyExistsException() {

        assertThrows(AccountAlreadyExistsException.class, () ->
                        outlookSetup.addNewAccount("gosho", "gosho@abv.bg")
                , "AccountAlreadyExists exception was expected, but never followed");
    }

    @Test
    @DisplayName("IllegalArgumentException being thrown for passing null, empty or blank for the .createFolder() method")
    void testCreateFolderMethodIllegalArgumentException() {
        assertAll(() ->
                        assertThrows(IllegalArgumentException.class, () ->
                                        outlookSetup.createFolder(null, "root/")
                                , "IllegalArgumentException was expected for passing null as param but never followed")
                , () ->
                        assertThrows(IllegalArgumentException.class, () -> {
                            String blankString = " ";
                            outlookSetup.createFolder(blankString, "root/");
                        }, "IllegalArgumentException was expected for passing null as param but never followed")
                , () ->
                        assertThrows(IllegalArgumentException.class, () -> {
                            String emptyString = "";
                            outlookSetup.createFolder(emptyString, "root/");
                        }, "IllegalArgumentException was expected for passing null as param but never followed")

        );
    }

    @Test
    @DisplayName("AccountNotFoundException test for the .createFolder() method")
    void testCreateFolderMethodAccountNotFoundException() {
        assertThrows(AccountNotFoundException.class, () ->
                        outlookSetup.createFolder("testAccount", "root/important/")
                , "AccountNotFoundException was expected for not finding the account that was passed as argument," +
                        " but never occurred");
    }

    @Test
    @DisplayName("Tests .createFolder() for InvalidPathException being thrown for invalid path")
    void testCreateFolderInvalidPathException() {
        Account account = new Account("gosho@abv.bg", "gosho");
        assertAll(() ->
                        assertThrows(InvalidPathException.class, () ->
                                        outlookSetup.createFolder(account.name(), "main/")
                                , "InvalidPathException was expected for passing invalid path as parameter" +
                                        " but never followed")
                ,
                () ->
                        assertThrows(InvalidPathException.class, () ->
                                        outlookSetup.createFolder(account.name(), "root/important/personal")
                                /** might be bad test design **/
                                , "InvalidPathException was expected for passing invalid path as parameter"
                                        + " but never followed")

        );
    }

    @Test
    @DisplayName("Exception tests the .createFolder() method for passing as argument a folder that already exists")
    void testCreateFolderFolderAlreadyExistsException() {
        assertThrows(FolderAlreadyExistsException.class, () -> {
            outlookSetup.createFolder("gosho", "root/important/");
            outlookSetup.createFolder("gosho", "root/important/");
        }, "FolderAlreadyExistsException expected but never followed");
    }

    @Test
    @DisplayName("Tests the correct work of the .createFolder() method ")
    void testCreateFolderMethodCorrectWork() {


        outlookSetup.createFolder(account.name(), "root/important/");

        Map<Account, Folder> folderMap = outlookSetup.getFoldersHashMap();
        Folder folder = folderMap.get(account);

        List<Folder> subFolders = folder.getSubFolders();
        boolean contain = false;

        for (Folder iterator : subFolders) {
            if (iterator.getFolderName().equals("important/")) {
                contain = true;
                break;
            }
        }

        assertTrue(contain);
    }

    @Test
    @DisplayName("Tests the functionality of the .createFolder() method with more complex path ")
    void testCreateFolderMethodWithMoreComplexPath() {
        outlookSetup.createFolder(account.name(), "root/important/");
        outlookSetup.createFolder(account.name(), "root/important/work/");

        Map<Account, Folder> folderMap = outlookSetup.getFoldersHashMap();
        Folder folder = folderMap.get(account);

        List<Folder> subFolders = folder.getSubFolders();
        boolean contain = false;
        Folder tempFolder = null;

        for (int i = 0; i < subFolders.size(); i++) {
            if (subFolders.get(i).getFolderName().equals("important/")) {
                tempFolder = subFolders.get(i);
            }
        }

        subFolders = tempFolder.getSubFolders();

        for (int i = 0; i < subFolders.size(); i++) {
            if (subFolders.get(i).getFolderName().equals("work/")) {
                contain = true;
            }
        }

        assertTrue(contain);
    }

    @Test
    @DisplayName("Test correct work of .receiveMail() method")
    void testReceiveMailMethod() {
        String metaData =
                "sender: gosho@abv.bg\n" +
                        "subject: test\n" +
                        "recipients: gosho@abv.bg \n" +
                        "received: 2022-12-08 14:14";
        String mailContent = "Hello";

        Mail mail = Mail.decodeMetadata(outlookSetup.getAddedAccounts(), metaData, mailContent);
        outlookSetup.receiveMail(account.name(), metaData, mail.body());

        Map<Account, Folder> foldersHashMap = outlookSetup.getFoldersHashMap();
        Folder folder = foldersHashMap.get(account);
        List<Folder> subFolders = folder.getSubFolders();

        for (Folder index : subFolders) {
            if (index.getFolderName().equals("inbox/")) {
                folder = index;
            }
        }

        List<Mail> mailList = folder.getMailList();
        boolean contains = mailList.contains(mail);
        assertTrue(contains, "The email that was sent is not present in the expected directory");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("IllegalArgumentException tests for the .getMailsFromFolder() method")
    void testGetMailsFromFolderMethodForIllegalArgumentException(String emptyOrBlankString) {
        assertAll(
                () ->
                        assertThrows(IllegalArgumentException.class, () ->
                                        outlookSetup.getMailsFromFolder(emptyOrBlankString, "root/inbox/")
                                , "IllegalArgumentException was expected for blank, empty or null passed as argument for account but never followed")
                ,
                () ->
                        assertThrows(IllegalArgumentException.class, () ->
                                        outlookSetup.getMailsFromFolder(account.name(), emptyOrBlankString)
                                , "IllegalArgumentException was expected for blank, empty or null passed as argument for folderPath but never followed")

        );
    }

    @Test
    @DisplayName("AccountNotFoundException for .getMailsFromFolder() method")
    void testGetMailsFromFolderAccountNotFoundException() {
        assertThrows(AccountNotFoundException.class, () ->
                        outlookSetup.getMailsFromFolder("petko", "root/inbox/")
                , "AccountNotFoundException was expected but never followed");
    }

    @Test
    @DisplayName("FolderNotFoundException for .getMailsFromFolder() method")
    void testGetMailsFromFolderForFolderNotFoundException() {
        assertThrows(FolderNotFoundException.class, () ->
                        outlookSetup.getMailsFromFolder("gosho", "root/important/")
                , "FolderNotFoundException was expected but never followed");
    }

    @Test
    @DisplayName("Test the .getMailsFromFolder() method")
    void testTheGetMailsFromFolderMethod() {
        String metaData =
                "sender: gosho@abv.bg\n" +
                        "subject: test\n" +
                        "recipients: gosho@abv.bg \n" +
                        "received: 2022-12-08 14:14";
        outlookSetup.receiveMail(account.name(), metaData, "Hello");
        Mail mail = Mail.decodeMetadata(outlookSetup.getAddedAccounts(), metaData, "Hello");

        Collection<Mail> receivedMails = outlookSetup.getMailsFromFolder(account.name(), "root/inbox/");
        boolean contains = receivedMails.contains(mail);

        assertTrue(contains, "The mail was not found in the expected directory");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Test .sendMail() method for IllegalArgumentException")
    void testSendMailMethodForIllegalArgumentException(String emptyString) {
        String mailMetadata = "mailMetadata";
        String mailContent = "test";

        assertAll(() ->
                        assertThrows(IllegalArgumentException.class, () ->
                                        outlookSetup.sendMail(emptyString, mailMetadata, mailContent)
                                , "IllegalArgumentException was expected for passing null, blank or empty as parameter for accountName but never followed ")
                , () ->
                        assertThrows(IllegalArgumentException.class, () ->
                                        outlookSetup.sendMail(account.name(), emptyString, mailContent)
                                , "IllegalArgumentException was expected for passing null, blank or empty as parameter for mailMetadata but never followed ")
                , () ->
                        assertThrows(IllegalArgumentException.class, () ->
                                        outlookSetup.sendMail(account.name(), mailMetadata, emptyString)
                                , "IllegalArgumentException was expected for passing null, blank or empty as parameter for mailContent but never followed ")
        );
    }

    @Test
    @DisplayName("Test the .sendMail() method placing the sent mail in the sender's sent folder")
    void testSendMailMethodPlacingTheSendMailInTheSentFolder() {
        Account petkoAccount = outlookSetup.addNewAccount("petko", "petko@abv.bg");
        String metaData =
                "sender: petko@abv.bg\n" +
                        "subject: test\n" +
                        "recipients: gosho@abv.bg \n" +
                        "received: 2022-12-08 14:14";
        String mailContent = "test email";
        outlookSetup.sendMail(account.name(), metaData, mailContent);
        Mail sentMail = Mail.decodeMetadata(outlookSetup.getAddedAccounts(), metaData, mailContent);
        Collection<Mail> receivedMails = outlookSetup.getMailsFromFolder(account.name(), "root/sent/");

        assertNotNull(receivedMails);
        assertTrue(receivedMails.contains(sentMail));
    }

    @Test
    @DisplayName("Test the .sendMail() method for sending the mails")
    void testSendMailMethodSendMails() {
        Account petkoAccount = outlookSetup.addNewAccount("petko", "petko@abv.bg");
        String metaData =
                "sender: gosho@abv.bg\n" +
                        "subject: test\n" +
                        "recipients: petko@abv.bg \n" +
                        "received: 2022-12-08 14:14";
        String mailContent = "test email";
        outlookSetup.sendMail(account.name(), metaData, mailContent);

        Mail sentMail = Mail.decodeMetadata(outlookSetup.getAddedAccounts(), metaData, mailContent);
        Collection<Mail> receivedMails = outlookSetup.getMailsFromFolder(petkoAccount.name(), "root/inbox/");
        System.out.println(sentMail);
        System.out.println(receivedMails);
        assertNotNull(receivedMails);
        assertTrue(receivedMails.contains(sentMail));
    }

    @Test
    @DisplayName("Test the .sendMail() method without including the sender field ")
    void testSendMailWithoutSenderFieldIncludedInMetaData() {
        Account petkoAccount = outlookSetup.addNewAccount("petko", "petko@abv.bg");
        String metaData =
                "subject: test\n" +
                        "recipients: petko@abv.bg \n" +
                        "received: 2022-12-08 14:14";
        String mailContent = "test email";
        outlookSetup.sendMail(account.name(), metaData, mailContent);

        metaData = "sender: gosho@abv.bg\n" +
                "subject: test\n" +
                "recipients: petko@abv.bg \n" +
                "received: 2022-12-08 14:14";
        Mail sentMail = Mail.decodeMetadata(outlookSetup.getAddedAccounts(), metaData, mailContent);
        Collection<Mail> receivedMails = outlookSetup.getMailsFromFolder(petkoAccount.name(), "root/inbox/");
        System.out.println(sentMail);
        System.out.println(receivedMails);
        assertNotNull(receivedMails);
        assertTrue(receivedMails.contains(sentMail));

    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @DisplayName("IllegalArgumentException being thrown for passing null, empty or blank Strings for the .addRule() method")
    void testAddRuleIllegalArgumentException(String emptyOrBlankString) {
        String folderPath = "root/inbox/";
        String ruleDefinition =
                "subject-includes: mjt, izpit, 2022\n" +
                        "subject-or-body-includes: izpit\n" +
                        "from: stoyo@fmi.bg";
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> outlookSetup.addRule(emptyOrBlankString, folderPath, ruleDefinition, 5),
                        "IllegalArgumentException was expected for passing null, empty or blank for accountName but never followed"
                ),
                () -> assertThrows(IllegalArgumentException.class, () -> outlookSetup.addRule("gosho", emptyOrBlankString, ruleDefinition, 5)
                        , "IllegalArgumentException was expected for passing null, empty or blank for folderPath but never followed"
                ),
                () -> assertThrows(IllegalArgumentException.class, () -> outlookSetup.addRule("gosho", folderPath, emptyOrBlankString, 5)
                        , "IllegalArgumentException was expected for passing null, empty or blank for ruleDefinition but never followed"
                ),
                () -> assertThrows(IllegalArgumentException.class, () -> outlookSetup.addRule("gosho", folderPath, ruleDefinition, 0)
                        , "IllegalArgumentException was expected for passing 0 for priority but never followed"
                ),
                () -> assertThrows(IllegalArgumentException.class, () -> outlookSetup.addRule("gosho", folderPath, ruleDefinition, 11)
                        , "IllegalArgumentException was expected for passing 11 for priority but never followed"
                )
        );
    }

    @Test
    @DisplayName("Test the .addRule() method for AccountNotFoundException")
    void testAddRuleAccountNotFoundException() {
        String ruleDefinition =
                "subject-includes: mjt, izpit, 2022\n" +
                        "subject-or-body-includes: izpit\n" +
                        "from: stoyo@fmi.bg";
        assertThrows(AccountNotFoundException.class, () -> outlookSetup.addRule("petko", "root/inbox/", ruleDefinition, 5),
                "AccountNotFoundException was expected to be thrown for passing accountName that isn't added but nothing was thrown");
    }

    @Test
    @DisplayName("Test the .addRule() method for FolderNotFoundException")
    void testAddRuleFolderNotFoundException() {
        String ruleDefinition =
                "subject-includes: mjt, izpit, 2022\n" +
                        "subject-or-body-includes: izpit\n" +
                        "from: stoyo@fmi.bg";
        assertThrows(AccountNotFoundException.class, () -> outlookSetup.addRule("gosho", "root/important/", ruleDefinition, 5),
                "AccountNotFoundException was expected to be thrown for passing a folder that doesn't exists but nothing was thrown");
    }

    @Test
    @DisplayName("Test .addRule() working correctly with one matching tag")
    void testAddRuleWorkingCorrectly() {
        String metaData = "sender: gosho@abv.bg\n" +
                "subject: test\n" +
                "recipients: petko@abv.bg \n" +
                "received: 2022-12-08 14:14";
        String mailContent = "Hello";

        String ruleDefinition =
                "subject-includes: test\n" +
                        "subject-or-body-includes: izpit\n";

        outlookSetup.createFolder(account.name(), "root/inbox/spam/");
        outlookSetup.receiveMail(account.name(), metaData, mailContent);
        Mail mail = Mail.decodeMetadata(outlookSetup.getAddedAccounts(), metaData, mailContent);
        outlookSetup.addRule(account.name(), "root/inbox/spam/", ruleDefinition, 5);

        Collection<Mail> mailList = outlookSetup.getMailsFromFolder(account.name(), "root/inbox/spam");
        assertTrue(mailList.contains(mail), "The mail is not in the expected directory, according to the rule");
    }

    @Test
    @DisplayName("Test .addRule() working correctly with multiple tags")
    void testAddRuleWithMultipleTags() {
        String metaData = "sender: gosho@abv.bg\n" +
                "subject: test\n" +
                "recipients: petko@abv.bg, gosho@abv.bg \n" +
                "received: 2022-12-08 14:14";
        String mailContent = "Hello";

        String ruleDefinition =
                "subject-includes: test\n" +
                        "recipients-includes: petko@abv.bg";

        outlookSetup.createFolder(account.name(), "root/inbox/spam/");
        outlookSetup.receiveMail(account.name(), metaData, mailContent);
        Mail mail = Mail.decodeMetadata(outlookSetup.getAddedAccounts(), metaData, mailContent);
        outlookSetup.addRule(account.name(), "root/inbox/spam/", ruleDefinition, 5);

        Collection<Mail> mailList = outlookSetup.getMailsFromFolder(account.name(), "root/inbox/spam/");
        assertTrue(mailList.contains(mail), "The mail is not in the expected directory, according to the rule");
    }

    @Test
    @DisplayName("Test .addRule() working correctly with Rule definition containing all possible Rule conditions")
    void testAddRuleWithRuleDefinitionHavingAllRuleConditions() {
        String metaData = "sender: gosho@abv.bg\n" +
                "subject: youtuber uploaded a new video\n" +
                "recipients: petko@abv.bg, gosho@abv.bg \n" +
                "received: 2022-12-08 14:14";
        String mailContent = "The youtuber uploaded a new video on his main channel. Click the link to watch it now";

        String ruleDefinition =
                "subject-includes: uploaded, video\n" +
                        "subject-or-body-includes: watch, youtuber\n" +
                        "recipients-includes: petko@abv.bg\n" +
                        "from: gosho@abv.bg";

        outlookSetup.createFolder(account.name(), "root/inbox/spam/");
        outlookSetup.receiveMail(account.name(), metaData, mailContent);
        Mail mail = Mail.decodeMetadata(outlookSetup.getAddedAccounts(), metaData, mailContent);
        System.out.println(mail);
        outlookSetup.addRule(account.name(), "root/inbox/spam/", ruleDefinition, 5);

        Collection<Mail> mailList = outlookSetup.getMailsFromFolder(account.name(), "root/inbox/spam/");
        assertTrue(mailList.contains(mail), "The mail is not in the expected directory, according to the rule");
    }

    @Test
    @DisplayName("Test .createFolder() then go to that folder")
    void testCreateFolderThenGoToThatFolder() {
        outlookSetup.createFolder(account.name(), "root/inbox/spam/");
        Map<Account, Folder> folderHashMap = outlookSetup.getFoldersHashMap();
        Folder folder = folderHashMap.get(account);
        folder = folder.goToFolder("root/inbox/spam/");

        String actualPath = folder.getFolderPath();
        String expectedPath = "root/inbox/spam/";
        assertEquals(expectedPath, actualPath, "Expected path is not matching the actual path");
    }

}
