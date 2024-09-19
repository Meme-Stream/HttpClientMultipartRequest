package com.memestream.httpclient.multipartrequest;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MultipartRecord {

    private String name;

    private String filename;

    private Object content;

    private String contentType;
}
