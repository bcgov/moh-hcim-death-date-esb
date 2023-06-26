FROM maven:3.8.6-openjdk-11-slim as build-stage
RUN mkdir -p /home/death_date
ARG env
ENV targetEnv $env
# config folder need for a test. one of the test case checks a file syntax in in this folder.
COPY config /home/death_date/config
COPY DeathDate-war /home/death_date/DeathDate-war
COPY ESB-common /home/death_date/ESB-common
COPY pom.xml /home/death_date
# copying config files from local as these files now have env variables which can be substituted at runtime based on env.
#Once we test, other config folders can be there for reference only if needed. And we can rename local to generic name.
COPY config/local/deathEnvironmentSpecific.xml  /home/death_date/DeathDate-war/src/main/resources
COPY config/local/logback.xml /home/death_date/DeathDate-war/src/main/resources
# needed if we want to copy kyes for different environments. Docker build has to pass the env name in this case.
#COPY config/$targetEnv/deathDevTest.openssh /home/death_date/DeathDate-war/src/main/resources
#COPY config/$targetEnv/*.jks  /home/death_date/DeathDate-war/src/main/resources
COPY config/local/deathDevTest.openssh /home/death_date/DeathDate-war/src/main/resources
COPY config/local/*.jks  /home/death_date/DeathDate-war/src/main/resources
#only copying below file. global-glassfish-resources.xml not needed for payara micro as we are using external jms.
COPY config/local/glassfish-resources.xml  /home/death_date/DeathDate-war/src/main/webapp/WEB-INF

RUN mvn -f /home/death_date/pom.xml clean package -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.insecure=true

FROM payara/micro:5.2022.5-jdk11
COPY --from=build-stage /home/death_date/DeathDate-war/target/Death*.war $DEPLOY_DIR/DeathDate.war
# to add additional jars like postgres in payara micro classpath. HOME_DIR points to /opt/payara
RUN mkdir -p $HOME_DIR/app-libs
COPY postgresql-42.4.0.jar $HOME_DIR/app-libs
COPY jce-jdk12-120.jar $HOME_DIR/app-libs
#cmd command below does not take Env variables like DEPLOY_DIR. Need to hardcode.
# --nocluster option is nice to have as we don't want payara clustering and will consume less resources.
CMD ["--addlibs", "/opt/payara/app-libs/","--deploymentDir", "/opt/payara/deployments","--nocluster"]



