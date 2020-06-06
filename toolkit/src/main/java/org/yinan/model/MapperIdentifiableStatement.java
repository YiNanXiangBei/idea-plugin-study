package org.yinan.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;

/**
 * @author yinan
 * @date 2020/5/31
 */
public interface MapperIdentifiableStatement extends DomElement, WithIncludes {
    @NameValue
    @Attribute("id")
    GenericAttributeValue<String> getId();
}
