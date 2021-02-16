package com.bubalex;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.form.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class PdfFieldImpl implements PdfField {
    private static final String FONT_TIMES = "Times";

    private static final int MIN_TITLE_ITEMS_COUNT = 3;


    private final PDField field;

    @Override
    public String pdfId() {
        return field.getFullyQualifiedName();
    }

    @Override
    public String title() {
        return Optional.ofNullable(field.getAlternateFieldName())
                .map(s -> s.replace("\r", " "))
                .orElse("");
    }

    @Override
    public FieldDesc getFieldDesc() {
        String[] items = title().split("\\.");
        if (isNotContentField() || items.length < MIN_TITLE_ITEMS_COUNT) {
            return FieldDesc.builder()
                    .sectionTitle("")
                    .groupTitle("")
                    .fieldTitle("")
                    .build();
        }
        return FieldDesc.builder()
                .sectionTitle(items[1].trim())
                .groupTitle(groupTitle(items))
                .fieldTitle(fieldTitle(items, StringUtils.isEmpty(groupTitle(items))))
                .build();
    }

    private String groupTitle(String[] items) {
        String groupTitle = "";
        int groupItemsCount = (items.length - 2) / 2;
        if (groupItemsCount > 0) {
            int minGroupItemNumber = 2;
            int maxGroupItemNumber = groupItemsCount + 2;
            for (int i = minGroupItemNumber; i < maxGroupItemNumber; i++) {
                groupTitle += items[i];
            }
        }
        groupTitle = groupTitle.trim();
        groupTitle = (groupTitle.length() < PdfField.MIN_GROUP_TITLE_LENGTH) ? "" : groupTitle;
        return groupTitle;
    }

    private String fieldTitle(String[] items, boolean groupTitleIsEmpty) {
        String fieldTitle = "";
        int minFieldItemNumber;
        if (groupTitleIsEmpty) {
            minFieldItemNumber = 2;
        } else {
            int groupItemsCount = (items.length - 2) / 2;
            minFieldItemNumber = 2 + groupItemsCount;
        }
        for (int i = minFieldItemNumber; i < items.length; i++) {
            fieldTitle += items[i];
        }
        fieldTitle = fieldTitle.trim();
        return fieldTitle;
    }

    @Override
    public int extractMaxLength() {
        COSDictionary dictionary = field.getCOSObject().asUnmodifiableDictionary();
        return dictionary.getInt(COSName.MAX_LEN);
    }

    @Override
    public String extractPopupText() {
        COSDictionary dictionary = field.getCOSObject().asUnmodifiableDictionary();
        return dictionary.getString(COSName.TU);
    }

    @Override
    public void setupFieldValue(String fieldValue) throws IOException {
        if (field instanceof PDTextField) {
            PDTextField f = (PDTextField) this.field;
            if (f.isMultiline()) {
                f.setDefaultAppearance("/" + FONT_TIMES + " 16 Tf 0 g");
            } else {
                f.setDefaultAppearance("/" + FONT_TIMES + " 11 Tf 0 g");
            }
            fieldValue = fieldValue.toUpperCase();
        } else if (field instanceof PDComboBox) {
            ((PDComboBox) field).setDefaultAppearance("/" + FONT_TIMES + " 11 Tf 0 g");
        }
        setValue(fieldValue);
    }

    public void setValue(String fieldValue) throws IOException {
        field.setValue(fieldValue);
    }

    @Override
    public void makeUneditable() {
        field.setReadOnly(true);
    }

    @Override
    public boolean isLeafField() {
        return field instanceof PDTerminalField;
    }

    @Override
    public List<String> getOptions() {
        if (getPdfRepresentation() == PdfFieldRepresentation.CHECKBOX) {
            PDCheckBox checkboxField = (PDCheckBox) this.field;
            return List.of(checkboxField.getValue(), checkboxField.getOnValue());
        } else if (getPdfRepresentation() == PdfFieldRepresentation.COMBOBOX) {
            PDComboBox comboBox = (PDComboBox) this.field;
            return comboBox.getOptions();
        } else {
            return List.of();
        }
    }

    @Override
    public String getDefaultValue() {
        if (getPdfRepresentation() == PdfFieldRepresentation.CHECKBOX) {
            PDCheckBox checkboxField = (PDCheckBox) this.field;
            return checkboxField.getDefaultValue();
        } else if (getPdfRepresentation() == PdfFieldRepresentation.COMBOBOX) {
            PDComboBox comboBox = (PDComboBox) this.field;
            return comboBox.getDefaultValue().stream().findFirst().orElse("");
        } else {
            return "";
        }
    }

    @Override
    public PdfFieldRepresentation getPdfRepresentation() {
        if (field instanceof PDTextField) {
            return PdfFieldRepresentation.SIMPLE_TEXT;
        } else if (field instanceof PDCheckBox) {
            return PdfFieldRepresentation.CHECKBOX; //PDCheckBox.getOnValue() // PDCheckBox.getValue() - values scope
        } else if (field instanceof PDComboBox) {
            return PdfFieldRepresentation.COMBOBOX; // PDComboBox.getOptionsDisplayValues(), PDComboBox.getOptions()
        } else {
            throw new IllegalArgumentException();
//            return PdfFieldRepresentation.SIMPLE_TEXT;
        }
    }
}
