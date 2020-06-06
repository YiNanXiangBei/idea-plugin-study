package org.yinan.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author yinan
 * @date 2020/5/31
 */
public interface WithResultMap extends DomElement {
    @Attribute("resultMap")
    GenericAttributeValue<ResultMap> getResultMap();
}
