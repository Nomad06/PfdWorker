package com.bubalex;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FieldDesc {
    private String sectionTitle;
    private String groupTitle;
    private String fieldTitle;
}
