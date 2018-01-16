package com.tp.locator;

import android.content.Context;

/**
 * Created by gkomandu on 5/26/2015.
 */
public class comsObject {
    private String url;
    private IsmsSender obj;

    public comsObject() {
        url = new String();

    }
    public String latitude;
    public String longitude;
    public String timeStamp;
	public String address;
    public String fromNumber;
    public String toNumber;
    public Integer isWire;

    public Boolean isMock() {
        return isMock;
    }

    public void setIsMock(Boolean isMock) {
        this.isMock = isMock;
    }

    Boolean isMock;
    private Context ctx;
    private Boolean delay = false;
    public void setUrl(String url)
    {
        this.url = url;
    }
    public void setContext(Context ctx)
    {
        this.ctx = ctx;
    }
    public Context getContext()
    {
        return this.ctx;
    }
    public String getUrl()
    {
        return this.url;
    }
    public void setSmsObj(IsmsSender obj)
    {
        this.obj = obj;
    }
    public  IsmsSender getSmsObj()
    {
        return this.obj;
    }
    public void setDelay(boolean isDelay)
    {
        delay = isDelay;
    }
    public Boolean getDelay()
    {
        return  delay;
    }


};
