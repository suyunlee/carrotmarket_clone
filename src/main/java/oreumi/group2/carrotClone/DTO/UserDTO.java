package oreumi.group2.carrotClone.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserDTO {
    private String username;    // 로그인용 아이디 (이멜)
    private String nickname;    // 사용자 닉네임
    private String phoneNumber; // 전화번호
    private String locattion;   // 위치
    private String role;    // 역할
    private String status;  // 회원 상태
    private Boolean NeighborhoodVerified;   //동네 인증 여부
    private String NeighborhoodName;    // 동네 이름
}
