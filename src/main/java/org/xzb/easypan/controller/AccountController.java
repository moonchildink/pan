package org.xzb.easypan.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xzb.easypan.entity.DTO.EmailDTO;
import org.xzb.easypan.entity.DTO.LoginFromDTO;
import org.xzb.easypan.entity.DTO.Result;
import org.xzb.easypan.entity.DTO.RegisterFormDTO;
import org.xzb.easypan.service.impl.UserInfoServiceImpl;
import org.xzb.easypan.utils.CreateImageCode;
import org.xzb.easypan.utils.SystemConstants;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;

@RestController("userInfoController")
public class AccountController {

    @Resource
    UserInfoServiceImpl userInfoService;


    @RequestMapping("/checkCode")
    public void checkCode(HttpServletResponse response, HttpSession session, Integer type) throws IOException {
        CreateImageCode vCode = new CreateImageCode(130, 38, 5, 10);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        String code = vCode.getCode();

        if (type == null || type == 0) {
            session.setAttribute(SystemConstants.CHECK_CODE_KEY, code);
        } else {
            session.setAttribute(SystemConstants.CHECK_CODE_KEY_EMAIL, code);
        }
        vCode.write(response.getOutputStream());
    }


    @PostMapping("/sendEmailCode")
    public Result sendEmailCode(@Valid EmailDTO emailDTO, HttpSession session) {
        return userInfoService.sendEmailCode(emailDTO, session);
    }

    /**
     * @param registerFormDTO
     * @return
     */
    @PostMapping("register")
    public Result register(@Valid RegisterFormDTO registerFormDTO, HttpSession session) {
        return userInfoService.register(registerFormDTO, session);
    }

    @PostMapping("/login")
    public Result login(@Valid LoginFromDTO loginFromDTO, HttpSession session) {
        return userInfoService.login(loginFromDTO, session);
    }


    @PostMapping("/updateUserAvatar")
    public Result updateUserAvatar(MultipartFile avatar) {
        return userInfoService.updateUserAvatar(avatar);
    }

    @PostMapping("/updatePassword")
    public Result updateUserPassword(String password) {
        return userInfoService.updateUserPassword(password);
    }

    @PostMapping("/logout")
    public Result logout() {
        return userInfoService.logout();
    }
}
