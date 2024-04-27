package com.java.node.simple.gitlab.operation;

import com.java.node.simple.gitlab.util.ProjectUtil;
import lombok.Getter;
import org.gitlab4j.api.GitLabApiException;

/**
 * 上线操作类型
 */
public enum OperationEnum {

    TO_GRAY(1, "发布灰度"),
    TO_PRODUCT(2, "发布生产"),
    ;

    /**
     * 备注
     */
    @Getter
    private String desc;
    @Getter
    private Integer code;


    OperationEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OperationEnum getOptByCode(Integer code) {
        for (OperationEnum value : OperationEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public void pub(PublishOperation operation, ProjectUtil projectUtil) throws GitLabApiException {
        switch (this) {
            case TO_GRAY:
                operation.toGray(projectUtil);
                break;
            case TO_PRODUCT:
                operation.toProduct(projectUtil);
                break;
            default:
                throw new RuntimeException("未知的操作类型");
        }
    }

}
