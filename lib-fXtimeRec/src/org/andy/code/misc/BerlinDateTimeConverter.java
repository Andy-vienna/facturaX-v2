package org.andy.code.misc;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BerlinDateTimeConverter implements AttributeConverter<OffsetDateTime, OffsetDateTime> {

    @Override
    public OffsetDateTime convertToDatabaseColumn(OffsetDateTime attribute) {
        return attribute; // Speichern wie es ist
    }

    @Override
    public OffsetDateTime convertToEntityAttribute(OffsetDateTime dbData) {
        if (dbData == null) return null;
        // Zwingt den Wert beim LADEN aus der DB in die Berlin-Zeit
        return dbData.atZoneSameInstant(ZoneId.of("Europe/Berlin")).toOffsetDateTime();
    }
}
