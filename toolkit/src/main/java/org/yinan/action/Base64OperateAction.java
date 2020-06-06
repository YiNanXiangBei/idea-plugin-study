package org.yinan.action;


import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.yinan.ui.Base64Operate;

/**
 * @author yinan
 * @date 2020/6/1
 */
public class Base64OperateAction extends BaseAnAction {

    @Override
    protected DialogWrapper getDialogWrapper(Project project, PsiFile psiFile, Editor editor, PsiClass psiClass) {
        return new Base64Operate(project, psiFile, editor, psiClass);
    }
}
