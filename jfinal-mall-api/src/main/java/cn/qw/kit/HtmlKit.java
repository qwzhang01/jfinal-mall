package cn.qw.kit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML 工具类
 */
public class HtmlKit {
    /**
     * 匹配字符串中的img标签
     */
    private static final Pattern p = Pattern.compile("&lt;(img|IMG)(.*?)(&gt;|&gt;&gt;/img&gt;|/&gt;)");
    /**
     * 匹配图片的地址
     */
    private static final Pattern srcText = Pattern.compile("(src|SRC)=(&quot;|&apos;)(.*?)(&quot;|&apos;)");

    /**
     * 获取HTML中的 图片地址，并组装成数组
     */
    public static List<String> getImg(String content) {
        //用来存储获取到的图片地址
        List<String> srcList = new ArrayList<>();


        Matcher matcher = p.matcher(content);
        boolean hasPic = matcher.find();
        //判断是否含有图片
        if (hasPic == true) {
            //如果含有图片，那么持续进行查找，直到匹配不到
            while (hasPic) {
                String group = matcher.group(2);//获取第二个分组的内容，也就是 (.*?)匹配到的
                Matcher matcher2 = srcText.matcher(group);
                if (matcher2.find()) {
                    srcList.add(matcher2.group(3));//把获取到的图片地址添加到列表中
                }
                hasPic = matcher.find();//判断是否还有img标签
            }

        }
        return srcList;
    }
}
