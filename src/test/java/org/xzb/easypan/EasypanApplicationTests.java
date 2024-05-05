package org.xzb.easypan;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.template.Template;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.xzb.easypan.entity.DTO.RegisterFormDTO;
import org.xzb.easypan.entity.VO.UserDTO;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@SpringBootTest
class EasypanApplicationTests {

    @Test
    void contextLoads() {
    }


    @Resource
    DataSource source;

    @Test
    public void generateCode() {
        FastAutoGenerator
                .create(new DataSourceConfig.Builder(source))
                .globalConfig(builder -> {
                    builder.author("moonchild");
                    builder.commentDate("2024-04-27");
                    builder.outputDir("D:\\code\\Java\\easypan\\src\\main\\java");
                })
                .dataSourceConfig(builder -> builder.typeConvertHandler(((globalConfig, typeRegistry, metaInfo) -> {
                    int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                    if (typeCode == Types.SMALLINT)
                        return DbColumnType.INTEGER;
                    return typeRegistry.getColumnType(metaInfo);
                })))
                .packageConfig(builder -> {
                    builder.parent("org.xzb")
                            .moduleName("easypan");
                })
                .execute();
    }

    @Test
    public void md5() {
        System.out.println(SecureUtil.md5("Mwoo1764"));
    }


    @Resource
    StringRedisTemplate template;

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testRegister() {
        template.opsForHash().put("wy","nu","n");
        Map<Object, Object> entries = template.opsForHash().entries("wy");
        System.out.println(entries.get("wy"));
    }
}
