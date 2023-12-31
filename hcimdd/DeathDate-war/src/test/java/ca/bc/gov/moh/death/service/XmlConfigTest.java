package ca.bc.gov.moh.death.service;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Checks that the XML configuration files are well-formed.
 *
 * @author David Sharpe (david.a.sharpe@cgi.com)
 */
@RunWith(Parameterized.class)
public class XmlConfigTest {

    @Parameter
    @SuppressWarnings("PublicField")
    public String filename;

    @Parameters(name = "{0}")
    public static String[] data() {
        return new String[]{
            "src/main/resources/applicationContext.xml",
            "src/main/resources/deathEnvironmentSpecific.xml",
            "src/main/resources/logback.xml",
            "src/main/webapp/WEB-INF/glassfish-resources.xml",
            "src/main/webapp/WEB-INF/web.xml"
        };
    }

    @Test
    public void testXmlConfig() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        DocumentBuilder builder = factory.newDocumentBuilder();

        builder.setErrorHandler(new DefaultHandler());
        // The "parse" will throw an exception if the document is not well formed.
        builder.parse(new InputSource(filename));
    }

}
