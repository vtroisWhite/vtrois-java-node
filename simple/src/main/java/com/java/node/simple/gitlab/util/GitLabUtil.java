package com.java.node.simple.gitlab.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.java.node.simple.gitlab.operation.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.ProjectApi;
import org.gitlab4j.api.models.Project;

import java.util.*;

/**
 * @Description gitLab API操作的包
 */
@Slf4j
public class GitLabUtil {

    private static String host = "http://gitlab.com/";
    private static PublishOperation opt1 = new PublishOperation1();
    private static PublishOperation opt2 = new PublishOperation2();
    /**
     * 不同的项目走不同的策略类
     */
    public static Map<String, PublishOperation> optTypeMap = new TreeMap<String, PublishOperation>() {{
        put("project_1", opt1);

        put("project_2", opt2);
    }};
    private static PublishOperation defaultOpt = opt1;
    @Getter
    private GitLabApi gitLabApi;
    private ProjectApi projectApi;
    private List<Project> projects;

    public GitLabUtil(String token) {
        gitLabApi = new GitLabApi(GitLabApi.ApiVersion.V3, host, token);
        projectApi = gitLabApi.getProjectApi();
        try {
            projects = projectApi.getProjects();
        } catch (GitLabApiException e) {
            if (e.getHttpStatus() == 401 && "Unauthorized".equals(e.getReason())) {
                throw new RuntimeException("非法token");
            }
            log.error("未知异常:", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void main(String[] args) throws GitLabApiException {
        //http://gitlabhost/profile/account 的Private token
        String token = "123";
        String nameSpace = "all";
        GitLabUtil util = new GitLabUtil(token);
        util.toProduct(nameSpace, "project");
    }

    /**
     * 没找到工具类的实现，所以自己实现下，用户当前用户的id、用户名
     * {
     * "name": "用户名",
     * "id": 用户id,
     * }
     *
     * @return
     */
    public JSONObject getOwnerUser() {
        String url = host + "/api/v3/user?private_token=" + gitLabApi.getAuthToken();
        HttpResponse response = HttpRequest.get(url).execute();
        String body = response.body();
        return JSONObject.parseObject(body);
    }

    /**
     * 根据前缀与项目名，获取项目
     *
     * @param nameSpace
     * @param projectName
     * @return
     * @throws GitLabApiException
     */
    public Project getProject(String nameSpace, String projectName) {
        for (Project project : projects) {
            if (project.getNamespace().getName().equals(nameSpace) && project.getName().equals(projectName)) {
                return project;
            }
        }
        return null;
    }

    /**
     * 检查是否有操作类型，没有的话返回null
     *
     * @param projectNames
     * @return
     */
    public String checkOptType(String[] projectNames) {
        for (String projectName : projectNames) {
            if (!optTypeMap.containsKey(projectName)) {
                return projectName;
            }
        }
        return null;
    }

    /**
     * 检查项目是否存在，不存在则返回项目的名字
     *
     * @param nameSpace
     * @param projectNames
     * @return
     * @throws GitLabApiException
     */
    public String checkProjectExist(String nameSpace, String[] projectNames) {
        for (String projectName : projectNames) {
            Project project = this.getProject(nameSpace, projectName);
            if (project == null) {
                return projectName;
            }
        }
        return null;

    }

    private void operation(String nameSpace, OperationEnum operationEnum, String... projectNames) {
        String command = operationEnum.getDesc();
        System.out.println(String.format("当前的操作为:%s,发布项目为:%s,请输入\"%s\"来确认操作", operationEnum.getDesc(), Arrays.toString(projectNames), command));
        Scanner scanner = new Scanner(System.in);
        String type = scanner.nextLine();
        if (!command.equals(type)) {
            System.out.println("命令不匹配,取消执行");
            return;
        }
        for (String projectName : projectNames) {
            try {
                Project project = getProject(nameSpace, projectName);
                if (project == null) {
                    throw new RuntimeException("找不到项目");
                }
                ProjectUtil projectUtil = new ProjectUtil(project, gitLabApi);
                PublishOperation operation = optTypeMap.containsKey(projectName) ? optTypeMap.get(projectName) : defaultOpt;
                operationEnum.pub(operation, projectUtil);
            } catch (Exception e) {
                log.error("自动部署-{},异常,nameSpace:{},projectName:{},e:", operationEnum, nameSpace, projectName, e);
            }
        }
    }

    /**
     * 灰度发布
     *
     * @param projectNames
     */
    public void toGray(String nameSpace, String... projectNames) {
        operation(nameSpace, OperationEnum.TO_GRAY, projectNames);
    }

    /**
     * 线上发布
     *
     * @param projectNames
     */
    public void toProduct(String nameSpace, String... projectNames) {
        operation(nameSpace, OperationEnum.TO_PRODUCT, projectNames);

    }

    public void publish(String nameSpace, String projectName, OperationEnum operationEnum) throws GitLabApiException {
        Project project = getProject(nameSpace, projectName);
        ProjectUtil projectUtil = new ProjectUtil(project, gitLabApi);
        PublishOperation operation = optTypeMap.get(projectName);
        operationEnum.pub(operation, projectUtil);

    }
}
