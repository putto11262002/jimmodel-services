package com.jimmodel.internalServices.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;

@Getter
@Setter
public class Month implements Comparable<Month> {
    private Integer year;
    private Integer month;

    public Month(Integer year, Integer month){
        this.year = year;
        this.month = month;
    }

    @Override
    public int compareTo(Month anotherMonth) {
        return (this.year + this.month) - (anotherMonth.getYear() + anotherMonth.getMonth());
    }

    @Override
    public String toString() {
        return this.year + "-" + (this.month);
    }
}
