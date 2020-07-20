# JQL

## 基本概念

### 语法结构

```sql
-- fields(project) 
-- operator(=) 
-- value("TEST") 

-- keyword(AND) 

-- fields(assignee) 
-- operator(=) 
-- function(currentuser())

project = "TEST" AND assignee = currentuser()
```

### 保留字

- `space (" ") + . , ; ? | * / % ^ $ # @ [ ]` 这些字符是保留字
- 如果需要查询这些字符， 需要使用两个反斜杠转义

## Fields ✔️

> - [Advanced search reference - JQL fields](https://support.atlassian.com/jira-software-cloud/docs/advanced-search-reference-jql-fields/)
> - [Search syntax for text fields](https://support.atlassian.com/jira-software-cloud/docs/search-syntax-for-text-fields/)
>
> > - 自定义字段 /secure/admin/ViewCustomFields.jspa 



|         系统字段          | 简介                                                         | 示例                                                   |
| :-----------------------: | :----------------------------------------------------------- | ------------------------------------------------------ |
|        **project**        | 项目                                                         |                                                        |
|      ~~projectType~~      | 项目类型                                                     |                                                        |
|         **text**          | 匹配所有 text 类型的字段，如：summary、description、comments |                                                        |
|        **summary**        | 主题                                                         |                                                        |
|   **type / issueType**    | 任务类型                                                     | */secure/admin/ViewIssueTypes.jspa*                    |
|        **status**         | 任务状态                                                     | */secure/admin/ViewStatuses.jspa*                      |
|      **description**      | 问题描述信息                                                 |                                                        |
|       **priority**        | 优先级                                                       | */secure/admin/ViewPriorities.jspa*                    |
|        environment        | 环境                                                         |                                                        |
|        **comment**        | 留言                                                         |                                                        |
|         component         | 模块，在项目管理里面自定义                                   | */plugins/servlet/project-config/{project}/components* |
|        attachments        | 附件                                                         | `attachments IS NOT EMPTY` 有附件的任务                |
|         category          | 类别                                                         |                                                        |
|        "epic link"        |                                                              | `"Epic Link" = EMPTY`                                  |
| **issueKey/id/issue/key** | issue ID 过滤                                                | issue in issueHistory()                                |
|  **issueLink["blocks"]**  | 关联查询                                                     | `issueLink["is blocked by"] = ABC-123`                 |
|     **issueLinkType**     | 链接类型                                                     | `issueLinkType = "is blocked by"`                      |
|          labels           | 问题标签                                                     |                                                        |
|          *level*          | 问题安全等级                                                 | */secure/admin/ViewIssueSecuritySchemes.jspa*          |
|        **parent**         | 子任务                                                       | `parent IN (Key)`                                      |
|        resolution         | 解决结果                                                     | */secure/admin/ViewResolutions.jspa*                   |
|           Rank            |                                                              |                                                        |


### 集合

|                   系统字段                    | 简介       | 示例                                  |
| :-------------------------------------------: | :--------- | ------------------------------------- |
| **filter /request/savedFilter/searchRequest** | 过滤器搜索 | `filter IN (filterName1,filterName2)` |

### User

|   系统字段   | 简介     | 示例                                     |
| :----------: | :------- | ---------------------------------------- |
| **creator**  | 创建人   |                                          |
| **assignee** | 经办人   |                                          |
| **reporter** | 报告人   |                                          |
|    voter     | 投票人   |                                          |
|    votes     | 投票人数 |                                          |
| **watcher**  | 关注人   |                                          |
| **watchers** | 关注人数 | `watchers > 5` 关注人数大于 5 个人的项目 |

### Time

|           系统字段            | 简介     | 示例 |
| :---------------------------: | :------- | ---- |
|   **created / createdDate**   | 创建时间 |      |
|         due / dueDate         | 到期时间 |      |
|          lastViewed           | 浏览时间 |      |
| **resolved / resolutionDate** | 解决时间 |      |
|   **updated / updatedDate**   | 更新时间 |      |

### Agile

| 系统字段 | 简介                                                   | 示例 |
| :------: | :----------------------------------------------------- | ---- |
|  sprint  |                                                        |      |
| **Rank** | 用于在 Kanban 中，同一个泳道内进行拖拽排序、置顶、置底 |      |

### time-tracking 功能

|                系统字段                 | 简介         | 示例                                 |
| :-------------------------------------: | :----------- | ------------------------------------ |
| originalEstimate / timeOriginalEstimate | 原始时间估时 | `originalEstimate > 2d`              |
|    remainingEstimate / timeEstimate     | 估时剩余     |                                      |
|                timeSpent                | 花费时间     |                                      |
|                workRatio                | 工作比率     | (timeSpent / originalEstimate) x 100 |

### Version

> - /plugins/servlet/project-config/{project}/versions

|    系统字段     | 简介 | 示例 |
| :-------------: | :--- | ---- |
| affectedVersion |      |      |
|   fixVersion    |      |      |

### Jira Service Desk 许可

|          系统字段          | 简介 | 示例 |
| :------------------------: | :--- | ---- |
|         approvals          |      |      |
|    change-control-type     |      |      |
|       organizations        |      |      |
|    request-channel-type    |      |      |
| request-last-activity-time |      |      |
| `<`your custom SLA name`>` |      |      |
|                            |      |      |



## Operators ✔️

> [Advanced search reference - JQL operators](https://support.atlassian.com/jira-software-cloud/docs/advanced-search-reference-jql-operators/)

|       Operators        |  简介  | 示例                                                         |
| :--------------------: | :----: | ------------------------------------------------------------ |
|       `=`、`!=`        |  等值  | `reporter = currentUser() and assignee != currentUser()`     |
| `>`、`>=`、`<`、`<=`、 |  大小  | `duedate < now()`、`duedate >= "2008/12/31"`<br />`created >= "-5d"`、`updated <= "-4w 2d"` |
|    `IN`、 `NOT IN`     | 多等值 | `assignee not in (Jack,Jill,John)` <br />`assignee in (membersOf(组))` |
|       `~`、`!~`        |  包含  | 用于 Summary、Description、Comments、自定义 Text 字段 等<br />`summary ~ frame` ：匹配 frame <br /> `summary ~ "frame*"`：匹配 framework、frame 等<br />`summary ~ "issue collector"`：匹配 issue AND collector<br />`summary ~ "\"full screen\""` ：全词匹配 |
|    `IS` 、`IS NOT`     |  空值  | `fixVersion is empty`、 `fixVersion is null`                 |
|                        |        |                                                              |

### ❤ 历史记录搜索

|        Keywords        |        简介         |
| :--------------------: | :-----------------: |
|    `WAS`、`WAS NOT`    | **以前** 有过指定值 |
| `WAS IN`、`WAS NOT IN` | **以前** 有过一匹值 |
|       `CHANGED`        | **以前** 字段修改过 |

**支持的字段**

- `Status` ： 状态
- `Assignee` ：经办人
- `Reporter` ：  报告人
- `Resolution`： 解决结果
- `Priority` ： 优先级
- fixVersion ：修复的版本 

**谓词**

- `ON "date"` ：  在指定时间的历史 
- `AFTER "date"` ： 在指定时间 **之后** 的历史
- `BEFORE "date"` ：在指定时间 **之前** 的历史 
- `DURING ("date1","date2")` ： 在指定时间 **之间** 的历史 
- `BY "username"` or ` BY (username1,username2)` ： 历史被谁修改
- CHANGED 独有，从什么变为什么
  - `FROM "oldvalue"` 
  - `TO "newvalue"`

**示例**

- `status WAS IN ("Resolved","In Progress")` 状态曾经经历过 Resolved、 In Progress
- `status IN (Done,Closed) AND status WAS NOT IN ("In Progress")` ：状态直接从 TODO 变成 Done
- `status WAS "Resolved" BY kail BEFORE "2019/02/02"` ： 在 2019/02/02 之前 被 kail 改为解决
- `status CHANGED FROM "In Progress" TO "Open"` 状态曾经从 "In Progress" 改为 "Open"



## Keywords ✔️

> [Advanced search reference - JQL keywords](https://support.atlassian.com/jira-software-cloud/docs/advanced-search-reference-jql-keywords/)



|  Keywords  |   简介    | 示例                                                         |
| :--------: | :-------: | ------------------------------------------------------------ |
|   `AND`    |    且     | `project = "New office" and status = "open"`<br />           |
|    `OR`    |    或     | `(duedate < now() or duedate is empty) AND project = "New office"` |
|   `NOT`    |    非     | `not assignee = jsmith`                                      |
|  `EMPTY`   | 字段无值  | `duedate is empty` 、 `duedate is not empty` 、`duedate = empty` |
|   `NULL`   | 同 `NULL` | 同 `NULL`                                                    |
| `ORDER BY` |   排序    | `duedate = empty order by created asc, priority desc`        |



## Functions ✔️

> [Advanced search reference - JQL functions](https://support.atlassian.com/jira-software-cloud/docs/advanced-search-reference-jql-functions/)

### User

|               Functions               |      简介      | 示例                                      |
| :-----------------------------------: | :------------: | ----------------------------------------- |
|            `currentUser()`            |  **当前用户**  | `assignee = currentUser()`                |
|          `membersOf(group)`           | **组内的用户** | `assignee = membersOf("group")`           |
| `updatedBy(user, [dateFrom, dateTo])` |   被谁更新了   | `issuekey IN updatedBy(jsmith)`           |
|    `projectsLeadByUser(username)`     |   项目负责人   | `project in projectsLeadByUser()`         |
|   `componentsLeadByUser(username)`    |   模块负责人   | `component in componentsLeadByUser(user)` |

### Time

> - `+1d` 明天
> - `-1d` 昨天
> - `yyyy/MM/dd HH:mm`、 `yyyy-MM-dd HH:mm` 、`yyyy/MM/dd`、`yyyy-MM-dd`

|                          Functions                           |                  简介                  | 示例                                                         |
| :----------------------------------------------------------: | :------------------------------------: | ------------------------------------------------------------ |
|                       `currentLogin()`                       |              当前登录时间              | `created > currentLogin()` 在当前登录的 Session 期间创建的问题 |
|                        `lastLogin()`                         |              最后登录时间              |                                                              |
|                           `now()`                            |              **当前时间**              |                                                              |
| `startOfDay()`<br />`startOfWeek()`<br />`startOfMonth()`<br />`startOfYear()` | **开始时间**<br />`(+/-)(y|M|w|d|h|m)` |                                                              |
| `endOfDay()`<br />`endOfWeek()`<br />`endOfMonth()`<br />`endOfYear()` | **结束时间**<br />`(+/-)(y|M|w|d|h|m)` | `due < endOfDay()` <br />`due < endOfDay("+1")`              |

### Field

|                          Functions                           |       简介       | 示例                                                         |
| :----------------------------------------------------------: | :--------------: | ------------------------------------------------------------ |
|             `linkedIssues(issueKey,[LinkType])`              |  **链接的问题**  | `issue in linkedIssues(ABC-123,"is duplicated by")`          |
| `cascadeOption(parentOption)`<br />`cascadeOption(parentOption,child)` |     级联筛选     |                                                              |
|                    `standardIssueTypes()`                    |  **查找主任务**  | `issuetype in standardIssueTypes()`                          |
|                    `subtaskIssueTypes()`                     |  **查找子任务**  |                                                              |
|         `projectsWhereUserHasPermission()`         | 有指定权限的项目 | `project in projectsWhereUserHasPermission("Resolve Issues")` |
|             `projectsWhereUserHasRole()`             | 有指定角色的项目 | `project in projectsWhereUserHasRole("Developers")`          |
|                       `issueHistory()`                       |            最近查看过的问题            | `issue in issueHistory() AND assignee = currentUser()`       |
|              issuesWithRemoteLinksByGlobalId()               |                  ？？                  |                                                              |
|                       `votedIssues()`                        |              我投票的问题              | `issue in votedIssues()`                                     |
|                      `watchedIssues()`                       |              **我关注的问题**              | `issue in watchedIssues()`                                   |

### Agile

|     Functions     |         简介          | 示例                        |
| :---------------: | :-------------------: | --------------------------- |
|  `openSprints()`  | 已开始未结束的 Sprint |                             |
| `closedSprints()` |     已完成 Sprint     | `sprint in closedSprints()` |
| `futureSprints()` |   尚未开始的 Sprint   |                             |
| ~~parentEpic()~~  |                       |                             |

### Version

|          Functions          | 简介 | 示例 |
| :-------------------------: | :--: | ---- |
| earliestUnreleasedVersion() |      |      |
|   latestReleasedVersion()   |      |      |
|     releasedVersions()      |      |      |
|    unreleasedVersions()     |      |      |

### Jira Service Desk 许可

|         Functions         |                         简介                          | 示例                                     |
| :-----------------------: | :---------------------------------------------------: | ---------------------------------------- |
|      ~~approved()~~       |                        被批准                         | `approval = approved()`                  |
|  ~~approver(user,user)~~  |                        批准人                         | `approval = approver(jsmith,skhan)`      |
|      ~~breached()~~       | 未达标 SLA <br />Service Level Agreement 服务级别协议 | `"Time to First Response" = breached()`  |
|      ~~completed()~~      |                      已完成 SLA                       | `"Time to First Response" = completed()` |
|       ~~elapsed()~~       |                                                       |                                          |
|    ~~everbreached()~~     |                                                       |                                          |
|     ~~myApproval()~~      |                    需要批准的问题                     |                                          |
|      ~~myPending()~~      |                      需要我批准                       |                                          |
| ~~organizationMembers()~~ |                     组织内的用户                      |                                          |
|       ~~paused()~~        |                                                       |                                          |
|       ~~pending()~~       |                                                       |                                          |
|      ~~pendingBy()~~      |                                                       |                                          |
|      ~~remaining()~~      |                                                       |                                          |
|       ~~running()~~       |                                                       |                                          |
| ~~withinCalendarHours()~~ |                                                       |                                          |



## 常见用例

- `issue in linkedIssues(BC-740)` ： 链接到某个任务的任务
- `issuetype in standardIssueTypes()` ： 标准问题类型
- `issuetype in subtaskIssueTypes()` ： 子任务类型
- `assignee in (membersOf(xx)) AND status in (ToDo,"To Do","In Progress") AND 预计完成日期 < startOfDay()` ： 超期的任务



## Read More

- [高级搜索 和  JQL 入门](https://www.atlassian.com/zh/software/jira/guides/expand-jira/jql)
- [Use advanced search with Jira Query Language (JQL)](https://support.atlassian.com/jira-software-cloud/docs/use-advanced-search-with-jira-query-language-jql/)
- [Search syntax for text fields](https://support.atlassian.com/jira-software-cloud/docs/search-syntax-for-text-fields/)

