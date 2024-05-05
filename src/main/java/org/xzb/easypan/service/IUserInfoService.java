package org.xzb.easypan.service;

import org.xzb.easypan.entity.DTO.EmailDTO;
import org.xzb.easypan.entity.DTO.Result;
import org.xzb.easypan.entity.DTO.UserDTO;
import org.xzb.easypan.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author moonchild
 * @since 2024-04-27
 */
public interface IUserInfoService extends IService<User> {
    public Result sendEmailCode(EmailDTO emailDTO, HttpSession session);

    public Result register(UserDTO userDTO);

    public Result login(UserDTO userDTO);
}
