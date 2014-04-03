package net.ctrdn.stuba.dbs.netmonitor.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmDevice;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbeType;
import net.ctrdn.stuba.dbs.netmonitor.server.annotation.NetmonitorProbe;
import net.ctrdn.stuba.dbs.netmonitor.server.dc.DeviceController;
import net.ctrdn.stuba.dbs.netmonitor.server.exception.InitializationException;
import net.ctrdn.stuba.dbs.netmonitor.server.logging.LogProxy;
import net.ctrdn.stuba.dbs.netmonitor.server.logging.LogSeverity;
import net.ctrdn.stuba.dbs.netmonitor.server.probe.Probe;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.reflections.Reflections;

public class ServerDaemon implements Runnable, Server {

    public final static String APP_NAME = "DBS Netmonitor Server";
    public final static String APP_VERSION = "1.0.0";

    private SessionFactory mysqlSessionFactory;
    private Properties configuration;
    private LogProxy log;
    private List<Class<? extends Probe>> probeTypeList;
    private List<DeviceController> deviceControllerList;

    public ServerDaemon() {
    }

    private void configureLogging() {
        this.log = new LogProxy() {

            @Override
            public void message(LogSeverity severity, String message) {
                System.out.println(new Date().toString() + " [" + severity.name() + "] " + message);
            }

            @Override
            public void exception(Throwable thrwbl) {
                thrwbl.printStackTrace();
            }
        };
        org.apache.log4j.PropertyConfigurator.configure("config/log4j.properties");
    }

    private void configurePropertiesConfiguration() throws InitializationException {
        try {
            File propertiesFile = new File("config/configuration.properties");
            if (propertiesFile.exists()) {
                this.configuration = new Properties();
                this.getConfiguration().load(new FileInputStream(propertiesFile));
                this.getLog().message(LogSeverity.INFO, "Loaded configuration from " + propertiesFile.getAbsolutePath());
            } else {
                throw new InitializationException("Configuration file is not present (configuraton.properties)");
            }
        } catch (IOException ex) {
            InitializationException finalEx = new InitializationException("Failed to load configuration file");
            finalEx.addSuppressed(ex);
            throw finalEx;
        }
    }

    private void configureDatabaseConnection() {
        Configuration hbConfig = new Configuration();
        hbConfig.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        hbConfig.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        hbConfig.setProperty("hibernate.connection.url", "jdbc:mysql://" + this.getConfiguration().getProperty("database.mysql.host") + ":" + this.getConfiguration().getProperty("database.mysql.port") + "/" + this.getConfiguration().getProperty("database.mysql.database") + "?zeroDateTimeBehavior=convertToNull");
        hbConfig.setProperty("hibernate.connection.username", this.getConfiguration().getProperty("database.mysql.username"));
        hbConfig.setProperty("hibernate.connection.password", this.getConfiguration().getProperty("database.mysql.password"));
        hbConfig.setProperty("hibernate.connection.provider_class", "org.hibernate.connection.C3P0ConnectionProvider");
        hbConfig.setProperty("hibernate.c3p0.max_size", "256");
        hbConfig.setProperty("hibernate.c3p0.min_size", "8");
        hbConfig.setProperty("hibernate.c3p0.timeout", "1800");
        hbConfig.setProperty("hibernate.c3p0.max_statements", "50");
        hbConfig.setProperty("hibernate.c3p0.idle_test_period", "3000");
        hbConfig.setProperty("hibernate.c3p0.preferredTestQuery", "SELECT 1");
        hbConfig.setProperty("hibernate.c3p0.validate", "true");
        hbConfig.setProperty("hibernate.c3p0.testConnectionOnCheckout", "true");
        hbConfig.addJar(new File(this.getConfiguration().getProperty("database.mappings")));
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(hbConfig.getProperties()).build();
        this.mysqlSessionFactory = hbConfig.buildSessionFactory(serviceRegistry);
        this.getLog().message(LogSeverity.INFO, "Connected to MySQL database at " + this.getConfiguration().getProperty("database.mysql.host"));
    }

    private void enumerateProbes() {
        this.probeTypeList = new ArrayList<>();

        Session mysqlSession = this.mysqlSessionFactory.openSession();
        mysqlSession.beginTransaction();
        List<NmProbeType> currentProbeTypeList = mysqlSession.createSQLQuery("SELECT * FROM `nm_probe_type`").addEntity(NmProbeType.class).list();

        Reflections reflections = new Reflections("net.ctrdn.stuba.dbs.netmonitor.server.probe");
        Set<Class<?>> foundProbes = reflections.getTypesAnnotatedWith(NetmonitorProbe.class);
        foundProbes.stream().forEach((probeClass) -> {
            Class<? extends Probe> probeClazz = (Class<? extends Probe>) probeClass;
            if (Probe.class.isAssignableFrom(probeClazz)) {
                this.probeTypeList.add(probeClazz);
                this.getLog().message(LogSeverity.INFO, "Registered probe " + probeClazz.getName());
                boolean foundInDatabase = false;
                for (NmProbeType type : currentProbeTypeList) {
                    if (type.getProbeClasspath().equals(probeClazz.getName())) {
                        foundInDatabase = true;
                        break;
                    }
                }
                if (!foundInDatabase) {
                    NetmonitorProbe annot = probeClazz.getDeclaredAnnotation(NetmonitorProbe.class);
                    mysqlSession.createSQLQuery("INSERT INTO `nm_probe_type` (`probe_name`, `probe_description`, `probe_classpath`) VALUES (\"" + annot.name() + "\", \"" + annot.description() + "\", \"" + probeClazz.getName() + "\")").executeUpdate();
                }
            } else {
                this.getLog().message(LogSeverity.ERROR, "Probe type " + probeClazz.getName() + " not registered - invalid parent type");
            }
        });
        mysqlSession.getTransaction().commit();
        mysqlSession.disconnect();
    }

    private void startDeviceControllers() {
        this.deviceControllerList = new ArrayList<>();
        Session mysqlSession = this.mysqlSessionFactory.openSession();
        mysqlSession.beginTransaction();
        List<NmDevice> deviceList = mysqlSession.createSQLQuery("SELECT * FROM `nm_device`").addEntity(NmDevice.class).list();
        mysqlSession.getTransaction().commit();
        mysqlSession.disconnect();
        for (NmDevice deviceRecord : deviceList) {
            this.getLog().message(LogSeverity.NOTICE, "Starting device controller for " + deviceRecord.getDeviceName() + " (" + deviceRecord.getIpv4Address() + ")");
            DeviceController dc = new DeviceController(this, deviceRecord);
            new Thread(dc).start();
            this.deviceControllerList.add(dc);
        }
    }

    @Override
    public void run() {
        this.configureLogging();
        try {
            this.getLog().message(LogSeverity.INFO, ServerDaemon.APP_NAME + " v" + ServerDaemon.APP_VERSION + " is loading");
            this.configurePropertiesConfiguration();
            this.configureDatabaseConnection();
            this.enumerateProbes();
            this.startDeviceControllers();
        } catch (InitializationException ex) {
            this.getLog().exception(ex);
        }
    }

    @Override
    public Probe newProbe(String classpath) {
        return null;
    }

    @Override
    public SessionFactory getMysqlSessionFactory() {
        return mysqlSessionFactory;
    }

    @Override
    public Properties getConfiguration() {
        return configuration;
    }

    @Override
    public LogProxy getLog() {
        return log;
    }
}
