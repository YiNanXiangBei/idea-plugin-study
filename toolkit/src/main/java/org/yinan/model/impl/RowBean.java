package org.yinan.model.impl;

import com.intellij.psi.PsiType;

/**
 * @author yinan
 * @date 2020/6/1
 */
public class RowBean {
    private Boolean enable;

    private String psiModifier;

    private PsiType psiType;

    private String fieldName;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getPsiModifier() {
        return psiModifier;
    }

    public void setPsiModifier(String psiModifier) {
        this.psiModifier = psiModifier;
    }

    public PsiType getPsiType() {
        return psiType;
    }

    public void setPsiType(PsiType psiType) {
        this.psiType = psiType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
