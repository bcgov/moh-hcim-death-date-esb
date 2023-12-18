package ca.bc.gov.moh.death.service;

import java.util.Properties;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * A health check route. This route runs only if the configuration property
 * healthCheck=true.
 *
 * Checks that all HCIM endpoints are reachable with the current SSL and URI
 * configuration. Checks that the database is reachable.
 *
 * @author David Sharpe (david.a.sharpe@cgi.com)
 */
@Component
public class HealthRoute extends SpringRouteBuilder {

    private String hcimRevisePersonEndPointURI;
    private boolean healthCheck;

    private EntityManager em;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HealthRoute.class);

    @Override
    public void configure() throws Exception {

        Properties appProperties = (Properties) new InitialContext().lookup("java:app/death/application_properties");
        healthCheck = Boolean.parseBoolean(appProperties.getProperty("healthCheck"));
        hcimRevisePersonEndPointURI = appProperties.getProperty("hcimRevisePersonEndPointURI");
        
        if (!healthCheck) {
            return;
        }

        hcimRevisePersonEndPointURI = hcimRevisePersonEndPointURI.replaceFirst("https://", "");
        hcimRevisePersonEndPointURI = hcimRevisePersonEndPointURI.replaceFirst("http://", "");

        restConfiguration()
                .component("servlet");
        
        RestDefinition restDefinition = rest("/health").get().produces(MediaType.APPLICATION_JSON_VALUE);
        
        RouteDefinition route = new RouteDefinition();
        route.setRestDefinition(restDefinition);

        route.routeId("health")
                .doTry()
                    .to("http4://" + hcimRevisePersonEndPointURI + "&bridgeEndpoint=true")
                    .setProperty("hcimEndpoint", simple("true"))
                .doCatch(Exception.class)
                    .to("log:ca.bc.gov.moh?level=ERROR&showCaughtException=true")
                    .setProperty("hcimEndpoint", simple("false"))
                .end()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        // Database check
                        boolean database = false;
                        try {
                            em = Persistence.createEntityManagerFactory("ESBPU").createEntityManager();
                            EntityTransaction et = em.getTransaction();
                            et.begin();
                            em.createNativeQuery("select * from dual").getResultList();
                            database = true;
                        } catch (Exception e) {
                            logger.error("Database healthcheck failed", e);
                        }
                        // Build JSON response
                        Boolean hcimEndpoint = exchange.getProperty("hcimEndpoint", Boolean.class);
                        exchange.getMessage().setBody(
                                String.format("{\"hcimEndpoint\": %s, \"database\": %s}", hcimEndpoint, database));
                    }
                });

    }
}
