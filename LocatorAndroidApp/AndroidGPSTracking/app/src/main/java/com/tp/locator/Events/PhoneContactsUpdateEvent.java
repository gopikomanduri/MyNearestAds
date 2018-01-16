package com.tp.locator.Events;

import com.tp.locator.PhoneUtil.PhoneContact;

import java.util.List;

/**
 * Created by user on 8/2/2015.
 */
public class PhoneContactsUpdateEvent {
    public PhoneContactsUpdateEvent(List<PhoneContact> cnts)
    {
        addedContacts = cnts;
    }
    public static List<PhoneContact> addedContacts;
}
