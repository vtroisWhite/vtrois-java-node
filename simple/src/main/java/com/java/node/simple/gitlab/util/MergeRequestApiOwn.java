package com.java.node.simple.gitlab.util;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.GitLabApiForm;
import org.gitlab4j.api.MergeRequestApi;
import org.gitlab4j.api.models.MergeRequest;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

/**
 * @Description MergeRequestApi.acceptMergeRequest的merge_requests应为merge_request, 故重写方法
 */
public class MergeRequestApiOwn extends MergeRequestApi {

    public MergeRequestApiOwn(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    @Override
    public MergeRequest acceptMergeRequest(Object projectIdOrPath, Integer mergeRequestIid,
                                           String mergeCommitMessage, Boolean shouldRemoveSourceBranch, Boolean mergeWhenPipelineSucceeds, String sha)
            throws GitLabApiException {

        if (mergeRequestIid == null) {
            throw new RuntimeException("mergeRequestIid cannot be null");
        }

        Form formData = new GitLabApiForm()
                .withParam("merge_commit_message", mergeCommitMessage)
                .withParam("should_remove_source_branch", shouldRemoveSourceBranch)
                .withParam((isApiVersion(GitLabApi.ApiVersion.V3) ?
                                "merge_when_build_succeeds" : "merge_when_pipeline_succeeds"),
                        mergeWhenPipelineSucceeds)
                .withParam("sha", sha);

        Response response = put(Response.Status.OK, formData.asMap(), "projects", getProjectIdOrPath(projectIdOrPath), "merge_request", mergeRequestIid, "merge");
        return (response.readEntity(MergeRequest.class));
    }

}
