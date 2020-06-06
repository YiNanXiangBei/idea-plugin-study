package org.yinan.ui;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author yinan
 * @date 2020/6/3
 */
public class Base64Operate2 extends DialogWrapper {
    private JPanel rootJpanel;
    private JButton encodeButton;
    private JButton decodeButton;
    private JButton cancelButton;
    private JButton nextButton;
    private JLabel fileReference;
    private JLabel errorCode;
    private JTextPane textPane;

    public Base64Operate2(@Nullable Project project, PsiFile psiFile, Editor editor, PsiClass psiClass) {
        super(project, true, IdeModalityType.PROJECT);
        cancelButton.addActionListener(e -> dispose());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootJpanel;
    }
}
