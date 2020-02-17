package com.qw.plugin;

import com.jfinal.log.Log;
import com.jfinal.plugin.IPlugin;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;

import java.io.IOException;

/**
 * FASTDFS 文件系统插件
 *
 * @author qw
 */
public class FastDfsPlugin implements IPlugin {
    private Log log = Log.getLog(getClass());

    @Override
    public boolean start() {
        // 加载 fastdfs配置
        try {
            ClientGlobal.initByProperties("fastdfs-client.properties");
            return true;
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (MyException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
