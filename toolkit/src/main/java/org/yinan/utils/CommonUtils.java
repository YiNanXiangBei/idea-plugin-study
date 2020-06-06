package org.yinan.utils;

/**
 * @author yinan
 * @date 2020/5/31
 */
public class CommonUtils {
    /**
     * 移除注释文本中的/ * 空格 换行符
     * @param commentText
     * @return
     */
    public static String removeSymbol(String commentText) {
        return commentText.replace("*", "")
                .replace("/", "")
                .replace(" ", "")
                .replace("\n", "")
                .replace("\t", "");
    }
}
