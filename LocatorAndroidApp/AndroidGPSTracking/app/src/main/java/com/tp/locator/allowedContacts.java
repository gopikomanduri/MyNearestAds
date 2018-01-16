package com.tp.locator;



/**
 * Created by gkomandu on 7/2/2015.
 */
//@Table(name = "allowedContacts")
public class allowedContacts  {
  //  @Column(name = "ContactNumber")
    public String contactNumber;

    //@Column(name = "ContactName")
    public String contactName;

   // @Column(name = "allowedStartTime")
    public String allowedStartTime;

    //@Column(name = "allowedEndTime")
    public String allowedEndTime;

    //@Column(name = "registeredMsg")
    public String registeredMsg;

    //@Column(name = "isMockAllowed")
    public Integer isMockAllowed;

    //@Column(name = "mockStartTime")
    public String mockStartTime;

    //@Column(name = "mockEndTime")
    public String mockEndTime;

    //@Column(name = "mockMsg")
    public String mockMsg;

    public String shaValue;

    public String isAllowed = "No";
    public  allowedContacts()
    {

    }

    public  allowedContacts(String contactNumber,String contactName,String registeredMsg,String allowedStartTime,String allowedEndTime,Integer isMockAllowed ,String mockStartTime , String mockEndTime,String mockMsg)
    {
        this.allowedEndTime = allowedEndTime;
        this.allowedStartTime = allowedStartTime;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.isMockAllowed = isMockAllowed;
        this.mockEndTime = mockEndTime;
        this.mockStartTime = mockStartTime;
        this.mockMsg = mockMsg;
        this.registeredMsg = registeredMsg;
        if(allowedStartTime.length() > 0 && allowedStartTime.equalsIgnoreCase("1"))
            isAllowed = "yes";
        else  if(allowedStartTime.length() > 0 && allowedStartTime.equalsIgnoreCase("0"))
            isAllowed = "No";
       else
            isAllowed = "Mock";
    }

 /*   public static List<allowedContacts> selectAll() {
        return new Select().from(allowedContacts.class).orderBy("ContactName").execute();
    }

    public static allowedContacts select(String contactNumber) {
        return new Select().from(allowedContacts.class).where("ContactNumber" + " = ?", contactNumber).executeSingle();
    }*/
/*    public boolean equals(ContactAdapter.ViewHolder another) {
        return (this.contactNumber.equalsIgnoreCase(another.contactObj.contactNumber) );
    }*/

}
