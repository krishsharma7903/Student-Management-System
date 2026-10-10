package com.sms.util;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Embedded Apache Tomcat 10.1+ server launcher.
 * Enables instant one-click execution of the WAR web application without manual Tomcat configuration.
 * 
 * Access URL: http://localhost:8080/StudentManagementSystem/
 */
public class EmbeddedTomcatServer {

    private static final Logger LOGGER = Logger.getLogger(EmbeddedTomcatServer.class.getName());
    private static final int PORT = 8080;
    private static final String CONTEXT_PATH = "/StudentManagementSystem";

    public static void main(String[] args) throws Exception {
        System.out.println("================================================================================");
        System.out.println("   STARTING APACHE TOMCAT 10.1+ EMBEDDED WEB SERVER");
        System.out.println("================================================================================");

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(PORT);
        tomcat.getConnector(); // Initialize default HTTP NIO connector

        Path baseDir = Paths.get("target", "tomcat-embed");
        Files.createDirectories(baseDir);
        tomcat.setBaseDir(baseDir.toFile().getAbsolutePath());

        File webappDir = new File("src/main/webapp");
        if (!webappDir.exists()) {
            webappDir = new File("webapp");
        }

        LOGGER.info("Configuring WebApp Context with docBase: " + webappDir.getAbsolutePath());

        StandardContext context = (StandardContext) tomcat.addWebapp(CONTEXT_PATH, webappDir.getAbsolutePath());
        context.setReloadable(true);
        context.setParentClassLoader(EmbeddedTomcatServer.class.getClassLoader());

        // Bind target/classes to /WEB-INF/classes so servlets and listeners are scanned
        File classesDir = new File("target/classes");
        if (classesDir.exists()) {
            WebResourceRoot resources = new StandardRoot(context);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    classesDir.getAbsolutePath(), "/"));
            context.setResources(resources);
        }

        // Also bind root context redirecting to /StudentManagementSystem
        StandardContext rootContext = (StandardContext) tomcat.addWebapp("", webappDir.getAbsolutePath());
        rootContext.setReloadable(true);
        rootContext.setParentClassLoader(EmbeddedTomcatServer.class.getClassLoader());
        if (classesDir.exists()) {
            WebResourceRoot rootResources = new StandardRoot(rootContext);
            rootResources.addPreResources(new DirResourceSet(rootResources, "/WEB-INF/classes",
                    classesDir.getAbsolutePath(), "/"));
            rootContext.setResources(rootResources);
        }

        tomcat.start();

        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("   SERVER RUNNING SUCCESSFULLY!");
        System.out.println("   Open URL: http://localhost:" + PORT + CONTEXT_PATH);
        System.out.println("   Or Root:  http://localhost:" + PORT + "/");
        System.out.println("   Login:    admin / admin123");
        System.out.println("--------------------------------------------------------------------------------\n");

        tomcat.getServer().await();
    }
}
