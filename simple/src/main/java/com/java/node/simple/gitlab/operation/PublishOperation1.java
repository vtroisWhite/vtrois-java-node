package com.java.node.simple.gitlab.operation;

import com.java.node.simple.gitlab.util.ProjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApiException;

/**
 * @Description 操作1
 */
@Slf4j
public class PublishOperation1 implements PublishOperation {

    @Override
    public void toGray(ProjectUtil projectUtil) throws GitLabApiException {
        log.info("开始执行opt1的toGray操作,projectName:{}", projectUtil.getProjectName());
        //删除gray
        projectUtil.deleteBranch(grayName);
        //dev1复制为gray
        projectUtil.createBranch(devName, grayName);
    }

    @Override
    public void toProduct(ProjectUtil projectUtil) throws GitLabApiException {
        log.info("开始执行opt1的toProduct操作,projectName:{}", projectUtil.getProjectName());
        //删除master备份
        projectUtil.deleteBranch(masterBakName);
        //master分支备份
        projectUtil.createBranch(masterName, masterBakName);
        //删除master
        projectUtil.deleteBranch(masterName);
        //灰度复制为master
        projectUtil.createBranch(grayName, masterName);
    }
}
