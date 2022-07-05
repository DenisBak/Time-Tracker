package com.denis.control;

import com.denis.domain.exceptions.ControlException;
import com.denis.domain.configs.ConfigFactory;
import com.denis.domain.configs.ConfigNames;
import org.apache.commons.configuration2.Configuration;

public class PasswordCheck {
    private static final Configuration exceptionConfig;

    private static int minPasswordLength = 8; // TODO: 7/5/22 maybe Enum
    private static int minUpCharsLength = 1;
    private static int minLowCharsLength = 1;
    private static int minDigitsLength = 1;
    private static int minSpecialsLength = 1;

    static {
        exceptionConfig = ConfigFactory.getConfigByName(ConfigNames.EXCEPTIONS);
    }

    public static boolean run(String firstPassword, String secondPassword) throws ControlException {
        if (firstPassword == null || secondPassword == null) {
            exceptionConfig.setProperty("failedParameter", "firstPassword or secondPassword");
            throw new ControlException(exceptionConfig.getString("parameterNull"));
        }
        if (!firstPassword.equals(secondPassword)) {
            throw new ControlException(exceptionConfig.getString("passwordsNotMatch"));
        }

        int totalUpChars = 0;
        int totalLowChars = 0;
        int totalSpecial = 0;
        int totalDigits = 0;

        @SuppressWarnings({"WeakerAccess"})
        String password = firstPassword; // For convenience (because passwords equals) -> password = firstPassword = secondPassword

        if (password.length() < minPasswordLength) {
            throw new ControlException(exceptionConfig.getString("passwordLengthTooLow"));
        } else {
            for (char ch : password.toCharArray()) {
                if (Character.isUpperCase(ch))
                    totalUpChars++;
                else if (Character.isLowerCase(ch))
                    totalLowChars++;
                else if (Character.isDigit(ch))
                    totalDigits++;
                else {
                    if (ch == '<' || ch == '>') {
                        throw new ControlException(exceptionConfig.getString("passwordHasForbiddenSymbols"));
                    } else
                        totalSpecial++;
                }
            }
        }

        if (totalUpChars < minUpCharsLength)
            throw new ControlException(exceptionConfig.getString("passwordMustContainUppercase"));
        if (totalLowChars < minLowCharsLength)
            throw new ControlException(exceptionConfig.getString("passwordMustContainLowercase"));
        if (totalDigits < minDigitsLength)
            throw new ControlException(exceptionConfig.getString("passwordMustContainDigits"));
        if (totalSpecial < minSpecialsLength)
            throw new ControlException(exceptionConfig.getString("passwordMustContainSpecials"));

        return true;
    }
}
