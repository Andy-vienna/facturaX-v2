package org.andy.code.misc;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Set;
import java.util.stream.Collectors;

import de.jollyday.Holiday;
import de.jollyday.HolidayManager;
import de.jollyday.ManagerParameters;

public final class BankHoliday {

    /** Zählt Arbeitstage (Mo–Fr) im Monat, abzüglich gesetzlicher Feiertage.
     *  @param ym Jahr+Monat
     *  @param country ISO-2 Ländercode, z.B. "at", "de"
     *  @param regions optionale regionale Codes, z.B. "w" für Wien, "by" für Bayern
     */
    public static int workdaysInMonth(YearMonth ym, String country, String... regions) {
        LocalDate start = ym.atDay(1);
        LocalDate end   = ym.atEndOfMonth();

        HolidayManager mgr = HolidayManager.getInstance(ManagerParameters.create("at")); // ISO-2
        // Feiertage im Datumsbereich (regional, falls angegeben)
        Set<LocalDate> holidays = mgr.getHolidays(start, end, regions)
                                     .stream()
                                     .map(Holiday::getDate)
                                     .collect(Collectors.toSet());

        return (int) start.datesUntil(end.plusDays(1))
                .filter(d -> {
                    DayOfWeek dow = d.getDayOfWeek();
                    boolean weekend = dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
                    return !weekend && !holidays.contains(d);
                })
                .count();
    }
}
