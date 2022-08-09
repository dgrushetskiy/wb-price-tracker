package com.testproject.WbPriceTrackerApi.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

@Component
public class ValidationErrMsgBuilder {

    public static String buildFieldErrMsg(BindingResult bindingResult) {
        StringBuilder errMsg = new StringBuilder();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError e : fieldErrors) {
            errMsg.append(e.getField())
                    .append(" - ").append(e.getDefaultMessage())
                    .append("; ");
        }
        return errMsg.toString();
    }

    public static String buildAllErrMsg(BindingResult bindingResult) {
        StringBuilder errMsg = new StringBuilder();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        for (ObjectError e : allErrors) {
            errMsg.append("error")
                    .append(" - ").append(e.getDefaultMessage()).append("; ");
        }
        return errMsg.toString();
    }
}
