package bg.sofia.uni.fkst.pik.minioutlook;

import bg.sofia.uni.fkst.pik.minioutlook.exceptions.*;

import java.util.*;

public class Outlook implements MailClient {

    private Map<String, Account> accountsHashMap = new HashMap<>();
    private Map<Account, Folder> foldersHashMap = new HashMap<>();
    private Map<Account, List<Rule>> rules = new HashMap<>();

    @Override
    public Account addNewAccount(String accountName, String email) throws IllegalArgumentException {
        if (accountName == null || accountName.isEmpty() || accountName.isBlank()) {
            throw new IllegalArgumentException("AccountName can't be null, empty or blank");
        }
        if (email == null || email.isEmpty() || email.isBlank()) {
            throw new IllegalArgumentException("Email can't be null, empty or blank");
        }
        if (accountsHashMap.containsKey(accountName)) {
            throw new AccountAlreadyExistsException("Account already exists", new Throwable());
        }

        Account account = new Account(email, accountName);
        accountsHashMap.put(accountName, account);
        foldersHashMap.put(account, new Folder());

        return account;
    }

    @Override
    public void createFolder(String accountName, String path) {
        if (accountName == null || accountName.isBlank() || accountsHashMap.isEmpty()) {
            throw new IllegalArgumentException("Account name can't be null, blank or empty", new Throwable());
        }
        if (path == null || path.isBlank() || path.isEmpty()) {
            throw new IllegalArgumentException("Path can't be null, blank or empty", new Throwable());
        }
        if (!path.contains("/") || path.startsWith("/") || !path.endsWith("/") || !path.startsWith("root/")) {
            throw new InvalidPathException("Path string doesn't start with root or it has invalid folder separator", new Throwable());
        }
        if (!accountsHashMap.containsKey(accountName)) {
            throw new AccountNotFoundException("Account was not found", new Throwable());
        }

        Folder folder = foldersHashMap.get(accountsHashMap.get(accountName));

        int indexLastSlash = path.lastIndexOf("/");
        int indexPreLastSlash = path.lastIndexOf("/", indexLastSlash - 1);
        String nameOfNewFolder = path.substring(indexPreLastSlash + 1);
        path = path.substring(0, indexPreLastSlash + 1);

        folder = folder.goToFolder(path);
        List<Folder> subFolders = folder.getSubFolders();
        for (Folder index : subFolders) {
            if (index.getFolderName().equals(nameOfNewFolder)) {
                throw new FolderAlreadyExistsException("Folder already exists", new Throwable());
            }
        }
        new Folder(nameOfNewFolder, folder);
    }

    @Override
    public void addRule(String accountName, String folderPath, String ruleDefinition, int priority) {
        if (accountName == null || accountName.isBlank() || accountName.isEmpty()) {
            throw new IllegalArgumentException("Account name can't be null, empty or blank");
        }
        if (folderPath == null || folderPath.isBlank() || folderPath.isEmpty()) {
            throw new IllegalArgumentException("Folder Path name can't be null, empty or blank");
        }
        if (ruleDefinition == null || ruleDefinition.isBlank() || ruleDefinition.isEmpty()) {
            throw new IllegalArgumentException("Rule Definition name can't be null, empty or blank");
        }
        if (priority == 0 || priority > 10) {
            throw new IllegalArgumentException("Priority can't be zero or bigger than 10");
        }
        Account account = accountsHashMap.get(accountName);
        if (account == null) {
            throw new AccountNotFoundException("Account was not found", new Throwable());
        }
        Rule rule = createRuleFromRuleDefinition(ruleDefinition);
        System.out.println("rule contains for subject " + rule.subjectIncludes());
        System.out.println("rule contains for subjectOrBody " + rule.subjectOrBodyIncludes());
        System.out.println("rule contains for recipients " + rule.recipientsIncludes());
        System.out.println("rule contains for sender " + rule.sender());
        System.out.println("--------------- numberOfRuleConditions is:" + rule.numberOfRuleConditions());

        Folder folder = foldersHashMap.get(account);
        folder = folder.goToFolder("root/inbox/");
        List<Mail> mailList = folder.getMailList();
        boolean contains = false;
        int counter = 0;
        int numberOfRuleConditions = rule.numberOfRuleConditions();

        for (Mail index : mailList) {
            counter = 0;
            if (rule.subjectIncludes() != null) {
                contains = checkIfContainsTags(index.subject(), rule.subjectIncludes());
                if (contains) {
                    counter++;
                    contains = false;
                }
            }
            if (rule.subjectOrBodyIncludes() != null) {
                contains = checkIfContainsTags(index.subject(), rule.subjectOrBodyIncludes());
                if (contains) {
                    counter++;
                    contains = false;
                }
            }
            if (rule.recipientsIncludes() != null) {
                contains = checkIfContainsTagsForRecipients(index.recipients(), rule.recipientsIncludes());
                if (contains) {
                    counter++;
                    contains = false;
                }
            }
            if (rule.sender() != null) {
                contains = index.sender().emailAddress().equals(rule.sender());
                if (contains) {
                    counter++;
                    contains = false;
                }
            }

            if (counter == numberOfRuleConditions) {
                moveMailFromInboxToFolder(index, folderPath, folder);
            }
        }
    }

    private void moveMailFromInboxToFolder(Mail index, String folderPath, Folder currentFolder) {
        currentFolder = currentFolder.goToFolder("root/inbox/");
        currentFolder.removeMailFromFolder(index);
        currentFolder = currentFolder.goToFolder(folderPath);
        currentFolder.placeEmailInFolder(index);
    }

    private boolean checkIfContainsTagsForRecipients(Set<String> recipientsFromMail, Set<String> tags) {
        boolean containsTags = false;
        for (String recipient : recipientsFromMail) {
            for (String tag : tags)
                if (recipient.equals(tag)) {
                    containsTags = true;
                    break;
                }
        }
        return containsTags;
    }

    private boolean checkIfContainsTags(String textToBeSearchedForTags, Set<String> tags) {
        boolean containsTags = false;
        for (String stringIndex : tags) {
            if (textToBeSearchedForTags.contains(stringIndex)) {
                containsTags = true;
            } else if (!textToBeSearchedForTags.contains(stringIndex)) {
                containsTags = false;
            }
        }
        return containsTags;
    }

    @Override
    public void receiveMail(String accountName, String mailMetadata, String mailContent) {
        Account account = accountsHashMap.get(accountName);
        Mail mail = Mail.decodeMetadata(accountsHashMap, mailMetadata, mailContent);

        Folder folder = foldersHashMap.get(account);
        folder = folder.goToFolder("root/inbox/");
        folder.placeEmailInFolder(mail);
    }

    @Override
    public Collection<Mail> getMailsFromFolder(String account, String folderPath) {
        if (account == null || folderPath == null) {
            throw new IllegalArgumentException("The passed arguments for account or email can't be null");
        }
        if (account.isEmpty() || account.isBlank() || folderPath.isEmpty() || folderPath.isBlank()) {
            throw new IllegalArgumentException("The passed arguments for account or email can't be empty or blank");
        }
        if (!accountsHashMap.containsKey(account)) {
            throw new AccountNotFoundException("Account does not exists", new Throwable());
        }

        Account mailAccount = accountsHashMap.get(account);
        Folder folder = foldersHashMap.get(mailAccount);
        folder = folder.goToFolder(folderPath);

        return folder.getMailList();
    }

    @Override
    public void sendMail(String accountName, String mailMetadata, String mailContent) {
        if (accountName == null || accountName.isBlank() || accountName.isEmpty()) {
            throw new IllegalArgumentException("AccountName can't be null, empty or blank ");
        }
        if (mailMetadata == null || mailMetadata.isBlank() || mailMetadata.isEmpty()) {
            throw new IllegalArgumentException("MailMetadata can't be null, empty or blank ");
        }
        if (mailContent == null || mailContent.isBlank() || mailContent.isEmpty()) {
            throw new IllegalArgumentException("MailContent can't be null, empty or blank ");
        }

        Account account = accountsHashMap.get(accountName);  //sender
        if (!mailMetadata.contains("sender:") || !mailMetadata.contains(account.emailAddress())) {
            mailMetadata = "sender: " + account.emailAddress() + "\n" + mailMetadata;
        }
        Mail mail = Mail.decodeMetadata(accountsHashMap, mailMetadata, mailContent);
        Set<String> recipients = mail.recipients();
        recipients = getAccountNamesFromEmails(recipients);

        for (String recipient : recipients) {
            receiveMail(recipient, mailMetadata, mailContent);
        }

        Folder folder = foldersHashMap.get(account);

        try {
            folder = folder.goToFolder("root/sent/");
        } catch (FolderNotFoundException e) {
            System.err.println("Exception occurred " + e);
            e.printStackTrace();
        }

        folder.placeEmailInFolder(mail);
    }

    public Map<String, Account> getAddedAccounts() {
        return Collections.unmodifiableMap(accountsHashMap);
    }

    public Map<Account, Folder> getFoldersHashMap() {
        return Collections.unmodifiableMap(foldersHashMap);
    }

    private Set<String> getAccountNamesFromEmails(Set<String> emails) {
        Set<String> accountNames = new HashSet<>();

        for (String accountEmail : emails) {
            for (Map.Entry<String, Account> entry : accountsHashMap.entrySet()) {
                if (entry.getValue().emailAddress().equals(accountEmail)) {
                    accountNames.add(entry.getKey());
                }
            }
        }

        return Collections.unmodifiableSet(accountNames);
    }

    private Rule createRuleFromRuleDefinition(String ruleDefinition) {
        int newLineIndex;
        Set<String> subjectKeyWords = null;
        Set<String> subjectOrBodyKeyWords = null;
        Set<String> recipientsKeyWords = null;
        String sender = null;
        int ruleConditionsCounter = 0;

        if (ruleDefinition.contains("subject-includes:")) {
            subjectKeyWords = takeKeyWords(ruleDefinition, "subject-includes: ");
            ruleConditionsCounter++;
        }
        if (ruleDefinition.contains("subject-or-body-includes:")) {
            subjectOrBodyKeyWords = takeKeyWords(ruleDefinition, "subject-or-body-includes: ");
            ruleConditionsCounter++;
        }
        if (ruleDefinition.contains("recipients-includes:")) {
            recipientsKeyWords = takeKeyWords(ruleDefinition, "recipients-includes: ");
            ruleConditionsCounter++;
        }
        if (ruleDefinition.contains("from:")) {
            newLineIndex = ruleDefinition.indexOf("from:");
            sender = ruleDefinition.substring(newLineIndex + 6);
            ruleConditionsCounter++;
        }
        return new Rule(subjectKeyWords, subjectOrBodyKeyWords, recipientsKeyWords, sender, ruleConditionsCounter);
    }

    private Set<String> takeKeyWords(String ruleDefinition, String ruleCondition) {

        int colonIndex = ruleDefinition.indexOf(ruleCondition);
        int newLineIndex = ruleDefinition.indexOf("\n", colonIndex);

        if (newLineIndex != -1) {
            ruleDefinition = ruleDefinition.substring(colonIndex + ruleCondition.length(), newLineIndex);
        } else {
            ruleDefinition = ruleDefinition.substring(colonIndex + ruleCondition.length());
        }

        String[] keyWords = ruleDefinition.split(",");

        for (int i = 0; i < keyWords.length; i++) {
            keyWords[i] = keyWords[i].trim();
        }
        return new HashSet<>(Arrays.asList(keyWords));
    }

    public void menu(){
        System.out.println("---------------------------");
        System.out.println("1 - Add new account");
        System.out.println("2 - Create new folder");
        System.out.println("3 - Create new rule");
        System.out.println("4 - Get mails from folder");
        System.out.println("5 - Send a mail");
        System.out.println("6 - Exit");
        System.out.println("---------------------------");

    }
}
