package com.bubalex;

import org.apache.commons.lang3.StringUtils;

public enum PdfFieldRepresentation {
    CHECKBOX,
    SIMPLE_TEXT,
    CELL_TEXT, // special case for 'cell' fields in pdf
    DATE_RANGE, // TODO
    DATE_PICKER_CALENDAR,
    ZIP_4_LAST,
    PHONE_PREFIX_3_LETTERS,
    PHONE_FIRST_3_LETTERS,
    PHONE_4_LAST_LETTERS,
    COMBOBOX,;

    public String emptyValue() {
        return this == CHECKBOX ? Constants.EMPTY_PDF_CHECKBOX : "";
    }

    public String formatValue(String fieldValue) {
        if (this == CELL_TEXT) {
            return formatCellText(fieldValue);
        } else if (this == PHONE_PREFIX_3_LETTERS || this == PHONE_FIRST_3_LETTERS || this == PHONE_4_LAST_LETTERS) {
            return phonePart(fieldValue);
        }
        return fieldValue;
    }

    private String phonePart(String fieldValue) {
        if (StringUtils.isEmpty(fieldValue)) {
            return "";
        }
        // removing US +1 code if present
        fieldValue = fieldValue.replace("+", "");
        if (fieldValue.startsWith("1")) {
            fieldValue = fieldValue.substring(1);
        }
        // padding with spaces to make string lenght exactly 10 chars
        fieldValue = StringUtils.rightPad(fieldValue, 10, " ");
        fieldValue = fieldValue.substring(0, 10); // exactly 10 symbols
        if (this == PHONE_PREFIX_3_LETTERS) {
            return fieldValue.substring(0, 3);
        } else if (this == PHONE_FIRST_3_LETTERS) {
            return fieldValue.substring(3,6);
        } else if (this == PHONE_4_LAST_LETTERS) {
            return fieldValue.substring(6);
        }
        throw new IllegalStateException();
    }

    private String formatCellText(String fieldValue) {
        StringBuilder updValue = new StringBuilder();
        updValue.append(fieldValue.charAt(0));
        for (int i = 1; i < fieldValue.length(); i++) {
            updValue.append("    ");
            updValue.append(fieldValue.charAt(i));
        }
        fieldValue = updValue.toString();
        return fieldValue;
    }

}
