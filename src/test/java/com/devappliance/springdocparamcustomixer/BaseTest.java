package com.devappliance.springdocparamcustomixer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Gibah Joseph
 * Email: gibahjoe@gmail.com
 * Mar, 2020
 **/

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

    public static String className;

    @Autowired
    protected MockMvc mockMvc;


}
