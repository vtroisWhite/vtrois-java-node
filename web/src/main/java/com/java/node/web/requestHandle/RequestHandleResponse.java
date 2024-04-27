package com.java.node.web.requestHandle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestHandleResponse {

    private String data;

    private String sign;

    private Long ts;

}
