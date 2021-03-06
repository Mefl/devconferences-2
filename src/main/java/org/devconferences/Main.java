package org.devconferences;

import net.codestory.http.WebServer;
import net.codestory.http.injection.GuiceAdapter;
import net.codestory.http.templating.ModelAndView;
import org.devconferences.elastic.DeveloppementESNode;
import org.devconferences.events.EventsEndPoint;
import org.devconferences.meetup.MeetupEndPoint;
import org.devconferences.security.Authentication;
import org.devconferences.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    public static final int PORT = 8080;
    public static final String SKIP_CREATE_ES_DEV_NODE = "SKIP_DEV_NODE";

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        WebServer webServer = new WebServer();

        webServer.configure(routes -> {
                    routes.setIocAdapter(new GuiceAdapter());
                    routes.filter(SecurityFilter.class);
                    routes.add(Authentication.class);
                    routes.add(EventsEndPoint.class);
                    routes.add(MeetupEndPoint.class);
                    routes.get("/ping", (context) -> "pong");
                    routes.get("/city/:id", (context, id) -> ModelAndView.of("index"));
                    routes.get("/search", (context) -> ModelAndView.of("index"));
                }
        );
        webServer.start(PORT);

        boolean prodMode = Boolean.parseBoolean(System.getProperty("PROD_MODE", "false"));
        boolean skipDevNode= Boolean.parseBoolean(System.getProperty(SKIP_CREATE_ES_DEV_NODE, "false"));
        if(!prodMode && !skipDevNode){
            LOGGER.info("-D"+ SKIP_CREATE_ES_DEV_NODE +"=true To skip ES dev node creation");
            DeveloppementESNode.createDevNode();
        }
    }

}
