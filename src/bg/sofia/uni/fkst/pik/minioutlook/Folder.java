package bg.sofia.uni.fkst.pik.minioutlook;

import bg.sofia.uni.fkst.pik.minioutlook.exceptions.FolderNotFoundException;
import bg.sofia.uni.fkst.pik.minioutlook.exceptions.InvalidPathException;
import bg.sofia.uni.fkst.pik.minioutlook.exceptions.MailNotInFolderException;

import java.util.*;

public class Folder {
    private List<Mail> mailList;
    private String folderName;
    private List<Folder> subFolders;
    private Folder parentFolder;

    public Folder() {
        folderName = "root/";
        subFolders = new ArrayList<>(3);
        new Folder("inbox/", this);
        new Folder("sent/", this);
        parentFolder = null;
    }

    public Folder(String folderName, Folder parentFolder) throws InvalidPathException, IllegalArgumentException {
        if (parentFolder == null) {
            throw new IllegalArgumentException("The parent folder passed as argument can't be null", new Throwable());
        }
        if (!folderName.contains("/")) {
            throw new InvalidPathException("Passed String must include '/'", new Throwable());
        }
        this.folderName = folderName;

        this.parentFolder = parentFolder;
        this.parentFolder.addFolder(this);  //adding the newly created folder to the list of subFolders of the parent folder
    }

    public void addFolder(Folder folder) {
        if (subFolders == null) {
            subFolders = new ArrayList<>();
        }

        subFolders.add(folder);
    }

    public void placeEmailInFolder(Mail mail) {
        if (mailList == null) {
            mailList = new LinkedList<>();
        }
        mailList.add(mail);
    }

    public void removeMailFromFolder(Mail mail) {
        if (mailList == null || !mailList.contains(mail)) {
            throw new MailNotInFolderException("The mail you are trying to remove is not in the folder", new Throwable());
        }
        mailList.remove(mail);
    }

    public List<Mail> getMailList() {
        if (mailList == null) {
            return null;
        }//probably have to remove it or make it private
        // cuz I have the same method in the outlook class
        return Collections.unmodifiableList(mailList);
    }

    public Folder getParentFolder() {
        return parentFolder;
    }

    public List<Folder> getSubFolders() {
        if (subFolders == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(subFolders);
    }

    public String getFolderName() {
        return folderName;
    }

    public String getFolderPath() {
        StringBuilder path = new StringBuilder();
        StringBuilder stringBuilder = new StringBuilder();
        Folder folder = this;

        while (folder != null) {  //getting the path, but from the current folder backwards
            path.append(folder.folderName);
            folder = folder.parentFolder;
        }

        List<String> foldersList = convertPathToSeparateFoldersList(path.toString());
        ListIterator<String> arrayListIterator = foldersList.listIterator(foldersList.size());

        while (arrayListIterator.hasPrevious()) {
            stringBuilder.append(arrayListIterator.previous());
        }
        return stringBuilder.toString();
    }

    public static List<String> convertPathToSeparateFoldersList(String path) {
        List<String> separateFolders = new ArrayList<>();
        int slashIndex = path.indexOf("/");
        int lastEncounteredSlashIndex;
        int order = 0;
        int lastIndexOfSlash = path.lastIndexOf("/");

        while (slashIndex <= lastIndexOfSlash) {
            if (order == 0) {
                separateFolders.add(path.substring(0, slashIndex + 1));
                order++;
                continue;
            }

            lastEncounteredSlashIndex = slashIndex;
            slashIndex = path.indexOf("/", slashIndex + 1);
            if (slashIndex == -1) {
                break;
            }

            separateFolders.add(path.substring(lastEncounteredSlashIndex + 1, slashIndex + 1));
        }
        return Collections.unmodifiableList(separateFolders);
    }

    public Folder goToFolder(String path) throws FolderNotFoundException {
        List<String> pathList = convertPathToSeparateFoldersList(path);
        ListIterator<String> pathIterator = pathList.listIterator();
        String destinationFolder = null;
        Folder folder = this;

        if (!folder.folderName.equals("root/")) {
            while (folder.getParentFolder() != null) {
                folder = folder.getParentFolder();
            }
        }

        while (pathIterator.hasNext()) {  //we get the destination folder which is the last folder in the path
            destinationFolder = pathIterator.next();
        }

        String currentFolderNameInPath;

        while (pathIterator.hasPrevious()) {
            currentFolderNameInPath = pathIterator.previous();
        }

        currentFolderNameInPath = pathIterator.next();

        while (!(folder.folderName.equals(destinationFolder))) {
            if (!folder.folderName.equals(currentFolderNameInPath) || folder.subFolders == null) {
                throw new FolderNotFoundException("Exception occurred, " +
                        "some folders that are in the path do not exists ", new Throwable());
            }
            currentFolderNameInPath = pathIterator.next();

            for (Folder subFolder : folder.subFolders) {
                if (subFolder.folderName.equals(currentFolderNameInPath)) {
                    folder = subFolder;
                    break;
                }
            }
        }
        return folder;
    }

    @Override
    public String toString() {
        return folderName;
    }
}
