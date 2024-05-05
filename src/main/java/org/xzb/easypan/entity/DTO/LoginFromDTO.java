package org.xzb.easypan.entity.DTO;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;


/**
 * email: carise@gmadk.com
 * password: 1ae12b53bbbcbc9c9140b87db6fb935d
 * checkCode: asdfz
 */

@Data
public class LoginFromDTO {
    @NotEmpty
    @Email
    String email;

    @NotEmpty
    String password;

    @NotEmpty
    String checkCode;

}
