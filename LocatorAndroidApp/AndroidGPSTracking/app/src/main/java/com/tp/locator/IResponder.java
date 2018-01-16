package com.tp.locator;

import com.tp.locator.PhoneUtil.DateTime;

/**
 * Created by user on 8/22/2015.
 */
public interface IResponder {
    public void sendMsg(String toAddress,String msg,DateTime time);

}
