package com.tp.locator;

/**
 * Created by Aruna on 01-11-2017.
 */

public class NearestPeopleClass {
    public String number;
    public String name;
    public String sex;
    public String status;

    @Override
    public String toString() {
        return
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", status='" + status + '\'';
    }
}
