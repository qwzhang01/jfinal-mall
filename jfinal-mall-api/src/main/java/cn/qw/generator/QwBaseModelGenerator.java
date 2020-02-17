package cn.qw.generator;

import com.jfinal.plugin.activerecord.generator.BaseModelGenerator;

public class QwBaseModelGenerator extends BaseModelGenerator {
    public QwBaseModelGenerator(String baseModelPackageName, String baseModelOutputDir) {
        super(baseModelPackageName, baseModelOutputDir);
        super.setTemplate("/cn/qw/generator/base_model_template.jf");
    }
}
