package com.examly.springapp.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class SMSController {

    @Autowired
    SmsService service;

    static int sms_count;
    static String sms_phone;

   @PostConstruct
    public void init() {
        sms_count = 0;
    }

    public int smsCount() {
        return sms_count;
    }

    @PostMapping(value = "/sms")
    public void number(@RequestBody SmsDTO sms) {
        sms_phone=sms.getPhone();
    }


    @Autowired
    private SimpMessagingTemplate webSocket;

    private final String TOPIC_DESTINATION = "/lesson/sms";

    @RequestMapping(value = "/smsapi", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void smsSubmit(@RequestBody SmsDTO sms) {
        try{
            sms.setPhone(sms_phone);
            service.send(sms);
            //System.out.println(sms);
            sms_count++;

        }
        catch(Exception e){
            webSocket.convertAndSend(TOPIC_DESTINATION, getTimeStamp() + ": Error sending the SMS: "+e.getMessage());
            throw e;
        }
    }
    private String getTimeStamp() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());

}
}