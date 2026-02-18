package org.andy.code.workTime;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.andy.code.dataStructure.entity.WorkTimeRaw;

public final class WorkTimeValidator {

    public enum EventType { IN, OUT, BREAK_START, BREAK_END }

    public static record Punch(OffsetDateTime ts, EventType type, String username) {}
    public static record BreakInterval(OffsetDateTime start, OffsetDateTime end, Duration duration) {}
    public static record WorkSession(OffsetDateTime in, OffsetDateTime out, List<BreakInterval> breaks, Duration gross, Duration breakTotal, Duration net) {}
    public static record ValidationResult(List<WorkSession> sessions, Duration totalNet, List<String> errors) {}

    public static ValidationResult validateDay(List<Punch> rawDayForUser, ZoneId zone) {
        List<Punch> punches = rawDayForUser.stream()
                .sorted(Comparator.comparing(Punch::ts))
                .toList();
        List<WorkSession> sessions = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        OffsetDateTime openIn = null;
        OffsetDateTime openBreak = null;
        List<BreakInterval> currentBreaks = new ArrayList<>();

        for (Punch p : punches) {
            switch (p.type()) {
                case IN -> {
                    if (openIn != null) {
                        errors.add(err(p.ts(), "zusätzliches IN ohne vorheriges OUT"));
                        openIn = p.ts();
                        openBreak = null;
                        currentBreaks.clear();
                    } else {
                        openIn = p.ts();
                    }
                }
                case BREAK_START -> {
                    if (openIn == null) {
                        errors.add(err(p.ts(), "BREAK_START außerhalb einer offenen Arbeits-Session"));
                    } else if (openBreak != null) {
                        errors.add(err(p.ts(), "doppeltes BREAK_START"));
                    } else {
                        openBreak = p.ts();
                    }
                }
                case BREAK_END -> {
                    if (openIn == null) {
                        errors.add(err(p.ts(), "BREAK_END außerhalb einer offenen Arbeits-Session"));
                    } else if (openBreak == null) {
                        errors.add(err(p.ts(), "BREAK_END ohne vorheriges BREAK_START"));
                    } else if (!p.ts().isAfter(openBreak)) {
                        errors.add(err(p.ts(), "BREAK_END liegt nicht nach BREAK_START"));
                        openBreak = null;
                    } else {
                        currentBreaks.add(new BreakInterval(openBreak, p.ts(),
                                Duration.between(openBreak, p.ts())));
                        openBreak = null;
                    }
                }
                case OUT -> {
                    if (openIn == null) {
                        errors.add(err(p.ts(), "OUT ohne vorheriges IN"));
                        break;
                    }
                    if (!p.ts().isAfter(openIn)) {
                        errors.add(err(p.ts(), "OUT liegt nicht nach IN"));
                        openIn = null;
                        openBreak = null;
                        currentBreaks.clear();
                        break;
                    }
                    if (openBreak != null) {
                        errors.add(err(p.ts(), "BREAK_START ohne BREAK_END; Pause am OUT abgeschnitten"));
                        Duration d = Duration.between(openBreak, p.ts());
                        if (!d.isNegative() && !d.isZero()) {
                            currentBreaks.add(new BreakInterval(openBreak, p.ts(), d));
                        }
                        openBreak = null;
                    }
                    Duration gross = Duration.between(openIn, p.ts());
                    Duration breakTotal = currentBreaks.stream()
                            .map(BreakInterval::duration)
                            .reduce(Duration.ZERO, Duration::plus);
                    if (breakTotal.compareTo(gross) > 0) {
                        errors.add(err(p.ts(), "Pausensumme größer als Arbeitszeit; auf Brutto gekappt"));
                        breakTotal = gross;
                    }
                    Duration net = gross.minus(breakTotal);
                    sessions.add(new WorkSession(openIn, p.ts(),
                            List.copyOf(currentBreaks), gross, breakTotal, net));

                    openIn = null;
                    openBreak = null;
                    currentBreaks.clear();
                }
            }
        }
        if (openIn != null) errors.add(err(openIn, "offenes IN ohne OUT am Tagesende"));
        if (openBreak != null) errors.add(err(openBreak, "offene Pause ohne BREAK_END am Tagesende"));

        Duration totalNet = sessions.stream().map(WorkSession::net)
                .reduce(Duration.ZERO, Duration::plus);

        return new ValidationResult(List.copyOf(sessions), totalNet, List.copyOf(errors));
    }

    public static List<Punch> toDayForUser(List<WorkTimeRaw> fromRepo, String username, LocalDate day, ZoneId zone) {
        OffsetDateTime start = day.atStartOfDay(zone).toOffsetDateTime();
        OffsetDateTime end = day.plusDays(1).atStartOfDay(zone).toOffsetDateTime();
        return fromRepo.stream()
                .filter(w -> username.equals(w.getUserName()))
                .filter(w -> !w.getTs().isBefore(start) && w.getTs().isBefore(end))
                .map(w -> new Punch(w.getTs(), mapEvent(w.getEvent()), w.getUserName()))
                .sorted(Comparator.comparing(Punch::ts))
                .collect(Collectors.toList());
    }

    private static EventType mapEvent(String raw) {
        String s = raw == null ? "" : raw.trim().toUpperCase(Locale.ROOT);
        return switch (s) {
            case "IN" -> EventType.IN;
            case "OUT" -> EventType.OUT;
            case "BREAK_START", "BS" -> EventType.BREAK_START;
            case "BREAK_END", "BE" -> EventType.BREAK_END;
            default -> throw new IllegalArgumentException("Unbekanntes Event: " + raw);
        };
    }

    private static String err(OffsetDateTime ts, String msg) {
        return "%s [%s]".formatted(msg, ts);
    }
}
