package com.bubalex;

import java.io.IOException;
import java.util.List;

public interface PdfField {
    static final int MIN_GROUP_TITLE_LENGTH = 10;

    static String toId(String s) {
        s = s.toLowerCase()
                .trim()
                .replace(":", "_")
                .replace("'", "_")
                .replace("|", "_")
                .replace("\\", "_")
                .replace("/", "_")
                .replace(",", "_")
                .replace(".", "_")
                .replace("-", "_")
                .replace(" ", "_")
                .replace("(", "_")
                .replace(")", "_")
                .replace("\r", "_")
                .replace("__", "_")
                .replace("__", "_")
                .replaceAll("[_]$", "");
//        s = StringUtils.abbreviate(s, 300);
        return s;
    }

    default boolean isNotContentField() {
        return !title().startsWith("Part"); //|| items.length < 3;
    }

    String pdfId();

    String title();

    FieldDesc getFieldDesc();

    int extractMaxLength();

    String extractPopupText();

    void setupFieldValue(String fieldValue) throws IOException;

    void setValue(String value) throws IOException;

    void makeUneditable();

    boolean isLeafField();

    List<String> getOptions();

    String getDefaultValue();

    PdfFieldRepresentation getPdfRepresentation();
}
