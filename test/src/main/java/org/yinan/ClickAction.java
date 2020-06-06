package org.yinan;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

/**
 * @author yinan
 * @date 2020/5/30
 */
public class ClickAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        Messages.showInfoMessage("Hello World", "My Title");
    }
}
