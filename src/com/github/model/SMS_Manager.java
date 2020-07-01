package com.github.model;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SMS_Manager {

    public void sendSMS(String string){

        Properties prop = new Properties();
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("resources/properties/sms.properties")) {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String ACCOUNT_SID = prop.getProperty("accountSSID");
        final String AUTH_TOKEN = prop.getProperty("authToken");

        // ssl certificate config
        System.setProperty("javax.net.ssl.trustStore", System.getProperty("user.dir")
                + System.getProperty("file.separator")
                + String.format("src/resources/keystore/myKeystore"));
        System.setProperty("javax.net.ssl.trustStorePassword", prop.getProperty("trustStorePassword"));

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message
            .creator(new PhoneNumber("+46707249511"), // to
                new PhoneNumber("+46765196371"), // from
                string)
            .create();

        System.out.println(message.getSid());
    }
}
