package org.yinan.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.yinan.ui.XmlFormat;

/**
 * @author yinan
 * @date 2020/6/2
 */
public class XmlFormatAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);


        DialogWrapper dialogWrapper = new XmlFormat(project);

        dialogWrapper.show();
    }
}
