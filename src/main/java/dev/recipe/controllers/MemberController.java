package dev.recipe.controllers;


import dev.recipe.entities.EmailAuthEntity;
import dev.recipe.entities.UserEntity;
import dev.recipe.entities.kakao.KakaoEntity;
import dev.recipe.entities.naver.NaverEntity;
import dev.recipe.enums.CommonResult;
import dev.recipe.services.MemberService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Controller(value = "dev.recipe.recipemember.controllers.MemberController")
@RequestMapping(value = "/member")
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @RequestMapping(value = "login",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getLogin() {
        ModelAndView modelAndView = new ModelAndView("member/login");
        return modelAndView;
    }

    @RequestMapping(value = "login",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postLogin(HttpSession session, UserEntity user) {
        Enum<?> result = this.memberService.login(user);
        JSONObject responseObject = new JSONObject();
        if (result == CommonResult.SUCCESS) {
            session.setAttribute("user", user);
            System.out.println("로그인 성공");
        } else {
            System.out.println("로그인 실패");
        }
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    @RequestMapping(value = "register",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRegister() {
        ModelAndView modelAndView = new ModelAndView("member/register");

        return modelAndView;
    }

    @RequestMapping(value = "register",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRegister(UserEntity user, EmailAuthEntity emailAuth) {
        Enum<?> result = this.memberService.register(user, emailAuth);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    @RequestMapping(value = "email",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postEmail(UserEntity user, EmailAuthEntity emailAuth, HttpServletRequest servletRequest) throws NoSuchAlgorithmException, MessagingException {
        Enum<?> result = this.memberService.sendEmail(user, emailAuth, servletRequest);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("salt", emailAuth.getSalt());
        }
        return responseObject.toString();
    }

    @RequestMapping(value = "email",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchEmail(EmailAuthEntity emailAuth) {
        Enum<?> result = this.memberService.verifyEmail(emailAuth);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());

        return responseObject.toString();
    }

    @RequestMapping(value = "recoverEmail",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public ModelAndView getRecoverEmail() {
        ModelAndView modelAndView = new ModelAndView("member/recoverEmail");
        return modelAndView;
    }

    @RequestMapping(value = "recoverEmail",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRecoverEmail(UserEntity user) {
        Enum<?> result = this.memberService.recoverEmail(user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("email", user.getEmail());
        }
        return responseObject.toString();
    }

    @RequestMapping(value = "recoverPassword",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public ModelAndView getRecoverPassword() {
        ModelAndView modelAndView = new ModelAndView("member/recoverPassword");
        return modelAndView;
    }

    @RequestMapping(value = "recoverPassword",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRecoverPassword(EmailAuthEntity emailAuth, HttpServletRequest servletRequest) throws MessagingException {
        Enum<?> result = this.memberService.recoverPasswordSend(emailAuth, servletRequest);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("index", emailAuth.getIndex());
        }

        return responseObject.toString();
    }

    @RequestMapping(value = "recoverPassword",
            method = RequestMethod.PATCH,
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String patchRecoverPassword(UserEntity user, EmailAuthEntity emailAuth) {
        Enum<?> result = this.memberService.recoverPassword(user, emailAuth);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());

        return responseObject.toString();
    }

    @RequestMapping(value = "recoverPasswordEmail",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String recoverPasswordCheck(EmailAuthEntity emailAuth) {
        Enum<?> result = this.memberService.recoverPasswordCheck(emailAuth);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("code", emailAuth.getCode());
            responseObject.put("salt", emailAuth.getSalt());
        }
        return responseObject.toString();
    }

    @RequestMapping(value = "recoverPasswordEmail",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public ModelAndView getRecoverPasswordEmail(EmailAuthEntity emailAuth) {
        Enum<?> result = this.memberService.recoverPasswordAuth(emailAuth);
        ModelAndView modelAndView = new ModelAndView("member/recoverPasswordEmail");
        modelAndView.addObject("result", result.name());

        return modelAndView;
    }

    @RequestMapping(value = "memberUpdate", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public ModelAndView getMemberUpdate(String user) {
        ModelAndView modelAndView = new ModelAndView("member/memberUpdate");
        UserEntity userEntity = this.memberService.memberUpdate(user);
        modelAndView.addObject("userEntity", userEntity);
        return modelAndView;
    }

//    @RequestMapping(value = "memberUpdate", method = RequestMethod.POST,
//    produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public String postMemberUpdate(UserEntity user){
//        Enum<?> result = this.memberService.memberUpdate(user);
//        JSONObject responseObject = new JSONObject();
//        responseObject.put("result", result.name().toLowerCase());
//        if (result == CommonResult.SUCCESS) {
//            responseObject.put("index", emailAuth.getIndex());
//        }
//
//        return responseObject.toString();
//    }

    @GetMapping(value = "kakao", produces = MediaType.TEXT_PLAIN_VALUE)
    public ModelAndView getKakao(@RequestParam(value = "code") String code,
                                 @RequestParam(value = "error", required = false) String error,
                                 @RequestParam(value = "error_description", required = false) String errorDescription, HttpServletRequest servletRequest,
                                 HttpSession session) throws IOException {
        String accessToken = this.memberService.getKakaoAccessToken(code, servletRequest);
        KakaoEntity kakaos = this.memberService.getKakaoUserInfo(accessToken);
        session.setAttribute("kakao", kakaos);
        return new ModelAndView("member/kakao");
    }

    @GetMapping(value = "naver", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getNaver(@RequestParam(value = "code") String code,
                                 @RequestParam(value = "error", required = false) String error,
                                 @RequestParam(value = "error_description", required = false) String errorDescription, HttpServletRequest servletRequest,
                                 HttpSession session) throws IOException {
        String accessToken = this.memberService.getNaverAccessToken(code, servletRequest);
        NaverEntity naver = this.memberService.getNaverUserInfo(accessToken);
        session.setAttribute("naver", naver);
        return new ModelAndView("member/naver");
    }

    @GetMapping(value = "google", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getGoogle(@RequestParam(value = "code") String code,
                                  @RequestParam(value = "error", required = false) String error,
                                  @RequestParam(value = "error_description", required = false) String errorDescription, HttpServletRequest servletRequest,
                                  HttpSession session) throws IOException {
        String accessToken = this.memberService.getGoogleAccessToken(code, servletRequest);
//        GoogleEntity google = this.memberService.getGoogleUserInfo(accessToken);
//        session.setAttribute("google", google);
        return new ModelAndView("member/google");
    }
}

