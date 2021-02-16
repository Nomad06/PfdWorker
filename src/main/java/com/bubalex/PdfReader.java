package com.bubalex;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.form.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PdfReader {

    private final TreeMap<String, PDField> pdParentFieldMap = new TreeMap<>((key1, key2) -> {
        int length1 = key2.length();
        int length2 = key1.length();
        if (length1 == length2) {
            return key2.compareTo(key1);
        }
        return Integer.compare(length1, length2);
    });
    private final TreeMap<String, PDField> pdChildFieldMap = new TreeMap<>((key1, key2) -> {
        int length1 = key1.length();
        int length2 = key2.length();
        if (length1 == length2) {
            return key1.compareTo(key2);
        }
        return Integer.compare(length1, length2);
    });
    public void read(Path path) {
        ObjectMapper objectMapper = new ObjectMapper();

        try(PDDocument pdDocument = PDDocument.load(path.toFile())) {
            List<PdfField> fields = unfoldFields(pdDocument, this::unfoldAllFields);
            List<PdfFieldFullInformation> pdfFieldFullInformations = fields.stream()
                    .map(field -> {
                        PDField pdfField = ((PdfFieldImpl) field).getField();
                        return getPdfFieldFullInformation(pdfField);
                    })
                    .collect(Collectors.toList());
            List<String> diffs = pdfsDiff(fields);
            System.out.println("Baby, we did it! We are rich!");
            TreeMap<String, PdfFieldFullInformation> pdfParentFields = pdParentFieldMap.values().stream()
                    .map(this::getPdfFieldFullInformation)
                    .collect(Collectors.toMap(PdfFieldFullInformation::getId, pdfField -> pdfField, (obj1, obj2) -> obj1, () -> {
                        return new TreeMap<>((key1, key2) -> {
                            int length1 = key2.length();
                            int length2 = key1.length();
                            if (length1 == length2) {
                                return key2.compareTo(key1);
                            }
                            return Integer.compare(length1, length2);
                        });
                    }));
            PdfFieldFullInformation root = pdfParentFields.lastEntry().getValue();
            pdfParentFields.entrySet()
                    .forEach(field -> attachElementToParent(pdfParentFields, field));
            Map<String, PdfFieldFullInformation> children = pdChildFieldMap.values().stream()
                    .map(this::getPdfFieldFullInformation)
                    .collect(Collectors.toMap(PdfFieldFullInformation::getId, Function.identity()));

            children.entrySet()
                    .forEach(field -> attachElementToParent(pdfParentFields, field));

            String json = objectMapper.writeValueAsString(root);
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void attachElementToParent(TreeMap<String, PdfFieldFullInformation> pdfParentFields, Map.Entry<String, PdfFieldFullInformation> field) {
        PdfFieldFullInformation pdfFieldFullInformation = field.getValue();
        String parentId = pdfFieldFullInformation.getId().replace("." + pdfFieldFullInformation.getShortId(), "");
        PdfFieldFullInformation parent = pdfParentFields.get(parentId);
        if (parent.getChildren() == null) {
            parent.setChildren(new ArrayList<>());
        }
        parent.getChildren().add(pdfFieldFullInformation);
    }

    private PdfFieldFullInformation getPdfFieldFullInformation(PDField pdfField) {
        PdfField field = new PdfFieldImpl(pdfField);

        if (pdfField instanceof PDNonTerminalField) {
            return PdfFieldFullInformation.builder()
                    .id(pdfField.getFullyQualifiedName())
                    .shortId(pdfField.getPartialName())
                    .required(pdfField.isRequired())
                    .title(field.title())
                    //.fieldType(getFieldType(pdfField, field.getPdfRepresentation()))
                    //.options(field.getOptions())
                    .build();
        } else {
            PDRectangle pdRectangle = getFieldDimensionsInfo(pdfField);
            return PdfFieldFullInformation.builder()
                    .id(pdfField.getFullyQualifiedName())
                    .shortId(pdfField.getPartialName())
                    .required(pdfField.isRequired())
                    .title(field.title())
                    .fieldType(getFieldType(pdfField, field.getPdfRepresentation()))
                    .options(field.getOptions())
                    .height(pdRectangle.getHeight())
                    .width(pdRectangle.getWidth())
                    .build();
        }
    }

    private FieldType getFieldType(PDField field, PdfFieldRepresentation fieldRepresentation) {

        if (field instanceof PDTextField) {
            if (fieldRepresentation == PdfFieldRepresentation.SIMPLE_TEXT) {
                PDTextField pdTextField = (PDTextField) field;
                if (pdTextField.isMultiline()) return FieldType.MULTILINE_TEXT_FIELD;
            }
            return switch (fieldRepresentation) {
                case CELL_TEXT -> FieldType.CELL_TEXT;
                case DATE_RANGE -> FieldType.DATE_RANGE;
                case ZIP_4_LAST -> FieldType.ZIP_4_LAST;
                case DATE_PICKER_CALENDAR -> FieldType.DATE_PICKER_CALENDAR;
                case PHONE_4_LAST_LETTERS -> FieldType.PHONE_4_LAST_LETTERS;
                case PHONE_FIRST_3_LETTERS -> FieldType.PHONE_FIRST_3_LETTERS;
                case PHONE_PREFIX_3_LETTERS -> FieldType.PHONE_PREFIX_3_LETTERS;
                default -> FieldType.TEXT_FIELD;
            };
        } else if (field instanceof PDComboBox) {
            return FieldType.COMBOBOX;
        } else if (field instanceof PDRadioButton) {
            return FieldType.RADIO_BUTTON;
        } else if (field instanceof PDCheckBox) {
            return FieldType.CHECKBOX;
        }

        return FieldType.TEXT_FIELD;

    }

    private PDRectangle getFieldDimensionsInfo(PDField field) {
        COSDictionary fieldDict = field.getCOSObject();
        COSArray fieldAreaArray = (COSArray) fieldDict.getDictionaryObject(COSName.RECT);
        return new PDRectangle(fieldAreaArray);
    }

    private List<String> pdfsDiff(List<PdfField> newFields) {
        List<String> newPdfIds = newFields.stream()
                .filter(field -> !field.isNotContentField())
                .map(field -> {
                    String pdfId = field.pdfId();
                    return pdfId.substring(pdfId.lastIndexOf(".") + 1);
                })
                .collect(Collectors.toList());
        List<String> newPdfDesc = newFields.stream()
                .filter(field -> !field.isNotContentField())
                .map(field -> field.extractPopupText())
                .collect(Collectors.toList());
        Map<String, List<String>> pairs = newFields.stream()
                .filter(field -> !field.isNotContentField())
                //.filter(field -> field.extractPopupText().toLowerCase().contains("part 7"))
                .collect(Collectors.toMap(PdfField::pdfId, PdfField::getOptions, (newValue, oldValue) -> oldValue, LinkedHashMap::new));

        Map<String, PdfField> pdfFields = newFields.stream()
                .filter(field -> !field.isNotContentField())
                //.filter(field -> field.extractPopupText().toLowerCase().contains("part 7"))
                .collect(Collectors.toMap(PdfField::pdfId, Function.identity(), (newValue, oldValue) -> oldValue, LinkedHashMap::new));

        Path path = Paths.get("C:/I_129.pdf");
        try(PDDocument pdDocument = PDDocument.load(path.toFile())) {
            List<PdfField> oldPdfFields = new ArrayList<>(unfoldFields(pdDocument, this::unfoldAllFields));
            List<String> oldPdfIds = oldPdfFields.stream()
                    .filter(field -> !field.isNotContentField())
                    .map(field -> {
                        String pdfId = field.pdfId();
                        return pdfId.substring(pdfId.lastIndexOf(".") + 1);
                    })
                    .collect(Collectors.toList());
            List<String> oldPdfDesc = oldPdfFields.stream()
                    .filter(field -> !field.isNotContentField())
                    .map(field -> field.extractPopupText().toLowerCase())
                    .collect(Collectors.toList());

            Map<String, String> oldPairs = oldPdfFields.stream()
                    .filter(field -> !field.isNotContentField())
                    .filter(field -> field.extractPopupText().toLowerCase().contains("specialty occupation"))
                    .collect(Collectors.toMap(PdfField::pdfId, PdfField::extractPopupText, (newValue, oldValue) -> oldValue, LinkedHashMap::new));
            //deleted fields
            Map<String, String> removedPairs = oldPdfFields.stream()
                    .filter(field -> !field.isNotContentField())
                    .filter(field -> {
                        String pdfId = field.pdfId();
                        return !newPdfIds.contains(pdfId.substring(pdfId.lastIndexOf(".") + 1));
                    })
                    .collect(Collectors.toMap(PdfField::pdfId, PdfField::extractPopupText, (newValue, oldValue) -> oldValue, LinkedHashMap::new));
            //added fields
            Map<String, String> addedPairs = newFields.stream()
                    .filter(field -> !field.isNotContentField())
                    .filter(field -> {
                        String pdfId = field.pdfId();
                        return !oldPdfIds.contains(pdfId.substring(pdfId.lastIndexOf(".") + 1));
                    })
                    .collect(Collectors.toMap(PdfField::pdfId, PdfField::extractPopupText, (newValue, oldValue) -> oldValue, LinkedHashMap::new));
            //deleted fields
            Map<String, String> removedPairsDesc = oldPdfFields.stream()
                    .filter(field -> !field.isNotContentField())
                    .filter(field -> !newPdfDesc.contains(field.extractPopupText().toLowerCase()))
                    .collect(Collectors.toMap(PdfField::pdfId, PdfField::extractPopupText, (newValue, oldValue) -> oldValue, LinkedHashMap::new));
            //added fields
            Map<String, String> addedPairsDesc = newFields.stream()
                    .filter(field -> !field.isNotContentField())
                    .filter(field -> !oldPdfDesc.contains(field.extractPopupText().toLowerCase()))
                    .collect(Collectors.toMap(PdfField::pdfId, PdfField::extractPopupText, (newValue, oldValue) -> oldValue, LinkedHashMap::new));
            List<String> removedFields = oldPdfIds.stream()
                    .filter(field -> !newPdfIds.contains(field))
                    .collect(Collectors.toList());
            return  (List<String>) CollectionUtils.intersection(oldPdfIds, newPdfIds);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private List<PdfField> unfoldFields(PDDocument document, Function<PDField, Stream<? extends PDField>> unfoldFunction) {
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        if (acroForm == null) {
            return Collections.emptyList();
        }
        return acroForm.getFields()
                .stream()
                .flatMap(unfoldFunction)
                .map(PdfFieldImpl::new)
                .collect(Collectors.toList());
    }

    private Stream<PDField> unfoldAllFields(PDField field) {
        if (field instanceof PDNonTerminalField) {
            pdParentFieldMap.put(field.getFullyQualifiedName(), field);
            return Stream.concat(Stream.of(field), fieldChildStream(field));
        } else {
            pdChildFieldMap.put(field.getFullyQualifiedName(), field);
            return Stream.of(field);
        }
    }

    private Stream<PDField> fieldChildStream(PDField field) {
        return ((PDNonTerminalField) field).getChildren().stream().flatMap(this::unfoldInputFields);
    }

    private Stream<PDField> unfoldInputFields(PDField field) {
        if (field instanceof PDNonTerminalField) {
            pdParentFieldMap.put(field.getFullyQualifiedName(), field);
            return fieldChildStream(field);
        } else {
            pdChildFieldMap.put(field.getFullyQualifiedName(), field);
            return Stream.of(field);
        }
    }


}
