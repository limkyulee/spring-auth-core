package hello.login.web.session;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//  FIXME : 직접 구현하는 Session
@Component
public class SessionManager {

//  PLUS : ConcurrentHashMap | 동시 요청에 안전한 ConcurrentHashMap 사용.
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();
    public static final String SESSION_COOKIE_NAME = "mySessionId";
    /**
     * 세션 생성
     * sessionId 생성. (임의의 추정 불가능한 랜덤 값 | uuid)
     * 세션 저장소에 sessionId 와 보관할 값 저장.
     * sessionId 로 응답 쿠키를 생성해서 클라이언트에 전달.
     */
    public void createSession(Object value, HttpServletResponse response) {
        // sessionId 를 생성하고, 값을 세션 저장소에 저장.
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, value);

        // 쿠키 생성 후 response 할당하여 클라이언트에 쿠카 전달.
        Cookie mySessonCookie = new Cookie(SESSION_COOKIE_NAME, response.encodeRedirectURL(sessionId));
        response.addCookie(mySessonCookie);
    }

    private Cookie findCookie(HttpServletRequest request, String cookieName) {
        if(request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(cookieName))
                .findAny()
                .orElse(null);
    }
    /**
     * 세션 조회
     */
    public Object getSession(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if(sessionCookie == null){
            return null;
        }

        return sessionStore.get(sessionCookie.getValue());
    }

    /**
     * 세션 만료
     */
    public void expireSession(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if(sessionCookie != null){
            sessionStore.remove(sessionCookie.getValue());
        }
    }
}
