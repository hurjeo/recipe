package dev.recipe.recipemember.mappers;

import dev.recipe.recipemember.entities.EmailAuthEntity;
import dev.recipe.recipemember.entities.UserEntity;
import dev.recipe.recipemember.entities.google.GoogleEntity;
import dev.recipe.recipemember.entities.kakao.KakaoEntity;
import dev.recipe.recipemember.entities.naver.NaverEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IMemberMapper {

    int insertUser(UserEntity user);

    int insertEmailAuth(EmailAuthEntity emailAuth);

    EmailAuthEntity selectEmailAuthByEmailCodeSalt(@Param(value = "email") String email,
                                                   @Param(value = "code") String code,
                                                   @Param(value = "salt") String salt);

    EmailAuthEntity selectEmailAuthByIndex(@Param(value = "index")int index);

    UserEntity selectUserByEmail(@Param(value = "email") String email);

    UserEntity selectUserByEmailPassword(@Param(value = "email") String email,
                                         @Param(value = "password") String password);

    int updateEmailAuth(EmailAuthEntity emailAuthEntity);

    int updateUser(UserEntity user);

    UserEntity selectUserByAgeNameContact(@Param(value = "age") int age,
                                       @Param(value = "name") String name,
                                       @Param(value = "contact") String contact);

    int insertKakao(KakaoEntity kakao);

    KakaoEntity selectUserById(@Param(value = "id") String id);

    int insertNaver(NaverEntity naver);

    NaverEntity selectUserByNaverId(@Param(value = "id")String id);

    int insertGoogle(GoogleEntity google);

    GoogleEntity selectUserByGoogleId(@Param(value = "id") String id);
}
