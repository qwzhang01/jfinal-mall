package cn.qw.flash;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于Session实现的Flash管理器
 *
 * @author dafei
 */
public class FlashManager {

    /**
     * 默认存储session前缀
     */
    private final static String SESSION_KEY_PREFIX = "_flash_";

    private static FlashManager flashManager;

    private FlashManager() {
    }

    public static synchronized FlashManager me() {
        if (flashManager == null) {
            flashManager = new FlashManager();
        }
        return flashManager;
    }

    @SuppressWarnings("unchecked")
    public void setFlash(HttpSession session, String curAction, String key,
                         Object value) {
        if ((!"/".equals(curAction)) && curAction.endsWith("/")) {
            curAction = curAction.substring(0, curAction.length() - 1);
        }
        String sessionKey = SESSION_KEY_PREFIX + curAction.replace("/", "_");
        Object obj = session.getAttribute(sessionKey);
        Map<String, Object> map = null;
        if (obj != null) {
            map = (Map<String, Object>) obj;
        } else {
            map = new ConcurrentHashMap<String, Object>();
            session.setAttribute(sessionKey, map);
        }
        map.put(key, value);
    }

    public void updateFlashKey(HttpSession session, String curAction,
                               String nextAction) {
        String oldKey = SESSION_KEY_PREFIX + curAction.replace("/", "_");
        String newkey = SESSION_KEY_PREFIX + nextAction.replace("/", "_");
        Object obj = session.getAttribute(oldKey);
        if (obj != null) {
            session.removeAttribute(oldKey);
            session.setAttribute(newkey, obj);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getFlash(HttpSession session, String curAction) {
        String sessionActionKey = SESSION_KEY_PREFIX
                + curAction.replace("/", "_");
        Map<String, Object> map = null;
        Object obj = session.getAttribute(sessionActionKey);
        if (obj != null) {
            map = (Map<String, Object>) obj;
            session.removeAttribute(sessionActionKey);
        }
        return map;
    }
}