package xyz.kail.demo.leader.atlassian.workflow

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
 * 父项目关闭时，关闭所有子项目
 */
IssueType typeObject = issue.getIssueTypeObject()
// 是主任务 (不是子任务)
if (typeObject.subTask) {
    return
}
// 并且有项目看板标签
// if (!issue.labels.collect { it.label }.contains("项目看板")) {
//     return
// }

Collection<Issue> issues = issue.subTaskObjects
for (Issue sub in issues) {

    List<Integer> flowStep = CloseStatusEnum.findCloseStep(sub.statusObject.id, CloseStatusEnum.CLOSED.status)
    changeIssueStatus(sub, flowStep, "auto.robot")
}


enum CloseStatusEnum {

    TO_DO("10000", "TO DO", 11, "开始"),
    IN_PROGRESS("3", "进行中", 41, "完成"),
    DONE("10001", "DONE", 71, "关闭"),
    CLOSED("6", "关闭", 111, "打开")

    String status
    String statusDesc
    Integer step
    String stepDesc

    CloseStatusEnum(String status, String statusDesc, Integer step, String stepDesc) {
        this.status = status
        this.statusDesc = statusDesc
        this.step = step
        this.stepDesc = stepDesc
    }

    static def findCloseStep(String source, String target) {
        List<Integer> flowStep = new ArrayList<>()
        if (null == source || null == target || source == target) {
            return flowStep
        }

        Map<String, Integer> workflow = new LinkedHashMap<>()
        workflow.put(TO_DO.status, TO_DO.step)
        workflow.put(IN_PROGRESS.status, IN_PROGRESS.step)
        workflow.put(DONE.status, DONE.step)
        workflow.put(CLOSED.status, null)

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



