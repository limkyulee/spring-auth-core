package hello.login.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
       log.info("LogFilter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("LogFilter doFilter");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        // 요청을 구분하기 위한 UUID 생성.
        String uuid = UUID.randomUUID().toString();

        try{
            log.info("REQUEST [{}][{}]",uuid,requestURI);
            // PLUS : doFilter() | 미실행 시, 컨트롤러와 서블릿 실행 안함. | 필수로 실행해야함.
            chain.doFilter(request, response);
        }catch(Exception e){
            throw e;
        }finally {
            log.info("REQUEST [{}][{}]",uuid,requestURI);
        }


    }

    @Override
    public void destroy() {
        log.info("LogFilter destroy");
    }
}
