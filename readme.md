# EasyPan 类网盘项目


## 注册登录部分
1. 修改了邮箱验证码的判断逻辑：存储验证码到session/redis，而非存储到数据库（验证码做持久化有什么作用？）
2. 参数校验逻辑：UP主使用了AspectJ定义注解，来实现对参数的校验。实际上，可以使用`spring-boot-starter-validation`组件，直接进行参数校验。倒是切面编程可以学习一下，
3. 