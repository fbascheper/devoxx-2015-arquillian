/*
 * Copyright 2009-2015 PIANOo; TenderNed programma.
 */
package nl.famscheper.devoxx.test.util;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * Deployment support utility for Arquillian tests.
 *
 * @author Erik-Berndt Scheper
 * @since 06-11-2015
 */
public final class ArquillianDeploymentSupport {

    private ArquillianDeploymentSupport() {
        // prevent instantiation.
    }

    /**
     * Create a basic {@code Integration-test} based test archive, which can be modified by the Arquillian test to be run.
     *
     * @param archiveName name of the archive to create
     * @return the archive to be deployed
     */
    public static WebArchive createIntegrationTestArchive(String archiveName) {

        WebArchive result = ShrinkWrap.create(WebArchive.class, archiveName)
                .addPackages(true, "nl/famscheper/devoxx/model")
                .addPackages(true, "nl/famscheper/devoxx/util");

        result.addClasses(org.junit.runner.notification.Failure.class)
                .addAsWebInfResource("test-jboss-deployment-structure.xml", "jboss-deployment-structure.xml")
                .addAsWebInfResource("beans.xml", "beans.xml");

        result.addAsWebInfResource(ArquillianDeploymentSupport.class.getClassLoader().getResource("META-INF/persistence.xml"),
                "classes/META-INF/persistence.xml");
        result.addAsWebInfResource(ArquillianDeploymentSupport.class.getClassLoader().getResource("META-INF/orm.xml"), "classes/META-INF/orm.xml");

        return result;
    }

}
