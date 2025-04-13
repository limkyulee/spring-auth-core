package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute LoginForm loginForm) {
        return "/login/loginForm";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletResponse response) {
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

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expireCookie(response, "memberId");

        return "redirect:/";
    }

    private static void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie idCookie = new Cookie(cookieName, null);
        idCookie.setMaxAge(0); // 해당 쿠키의 종료 날짜를 0으로 지정 (즉시 종료)

        response.addCookie(idCookie);
    }
}
