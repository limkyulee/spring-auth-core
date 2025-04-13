package hello.login.web.filter;

import hello.login.web.SessionConst;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;

@Slf4j
public class LoginCheckFilter implements Filter {

    // 홈, 회원가입, 로그인, 로그아웃, css 관련 파일은 세션 없이 접근 가능하도록 함.
    private static final String[] whiteList = {"/", "/members/add", "/login", "/logout", "/css/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try{
            log.info("인증 체크 로직 시작 URI: {}", requestURI);

            // 인증 체크 대상 URI 일 경우
            if(isLoginCheckPath(requestURI)) {
                log.info("인증 체크 로직 실행 URI {}", requestURI);

                // 헤당 요청에 대한 세션 조회
                HttpSession session = httpRequest.getSession(false);

                if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                    log.info("미인증 사용자 요청 URI {}", requestURI);

                    // 로그인으로 redirect
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);

                    return;
                }
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e;
        }finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }
    }

    /**
     * 화이트 리스트의 경우 인증 체크하지 않음.
     */
    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
    }
}
