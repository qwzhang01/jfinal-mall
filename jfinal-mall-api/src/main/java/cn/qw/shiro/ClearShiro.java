package cn.qw.shiro;

import java.lang.annotation.*;

/**
 * 用来清除所有的Shiro访问控制注解，适合于Controller绝大部分方法都需要做访问控制，个别不需要做访问控制的场合。 仅能用在方法上。
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ClearShiro {
}
