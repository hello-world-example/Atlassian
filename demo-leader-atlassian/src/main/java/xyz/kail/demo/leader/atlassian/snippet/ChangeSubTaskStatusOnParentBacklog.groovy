package xyz.kail.demo.leader.atlassian.snippet

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueImpl
import com.atlassian.jira.issue.issuetype.IssueType
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl

// region 代码提示
Issue issue = ComponentAccessor.issueManager.getIssueObject("ARCH-1")
// endregion


/**
 * 还原项目状态【用测测试】
 */

IssueType typeObject = issue.getIssueTypeObject()

// 是主任务 (不是子任务)
if (typeObject.subTask) {
    return
}
Collection<Issue> issues = issue.subTaskObjects
for (Issue sub in issues) {

    List<Integer> flowStep = ResetStatusEnum.findResetStep(sub.statusObject.id, ResetStatusEnum.TO_DO.status)

    changeIssueStatus(sub, flowStep, "auto.robot")
}


enum ResetStatusEnum {

    TO_DO("10000", "BackLog", 11, "开始"),
    IN_PROGRESS("3", "开发中", 91, "Done"),
    DONE("10002", "完成", 101, "Close"),
    CLOSED("6", "已关闭", 111, "打开")

    String status
    String statusDesc
    Integer step
    String stepDesc

    ResetStatusEnum(String status, String statusDesc, Integer step, String stepDesc) {
        this.status = status
        this.statusDesc = statusDesc
        this.step = step
        this.stepDesc = stepDesc
    }

    static def findResetStep(String source, String target) {
        List<Integer> flowStep = new ArrayList<>()
        if (null == source || null == target || source == target) {
            return flowStep
        }

        Map<String, Integer> workflow = new LinkedHashMap<>()
        workflow.put(IN_PROGRESS.status, IN_PROGRESS.step)
        workflow.put(DONE.status, DONE.step)
        workflow.put(CLOSED.status, CLOSED.step)
        workflow.put(TO_DO.status, null)

        // 状态 ID 不匹配
        if (!workflow.containsKey(source) || !workflow.containsKey(target)) {
            return flowStep
        }


        for (Map.Entry<String, Integer> flow : workflow.entrySet()) {
            // 匹配到结束状态，退出
            if (flow.key == target) {
                break
            }

            // 从第一个开始记录
            if (flow.key == source || !flowStep.isEmpty()) {
                flowStep.add(flow.value)
            }
        }
        // 空集合，说明不需要状态变更
        return flowStep
    }
}

static def changeIssueStatus(Issue issue, List<Integer> flowStep, String userKey) {

    WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class)

    IssueImpl subIssueImpl = (IssueImpl) issue

    for (Integer action : flowStep) {
        // 指定任务
        workflowTransitionUtil.setIssue(subIssueImpl)
        // 指定操作人
        workflowTransitionUtil.setUserkey(userKey)
        // 指定 转换(ID)
        workflowTransitionUtil.setAction(action)

        // 校验和执行
        workflowTransitionUtil.validate()
        workflowTransitionUtil.progress()
    }
}





