package org.yinan.action;


import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.yinan.ui.UrlOperate;

/**
 * @author yinan
 * @date 2020/6/2
 */
public class UrlOperateAction extends BaseAnAction {

    @Override
    protected DialogWrapper getDialogWrapper(Project project, PsiFile psiFile, Editor editor, PsiClass psiClass) {
        return new UrlOperate(project, psiFile, editor, psiClass);
    }
}
