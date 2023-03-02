package bg.sofia.uni.fkst.pik.minioutlook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

public class Main {
    public static void main(String[] args) throws IOException {
        start();
    }

    public static void start() {
        Outlook outlook = new Outlook();
        BufferedReader br = new BufferedReader(
                new InputStreamReader(System.in));
        int choice;
        int state;
        String metadata = "sender: gosho@abv.bg\n" +
                "subject: subject example\n" +
                "recipients: petko@abv.bg, gosho@abv.bg \n" +
                "received: 2022-12-08 14:14";

        String ruleDefinition = "subject-includes: uploaded, video\n" +
                "subject-or-body-includes: watch, youtuber\n" +
                "recipients-includes: petko@abv.bg\n" +
                "from: gosho@abv.bg";

        try {
            do {
                state = 1;
                outlook.menu();
                choice = Integer.parseInt(br.readLine());
                String accountName = null;

                switch (choice) {
                    case 1:
                        System.out.println("Enter account name");
                        accountName = br.readLine();
                        System.out.println("Enter account name");
                        String email = br.readLine();
                        outlook.addNewAccount(accountName, email);
                        break;
                    case 2:
                        System.out.println("Enter account name");
                        accountName = br.readLine();
                        System.out.println("Enter folder's path ");
                        System.out.println("Folder path must always start with root and end with '/'");
                        System.out.println("Example: root/inbox/spam/ ");
                        String folderPath = br.readLine();
                        outlook.createFolder(accountName, folderPath);
                        break;
                    case 3:
                        System.out.println("Enter account name");
                        accountName = br.readLine();
                        System.out.println("Enter destination folder's path ");
                        folderPath = br.readLine();
                        System.out.println("Rule definition added automatically");
                        System.out.println("Enter priority of rule");
                        int priority = Integer.parseInt(br.readLine());
                        outlook.addRule(accountName, folderPath, ruleDefinition, priority);
                        break;
                    case 4:
                        System.out.println("Enter account name");
                        accountName = br.readLine();
                        System.out.println("Enter folder's path ");
                        folderPath = br.readLine();
                        Collection<Mail> mails = outlook.getMailsFromFolder(accountName, folderPath); //  root/inbox/
                        System.out.println(mails);
                        break;
                    case 5:
                        System.out.println("Enter account name");
                        accountName = br.readLine();
                        // System.out.println("Enter account metadata");
                        // String mailMetadata = br.readLine();
                        System.out.println("Metadata added automatically");
                        System.out.println("Enter mail content");
                        String mailContent = br.readLine();
                        outlook.sendMail(accountName, metadata, mailContent);
                        break;
                    case 6:
                        state = 0;
                        break;
                }
            }
            while (state == 1);
        } catch (Exception e) {
            System.out.println("Exception occurred " + e);
            e.printStackTrace();
        }
    }
}
