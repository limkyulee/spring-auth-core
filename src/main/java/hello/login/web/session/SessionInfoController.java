package hello.login.web.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
public class SessionInfoController {

    @GetMapping("/session-info")
    public String sessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "no session";
        }

        session.getAttributeNames().asIterator()
                .forEachRemaining(name -> log.info("{}={}", name, session.getAttribute(name)));

        log.info("{}", session.getId());
        log.info("{}", session.getMaxInactiveInterval());
        log.info("{}", new Date(session.getCreationTime()));
        log.info("{}", new Date(session.getLastAccessedTime()));
        log.info("{}", session.isNew());


        return "session print";
    }
}
