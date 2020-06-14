package org.yinan.core.service.impl;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.yinan.core.service.RestfulToolService;
import org.yinan.core.view.window.frame.RightToolWindow;
import org.yinan.core.window.SecondPanel;

/**
 * @author ZhangYuanSheng
 * @version 1.0
 */
public class RestfulToolServiceImpl implements RestfulToolService {

    private final Project project;

    public RestfulToolServiceImpl(Project project) {
        this.project = project;
    }

    @Override
    public void setupImpl(@NotNull ToolWindow toolWindow) {
        RightToolWindow view = new RightToolWindow(project);
//        SecondPanel secondPanel = new SecondPanel(project);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(view, "", false);
//        Content content1 = contentFactory.createContent(secondPanel, "333", false);

        toolWindow.getContentManager().addContent(content);
//        toolWindow.getContentManager().addContent(content1);
    }
}
