package org.xzb.easypan.utils;

import org.xzb.easypan.entity.DTO.UserDTO;
import org.xzb.easypan.entity.VO.UserVO;

public class UserHolder {
    private static final ThreadLocal<UserVO> tl = new ThreadLocal<>();

    public static void saveUser(UserVO user) {
        tl.set(user);
    }

    public static UserVO getUser() {
        return tl.get();
    }

    public static void removeUser() {
        tl.remove();
    }
}
