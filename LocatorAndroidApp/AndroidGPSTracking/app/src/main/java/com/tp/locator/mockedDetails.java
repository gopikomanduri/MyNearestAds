package com.tp.locator;



import java.util.List;

/**
 * Created by gkomandu on 7/2/2015.
 */
//@Table(name = "mockedDetails")
public class mockedDetails {
  //  @Column(name = "contact")
    public String contact;

 //   @Column(name = "mockStartTime")
    public String mockStartTime;

  //  @Column(name = "mockEndTime")
    public String mockEndTime;

  //  @Column(name = "mockMsg")
    public String mockMsg;

   /* public static List<mockedDetails> selectAll() {
        return new Select().from(mockedDetails.class).orderBy("contact").execute();
    }

    public static allowedContacts select(String contactNumber) {
        return new Select().from(mockedDetails.class).where("contact" + " = ?", contactNumber).executeSingle();
    }*/
}
