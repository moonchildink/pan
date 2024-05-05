package org.xzb.easypan.entity.DTO;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Data
public class UserDTO {

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

    String user_id;
}
