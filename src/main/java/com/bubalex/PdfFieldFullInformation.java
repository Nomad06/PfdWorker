package com.bubalex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PdfFieldFullInformation {
    private String id;
    private String shortId;
    private FieldType fieldType;
    private int textSize;
    private boolean required;
    private List<String> options;
    private float height;
    private float width;
    private String title;
    private List<PdfFieldFullInformation> children;
}
