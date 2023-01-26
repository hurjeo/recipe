package dev.recipe.services;


import dev.recipe.entities.naver.NaverEntity;
import dev.recipe.interfaces.IResult;
import dev.recipe.entities.EmailAuthEntity;
import dev.recipe.entities.UserEntity;
import dev.recipe.entities.kakao.KakaoEntity;
import dev.recipe.enums.CommonResult;
import dev.recipe.enums.member.RegisterResult;
import dev.recipe.enums.member.SendEmailAuthResult;
import dev.recipe.enums.member.VerifyEmailResult;
import dev.recipe.mappers.IMemberMapper;
import dev.recipe.utils.CryptoUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service(value = "dev.recipe.recipermember.services.MemberService")
public class MemberService {
    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;
    private final IMemberMapper memberMapper;

    @Autowired
    public MemberService(JavaMailSender mailSender, TemplateEngine templateEngine, IMemberMapper memberMapper) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.memberMapper = memberMapper;
    }

    @Transactional
    public Enum<? extends IResult> login(UserEntity user) {
        UserEntity userLogin = this.memberMapper.selectUserByEmailPassword(
                user.getEmail(),
                CryptoUtils.hashSha512(user.getPassword()));
        if (userLogin == null) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }

    @Transactional
    public Enum<? extends IResult> register(UserEntity user, EmailAuthEntity emailAuth) {
        EmailAuthEntity emailAuthEntity = this.memberMapper.selectEmailAuthByEmailCodeSalt(emailAuth.getEmail(),
                emailAuth.getCode(),
                emailAuth.getSalt());
        if (emailAuthEntity == null || !emailAuthEntity.getExpiresFlag()) {
            return RegisterResult.EMAIL_NOT_VERIFIED;
        }
        user.setPassword(CryptoUtils.hashSha512(user.getPassword()));
        if (this.memberMapper.insertUser(user) == 0) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }

    @Transactional
    public Enum<? extends IResult> sendEmail(UserEntity user, EmailAuthEntity emailAuth, HttpServletRequest servletRequest) throws NoSuchAlgorithmException, MessagingException {
        UserEntity userEntity = this.memberMapper.selectUserByEmail(user.getEmail());
        if (userEntity != null) {
            return SendEmailAuthResult.EMAIL_DUPLICATED;
        }
        String authCode = RandomStringUtils.randomNumeric(6);
        String authSalt = String.format("%s%s%f",
                user.getEmail(),
                authCode,
                Math.random(),
                Math.random());

        StringBuilder authSaltHashBuilder = new StringBuilder();
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(authSalt.getBytes(StandardCharsets.UTF_8));
        for (byte hashByte : md.digest()) {
            authSaltHashBuilder.append(String.format("%02x", hashByte));
        }
        authSalt = authSaltHashBuilder.toString();

        Date createdOn = new Date();
        Date expiresOn = DateUtils.addMinutes(createdOn, 5);

        emailAuth.setEmail(user.getEmail());
        emailAuth.setCode(authCode);
        emailAuth.setSalt(authSalt);
        emailAuth.setCreatedOn(createdOn);
        emailAuth.setExpiresOn(expiresOn);
        emailAuth.setExpiresFlag(false);
        if (this.memberMapper.insertEmailAuth(emailAuth) == 0) {
            return CommonResult.FAILURE;
        }

        Context context = new Context(); //org.thymeleaf ...
        context.setVariable("code", emailAuth.getCode());
        context.setVariable("domain", String.format("%s://%s",
                servletRequest.getScheme(),
                servletRequest.getServerName()));

        String text = this.templateEngine.process("member/registerEmailAuth", context);
        MimeMessage mail = this.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, "UTF-8");
        helper.setFrom("leeju1316@gmail.com");
        helper.setTo(user.getEmail());
        helper.setSubject("[모두의 레시피] 회원가입 인증 번호");
        helper.setText(text, true);
        this.mailSender.send(mail);

        return CommonResult.SUCCESS;
    }

    public Enum<? extends IResult> verifyEmail(EmailAuthEntity emailAuth) {
        EmailAuthEntity emailAuthEntity = this.memberMapper.selectEmailAuthByEmailCodeSalt(
                emailAuth.getEmail(),
                emailAuth.getCode(),
                emailAuth.getSalt());
        if (emailAuthEntity == null) {
            return CommonResult.FAILURE;
        }
        if (emailAuthEntity.getExpiresOn().compareTo(new Date()) < 0) {
            return VerifyEmailResult.EXPIRED;
        }
        emailAuthEntity.setExpiresFlag(true);
        if (this.memberMapper.updateEmailAuth(emailAuthEntity) == 0) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }

    public Enum<? extends IResult> recoverEmail(UserEntity user) {
        UserEntity userEmail = this.memberMapper.selectUserByAgeNameContact(
                user.getAge(),
                user.getName(),
                user.getContact());
        if (userEmail == null) {
            return CommonResult.FAILURE;
        }
        user.setEmail(userEmail.getEmail());
        return CommonResult.SUCCESS;
    }

    @Transactional
    public Enum<? extends IResult> recoverPasswordSend(EmailAuthEntity emailAuth, HttpServletRequest servletRequest) throws MessagingException {
        UserEntity user = this.memberMapper.selectUserByEmail(emailAuth.getEmail());
        if (user == null) {
            return CommonResult.FAILURE;
        }
        String authCode = RandomStringUtils.randomNumeric(6);
        String authSalt = String.format("%s%s%f%f",
                authCode,
                emailAuth.getEmail(),
                Math.random(),
                Math.random());
        authSalt = CryptoUtils.hashSha512((authSalt));
        Date createdOn = new Date();
        Date expiresOn = DateUtils.addMinutes(createdOn, 5);
        emailAuth.setCode(authCode);
        emailAuth.setSalt(authSalt);
        emailAuth.setCreatedOn(createdOn);
        emailAuth.setExpiresOn(expiresOn);
        emailAuth.setExpiresFlag(false);
        if (this.memberMapper.insertEmailAuth(emailAuth) == 0) {
            return CommonResult.FAILURE;
        }
        Context context = new Context();
        context.setVariable("email", emailAuth.getEmail());
        context.setVariable("code", emailAuth.getCode());
        context.setVariable("salt", emailAuth.getSalt());
        context.setVariable("domain", String.format("%s://%S",
                servletRequest.getScheme(),
                servletRequest.getServerName()));

        String text = this.templateEngine.process("member/recoverPasswordEmailAuth", context);
        MimeMessage mail = this.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, "UTF-8");
        helper.setFrom("leeju1316@gmail.com");
        helper.setTo(emailAuth.getEmail());
        helper.setSubject("[모두의 레시피] 비밀번호 인증 링크");
        helper.setText(text, true);
        this.mailSender.send(mail);

        return CommonResult.SUCCESS;
    }

    @Transactional
    public Enum<? extends IResult> recoverPassword(UserEntity user, EmailAuthEntity emailAuth) {
        EmailAuthEntity existingEmailAuth = this.memberMapper.selectEmailAuthByEmailCodeSalt(
                emailAuth.getEmail(),
                emailAuth.getCode(),
                emailAuth.getSalt());
        if (existingEmailAuth == null || !existingEmailAuth.getExpiresFlag()) {
            return CommonResult.FAILURE;
        }
        UserEntity existingUser = this.memberMapper.selectUserByEmail(existingEmailAuth.getEmail());
        existingUser.setPassword(CryptoUtils.hashSha512(user.getPassword()));
        if (this.memberMapper.updateUser(existingUser) == 0) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }

    public Enum<? extends IResult> recoverPasswordCheck(EmailAuthEntity emailAuth) {
        EmailAuthEntity existingEmailAuth = this.memberMapper.selectEmailAuthByIndex(emailAuth.getIndex());
        if (existingEmailAuth == null || !existingEmailAuth.getExpiresFlag()) {
            return CommonResult.FAILURE;
        }
        emailAuth.setCode(existingEmailAuth.getCode());
        emailAuth.setSalt((existingEmailAuth.getSalt()));
        return CommonResult.SUCCESS;
    }

    @Transactional
    public Enum<? extends IResult> recoverPasswordAuth(EmailAuthEntity emailAuth) {
        EmailAuthEntity existingEmailAuth = this.memberMapper.selectEmailAuthByEmailCodeSalt(
                emailAuth.getEmail(),
                emailAuth.getCode(),
                emailAuth.getSalt());
        if (existingEmailAuth == null) {
            return CommonResult.FAILURE;
        }
        if (new Date().compareTo(existingEmailAuth.getExpiresOn()) > 0) {
            return CommonResult.FAILURE;
        }
        existingEmailAuth.setExpiresFlag(true);
        if (this.memberMapper.updateEmailAuth(existingEmailAuth) == 0) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }

    public UserEntity memberUpdate(String user){
        UserEntity userEntity = this.memberMapper.selectUserByEmail(user);
        return userEntity;
    }

    public String getKakaoAccessToken(String code, HttpServletRequest servletRequest) throws IOException {
        URL url = new URL("https://kauth.kakao.com/oauth/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        int responseCode;
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream())) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
                StringBuilder requestBuilder = new StringBuilder();
                requestBuilder.append("grant_type=authorization_code");
                requestBuilder.append("&client_id=5ed9066f02e45714ed6822817956260d");
////                requestBuilder.append("&redirect_uri=https://ofallrecipe.com/member/kakao");
                requestBuilder.append(String.format("&redirect_uri=%s://%s/member/kakao",
                        servletRequest.getScheme(),
                        servletRequest.getServerName()));
                requestBuilder.append("&code=").append(code);
                bufferedWriter.write(requestBuilder.toString());
                bufferedWriter.flush();
                responseCode = connection.getResponseCode();
            }
            System.out.println("응답 코드 : " + responseCode);
        }
        StringBuilder responseBuilder = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream())) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
            System.out.println("응답 내용 : " + responseBuilder);
        }
        JSONObject responseObject = new JSONObject(responseBuilder.toString());
        return responseObject.getString("access_token");
    }

    public KakaoEntity getKakaoUserInfo(String accessToken) throws IOException {
        URL url = new URL("https://kapi.kakao.com/v2/user/me");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", String.format("Bearer %s", accessToken));
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        System.out.println("응답 코드 : " + responseCode);
        StringBuilder responseBuilder = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream())) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
        }
        System.out.println("응답 내용 : " + responseBuilder);
        JSONObject responseObject = new JSONObject(responseBuilder.toString());
        JSONObject propertyObject = responseObject.getJSONObject("properties");
        JSONObject kakaoObject = responseObject.getJSONObject("kakao_account");
        String id = String.valueOf(responseObject.getLong("id"));
        KakaoEntity kakaos = this.memberMapper.selectUserById(id);
        if (kakaos == null) {
            kakaos = new KakaoEntity();
            kakaos.setId(id);
            kakaos.setEmail(kakaoObject.getString("email"));
            kakaos.setAgeRange(kakaoObject.getString("age_range"));
            kakaos.setGender(kakaoObject.getBoolean("has_gender") ? kakaoObject.getString("gender") : null);
            kakaos.setNickname(propertyObject.getString("nickname"));
            kakaos.setProfileImage(propertyObject.getString("profile_image"));
            kakaos.setThumbnailImage(propertyObject.getString("thumbnail_image"));
            this.memberMapper.insertKakao(kakaos);
        }
        return kakaos;
    }

    public String getNaverAccessToken(String code, HttpServletRequest servletRequest) throws IOException {
        URL url = new URL("https://nid.naver.com/oauth2.0/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        int responseCode;
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream())) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
                StringBuilder requestBuilder = new StringBuilder();
                requestBuilder.append("grant_type=authorization_code");
                requestBuilder.append("&client_id=0sxOPxjHRB5ik5dfST85");
                requestBuilder.append("&client_secret=4X39q7SDzu");
//                requestBuilder.append("&redirect_uri=https://ofallrecipe.com/member/naver");
                requestBuilder.append(String.format("&redirect_uri=%s://%s/member/naver",
                        servletRequest.getScheme(),
                        servletRequest.getServerName()));
                requestBuilder.append("&code=").append(code);
                requestBuilder.append("&state=state");
                bufferedWriter.write(requestBuilder.toString());
                bufferedWriter.flush();
                responseCode = connection.getResponseCode();
            }
            System.out.println("응답 코드 : " + responseCode);
        }
        StringBuilder responseBuilder = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream())) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
            System.out.println("응답 내용 : " + responseBuilder);
        }
        JSONObject resultObject = new JSONObject(responseBuilder.toString());
        return resultObject.getString("access_token");
    }

    public NaverEntity getNaverUserInfo(String accessToken) throws IOException {
        URL url = new URL("https://openapi.naver.com/v1/nid/me");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", String.format("Bearer %s", accessToken));
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        System.out.println("응답 코드 : " + responseCode);
        StringBuilder responseBuilder = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream())) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    responseBuilder.append(line);
                }
            }
        }
        System.out.println("응답 내용 : " + responseBuilder);
        JSONObject responseObject = new JSONObject(responseBuilder.toString());
        JSONObject naverObject = responseObject.getJSONObject("response");
        String id = String.valueOf(naverObject.getString("id"));
        NaverEntity naver = this.memberMapper.selectUserByNaverId(id);
        if (naver == null) {
            naver = new NaverEntity();
            naver.setId(id);
            naver.setEmail(naverObject.getString("email"));
            naver.setAge(naverObject.getString("age"));
            naver.setGender(naverObject.getString("gender"));
            naver.setNickname(naverObject.getString("nickname"));
            naver.setProfileImage(naverObject.getString("profile_image"));
            this.memberMapper.insertNaver(naver);
        }
        return naver;
    }

    public String getGoogleAccessToken(String code, HttpServletRequest servletRequest)throws IOException {
            URL url = new URL("https://oauth2.googleapis.com/token");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            int responseCode;
            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream())) {
                try (BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
                    StringBuilder requestBuilder = new StringBuilder();
                    requestBuilder.append("grant_type=authorization_code");
                    requestBuilder.append("&client_id=608196039616-p7on7p7379h5j4vatkh0uttgg14nrr4u.apps.googleusercontent.com");
                    requestBuilder.append("&client_secret=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");
//                    requestBuilder.append("&redirect_uri=https://ofallrecipe.com/member/google");
                    requestBuilder.append(String.format("&redirect_uri=%s://%s/member/google",
                            servletRequest.getScheme(),
                            servletRequest.getServerName()));
                    requestBuilder.append("&code=").append(code);
                    bufferedWriter.write(requestBuilder.toString());
                    bufferedWriter.flush();
                    responseCode = connection.getResponseCode();
                }
                System.out.println("응답 코드 : " + responseCode);
            }
            StringBuilder responseBuilder = new StringBuilder();
            try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream())) {
                try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                }
                System.out.println("응답 내용 : " + responseBuilder);
            }
            JSONObject resultObject = new JSONObject(responseBuilder.toString());
            return resultObject.getString("access_token");
        }

//    public GoogleEntity getGoogleUserInfo(String accessToken) throws IOException {
//        URL url = new URL("https://people.googleapis.com/v1/people/me");
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestProperty("Authorization", String.format("Bearer %s", accessToken));
//        connection.setRequestMethod("GET");
//        int responseCode = connection.getResponseCode();
//        System.out.println("응답 코드 : " + responseCode);
//        StringBuilder responseBuilder = new StringBuilder();
//        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream())) {
//            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
//                String line;
//                while ((line = bufferedReader.readLine()) != null) {
//                    responseBuilder.append(line);
//                }
//            }
//        }
//        System.out.println("응답 내용 : " + responseBuilder);
//        JSONObject responseObject = new JSONObject(responseBuilder.toString());
//        JSONObject googleObject = responseObject.getJSONObject("personFields");
//        String id = String.valueOf(googleObject.getString("id"));
//        GoogleEntity google = this.memberMapper.selectUserByGoogleId(id);
//        if (google == null) {
//            google = new GoogleEntity();
//            google.setId(id);
//            google.setEmail(googleObject.getString("emailAddresses"));
//            google.setAge(googleObject.getString("ageRanges"));
//            google.setGender(googleObject.getString("genders"));
//            google.setNickname(googleObject.getString("Nickname"));
//            google.setProfileImage(googleObject.getString("photo"));
//            this.memberMapper.insertGoogle(google);
////            System.out.println("응답 내용 : " + googleObject);
//        }
//        return google;
//    }

    }

