package org.yinan.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagsList;

import java.util.List;

/**
 * @author yinan
 * @date 2020/5/31
 */
public interface Mapper extends DomElement {
    @Attribute("namespace")
    GenericAttributeValue<String> getNamespace();

    @SubTagsList({"sql", "select", "insert", "update", "delete"})
    List<MapperIdentifiableStatement> getIdentifiableStatements();

    @SubTagsList("resultMap")
    List<ResultMap> getResultMaps();

    @SubTagsList("sql")
    List<MapperIdentifiableStatement> getSqls();

    @SubTagsList("select")
    List<Select> getSelects();

    @SubTagsList("insert")
    List<Insert> getInserts();

    @SubTagsList("update")
    List<Update> getUpdates();

    @SubTagsList("delete")
    List<Delete> getDeleted();
}
