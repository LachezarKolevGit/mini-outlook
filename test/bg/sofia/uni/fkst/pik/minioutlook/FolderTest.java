package bg.sofia.uni.fkst.pik.minioutlook;

import bg.sofia.uni.fkst.pik.minioutlook.exceptions.InvalidPathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FolderTest {

    Folder rootFolderSetUp;
    Folder importantFolderSetUp;

    @BeforeEach
    void setUpFolderObject() {
        rootFolderSetUp = new Folder();  //creating a root folder
        importantFolderSetUp = new Folder("important/", rootFolderSetUp);

    }

    @Test
    @DisplayName("Exception testing the constructor for being passed invalid arguments")
    void exceptionTestingTheConstructor() {
        assertAll(
                () ->
                        assertThrows(InvalidPathException.class, () -> {
                            Folder parentFolder = new Folder("root", rootFolderSetUp);
                            Folder folder = new Folder("important", parentFolder);
                        }, "InvalidPathException was expected but never followed")
                ,
                () ->
                        assertThrows(IllegalArgumentException.class, () -> {
                            Folder folder = new Folder("folderName/", null);
                        }, "IllegalArgumentException was expected for passing null as parentFolder" +
                                " but wasn't thrown")
        );
    }

    @Disabled
    @Test
    @DisplayName("Testing the .addFolder() for not throwing unexpected exceptions")
    void testingTheAddFolderForNotThrowingUnexpectedExceptions() {
        assertDoesNotThrow(() -> {
            Folder spamFolder = new Folder("spam/", rootFolderSetUp);
            // rootFolderSetUp.addFolder(spamFolder);
        }, "using the .addFolder() method on Folder object constructed with no-parameter" +
                " constructor should not throw Exceptions ");
        assertDoesNotThrow(() -> {

            Folder spamFolder = new Folder("spam/", rootFolderSetUp);
            Folder socials = new Folder("socials/", spamFolder);
            //spamFolder.addFolder(spamFolder);
        }, " using the .addFolder() method on Folder object constructed with the parameterized" +
                " constructor should not throw Exceptions");
    }

    @Test
    @DisplayName("Testing the returned String of the .getFolderPath() method for one subFolder")
    void testTheReturnedStringOfTheGetFolderPathMethod() {
        String actualReturnedPath = importantFolderSetUp.getFolderPath();
        String expectedReturnedPath = "root/important/";
        assertEquals(expectedReturnedPath, actualReturnedPath,
                "Actual returned path is not the same as the expected returned path");
    }

    @Test
    @DisplayName("Testing the returned String of the .getFolderPath() method for two subFolders")
    void testTheReturnedStringOfTheGetFolderPathMethodTwoSubFolders() {
        Folder workFolder = null;
        try {
            workFolder = new Folder("work/", importantFolderSetUp);
        } catch (InvalidPathException e) {
            System.err.println("Exception occurred " + e);
            e.printStackTrace();
        }
        String actualReturnedPath = workFolder.getFolderPath();
        String expectedReturnedPath = "root/important/work/";

        assertEquals(expectedReturnedPath, actualReturnedPath,
                "Actual returned path is not the same as the expected returned path");
    }

    @Test
    @DisplayName("Testing the returned String of the .getFolderPath() method for three subFolders")
    void testTheReturnedStringOfTheGetFolderPathMethodThreeSubFolders() {
        Folder workFolder = null;
        Folder meetingsFolder = null;
        try {
            workFolder = new Folder("work/", importantFolderSetUp);
            meetingsFolder = new Folder("meetings/", workFolder);
        } catch (InvalidPathException e) {
            System.err.println("Exception occurred " + e);
            e.printStackTrace();
        }

        String actualReturnedPath = meetingsFolder.getFolderPath();
        String expectedReturnedPath = "root/important/work/meetings/";
        System.out.println(rootFolderSetUp.getSubFolders());
        assertEquals(expectedReturnedPath, actualReturnedPath,
                "Actual returned path is not the same as the expected returned path");
    }

    @Test
    @DisplayName("Testing the .convertWholePathToSeparateFolders() method with basic path")
    void testConvertWholePathToSeparateFoldersMethod() {
        List<String> expectedList = new ArrayList<>();
        expectedList.add("root/");
        expectedList.add("important/");
        List<String> actualList = Folder.convertPathToSeparateFoldersList("root/important/");
        assertEquals(expectedList, actualList, "Expected List is not the same with the actual list");
    }

    @Test
    @DisplayName("Testing the .convertWholePathToSeparateFolders() method with more complex path")
    void testConvertWholePathToSeparateFoldersMethodWithMoreComplexPath() {
        List<String> expectedList = new ArrayList<>();
        expectedList.add("root/");
        expectedList.add("important/");
        expectedList.add("work/");
        expectedList.add("meetings/");
        List<String> actualList = Folder.convertPathToSeparateFoldersList("root/important/work/meetings/");
        assertEquals(expectedList, actualList, "Expected List is not the same with the actual list");
    }

    @Test
    @DisplayName("Test for folders with the same name")
    void testForFoldersWithTheSameName() {
        boolean hasDuplicate = false;
        List<Folder> subFolders = rootFolderSetUp.getSubFolders();

        for (int i = 0; i < subFolders.size(); i++) {
            for (int j = i + 1; j < subFolders.size(); j++) {

                if (subFolders.get(i).getFolderName().equals(subFolders.get(j).getFolderName())) {
                    hasDuplicate = true;
                    break;
                }
            }
        }

        assertFalse(hasDuplicate,
                "There are two or more folders with the same name");
    }

    @Test
    @DisplayName("Test .goToFolder() method for functionality")
    void testGoToFolderMethodForFunctionality() {
        Folder folder = null;
        try {
            folder = new Folder("spam/", importantFolderSetUp);
        } catch (InvalidPathException e) {
            System.err.println("Exception occurred " + e);
            e.printStackTrace();
        }
        String path = folder.getFolderPath();

        folder = folder.goToFolder(path);

        String actualPathAfterTraversing = folder.getFolderPath();
        String expectedPathAfterTraversing = "root/important/spam/";
        assertEquals(expectedPathAfterTraversing, actualPathAfterTraversing,
                "Expected path is not matching the actual path");
    }

    @Test
    @DisplayName("Test .goToFolder() with path containing only the starting folder")
    void testGoToFolderWithOnlyTheStartingFolder() {
        rootFolderSetUp = rootFolderSetUp.goToFolder("root/");


        String actualPathAfterTraversing = rootFolderSetUp.getFolderPath();
        String expectedPathAfterTraversing = "root/";
        assertEquals(expectedPathAfterTraversing, actualPathAfterTraversing,
                "Expected path is not matching the actual path");
    }

    @Test
    @DisplayName("Test .goToFolder() with multiple folders path")
    void testGoToFolderMultipleFoldersPath() {
        Folder inboxFolder = null;
        List<Folder> subFolders = rootFolderSetUp.getSubFolders();
        for (Folder subFolder : subFolders) {
            if (subFolder.getFolderName().equals("inbox/")) {
                inboxFolder = subFolder;
            }
        }
        Folder spamFolder = new Folder("spam/", inboxFolder);
        Folder socialsFolder = new Folder("socials/", spamFolder);
        Folder facebookFolder = new Folder("facebook/", socialsFolder);
        new Folder("friendRequest/", facebookFolder);

        rootFolderSetUp = rootFolderSetUp.goToFolder("root/inbox/spam/socials/facebook/friendRequest/");

        String actualPathAfterTraversing = rootFolderSetUp.getFolderPath();
        String expectedPathAfterTraversing = "root/inbox/spam/socials/facebook/friendRequest/";
        assertEquals(expectedPathAfterTraversing, actualPathAfterTraversing,
                "Expected path is not matching the actual path");
    }
}


