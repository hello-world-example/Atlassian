package xyz.kail.demo.leader.atlassian.workflow


/**
 * 根据子任务更新父任务状态
 */

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueImpl
import com.atlassian.jira.issue.status.Status
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl

// region 代码提示
// Issue issue = ComponentAccessor.issueManager.getIssueObject("ARCH-1")
// endregion


// 默认操作人
final String OPT_KEY = "auto.robot"
// 父项目标签检测（空字符串时不检测）
final String LABEL_CHECK = ""

// 不自动修改状态的项目
if (["AUTOTEST"].contains(issue.projectObject.key)) {
    return
}

// 子任务
if (issue.subTask) {
    if (LABEL_CHECK != "" && !issue.parentObject.labels.collect { it.label }.contains(LABEL_CHECK)) {
        return
    }
    // 父任务状态已经结束
    if (MainStatusEnum.isEnd(issue.parentObject.statusObject.id)) {
        return
    }

    String statusShould = computeStatusByIssue(issue)
    // 未找到配置的状态
    if (null == statusShould) {
        return
    }

    // 计算 parentStatus 和 需要变更的状态之间需要经历几步
    List<Integer> flowStep = findStatusStep(issue.parentObject.statusObject.id, statusShould)
    // 修改父任务状态
    changeIssueStatus(issue.parentObject, flowStep, OPT_KEY)
}

/**
 * 状态页面： /secure/admin/ViewStatuses.jspa
 */
enum MainStatusEnum {

    BACKLOG("10000", "To Do", 41, ""),
    ANALYZE("10500", "分析中", 51, ""),
    DEV_WAIT("10501", "待开发", 61, ""),
    DEV("10204", "开发中", 71, ""),
    TEST_WAIT("10502", "待测试", 81, ""),
    TEST("10205", "测试中", 101, ""),
    CHECK_WAIT("10503", "待验收", 111, ""),
    CHECK("10504", "验收中", 121, ""),
    DONE("10001", "Done", null, ""),
    CLOSED("10407", "关闭", null, "")

    String status
    String statusDesc
    Integer step
    String stepDesc

    MainStatusEnum(String status, String statusDesc, Integer step, String stepDesc) {
        this.status = status
        this.statusDesc = statusDesc
        this.step = step
        this.stepDesc = stepDesc
    }

    /**
     * 任务是否 已结束（子任务）
     */
    static boolean isEnd(String status) {
        return DONE.status == status || CLOSED.status == status
    }


    static Map<String, Integer> getWorkflow() {
        Map<String, Integer> workflow = new LinkedHashMap<>()
        workflow.put(BACKLOG.status, BACKLOG.step)
        workflow.put(ANALYZE.status, ANALYZE.step)
        workflow.put(DEV_WAIT.status, DEV_WAIT.step)
        workflow.put(DEV.status, DEV.step)
        workflow.put(TEST_WAIT.status, TEST_WAIT.step)
        workflow.put(TEST.status, TEST.step)
        workflow.put(CHECK_WAIT.status, CHECK_WAIT.step)
        workflow.put(CHECK.status, CHECK.step)
        workflow.put(DONE.status, null)
        return workflow
    }
}

/**
 * 状态页面： /secure/admin/ViewStatuses.jspa
 */
enum SubStatusEnum {

    TODO("10000", "To Do"),
    IN_PROGRESS("3", "进行中"),
    DONE("10001", "Done"),
    CLOSED("6", "关闭")

    String status
    String statusDesc

    SubStatusEnum(String status, String statusDesc) {
        this.status = status
        this.statusDesc = statusDesc
    }


    /**
     * 任务是否 在进行中（子任务）
     */
    static boolean isDoing(String status) {
        return IN_PROGRESS.status == status
    }

    /**
     * 任务是否 已结束（子任务）
     */
    static boolean isEnd(String status) {
        return DONE.status == status || CLOSED.status == status
    }

}

//return KeyEnum.matchOne(["12", "34"] as String[], "23") // > false
//return KeyEnum.matchOne(["12", "34"] as String[], "12") // > true
//return KeyEnum.matchOne(["12", "34"] as String[], "23") // > false
//return KeyEnum.matchOne(["12", "34"] as String[], null) // > false
enum KeyEnum {

    ANALYZE(["需求分析", "调研", "移交", "串讲", "评审"] as String[]),
    DEV(["开发", "连调"] as String[]),
    TEST(["测试", "自测"] as String[]),
    ONLINE(["上线"] as String[])

    String[] keys

    KeyEnum(String[] keys) {
        this.keys = keys
    }

    boolean match(String summary) {
        return matchOne(keys, summary)
    }

    /**
     * 匹配到一个关键字就算匹配
     *
     * @param keys 逗号分割的字符串
     * @param text 文本
     * @return true/false
     */
    static matchOne(String[] keys, String text) {
        if (null == keys || keys.length <= 0 || null == text) {
            return false
        }
        for (String key : keys) {
            if (text.contains(key)) {
                return true
            }
        }
        return false
    }
}

/**
 * 根据子任判断父任务的状态
 * @param issue 子任务
 */
//return computeStatusByIssue(ComponentAccessor.issueManager.getIssueObject("ARCH-2"))
static def computeStatusByIssue(Issue issue) {
    if (!issue.subTask) {
        return null
    }
    Issue parentIssue = issue.parentObject
    Collection<Issue> subIssues = parentIssue.subTaskObjects

    Status issueStatus = issue.statusObject
    String issueSummary = issue.summary
    String statusShould = null

    // 子任务状态变为 (开发中)，【存在进行中状态】
    if (SubStatusEnum.isDoing(issueStatus.id)) {
        if (KeyEnum.ANALYZE.match(issueSummary)) {
            statusShould = MainStatusEnum.ANALYZE.status
        } else if (KeyEnum.DEV.match(issueSummary)) {
            statusShould = MainStatusEnum.DEV.status
        } else if (KeyEnum.TEST.match(issueSummary)) {
            statusShould = MainStatusEnum.TEST.status
        }
    }

    // 子任务状态变为 (完成、已关闭)，判断指定关键字【是否都已关闭】
    if (SubStatusEnum.isEnd(issueStatus.id)) {
        if (KeyEnum.ANALYZE.match(issueSummary) && allDone(KeyEnum.ANALYZE.keys, subIssues)) {
            statusShould = MainStatusEnum.DEV_WAIT.status
        } else if (KeyEnum.DEV.match(issueSummary) && allDone(KeyEnum.DEV.keys, subIssues)) {
            statusShould = MainStatusEnum.TEST_WAIT.status
        } else if (KeyEnum.TEST.match(issueSummary) && allDone(KeyEnum.TEST.keys, subIssues)) {
            statusShould = MainStatusEnum.CHECK_WAIT.status
        } else if (KeyEnum.ONLINE.match(issueSummary) && allDone(KeyEnum.ONLINE.keys, subIssues)) {
            statusShould = MainStatusEnum.DONE.status
        }
    }

    return statusShould
}

/**
 * 修改任务状态
 * @param issue 任务
 * @param flowStep 状态步骤
 * @param userKey 操作人
 */
//Issue issue = ComponentAccessor.issueManager.getIssueObject("ARCH-1")
//changeIssueStatus(issue, [41, 51], "kaibin.yang")
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

/**
 * 计算 parentStatus 和 需要变更的状态之间需要经历几步
 */
// return findStatusStep("1", "2")         //  》 []
// return findStatusStep("10000", "2")     //  》 []
// return findStatusStep("10000", "10000") //  》 []
// return findStatusStep("10000", "10004") //  》 [41]
// return findStatusStep("10004", "10000") //  》 []
static def findStatusStep(String source, String target) {
    List<Integer> flowStep = new ArrayList<>()
    if (null == source || null == target || source == target) {
        return flowStep
    }

    Map<String, Integer> workflow = MainStatusEnum.workflow

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

/**
 * 指定关键字的子任务是否都已经 Done 或 Closed
 * @param keys 逗号分割的字符串
 * @param subIssues 子任务
 * @return true/false
 */
//Issue issue = ComponentAccessor.issueManager.getIssueObject("ARCH-1")
// return allDone(["sub"] as String[], issue.subTaskObjects) // > true
// return allDone(["12123123"] as String[], issue.subTaskObjects) // > false
static def allDone(String[] keys, Collection<Issue> subIssues) {
    List<Issue> matchIssues = new ArrayList<>()
    for (Issue subIssue : subIssues) {
        // 找到匹配的任务
        if (KeyEnum.matchOne(keys, subIssue.summary)) {
            matchIssues.add(subIssue)
        }
    }
    // 没有找到匹配的任务
    if (matchIssues.isEmpty()) {
        return false
    }
    for (Issue subIssue : matchIssues) {
        if (!SubStatusEnum.isEnd(subIssue.statusObject.id)) {
            // 指定的 子任务 是 非结束状态
            return false
        }
    }
    return true
}