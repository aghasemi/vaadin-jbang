
//JAVA 21

//DEPS com.vaadin:vaadin:24.2.0
//DEPS jakarta.servlet:jakarta.servlet-api:6.0.0
//DEPS org.slf4j:slf4j-simple:2.0.9
//DEPS org.eclipse.jetty:jetty-webapp:11.0.17
//DEPS org.eclipse.jetty.websocket:websocket-jakarta-server:11.0.17

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.theme.Theme;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("Hello World")
@Route(value = "")
public class VaadinWebApp extends HorizontalLayout implements AppShellConfigurator {

    private static final String PUBLIC_FOLDER = "public";

    public static void main(String[] args) throws Exception {
        // We need a dummy, empty POM file to trick Vaadin into working
        var pomPath = Path.of("./pom.xml");
        Files.writeString(pomPath, "<project></project>");

        final var context = createWebAppContext();
        Server server = new Server(9090);
        server.setHandler(context);
        server.start();
        server.join();

    }

    // copied from: https://github.com/mvysny/vaadin-boot/tree/main/vaadin-boot
    private static WebAppContext createWebAppContext() throws IOException {
        final WebAppContext context = new WebAppContext();
        Files.createDirectories(Paths.get(PUBLIC_FOLDER));
        context.setBaseResource(Resource.newResource(PUBLIC_FOLDER));
        context.addServlet(VaadinServlet.class, "/*");
        // this will properly scan the classpath for all @WebListeners,
        // including the most important
        // com.vaadin.flow.server.startup.ServletContextListeners.
        // See also https://mvysny.github.io/vaadin-lookup-vs-instantiator/
        context.setAttribute(
                "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*\\.jar|.*/classes/.*");
        context.setConfigurationDiscovered(true);
        context.getServletContext().setExtendedListenerTypes(true);

        return context;
    }

    private TextField name;
    private Button sayHello;

    public VaadinWebApp() {
        name = new TextField("Your name");
        sayHello = new Button("Say hello");
        sayHello.addClickListener(e -> {
            Notification.show("Hello " + name.getValue());
        });
        sayHello.addClickShortcut(Key.ENTER);

        setMargin(true);
        setVerticalComponentAlignment(Alignment.END, name, sayHello);

        add(name, sayHello);
    }

}
