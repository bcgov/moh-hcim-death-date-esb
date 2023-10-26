# HCIM DeathDate ESBApp

[![Lifecycle:Maturing](https://img.shields.io/badge/Lifecycle-Maturing-007EC6)](https://github.com/bcgov/repomountie/blob/master/doc/lifecycle-badges.md)

Welcome to the code repository for the HCIM DeathDate ESB App.

## Running the App in Docker on a Local Machine

Prerequisites:

- You have an ActiveMQ JMS broker running and listening for tcp requests on port 61616
  - See [Setting Up an ActiveMQ Broker](#setting-up-an-activemq-broker)
- You have created and configured a Postgres database
  - See [Database Setup](#database-setup)
- You have copies of the configuration files not included in this GitHub repository
  - You will need the ftp and ssl certificates and the file `local.env`
  - Ensure that all of the passwords are set in `local.env`

Before building the app, also ensure that the lines in the Dockerfile for copying the certificates into the container reflect the way you are using it. If you are building locally and you have the files deathDevTest.openssh, keys.jks, and trust.jks in config/local, you can comment out the lines that copy the keys from build args and uncomment the lines that copy the files directly. The method using build args is meant for builds executed from a GitHub workflow since the certificates are stored in GitHub Secrets rather than files.

To run the app in docker on a local machine, build the Docker image from the Dockerfile in this repo's root directory. Note the period for the path at the end of the command, and change it if necessary.

```bash
docker build -t {name-of-image} .
```

Then run the Docker container and map it to port 8080. Make sure you have the correct environment variables in your .env file and that all of the passwords are correct.

```bash
docker run --name {name-of-container} --env-file {path-to-env-file} -p 8080:8080 {name-of-image}
```

### Verifiying that the app is running

To make sure the app is running, visit `http://localhost:8080/DeathDate`. You should see a simple web page showing the header "CRS Death Date".

The app is also configured to check the ftp server for files at times determined by the SCHEDULER_CRON environment variable (note that this cron syntax includes a "seconds" field in the leftmost position in addition to all of the regular fields). Check the logs for output at the appropriate times and ensure that there are no errors. The program should grab a file from the ftp server if one is present, log some information in the database, and delete the file. If there is no file, the app should log two messages indicating that it started the process and then finished it.

## Database Setup

To set up the application's database, run `deathdate_pg.sql` in a new Postgres database named `registries`. The script will create a user called "role_esb_death" and all of the tables and sequences required by the application. Then you need to add a password for the user; you can do this either with the psql command `\password role_esb_death`, or you can modify the `create user role_esb_death` line in `deathdate_pg.sql` to end with `password {password}`.

You may need to restart the database in order for the password change to be reflected.

## Setting Up an ActiveMQ Broker

The app uses an ActiveMQ JMS broker. To set one up, first download [ActiveMQ 5](https://activemq.apache.org/components/classic/download/) and unzip it in a convenient place. Then navigate to the folder's `bin` directory in a terminal and run `./activemq console` (or just `activemq console` depending on your terminal) to make sure it's working. To be able to run activemq commands from anywhere, you can add this directory to your PATH environment variable if you want to. If you don't, you'll only be able to run activemq commands from one of the activemq `bin` directories, including those created with each broker you instantiate.

If the activemq console is working, you can create a new broker with `activemq create {path-to-broker}`. This will create the default broker in the designated directory. You can then start the broker with `activemq start xbean:file:{path-to-broker}/conf/activemq.xml`. This will start the broker from the configuration given in `activemq.xml`.

The broker should start up and print some logs in the terminal window. To stop it, press Ctrl+C. If the broker fails to start up, it may be because one or more of the ports it is trying to use are already occupied. Make sure you are not already running a broker in another terminal. If you aren't, you can either try to find out which apps are using the ports and stop them, or you can change the ports in `conf/activemq.xml` and try to start it up again. Make sure to update the port in your `local.env` file to reflect the change.

If you are trying to discover the app that is using the ports and you cannot find it, it may be because some of the ports the broker uses by default are in the ephemeral port range. On this range, apps and system services can freely use ports without reporting their usage. If this is the case, you can likely fix the issue by restarting your computer. If that is impossible and you cannot change the ports in the configuration file, you'll have to explore alternative solutions.

You can verify that the broker is running by accessing http://localhost:8161 in a web browser. After logging in with default credentials `username: admin` and `password: admin`, you should see a simple page showing "Welcome to the Apache ActiveMQ!".

### Username and password authentication

The ActiveMQ broker in AWS requires a username and password, while the default broker you get locally does not. This does not cause any problems as long as the app is correctly configured to use a username and password. If you want your local broker to also require a username and password, add the following code to `{path-to-broker}/conf/activemq.xml` in between the `<broker>` and `</broker>` tags. Substitue `{username}` and `{password}` with the desired username and password, respectively.

```xml
<plugins>
    <simpleAuthenticationPlugin anonymousAccessAllowed="false">
        <users>
            <authenticationUser username="{username}" password="{password}" groups="users,admins"/>
        </users>
    </simpleAuthenticationPlugin>
</plugins>
```

## Code Organization

This project uses Java Architecture for XML Binding (JAXB), which automatically generates files in DeathDate-war/src/main/java/org/hl7/v3. Some of these files contain the following header:

```java
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: {date} at {time} 
//
```

Note that the URL in the header is broken; see https://www.oracle.com/technical-resources/articles/javase/jaxb.html for more information on JAXB.
