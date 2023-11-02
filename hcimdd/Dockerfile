FROM maven:3.8.6-openjdk-11-slim as build-stage
RUN mkdir -p /home/death_date

COPY hcimdd/DeathDate-war /home/death_date/DeathDate-war
COPY hcimdd/ESB-common /home/death_date/ESB-common
COPY hcimdd/pom.xml /home/death_date

# copying config files from local as these files now have env variables which can be substituted at runtime based on env file
# once we test, other config folders will be for reference only if needed and we can rename local to generic name
COPY hcimdd/config/local/deathEnvironmentSpecific.xml  /home/death_date/DeathDate-war/src/main/resources
COPY hcimdd/config/local/logback.xml /home/death_date/DeathDate-war/src/main/resources
COPY hcimdd/config/local/glassfish-resources.xml  /home/death_date/DeathDate-war/src/main/webapp/WEB-INF

# copying the key files directly is possible in a local configuration, but not in a github workflow
#COPY config/local/deathDevTest.openssh /home/death_date/DeathDate-war/src/main/resources
#COPY config/local/*.jks  /home/death_date/DeathDate-war/src/main/resources

# in a github workflow, the secrets FTP_PRIVATE_KEY, SSL_KEY, and SSL_TRUST must be passed to the build
ARG FTP_PRIVATE_KEY
ARG SSL_KEY
ARG SSL_TRUST

RUN echo $FTP_PRIVATE_KEY_BASE64 | base64 -d > /home/death_date/DeathDate-war/src/main/resources/deathDevTest.openssh
RUN echo $SSL_KEY_BASE64 | base64 -d > /home/death_date/DeathDate-war/src/main/resources/keys.jks
RUN echo $SSL_TRUST_BASE64 | base64 -d > /home/death_date/DeathDate-war/src/main/resources/trust.jks

RUN chmod 640 /home/death_date/DeathDate-war/src/main/resources/deathDevTest.openssh /home/death_date/DeathDate-war/src/main/resources/keys.jks /home/death_date/DeathDate-war/src/main/resources/trust.jks

# build the project with maven
RUN mvn -f /home/death_date/pom.xml clean package -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.insecure=true

# load payara micro
FROM payara/micro:5.2022.5-jdk11
COPY --from=build-stage /home/death_date/DeathDate-war/target/Death*.war $DEPLOY_DIR/DeathDate.war

# HOME_DIR points to /opt/payara
RUN mkdir -p $HOME_DIR/app-libs
COPY hcimdd/postgresql-42.4.0.jar $HOME_DIR/app-libs
COPY hcimdd/jce-jdk12-120.jar $HOME_DIR/app-libs

# the cmd instruction does not take env variables like DEPLOY_DIR, so we need to hardcode their values
# --nocluster option is nice to have as we don't want payara clustering and it will consume fewer resources
CMD ["--addlibs", "/opt/payara/app-libs/", "--deploymentDir", "/opt/payara/deployments", "--nocluster"]
