package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.argumentresolver.Login;
import hello.login.web.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

//    @GetMapping("/")
    public String home() {
        return "home";
    }

//    @GetMapping("/")
    public String homeLoginUsingCookie(@CookieValue(name="memberId", required = false) Long memberId, Model model) {
        if(memberId == null) {
            return "home";
        }

        Member loginMember = memberRepository.findById(memberId);
        if(loginMember == null) {
            return "home";
        }

        // 로그인 시, 로그인 사용자 전용 화면으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

//    @GetMapping("/")
    public String homeLoginUsingSession(HttpServletRequest request, Model model) {
        // 세션 관리자에 저장된 회원 정보 조회
        Member member = (Member) sessionManager.getSession(request);

        if(member == null) {
            return "home";
        }

        // 로그인 시, 로그인 사용자 전용 화면으로 이동
        model.addAttribute("member", member);
        return "loginHome";
    }

//    @GetMapping("/")
    public String homeLoginUsingHttpSession(HttpServletRequest request, Model model) {
        // 세션 관리자에 저장된 회원 정보 조회
        HttpSession session = request.getSession(false);

        if(session == null){
            return "home";
        }

         Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            return "home";
        }

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }


//  REFACTOR : @SessionAttribute 사용하도록 로직 수정. | 세션을 생성하는 기능은 없고 세션을 가져오기만 함.
//    @GetMapping("/")
    public String homeLoginUsingSpring(@SessionAttribute(name=SessionConst.LOGIN_MEMBER, required = false) Member member, Model model) {
        if(member == null) {
            return "home";
        }

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", member);
        return "loginHome";
    }

//  REFACTOR : ArgumentResolver 사용하도록 로직 수정. | Login 전용 애노테이션 생성하여 사용. (WebConfig 에 등록하여 사용)
    @GetMapping("/")
    public String homeLoginUsingArgumentResolver(@Login Member member, Model model) {
        if(member == null) {
            return "home";
        }

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", member);
        return "loginHome";
    }
}