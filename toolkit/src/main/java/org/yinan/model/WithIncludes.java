package org.yinan.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagsList;

import java.util.List;

/**
 * @author yinan
 * @date 2020/5/31
 */
public interface WithIncludes extends DomElement {
    @SubTagsList("include")
    List<Include> getIncludes();
}
