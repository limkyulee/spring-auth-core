package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute LoginForm loginForm) {
        return "/login/loginForm";
    }

//    @PostMapping("/login")
    public String loginUsingCookie(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletResponse response) {
        // 입력값 오류 검증.
        if(bindingResult.hasErrors()) {
            log.error(bindingResult.getFieldError().getDefaultMessage());
            return "/login/loginForm";
        }

        // 로그인 실행
        Member login = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        // 로그인 실패 검증
        if(login == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지않습니다.");
            return "/login/loginForm";
        }

        // 로그인 성공 로직

        // 쿠키에 시간 정보를 주지 않으면 브라우저를 닫을 때 쿠키 만료(세션쿠키)
        Cookie idCookie = new Cookie("memberId", String.valueOf(login.getId()));
        response.addCookie(idCookie);

        return "redirect:/";
    }

//    @PostMapping("/login")
    public String loginUsingSession(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletResponse response) {
        // 입력값 오류 검증.
        if(bindingResult.hasErrors()) {
            log.error(bindingResult.getFieldError().getDefaultMessage());
            return "/login/loginForm";
        }

        // 로그인 실행
        Member login = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        // 로그인 실패 검증
        if(login == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지않습니다.");
            return "/login/loginForm";
        }

        // 로그인 성공 로직
        // REFACTOR : 세션 관리자를 통해 세션을 생성하고, 회원 데이터 보관
        sessionManager.createSession(login, response);

        return "redirect:/";
    }

    @PostMapping("/login")
    public String loginUsingHttpSession(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletRequest request) {
        // 입력값 오류 검증.
        if(bindingResult.hasErrors()) {
            log.error(bindingResult.getFieldError().getDefaultMessage());
            return "/login/loginForm";
        }

        // 로그인 실행
        Member login = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        // 로그인 실패 검증
        if(login == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지않습니다.");
            return "/login/loginForm";
        }

        // 로그인 성공 로직
        // REFACTOR : HttpSession 사용하도록 로직 수정.
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, login);

        return "redirect:/";
    }

//    @PostMapping("/logout")
    public String logoutUsingCookie(HttpServletResponse response) {
        expireCookie(response, "memberId");

        return "redirect:/";
    }

//    @PostMapping("/logout")
    public String logoutUsingSession(HttpServletRequest request) {
        sessionManager.expireSession(request);

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutUsingHttpSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }

        return "redirect:/";
    }

    private static void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie idCookie = new Cookie(cookieName, null);
        idCookie.setMaxAge(0); // 해당 쿠키의 종료 날짜를 0으로 지정 (즉시 종료)

        response.addCookie(idCookie);
    }
}
