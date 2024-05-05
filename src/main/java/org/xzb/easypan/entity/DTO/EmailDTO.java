package org.xzb.easypan.entity.DTO;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


/**
 * email: carise0102@gmail.com
 * checkCode: 5ogx
 * type: 0
 */
@Data
public class EmailDTO {
    @NotEmpty
    @Email(message = "email address required")
    String email;
    @NotEmpty
    String checkCode;

    Integer type;

}
