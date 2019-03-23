package com.kakaopay.homework.domain.request;

import lombok.Data;

@Data
public class LocalFileReadRequest {
    private String fileName;
    private String charset;
}
