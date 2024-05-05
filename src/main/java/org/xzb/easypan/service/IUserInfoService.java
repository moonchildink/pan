package org.xzb.easypan.service;

import org.springframework.web.multipart.MultipartFile;
import org.xzb.easypan.entity.DTO.EmailDTO;
import org.xzb.easypan.entity.DTO.LoginFromDTO;
import org.xzb.easypan.entity.DTO.Result;
import org.xzb.easypan.entity.DTO.RegisterFormDTO;
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

    public Result register(RegisterFormDTO registerFormDTO, HttpSession session);

    public Result login(LoginFromDTO loginFromDTO, HttpSession session);

    Result updateUserAvatar(MultipartFile avatar);

    Result updateUserPassword(String password);

    Result logout();
}
