ARG VERSION=7.13.15-ubuntu
FROM atlassian/jira-software:${VERSION}

USER root

ENV JIRA_INSTALL=/opt/atlassian/jira
ENV AGENT_NAME=atlassian-agent.jar

# 将代理破解包加入容器
# RUN curl -o ${JIRA_INSTALL}/${AGENT_NAME} https://github.com/keygood/atlassian-agent/releases/download/v1.2.3/atlassian-agent.jar -L
COPY build/atlassian-agent-1.2.3.jar ${JIRA_INSTALL}/${AGENT_NAME}

# 设置启动加载代理包
RUN echo 'export CATALINA_OPTS="-javaagent:${JIRA_INSTALL}/${AGENT_NAME} ${CATALINA_OPTS}"' >> ${JIRA_INSTALL}/bin/setenv.sh

# docker build -t kail/jira-software:7.13.15-crack .
# docker run -d -p 8080:8080 -v /opt/data/atlassian:/var/atlassian --name jira7 kail/jira-software:7.13.15-crack
# docker logs -f jira7

# curl http://localhost:8080
# 选择语言
# 将设置它自己
# 记录 服务器ID：BJGV-9DWN-HRON-R05Z
# docker exec -it jira7 bash
# java -jar /opt/atlassian/jira/atlassian-agent-1.2.3.jar -d -m test@test.com -n BAT -p jira -o http://172.16.2.220:8080 -s BIEB-GB13-SXRC-8J3F
