package org.xzb.easypan.entity.DTO;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

/**
 * email: carise0102@gmail.com
 * emailCode: s3s1yh
 * nickName: moonchild
 * checkCode: xw25t
 * password: Mwoo1764
 */

@Data
@Accessors(chain = true)
public class RegisterFormDTO {

    @NotEmpty
    @Email(message = "email required")
    String email;
    @NotEmpty
    @Length(min = 6, max = 6)
    String emailCode;
    @NotBlank
    String nickName;
    @NotBlank
    String checkCode;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{8,}$", message = "密码强度不符合规则")
    String password;
}
