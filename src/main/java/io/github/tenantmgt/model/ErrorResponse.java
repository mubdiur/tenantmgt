package io.github.tenantmgt.model;

import lombok.Data;

@Data
public class ErrorResponse {
    private String errorType;
    private String errorMsg; 
}
