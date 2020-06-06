package org.yinan.description;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yinan.model.Mapper;

/**
 * @author yinan
 * @date 2020/6/1
 */
public class MapperDescription extends DomFileDescription<Mapper> {
    public MapperDescription() {
        super(Mapper.class, "mapper");

    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        XmlTag rootTag = file.getRootTag();
        return rootTag != null && rootTag.getName().equals(getRootTagName());
    }

}
