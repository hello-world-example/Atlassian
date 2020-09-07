# Docker 方式安装 Jira



## 6.4.4 老版本 Dockerfile

```dockerfile
ARG JDK_VERSION=8u102
ARG JIRA_PRODUCT=jira
ARG JIRA_VERSION=6.4.4
ARG AGENT_VERSION=1.2.2

# 归档下载 https://www.atlassian.com/zh/software/jira/download-archives
#         https://www.atlassian.com/software/jira/downloads/binary/atlassian-jira-6.4.4.tar.gz
FROM openjdk:${JDK_VERSION}
#
# Env
#
ENV JIRA_HOME=/opt/data/jira
ENV JIRA_INSTALL=/opt/websuite/jira
RUN mkdir -p ${JIRA_INSTALL} ${JIRA_HOME}
ENV JIRA_USER=jira
ENV JIRA_GROUP=jira
ENV JAVA_OPTS="-Xms1024m -Xmx1024m"
#
# Jira
#
# RUN curl -o /tmp/atlassian.tar.gz https://product-downloads.atlassian.com/software/jira/downloads/atlassian-${JIRA_PRODUCT}-${JIRA_VERSION}.tar.gz -L
COPY build/atlassian-jira-6.4.4.tar.gz /tmp/atlassian.tar.gz
RUN tar zxvf /tmp/atlassian.tar.gz -C ${JIRA_INSTALL}/ --strip-components 1
RUN rm -f /tmp/atlassian.tar.gz
RUN echo "jira.home = ${JIRA_HOME}" > ${JIRA_INSTALL}/atlassian-jira/WEB-INF/classes/jira-application.properties
#
# Crack
#
# COPY build/atlassian-agent-1.2.3.jar ${JIRA_INSTALL}/atlassian-agent.jar
RUN curl -o ${JIRA_INSTALL}/atlassian-agent.jar https://github.com/keygood/atlassian-agent/releases/download/v1.2.3/atlassian-agent.jar -L
# 解决 Invalid byte tag in constant pool: 19 问题，https://www.cnblogs.com/klbc/p/12025002.html
RUN sed -i 's/.DefaultJarScanner.jarsToSkip=/.DefaultJarScanner.jarsToSkip=atlassian-agent.jar,/' ${JIRA_INSTALL}/conf/catalina.properties
ENV JAVA_OPTS="${JAVA_OPTS} -javaagent:${JIRA_INSTALL}/atlassian-agent.jar"
#
# User Auth
#
RUN export CONTAINER_USER=${JIRA_USER}
RUN export CONTAINER_GROUP=${JIRA_GROUP}
RUN groupadd -r ${JIRA_GROUP} && useradd -r -g ${JIRA_GROUP} ${JIRA_USER}
RUN chown -R ${JIRA_USER}:${JIRA_GROUP} ${JIRA_INSTALL} ${JIRA_HOME}
#
# Info
#
EXPOSE 8080
VOLUME ${JIRA_HOME}
USER ${JIRA_USER}
#
# Work
#
WORKDIR ${JIRA_INSTALL}
# -fg 指定 JIRA 前台运行
ENTRYPOINT ["./bin/start-jira.sh", "-fg"]
#
#
#
# 重新构建
# docker kill jira ;; docker rm jira ;; docker rmi kail/jira:6.4.4
#
# 构建
# docker build -t kail/jira:6.4.4 .
# docker run -d -p 8080:8080 --name jira kail/jira:6.4.4
# docker logs jira -f
#
# Crack
# docker exec -it jira bash
# java -jar atlassian-agent.jar -d -m test@test.com -n BAT -p jira -o http://localhost -s BJGV-9DWN-HRON-R05Z



```





## Read More

- [ChampagneCui/atlassian-agent](https://github.com/ChampagneCui/atlassian-agent)
- [atlassian/jira-software](https://hub.docker.com/r/atlassian/jira-software)

