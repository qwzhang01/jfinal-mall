package com.qw.plugin;

import cn.qw.kit.BeanKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.IPlugin;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.annotation.JobHander;
import net.dreamlu.utils.ClassUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Set;

/**
 * xxl-job 插件
 */
public class XxlJobPlugin implements IPlugin {
    private Log log = Log.getLog(getClass());
    private XxlJobExecutor xxlJobExecutor = null;

    @Override
    public boolean start() {
        XxlJobExecutor xxlJobExecutor = initXxlJobExecutor("com.qw");
        if(xxlJobExecutor != null) {
            this.xxlJobExecutor = xxlJobExecutor;
        }
        return true;
    }

    @Override
    public boolean stop() {
        destoryXxlJobExecutor(this.xxlJobExecutor);
        return true;
    }

    /**
     * 初始化Xxl任务执行器
     */
    private XxlJobExecutor initXxlJobExecutor(String scanPackage) {
        // registJobHandler
        registJobHandler(scanPackage);
        // load executor prop
        Prop xxlJobProp = PropKit.use("xxl-job-executor.properties");
        String address = xxlJobProp.get("xxl.job.admin.addresses");
        if(StrKit.isBlank(address)) {
            log.error("XXL-JOB没有配置，在resources中配置xxl-job-executor.properties文件");
            return null;
        }
        // init executor
        XxlJobExecutor xxlJobExecutor = new XxlJobExecutor();
        xxlJobExecutor.setIp(xxlJobProp.get("xxl.job.executor.ip"));
        xxlJobExecutor.setPort(xxlJobProp.getInt("xxl.job.executor.port"));
        xxlJobExecutor.setAppName(xxlJobProp.get("xxl.job.executor.appname"));
        xxlJobExecutor.setAdminAddresses(xxlJobProp.get("xxl.job.admin.addresses"));
        xxlJobExecutor.setLogPath(xxlJobProp.get("xxl.job.executor.logpath"));
        xxlJobExecutor.setAccessToken(xxlJobProp.get("xxl.job.accessToken"));

        // start executor
        try {
            xxlJobExecutor.start();
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return xxlJobExecutor;
    }

    /**
     * 配置bean模式的jobHandler
     */
    private void registJobHandler(String scanPackage) {
        //使用的是Jfinal-Event中的工具包ClassUtil、BeanUtil，注意以后升级Jfinal-Event的是否影响这里
        Set<Class<?>> classSet = ClassUtil.scanPackageByAnnotation(scanPackage, false, JobHander.class);
        classSet.stream().forEach(c -> {
            JobHander annotation = c.getAnnotation(JobHander.class);
            String jobName = annotation.value();
            if (StrKit.isBlank(jobName)) {
                jobName = c.getSimpleName();
            }
            XxlJobExecutor.registJobHandler(jobName, BeanKit.newInstance(c));
        });
    }

    /**
     * 关闭XxlJob任务执行器
     *
     * @param xxlJobExecutor
     */
    public void destoryXxlJobExecutor(XxlJobExecutor xxlJobExecutor) {
        if (xxlJobExecutor != null) {
            xxlJobExecutor.destroy();
        }
    }
}