package org.yinan.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SyntheticElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yinan
 * @date 2020/6/2
 */
public abstract class BaseAnAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        if (editor == null || project == null || psiFile == null) {
            return;
        }
        PsiClass psiClass = getTargetClass(editor, psiFile);
        DialogWrapper dialogWrapper = getDialogWrapper(project, psiFile, editor, psiClass);
        dialogWrapper.show();
    }

    /**
     * editor是用来获取偏移量
     * psiFile是用来依据偏移量获取元素
     * @param editor
     * @param psiFile
     * @return
     */
    @Nullable
    protected PsiClass getTargetClass(Editor editor, PsiFile psiFile) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        if (element == null) {
            return null;
        } else {
            PsiClass target = PsiTreeUtil.getParentOfType(element, PsiClass.class);
            //SyntheticElement 代码中实际上不存在的类，例如jsp生成的类等等
            return target instanceof SyntheticElement ? null : target;
        }
    }

    protected abstract DialogWrapper getDialogWrapper(Project project, PsiFile psiFile,
                                                      Editor editor, PsiClass psiClass);
}
