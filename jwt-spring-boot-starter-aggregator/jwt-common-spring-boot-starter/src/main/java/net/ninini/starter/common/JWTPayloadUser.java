package net.ninini.starter.common;

import lombok.Data;

import java.util.List;

/**
 * @ClassName: JWTUser
 * @ProjectName scaffold
 * @Description: todo
 * @Author HanYu
 * @Date 2021/7/12 12:18
 * @Version 1.0.0
 */

@Data
public class JWTPayloadUser {

    public JWTPayloadUser(String userid, List<String> authorities) {
        this.userid = userid;
        this.authorities = authorities;
    }

    public JWTPayloadUser(String userid) {
        this.userid = userid;
    }

    public JWTPayloadUser() {
    }

    private String userid;

    private List<String> authorities;

}
