package com.tp.locator.Events;

import com.tp.locator.allowedContacts;

import java.util.List;

/**
 * Created by user on 8/2/2015.
 */
public class UpdateUI {
    public static List<allowedContacts> newContacts;
    public UpdateUI(List<allowedContacts> cnts)
    {
        newContacts = cnts;
    }
    public  UpdateUI()
    {

    }
}
