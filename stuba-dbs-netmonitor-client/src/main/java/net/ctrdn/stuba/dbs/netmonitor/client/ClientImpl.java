package net.ctrdn.stuba.dbs.netmonitor.client;

import com.jtattoo.plaf.acryl.AcrylLookAndFeel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.ctrdn.stuba.dbs.netmonitor.client.annotation.NetmonitorView;
import net.ctrdn.stuba.dbs.netmonitor.client.exception.InitializationException;
import net.ctrdn.stuba.dbs.netmonitor.client.view.LoadingFrame;
import net.ctrdn.stuba.dbs.netmonitor.client.view.MainFrame;
import net.ctrdn.stuba.dbs.netmonitor.client.view.View;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.reflections.Reflections;

public class ClientImpl implements Client {

    public final static String APP_NAME = "DBS Netmonitor Client";
    public final static String APP_VERSION = "1.0.0";

    private LoadingFrame loadingFrame;
    private MainFrame mainFrame;
    private SessionFactory mysqlSessionFactory;
    private Properties configuration;

    private void configureLogging() {
        org.apache.log4j.PropertyConfigurator.configure("config/log4j.properties");
    }

    private void configurePropertiesConfiguration() throws InitializationException {
        try {
            File propertiesFile = new File("config/configuration.properties");
            if (propertiesFile.exists()) {
                this.configuration = new Properties();
                this.getConfiguration().load(new FileInputStream(propertiesFile));
            } else {
                throw new InitializationException("Configuration file is not present (configuraton.properties)");
            }
        } catch (IOException ex) {
            InitializationException finalEx = new InitializationException("Failed to load configuration file");
            finalEx.addSuppressed(ex);
            throw finalEx;
        }
    }

    private void configureDatabaseConnection() throws InitializationException {
        try {
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
            hbConfig.setProperty("hibernate.connection.CharSet", "utf8");
            hbConfig.setProperty("hibernate.connection.characterEncoding", "utf8");
            hbConfig.setProperty("hibernate.connection.useUnicode", "true");
            hbConfig.addJar(new File(this.getConfiguration().getProperty("database.mappings")));
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(hbConfig.getProperties()).build();
            this.mysqlSessionFactory = hbConfig.buildSessionFactory(serviceRegistry);
        } catch (Exception ex) {
            InitializationException finalEx = new InitializationException("Failed to connect to database");
            finalEx.addSuppressed(ex);
            throw finalEx;
        }
    }

    private void configureLoadingView() {
        this.loadingFrame = new LoadingFrame();
        this.loadingFrame.setVisible(true);
    }

    @Override
    public void start() {
        try {
            try {
                javax.swing.UIManager.setLookAndFeel(new AcrylLookAndFeel());
            } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }

            Random randomDelay = new Random(new Date().getTime());
            this.configureLoadingView();

            // Initialize logging
            Thread.sleep(150 + randomDelay.nextInt(300));
            this.loadingFrame.updateStatus("Initializing logging...", 10, 100);
            this.configureLogging();

            // Load configuration
            Thread.sleep(150 + randomDelay.nextInt(300));
            this.loadingFrame.updateStatus("Loading configuration...", 30, 100);
            this.configurePropertiesConfiguration();

            // Connect to database
            Thread.sleep(150 + randomDelay.nextInt(500));
            this.loadingFrame.updateStatus("Connecting to database...", 40, 100);
            this.configureDatabaseConnection();

            // Prepare GUI
            this.loadingFrame.updateStatus("Initializing graphical user interface...", 50, 100);
            Thread.sleep(150 + randomDelay.nextInt(500));
            this.mainFrame = new MainFrame(this);

            // Load views
            Reflections viewsReflections = new Reflections("net.ctrdn.stuba.dbs.netmonitor.client.view");
            Set<Class<?>> viewClassSet = viewsReflections.getTypesAnnotatedWith(NetmonitorView.class);
            List<Class<?>> viewClassList = new ArrayList<>();
            for (Class<?> viewClass : viewClassSet) {
                viewClassList.add(viewClass);
            }
            Collections.sort(viewClassList, new Comparator<Class<?>>() {

                @Override
                public int compare(Class<?> o1, Class<?> o2) {
                    NetmonitorView annot1 = o1.getDeclaredAnnotation(NetmonitorView.class);
                    NetmonitorView annot2 = o2.getDeclaredAnnotation(NetmonitorView.class);
                    return (annot1.orderKey() < annot2.orderKey()) ? -1 : (annot1.orderKey() == annot2.orderKey()) ? 0 : 1;
                }
            });

            int setSize = viewClassSet.size();
            int stepPerView = 50 / setSize;
            int i = 1;
            for (Class<?> viewClassUncasted : viewClassList) {
                NetmonitorView viewAnnotation = viewClassUncasted.getDeclaredAnnotation(NetmonitorView.class);
                this.loadingFrame.updateStatus("Loading view " + viewAnnotation.name() + " v" + viewAnnotation.version() + " by " + viewAnnotation.author() + "...", 50 + (i * stepPerView - 2), 100);
                Thread.sleep(300 + randomDelay.nextInt(500));
                if (View.class.isAssignableFrom(viewClassUncasted)) {
                    try {
                        Class[] constrParams = new Class[1];
                        constrParams[0] = Client.class;
                        Constructor viewConstructor = viewClassUncasted.getDeclaredConstructor(constrParams);
                        View view = (View) viewConstructor.newInstance(new Object[]{this});
                        this.mainFrame.addView(view);
                    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                        InitializationException finalEx = new InitializationException("Failed to create view");
                        finalEx.addSuppressed(ex);
                        throw finalEx;
                    }
                }
                i++;
            }

            // Load main frame
            this.loadingFrame.updateStatus("Starting application...", 100, 100);
            Thread.sleep(800 + randomDelay.nextInt(1000));
            this.loadingFrame.setVisible(false);
            this.mainFrame.setVisible(true);

        } catch (InterruptedException | InitializationException ex) {
            this.handleException(ex);
        }
    }

    @Override
    public SessionFactory getMysqlSessionFactory() {
        return this.mysqlSessionFactory;
    }

    @Override
    public Properties getConfiguration() {
        return this.configuration;
    }

    @Override
    public void handleException(Throwable thrwbl) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        thrwbl.printStackTrace(ps);
        JOptionPane.showMessageDialog(this.loadingFrame, baos.toString(), "Exception caught", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    @Override
    public String getApplicationName() {
        return ClientImpl.APP_NAME;
    }

    @Override
    public String getApplicationVersion() {
        return ClientImpl.APP_VERSION;
    }

    @Override
    public MainFrame getApplicationMainFrame() {
        return this.mainFrame;
    }
}
