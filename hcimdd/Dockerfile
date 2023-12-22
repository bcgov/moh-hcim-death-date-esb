FROM maven:3.8.6-openjdk-11-slim as build-stage
RUN mkdir -p /home/hcimdd
COPY hcimdd/DeathDate-war /home/hcimdd/DeathDate-war
COPY hcimdd/ESB-common /home/hcimdd/ESB-common
COPY hcimdd/pom.xml /home/hcimdd

# the line below can't be used in the github workflow because the files don't exist
# uncomment it for local development, but leave it commented in the repo
# COPY *.jks  /home/hcimdd/DeathDate-war/src/main/resources/

# use these four lines instead to get the keystores from build args
# the github workflow needs these lines, comment them out for local development
ARG SSL_KEY
ARG SSL_TRUST
RUN echo $SSL_KEY | base64 -d > /home/hcimdd/DeathDate-war/src/main/resources/keys.jks
RUN echo $SSL_TRUST | base64 -d > /home/hcimdd/DeathDate-war/src/main/resources/trust.jks

RUN chmod 640 /home/hcimdd/DeathDate-war/src/main/resources/keys.jks /home/hcimdd/DeathDate-war/src/main/resources/trust.jks

RUN mvn -f /home/hcimdd/pom.xml clean package

FROM payara/micro:5.2022.5-jdk11

# HOME_DIR points to /opt/payara
RUN mkdir -p $HOME_DIR/app-libs
RUN mkdir -p $HOME_DIR/.ssh
RUN chmod 700 $HOME_DIR/.ssh

# the line below can't be used in the github workflow because the files don't exist
# uncomment it for local development, but leave it commented in the repo
# COPY id_rsa $HOME_DIR/.ssh/id_rsa

# use these two lines instead to get the key from build args
# the github workflow needs these lines, comment them out for local development
ARG FTP_PRIVATE_KEY
RUN echo $FTP_PRIVATE_KEY | base64 -d > $HOME_DIR/.ssh/id_rsa

RUN chmod 600 $HOME_DIR/.ssh/id_rsa

COPY --from=build-stage /home/hcimdd/DeathDate-war/target/DeathDate*.war $DEPLOY_DIR
COPY hcimdd/postgresql-42.4.0.jar $HOME_DIR/app-libs

# the cmd instruction does not take env variables like DEPLOY_DIR, so we need to hardcode their values
# --nocluster option is nice to have as we don't want payara clustering and it will consume fewer resources
CMD ["--addlibs", "/opt/payara/app-libs/", "--deploymentDir", "/opt/payara/deployments", "--nocluster"]
