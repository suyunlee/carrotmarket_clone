package oreumi.group2.carrotClone.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import oreumi.group2.carrotClone.model.enums.AuthProvider;
import oreumi.group2.carrotClone.validation.ValidPassword;
import oreumi.group2.carrotClone.validation.ValidUserrname;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "아이디는 필수 입력입니다!")
    @ValidUserrname
    private String username;    // 로그인용 아이디 (이멜)

    @NotBlank (message = "비밀번호는 필수 입력입니다!")
    @ValidPassword
    private String password;    //비밀번호

    private String nickname;    // 사용자 닉네임
    private String phoneNumber; // 전화번호
    private String location;   // 위치
    private String role;    // 역할
    private String status;  // 회원 상태
    private Boolean NeighborhoodVerified;   //동네 인증 여부
    private String NeighborhoodName;    // 동네 이름
    private AuthProvider provider;  // 로그인 방식 구분 로칼 or google
    private String providerId; //소셜로그인 (구글) 고유 ID
}