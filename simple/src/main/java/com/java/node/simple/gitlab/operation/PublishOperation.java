package com.java.node.simple.gitlab.operation;

import com.java.node.simple.gitlab.util.ProjectUtil;
import org.gitlab4j.api.GitLabApiException;

/**
 *
 */
public interface PublishOperation {

    String devName = "dev_1";

    String masterName = "master";

    String masterBakName = "master_bak";

    String grayName = "gray";

    /**
     * 发布至灰度
     *
     * @param projectUtil
     */
    void toGray(ProjectUtil projectUtil) throws GitLabApiException;

    /**
     * 发布到线上
     *
     * @param projectUtil
     */
    void toProduct(ProjectUtil projectUtil) throws GitLabApiException;
}
