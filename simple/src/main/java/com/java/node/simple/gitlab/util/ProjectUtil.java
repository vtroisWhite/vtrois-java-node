package com.java.node.simple.gitlab.util;

import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.*;
import org.gitlab4j.api.models.*;

import java.util.List;

/**
 * @Description gitlab的分支操作：删除、合并、复制
 */
@Slf4j
public class ProjectUtil {

    private Project project;

    private GitLabApi gitLabApi;

    private RepositoryApi repositoryApi;

    private MergeRequestApi mergeRequestApi;

    public ProjectUtil(Project project, GitLabApi gitLabApi) {
        this.project = project;
        this.gitLabApi = gitLabApi;
        this.repositoryApi = gitLabApi.getRepositoryApi();
        this.mergeRequestApi = gitLabApi.getMergeRequestApi();
    }

    public String getProjectName() {
        return project.getName();
    }

    /**
     * 删除分支
     *
     * @param branchName
     * @return
     */
    public void deleteBranch(String branchName) throws GitLabApiException {
        try {
            if (!branchExist(branchName)) {
                log.error("删除分支,项目:{},branchName:{}不存在", project.getName(), branchName);
                return;
            }
            repositoryApi.deleteBranch(project.getId(), branchName);
            log.info("删除分支,项目:{},branchName:{},成功", project.getName(), branchName);
        } catch (GitLabApiException e) {
            log.error("删除分支,异常,项目:{},branchName:{},e:", project.getName(), branchName, e);
            throw e;
        }
    }

    /**
     * 创建分支
     *
     * @param branchName
     * @param newBranchName
     * @return
     */
    public void createBranch(String branchName, String newBranchName) throws GitLabApiException {
        try {
            if (!branchExist(branchName)) {
                log.error("创建分支,项目:{},branchName:{}不存在", project.getName(), branchName);
                return;
            }
            if (branchExist(newBranchName)) {
                log.error("创建分支,项目:{},newBranchName:{}已经存在", project.getName(), newBranchName);
                return;
            }
            repositoryApi.createBranch(project.getId(), newBranchName, branchName);
            log.info("创建分支,项目:{},branchName:{},newBranchName:{},成功", project.getName(), branchName, newBranchName);
        } catch (GitLabApiException e) {
            log.error("创建分支,异常,项目:{},branchName:{},newBranchName:{},e:", project.getName(), branchName, newBranchName, e);
            throw e;
        }
    }

    /**
     * 合并分支
     *
     * @param sourceBranch
     * @param targetBranch
     * @return
     */
    public void mergeBranch(String sourceBranch, String targetBranch) throws GitLabApiException {
        try {
            if (!branchExist(sourceBranch)) {
                log.error("合并分支,项目:{},sourceBranch:{}不存在", project.getName(), sourceBranch);
                return;
            }
            if (!branchExist(targetBranch)) {
                log.error("合并分支,项目:{},targetBranch:{}不存在", project.getName(), targetBranch);
                return;
            }
            if (!hasDiffCommit(sourceBranch, targetBranch)) {
                log.info("合并分支,项目:{},sourceBranch:{},targetBranch:{},没有不同的commit,不处理", project.getName(), sourceBranch, targetBranch);
                return;
            }
            List<MergeRequest> mergeRequests = mergeRequestApi.getMergeRequests(project.getId(), Constants.MergeRequestState.OPENED);
            for (MergeRequest mergeRequest : mergeRequests) {
                if (mergeRequest.getSourceBranch().equals(sourceBranch) && mergeRequest.getTargetBranch().equals(targetBranch)) {
                    log.warn("合并分支,项目:{},sourceBranch:{},targetBranch:{}的合并请求已存在,直接进行通过处理,mergeRequestUrl:{}",
                            project.getName(), sourceBranch, targetBranch, this.getMergeRequestUrl(mergeRequest));
                    this.acceptMergeRequest(mergeRequest);
                    return;
                }
            }
            MergeRequest mergeRequest = this.createMergeRequest(sourceBranch, targetBranch);
            this.acceptMergeRequest(mergeRequest);
        } catch (GitLabApiException e) {
            log.error("合并分支,异常,项目:{},sourceBranch:{},targetBranch:{},e:", project.getName(), sourceBranch, targetBranch, e);
            throw e;
        }
    }

    private MergeRequest createMergeRequest(String sourceBranch, String targetBranch) throws GitLabApiException {
        MergeRequestParams params = new MergeRequestParams()
                .withSourceBranch(sourceBranch)
                .withTargetBranch(targetBranch)
                .withTitle("api自动合并分支")
                .withDescription("api自动合并分支");
        MergeRequest mergeRequest = mergeRequestApi.createMergeRequest(project.getId(), params);
        log.info("合并请求创建成功,项目:{},sourceBranch:{},targetBranch:{},url:{}",
                project.getName(), sourceBranch, targetBranch, this.getMergeRequestUrl(mergeRequest));
        return mergeRequest;
    }

    private boolean acceptMergeRequest(MergeRequest mergeRequest) throws GitLabApiException {
        MergeRequestApi mergeRequestApiOwn = new MergeRequestApiOwn(gitLabApi);
        mergeRequestApiOwn.acceptMergeRequest(project.getId(), mergeRequest.getId());
        log.info("通过mergeRequest成功,项目:{},sourceBranch:{},targetBranch:{},url:{}",
                project.getName(), mergeRequest.getSourceBranch(), mergeRequest.getTargetBranch(), this.getMergeRequestUrl(mergeRequest));
        return true;
    }

    public boolean hasDiffCommit(String sourceBranch, String targetBranch) throws GitLabApiException {
        CompareResults compare = repositoryApi.compare(project.getId(), targetBranch, sourceBranch);
//        System.out.println(JSON.toJSONString(compare, SerializerFeature.PrettyFormat));
        return !compare.getCommits().isEmpty();
    }

    private String getMergeRequestUrl(MergeRequest mergeRequest) {
        return gitLabApi.getGitLabServerUrl() + project.getNamespace().getPath() + "/" + project.getName() + "/" + "merge_requests" + "/" + mergeRequest.getIid();
    }

    /**
     * 检查分支是否存在
     *
     * @param branchName
     * @return
     */
    public boolean branchExist(String branchName) throws GitLabApiException {
        for (Branch branch : repositoryApi.getBranches(project.getId())) {
            if (branch.getName().equals(branchName)) {
                return true;
            }
        }
        return false;
    }
}
