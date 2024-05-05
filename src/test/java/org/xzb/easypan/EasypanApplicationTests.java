package org.xzb.easypan;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Types;

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
                    if(typeCode== Types.SMALLINT)
                            return DbColumnType.INTEGER;
                    return typeRegistry.getColumnType(metaInfo);
                })))
                .packageConfig(builder -> {
                    builder.parent("org.xzb")
                            .moduleName("easypan");
                })
                .execute();
    }


}
