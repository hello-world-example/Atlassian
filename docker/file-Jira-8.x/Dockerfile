ARG JDK_VERSION=8u102
FROM openjdk:${JDK_VERSION}
#
# Env
#
ENV JIRA_PRODUCT=jira-software
ENV JIRA_VERSION=8.5.7
ENV AGENT_NAME=atlassian-agent-1.2.3.jar
# 
# 
ENV JIRA_FILE_NAME=atlassian-${JIRA_PRODUCT}-${JIRA_VERSION}.tar.gz
ENV JIRA_DOWNLOAD_URL=https://product-downloads.atlassian.com/software/jira/downloads/${JIRA_FILE_NAME}
ENV JIRA_HOME=/opt/data/jira
ENV JIRA_INSTALL=/opt/websuite/jira
RUN mkdir -p ${JIRA_INSTALL} ${JIRA_HOME}

ENV JIRA_USER=jira
ENV JIRA_GROUP=jira
ENV JAVA_OPTS="-Xms1024m -Xmx1024m"
#
# Jira
#
# RUN curl -o /tmp/${JIRA_FILE_NAME} ${JIRA_DOWNLOAD_URL}
COPY build/${JIRA_FILE_NAME} /tmp/${JIRA_FILE_NAME}
RUN tar zxvf /tmp/${JIRA_FILE_NAME} -C ${JIRA_INSTALL}/ --strip-components 1
RUN rm -f /tmp/${JIRA_FILE_NAME}
RUN echo "jira.home = ${JIRA_HOME}" > ${JIRA_INSTALL}/atlassian-jira/WEB-INF/classes/jira-application.properties
#
# Crack
#
COPY build/${AGENT_NAME} ${JIRA_INSTALL}/${AGENT_NAME}
# RUN curl -o ${JIRA_INSTALL}/${AGENT_NAME} https://github.com/keygood/atlassian-agent/releases/download/v1.2.3/atlassian-agent.jar -L
ENV JAVA_OPTS="${JAVA_OPTS} -javaagent:${JIRA_INSTALL}/${AGENT_NAME}"
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
# docker kill jira ; docker rm jira ; docker rmi kail/jira:8.5.7
#
# 构建
# docker build -t kail/jira:8.5.7 .
# docker run -d -p 8080:8080 --name jira kail/jira:8.5.7
# docker logs jira -f
#
# Crack
# docker exec -it jira bash
# java -jar atlassian-agent.jar -d -m test@test.com -n BAT -p jira -o http://localhost -s BAVJ-O3CL-9EBM-L2PG


