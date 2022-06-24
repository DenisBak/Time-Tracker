package com.denis.domain.factories;

import org.apache.commons.configuration2.Configuration;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.*;

public class ConfigFactoryTest {
    @Test
    public void getConfigByNameTest() {
        String closeResultSetFail_expected = null;
        try (FileInputStream fis = new FileInputStream("src/main/resources/propertiesLocations.properties")) {
            String pathToExceptionProps;
            Properties props = new Properties();
            props.load(fis);
            pathToExceptionProps = props.getProperty("exceptions");
            Properties exceptions = new Properties();
            exceptions.load(new FileInputStream(pathToExceptionProps));
            closeResultSetFail_expected = exceptions.getProperty("closeResultSetFail");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Configuration exceptionProps = ConfigFactory.getConfigByName("exceptions");
        String closeResultSetFail = exceptionProps.getString("closeResultSetFail");
        assertEquals(closeResultSetFail_expected, closeResultSetFail);
    }
}
