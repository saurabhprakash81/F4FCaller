package monsanto.b2b.F4FCaller.controller;

import monsanto.b2b.F4FCaller.model.PricePushRequest;
import monsanto.b2b.F4FCaller.model.PricePushResponse;
import monsanto.b2b.F4FCaller.service.PricePushService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;


@Controller
public class F4FWrapperController {

    private static final Logger logger = Logger.getLogger(F4FWrapperController.class);

    @Autowired
    PricePushService pricePushService;

    @RequestMapping(value = "/f4fcaller", method = {RequestMethod.POST}, consumes = {"*/*"}, produces = {"*/*"})
    @ResponseBody
    public PricePushResponse wsWrapperPOST(@RequestBody PricePushRequest pricePushRequest) {
        PricePushResponse pricePushResponse = pricePushService.processRequest(pricePushRequest);
        return pricePushResponse;
    }




}


