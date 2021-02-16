package com.bubalex;

import java.math.BigDecimal;

public interface Constants {
    String REST_API_ROOT = "/api";
    String REST_API_V2_ROOT = "/api/v2";

    String IMAGE_PNG_VALUE = "image/png";
    String IMAGE_JPEG_VALUE = "image/jpeg";
    String APPLICATION_PDF_VALUE = "application/pdf";

    String PDF_EXTENSION = ".pdf";
    String JSON_EXTENSION = ".json";
    String JPG_EXTENSION = ".jpg";
    String JPEG_EXTENSION = ".jpeg";
    String PNG_EXTENSION = ".png";
    String DOCX_EXTENSION = ".docx";
    String XLSX_EXTENSION = ".xlsx";
    String PDF_TEMP_FOLDER = "generated_pdf";
    String DOC_PACKAGE_TITLE = "package";
    String FILE_PREVIEW_SUFFIX = "_preview";

    String UPS_LABEL = "UPS";
    String SHIPPING_LABEL = "Shipping label";
    String SHIPPING_LABEL_FILE_NAME = "shipping_label";

    int MAX_REPEAT_COUNT = 10;
    int MAX_DEPENDENT_COUNT = 6;

    Long UNASSIGNED_CASE_ATTORNEY_ID = -1L;

    int DEFAULT_PDF_PADDING = 20;

    BigDecimal TWO = BigDecimal.valueOf(2L);

    String UI_DATE_FORMAT = "MM/dd/yyyy";
    String UI_DATE_HOURS_MINUTES_FORMAT = "MM/dd/yyyy HH:mm";
    String UI_DATE_HOURS_MINUTES_FORMAT_AT = "MM/dd/yyyy at HH:mm";
    String UI_HOURS_MINUTES_FORMAT = "HH:mm";
    String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    String VISA_TRANSLATIONS = "visa_messages";

    String TITLE_SUFFIX = "_title";
    String HOVER_SUFFIX = "_hover";
    String SUPER_SUFFIX = "_super";
    String GROUP_SUFFIX = "_group";
    String SKIPPED_SUFFIX = "_skipped";

    //------------------ datasets ---------------------------
    String ADDITIONAL_FORMS_ = "additional_forms_";

    String DEPENDENT = "dependent";
    String DEPENDENT_ = "dependent_";
    String DEPENDENT_SPOUSE = "dependent_spouse";
    String DEPENDENT_SPOUSE_ID = "_dependent_spouse";
    String DEPENDENT_CHILD_ID = "_dependent_child";

    String EMPTY_PDF_CHECKBOX = "Off";
    String CONDITION_PDF_CHECKBOX = "_conditionBox";
    String NOT_EMPTY = "_notEmpty";
    String EMPTY = "_empty";
    String MAX_LENGTH = "_maxLength";
    String CHECKED = "_checked";

    String FREE_UPLOAD_DOC = "free_upload";

    String NONE_VALUE = "NONE";
    String NA_VALUE = "N/A";
    String PRESENT_VALUE = "PRESENT";

    String UCT = "UCT";

    String HANDLE_ESIGN = "/en/handle-esign";

    String DOES_NOT_APPLY = "DOES_NOT_APPLY";

    //------------------ Media types ---------------------------
    String MEDIA_TYPE_TEXT_CSV = "text/csv";
    String MEDIA_TYPE_APPLICATION_JWT = "application/jwt";

}
