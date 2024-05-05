package org.xzb.easypan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xzb.easypan.entity.DTO.EmailDTO;
import org.xzb.easypan.entity.DTO.LoginFromDTO;
import org.xzb.easypan.entity.DTO.RegisterFormDTO;
import org.xzb.easypan.entity.DTO.Result;
import org.xzb.easypan.entity.User;
import org.xzb.easypan.entity.VO.UserDTO;
import org.xzb.easypan.mapper.UserInfoMapper;
import org.xzb.easypan.service.IUserInfoService;
import org.xzb.easypan.utils.EmailPostman;
import org.xzb.easypan.utils.SystemConstants;
import org.xzb.easypan.utils.UserHolder;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

    private boolean isCheckCodeRight(String checkCode, HttpSession session, int type) {
        String key = type == 0 ? SystemConstants.CHECK_CODE_KEY_EMAIL : SystemConstants.CHECK_CODE_KEY;
        String verifyCode = (String) session.getAttribute(key);
        System.out.println(verifyCode);
        System.out.println(checkCode);
        session.removeAttribute(key);
        boolean flag = verifyCode != null && Objects.equals(checkCode, verifyCode);
        System.out.println(flag);
        return flag;
    }

    @Override
    public Result sendEmailCode(EmailDTO emailDTO, HttpSession session) {
        if (template.opsForValue().get(SystemConstants.REDIS_EMAIL_CODE_PREFIX + emailDTO.getEmail()) != null) {
            return Result.fail("重复获取验证码");
        }
        if (!isCheckCodeRight(emailDTO.getCheckCode(), session, 0))
            return Result.fail("图片验证码错误");
        String emailCode = RandomUtil.randomNumbers(6);
        emailPostman.sendEmail(emailDTO.getEmail(), "【EasyPan】您的邮箱验证码", "您的验证码为" + emailCode);
        session.setAttribute("email_address", emailDTO.getEmail());
        session.setAttribute("verify_code", emailCode);
        template.opsForValue().set(SystemConstants.REDIS_EMAIL_CODE_PREFIX + emailDTO.getEmail(), emailCode, 5, TimeUnit.MINUTES);
        return Result.ok();
    }

    @Override
    public Result register(RegisterFormDTO registerFormDTO, HttpSession session) {
        // 首先判断用户输入的checkcode是否正确
        System.out.println(registerFormDTO.getCheckCode());
        System.out.println(session.getAttribute(SystemConstants.CHECK_CODE_KEY));
        boolean checkCodeRight = isCheckCodeRight(registerFormDTO.getCheckCode(), session, 1);
        if (!checkCodeRight)
            return Result.fail("图片验证码错误");
        String email = registerFormDTO.getEmail();
        String key = SystemConstants.REDIS_EMAIL_CODE_PREFIX + email;
        String redisCode = template.opsForValue().get(key);
        if (redisCode == null || !redisCode.equals(registerFormDTO.getEmailCode()))
            return Result.fail("验证码有误");
        // 然后查数据库
        Long count = query().eq("email", email).count();
        if (count > 0)
            return Result.fail("邮箱已被占用");
        User user = createUser(registerFormDTO);
        UserDTO userDTO = saveUserToCache(user);
        UserHolder.saveUser(userDTO);
        return Result.ok(userDTO);
    }

    private UserDTO saveUserToCache(User user) {
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        String token = SystemConstants.REDIS_USER_CACHE_PREFIX + userDTO.getEmail();
        System.out.println(userDTO);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreError(true)
                        .setIgnoreProperties("qqAvatar")
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((key1, value) -> value.toString()));
        template.opsForHash().putAll(token, userMap);
        template.expire(token, SystemConstants.LOGIN_TOKEN_TIME, TimeUnit.MINUTES);
        return userDTO;
    }

    private User createUser(RegisterFormDTO registerFormDTO) {
        User user = BeanUtil.copyProperties(registerFormDTO, User.class);
        user.setUserId(UUID.randomUUID().toString(true));
        save(user);
        return query().eq("email", registerFormDTO.getEmail()).one();
    }

    @Override
    public Result login(LoginFromDTO loginFromDTO, HttpSession session) {

        if (!isCheckCodeRight(loginFromDTO.getCheckCode(), session, 1))
            return Result.fail("图片验证码错误");

        // 1. 首先从userholder中查找是否存在指定用户
        if (UserHolder.getUser() != null && UserHolder.getUser().getEmail().equals(loginFromDTO.getEmail()) && UserHolder.getUser().getStatus() == 1) {
            //
            return Result.ok(UserHolder.getUser());
        }
        // 说明当前User Holder中不存在用户，需要依次尝试从Redis、MySQL中查询用户然后存储到ThreadLocal之中
        String key = SystemConstants.REDIS_USER_CACHE_PREFIX + loginFromDTO.getEmail();
        Map<Object, Object> entries = template.opsForHash().entries(key);
        if (entries.isEmpty()) {
            // 说明未查询到缓存，再次查询数据库
            User user = query().eq("email", loginFromDTO.getEmail()).one();
            if (user == null) {
                // 保存空对象到Redis之中
                template.opsForHash().put(key, "null", "null");
                template.expire(key, SystemConstants.NULL_OBJ_TTL, TimeUnit.SECONDS);
                return Result.fail("您尚未注册！请首先注册。");
            }
            UserDTO userDTO = saveUserToCache(user);
            return Result.ok(userDTO);
        }
        if (entries.get("null")!=null)
            return Result.fail("请先注册");
        // 缓存中存在当前对象，返回
        UserDTO userDTO = BeanUtil.fillBeanWithMap(entries, new UserDTO(), true);
        UserHolder.saveUser(userDTO);
        template.expire(key, SystemConstants.LOGIN_TOKEN_TIME, TimeUnit.MINUTES);
        return Result.ok(userDTO);

    }

    @Override
    public Result updateUserAvatar(MultipartFile avatar) {
        return null;
    }

    @Override
    public Result updateUserPassword(String password) {
        return null;
    }

    @Override
    public Result logout() {
        return null;
    }
}
