package com.activiti.oaf.adapter.util;

import com.activiti.oaf.adapter.model.UIQuotationDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
/**
 * OAF utilities
 * @author Pradip Patel
 */
public class OAFUtil {

    /**
     * This method is responsible to get Current SQL date.
     * @return
     */
    public static java.sql.Date getCurrentSQLDate() {

        return new java.sql.Date(Calendar.getInstance().getTimeInMillis());
    }

    /**
     * This method is responsible to get Current TimeStamp.
     * @return
     */
    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    /**
     * This method is responsible to get CurrentTime
     * @param date
     * @return
     */
    public static Time getCurrentTime(Date date) {

        return new Time(date.getTime());

    }

    /**
     * This method is responsible to addMinutes in time.
     * @param date
     * @param minutes
     * @return
     */
    public static Time addMinutesToTime(Date date, int minutes) {

        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.MINUTE, 30);
        cal.add(Calendar.MINUTE, minutes);

        return new Time(cal.getTimeInMillis());

    }

    /**
     * This method is responsible to convert quotation detail to json.
     * @param uiQuotationDetail
     * @return
     */
    public static String convertQuoteToJsonString(UIQuotationDetail uiQuotationDetail) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(uiQuotationDetail);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method is responsible to convert json to quotation detail.
     * @param inputJson
     * @return
     */
    public static UIQuotationDetail convertJsonStringToQuote(String inputJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(inputJson, UIQuotationDetail.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static long generateRandomLongId() {
        long id = new Random().nextLong();
        return Math.abs(id);
    }
}
