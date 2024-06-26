package org.xzb.easypan.entity.VO;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 该数据用于返回前端
 */

@Data
@Accessors(chain = true)
public class UserDTO {

    private String userId;

    private String nickName;


    private String email;

    private String qqAvatar;
    private Date joinTime;
    private Date lastLoginTime;

    /**
     * 0:禁用 1:正常
     */
    private Integer status;

    private Long useSpace;

    /**
     * 总空间单位byte
     */
    private Long totalSpace;
}
