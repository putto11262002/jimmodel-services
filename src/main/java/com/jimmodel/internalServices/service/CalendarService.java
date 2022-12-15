package com.jimmodel.internalServices.service;

import com.jimmodel.internalServices.domain.Slot;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

public interface CalendarService {

    public Map<LocalDate, Collection<Slot>> getMonthlyCalendar(Integer month, Integer year);

}
