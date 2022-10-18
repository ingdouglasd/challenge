package com.drdg.challenge.business;

import com.drdg.challenge.web.model.ApplicationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CountRequestHelper {
    // Keeps last 3 records processed sorted by requestedDate, after the application is ACCEPTED
    // When application is accepted it should have at least one record.
    private static final ApplicationDto[] appRequests= new ApplicationDto[3];
    private static int size = 0;

    // Adds a request and sorts it by applicationDto requestedDate
    public synchronized void addAppRequest(ApplicationDto toAdd){
        // array has no elements, just add
        if(appRequests[0] == null) {
            appRequests[0] = toAdd;
            size++;
            return;
        }
        // array has one element, just add
        if(appRequests[1] == null) {
            if(isItemToAddNewer( toAdd, appRequests[0])){
                appRequests[1] = toAdd;
            }
            else {
                //switch left as older should be the one in the first position
                appRequests[1] = appRequests[0];
                appRequests[0] = toAdd;
            }
            size++;
            return;
        }
        // array has two elements
        if(appRequests[2] == null) {
            if(isItemToAddNewer( toAdd, appRequests[1])){
                appRequests[2] = toAdd;
            }
            else if(isItemToAddNewer( toAdd, appRequests[0])) {
                //shift right from item 2
                appRequests[2] = appRequests[1];
                appRequests[1] = toAdd;
            }else{
                //shift right from item 1
                appRequests[2] = appRequests[1];
                appRequests[1] = appRequests[0];
                appRequests[0] = toAdd;
            }
            size++;
        } // array has three elements, remove the oldest one
        if(appRequests[2] != null) {
            // Shift left all, looses first element
            if(isItemToAddNewer( toAdd, appRequests[2])){
                appRequests[1] = appRequests[2];
                appRequests[0] = appRequests[1];
                appRequests[2] = toAdd;
            }else if(isItemToAddNewer( toAdd, appRequests[1])){
                appRequests[0] = appRequests[1];
                appRequests[1] = toAdd;
            }
            else if(isItemToAddNewer( toAdd, appRequests[0])) {
                //shift left from item 0
                appRequests[0] = toAdd;
            }
        }
    }

    /**
     * Compares two longs belonging to the requestDate to indicate the requestedDate is bigger
     * @param toAdd
     * @param toCompare
     * @return
     */
    private boolean isItemToAddNewer(ApplicationDto toAdd, ApplicationDto toCompare){
        boolean returntype = false;
        if(toAdd.getSystemRequestedDate().getTime()>=toCompare.getSystemRequestedDate().getTime()) {
            returntype = true;
        }
        return returntype;
    }

    /**
     * Gets the last date from the application systemRequestedDate or -1 if application has not been ACCEPTED.
     * @return
     */
    public long getLastSystemRequestedDate(){
        if(appRequests[2] !=null)
            return appRequests[2].getSystemRequestedDate().getTime();
        if(appRequests[1] !=null)
            return appRequests[1].getSystemRequestedDate().getTime();
        if(appRequests[0] !=null)
            return appRequests[0].getSystemRequestedDate().getTime();
        return -1;
    }

    /**
     * Gets the first date from the application requestedDate or -1 if application has not been ACCEPTED.
     * @return
     */
    public long getFirstSystemRequestedDate(){
        if(appRequests[0] !=null)
            return appRequests[0].getSystemRequestedDate().getTime();
        return -1;
    }

    /**
     * When ACCEPTED If the system receives 3 or more requests within two minutes
     * @return True when 3 requests have been received in the last 2 minutes
     */
    public boolean isTwoMinutesRuleBroken(long timeRequested){
        //Review if the array has 3 after ACCEPTED
        if(size>2){
            // Difference between times must be less than 2 mins (from millis to minutes -> divide 1000*60)
            double differenceInMillis = (timeRequested - getFirstSystemRequestedDate());
            if(differenceInMillis< 2*60*1000) {
                log.warn("TwoMinutesRuleBroken:differenceInMinutes["+differenceInMillis+"]");
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the size of the last application requests since the request is APPROVED
     * @return
     */
    public Integer getSize(){
        return size;
    }

    /**
     * Clears the buffer of requests.  Should be called when the application is ACCEPTED for the first time.
     */
    public static void clearRequestBuffer(){
        appRequests[2]=null;
        appRequests[1]=null;
        appRequests[0]=null;
        size = 0;
    }

}
