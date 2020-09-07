FROM atlassian/jira-software:7.13.15-ubuntu

USER root

# 将代理破解包加入容器
COPY atlassian-agent-1.2.3.jar /opt/atlassian/jira/

# 设置启动加载代理包
RUN echo 'export CATALINA_OPTS="-javaagent:/opt/atlassian/jira/atlassian-agent-1.2.3.jar ${CATALINA_OPTS}"' >> /opt/atlassian/jira/bin/setenv.sh

# docker build -t kail/jira-software:7.13.15-crack .
# docker run -d --name jira -p 9005:8080 kail/jira-software:7.13.15-crack
# docker logs -f jira

# curl http://localhost:9005
# 选择语言
# 将设置它自己
# 记录 服务器ID：BJGV-9DWN-HRON-R05Z
# docker exec -it jira bash
# java -jar /opt/atlassian/jira/atlassian-agent-1.2.3.jar -d -m test@test.com -n BAT -p jira -o http://172.16.2.220:8080 -s BIEB-GB13-SXRC-8J3F
