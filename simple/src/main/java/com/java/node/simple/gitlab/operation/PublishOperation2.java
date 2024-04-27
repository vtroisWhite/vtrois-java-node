package com.java.node.simple.gitlab.operation;

import com.java.node.simple.gitlab.util.ProjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApiException;

/**
 * @Description 操作2
 */
@Slf4j
public class PublishOperation2 implements PublishOperation {

    @Override
    public void toGray(ProjectUtil projectUtil) throws GitLabApiException {
        log.info("开始执行opt2的toGray操作,projectName:{}", projectUtil.getProjectName());
        //dev1合并到master
        projectUtil.mergeBranch(devName, masterName);
    }

    @Override
    public void toProduct(ProjectUtil projectUtil) throws GitLabApiException {
        log.info("开始执行opt2的toProduct操作,projectName:{}", projectUtil.getProjectName());

    }
}
