package com.denis.controls;

import com.denis.control.PasswordCheck;
import com.denis.domain.exceptions.ControlException;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class PasswordCheckTest {
    @Test
    public void testRun() throws ControlException {
        String wrongPassword = "<lskaDda1!sdf";
        String noLength = "dD1!";
        String firstRight = "PasswordTest1!";
        String secondRight = "PasswordTest1!";
        String noUppercase = "nouppercase1!";
        String noLowercase = "NOLOWERCASE1!";


        assertThrows(ControlException.class, () -> PasswordCheck.run(wrongPassword, firstRight));
        try {
            PasswordCheck.run(wrongPassword, firstRight);
        } catch (ControlException e) {
            MatcherAssert.assertThat(e.getMessage(), containsString("Passwords doesn't match"));
        }

        assertThrows(ControlException.class, () -> PasswordCheck.run(noLength, noLength));
        try {
            PasswordCheck.run(noLength, noLength);
        } catch (ControlException e) {
            MatcherAssert.assertThat(e.getMessage(), containsString("The Password's Length has to be of 8 characters or more"));
        }

        assertThrows(ControlException.class, () -> PasswordCheck.run(null, null));
        try {
            PasswordCheck.run(null, null);
        } catch (ControlException e) {
            MatcherAssert.assertThat(e.getMessage(), containsString("can't be null"));
        }

        assertThrows(ControlException.class, () -> PasswordCheck.run(wrongPassword, wrongPassword));
        try {
            PasswordCheck.run(wrongPassword, wrongPassword);
        } catch (ControlException e) {
            MatcherAssert.assertThat(e.getMessage(), containsString("Password is Malicious"));
        }

        assertThrows(ControlException.class, () -> PasswordCheck.run(noUppercase, noUppercase));
        try {
            PasswordCheck.run(noUppercase, noUppercase);
        } catch (ControlException e) {
            MatcherAssert.assertThat(e.getMessage(), containsString("at least one uppercase"));
        }

        assertThrows(ControlException.class, () -> PasswordCheck.run(noLowercase, noLowercase));
        try {
            PasswordCheck.run(noLowercase, noLowercase);
        } catch (ControlException e) {
            MatcherAssert.assertThat(e.getMessage(), containsString("at least one lowercase"));
        }

        assertTrue(PasswordCheck.run(firstRight, secondRight));
    }
}
