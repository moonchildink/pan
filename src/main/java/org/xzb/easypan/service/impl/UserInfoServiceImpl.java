package org.xzb.easypan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jdk.internal.platform.SystemMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.xzb.easypan.entity.DTO.EmailDTO;
import org.xzb.easypan.entity.DTO.Result;
import org.xzb.easypan.entity.DTO.UserDTO;
import org.xzb.easypan.entity.User;
import org.xzb.easypan.mapper.UserInfoMapper;
import org.xzb.easypan.service.IUserInfoService;
import org.xzb.easypan.utils.EmailPostman;
import org.xzb.easypan.utils.SystemConstants;
//import org.xzb.easypan.service.UserInfoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, User> implements IUserInfoService {

    /**
     * todo:
     *  1. 使用JWT
     *  2. 使用ThreadLocal保存用户信息
     *  3. 前端传输用户数据
     */

    @Resource
    StringRedisTemplate template;

    @Resource
    EmailPostman emailPostman;

    @Override
    public Result sendEmailCode(EmailDTO emailDTO, HttpSession session) {
        System.out.println(emailDTO);
        if (template.opsForValue().get(SystemConstants.REDIS_EMAIL_CODE_PREFIX + emailDTO.getEmail()) != null) {
            return Result.fail("重复获取验证码");
        }

        String verifyCode = (String) session.getAttribute(SystemConstants.CHECK_CODE_KEY_EMAIL);
        if (!Objects.equals(emailDTO.getCheckCode(), verifyCode)) {
            session.removeAttribute(SystemConstants.CHECK_CODE_KEY_EMAIL);
            return Result.fail("您输入的验证码有误");
        }
        String emailCode = RandomUtil.randomString(6);
        emailPostman.sendEmail(emailDTO.getEmail(), "【EasyPan】您的邮箱验证码", "您的验证码为" + emailCode);
        session.setAttribute("email_address", emailDTO.getEmail());
        session.setAttribute("verify_code", emailCode);
        template.opsForValue().set(SystemConstants.REDIS_EMAIL_CODE_PREFIX + emailDTO.getEmail(), emailCode, 5, TimeUnit.MINUTES);
        return Result.ok();
    }


    /**
     * @param userDTO
     * @return
     * @author moonchild
     * 实现注册功能
     */
    @Override
    public Result register(UserDTO userDTO) {
        String email = userDTO.getEmail();
        String key = SystemConstants.REDIS_EMAIL_CODE_PREFIX + email;
        String redisSavedCode = template.opsForValue().get(key);
        if (redisSavedCode == null)
            return Result.fail("验证码已过期");
        String emailCode = userDTO.getEmailCode();
        if (!redisSavedCode.equals(emailCode))
            return Result.fail("验证码错误");
        template.delete(key);
        String hashpw = BCrypt.hashpw(userDTO.getPassword());
        userDTO.setPassword(hashpw);
        userDTO.setUser_id(SystemConstants.NICK_NAME_PREFIX + UUID.randomUUID().toString(true));
        User user = query().eq("email", email).or().eq("nick_name", userDTO.getNickName()).one();
        if (user == null) {
            user = BeanUtil.copyProperties(userDTO, User.class);
            save(user);
            return Result.ok(userDTO);
        } else {
            return Result.fail("邮箱或昵称重复");
        }

    }

    public Result login(UserDTO userDTO) {

    }
}
