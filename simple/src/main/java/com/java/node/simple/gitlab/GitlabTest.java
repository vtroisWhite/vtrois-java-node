package com.java.node.simple.gitlab;

import com.java.node.simple.gitlab.util.GitLabUtil;
import com.java.node.simple.gitlab.util.ProjectUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;

import java.util.*;
import java.util.function.Function;

/**
 * @Description 批量合并分支
 */
@Slf4j
public class GitlabTest {

    private static final GitLabUtil gitLabUtil = new GitLabUtil("12345token");

    public static void main(String[] args) throws GitLabApiException {
        //要发布的项目
        List<ProjectEnum> projectList = new ArrayList<>(Arrays.asList(
                ProjectEnum.project_1,
                ProjectEnum.project_2,
                null
        ));
        projectList.removeIf(Objects::isNull);
        //要合并到哪个分支
        Function<ProjectEnum, String> targetBranch = ProjectEnum::getDev2;
        //来自哪个分支，会依次判断哪个分支存在，使用第一个存在的分支
        List<String> sourceBranch = Arrays.asList("branch-1", "branch-2");

        String devName = targetBranch.apply(projectList.get(0));
        System.out.println(String.format("目标分支：%s，source分支：%s，输入【%s】确认", devName, sourceBranch, devName));
        if (!devName.equals(new Scanner(System.in).nextLine())) {
            System.out.println("输入错误，执行取消");
            return;
        }
        System.out.println("开始执行");
        for (ProjectEnum projectEnum : projectList) {
            Project project = gitLabUtil.getProject(projectEnum.getNameSpace(), projectEnum.getName());
            ProjectUtil projectUtil = new ProjectUtil(project, gitLabUtil.getGitLabApi());
            String source = getSourceBranch(projectUtil, sourceBranch);
            if (source == null) {
                log.error(String.format("项目的分支都不存在,项目:%s,分支：%s", projectUtil.getProjectName(), sourceBranch));
                continue;
            }
            String target = targetBranch.apply(projectEnum);
            System.out.printf("%s：%s合并到%s\n", projectUtil.getProjectName(), source, target);
            try {
                projectUtil.mergeBranch(source, target);
            } catch (Exception e) {
                log.error("项目:{},合并异常，e:{}", projectEnum.getName(), e.getMessage());
            }
        }
    }

    @SneakyThrows
    private static String getSourceBranch(ProjectUtil projectUtil, List<String> sourceBranch) {
        for (String branch : sourceBranch) {
            if (projectUtil.branchExist(branch)) {
                return branch;
            }
        }
        return null;
//        throw new RuntimeException(String.format("项目的分支都不存在,项目:%s,分支：%s", projectUtil.getProjectName(), sourceBranch));
    }

    enum ProjectEnum {
        project_1("project-1"),
        project_2("project-2"),
        ;
        @Getter
        private final String name;
        @Getter
        @Setter
        private final String nameSpace;
        @Getter
        @Setter
        private String master;
        @Getter
        @Setter
        private String dev1;
        @Getter
        @Setter
        private String dev2;
        @Getter
        @Setter
        private String dev3;

        ProjectEnum(String name) {
            this.name = name;
            this.nameSpace = "all";
            this.master = "master";
            this.dev1 = "dev_1";
            this.dev2 = "dev_2";
            this.dev3 = "dev_3";
        }
    }
}
