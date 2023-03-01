package bg.sofia.uni.fkst.pik.minioutlook;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @DisplayName("Passing null as parameters for creating an account")
    void testPassedParametersThrowExceptions(String emptyOrBlankString) {
        String emailAddress = "test@abv.bg";
        String name = "test";

        assertAll(
                () ->
                        assertThrows(IllegalArgumentException.class, () ->
                                        new Account(emptyOrBlankString, name)
                                , "IllegalArgumentException was expected, but didn't follow"),
                () ->
                        assertThrows(IllegalArgumentException.class, () ->
                                        new Account(emailAddress, emptyOrBlankString)
                                , "IllegalArgumentException was expected, but didn't follow")
        );
    }
}
