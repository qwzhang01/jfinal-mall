package cn.qw.plugin.event;

import cn.qw.kit.ThreadKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.IPlugin;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class AsyncEventPlugin implements IPlugin {
    private static final Log logger = Log.getLog(AsyncEventPlugin.class);

    // 核心线程池大小
    private int corePoolSize = 50;

    // 最大可创建的线程数
    private int maxPoolSize = 200;

    // 队列最大长度
    private int queueCapacity = 1000;

    // 线程池维护线程所允许的空闲时间
    private int keepAliveSeconds = 300;


    private ScheduledExecutorService scheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(corePoolSize,
                new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build()) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                ThreadKit.printException(r, t);
            }
        };
    }

    @Override
    public boolean start() {
        try {
            logger.info("====启动后台任务任务线程池====");
            AsyncManager.me().setExecutor(this.scheduledExecutorService());
            return true;
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    @Override
    public boolean stop() {
        try {
            logger.info("====关闭后台任务任务线程池====");
            AsyncManager.me().shutdown();
            return true;
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }
}