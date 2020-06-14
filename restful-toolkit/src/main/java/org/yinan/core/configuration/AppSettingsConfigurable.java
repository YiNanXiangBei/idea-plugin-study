/*
  Copyright (C), 2018-2020, ZhangYuanSheng
  FileName: AppSettingsConfigurable
  Author:   ZhangYuanSheng
  Date:     2020/5/27 18:06
  Description: 
  History:
  <author>          <time>          <version>          <desc>
  作者姓名            修改时间           版本号              描述
 */
package org.yinan.core.configuration;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import org.yinan.core.view.window.AppSettingsWindow;

import javax.swing.*;

/**
 * @author ZhangYuanSheng
 * @version 1.0
 */
public class AppSettingsConfigurable implements Configurable {

    private AppSettingsWindow settingsComponent;

    /**
     * 设置显示的名字，这里为什么使用的是title暂时不清楚，暂时不清楚设置这个是在哪使用，
     * 配置文件中有写displayName估计是覆盖了
     * @return
     */
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Restful Tools";
    }

    /**
     * 当对话框出现在屏幕上时，应该关注的组件
     * @return
     */
    @Override
    public JComponent getPreferredFocusedComponent() {
        return settingsComponent.getPreferredFocusedComponent();
    }

    /**
     * 返回setting页面
     * @return
     */
    @Nullable
    @Override
    public JComponent createComponent() {
        settingsComponent = new AppSettingsWindow();
        return settingsComponent.getContent();
    }

    /**
     * 是否修改了swing表单
     * @return
     */
    @Override
    public boolean isModified() {
        return AppSettingsState.getInstance().isModified(settingsComponent.getAppSetting());
    }

    /**
     * 将表单中的设置存储到可配置组件中
     */
    @Override
    public void apply() {
        AppSettingsState.getInstance().setAppSetting(settingsComponent.getAppSetting());
    }

    /**
     * 加载未修改之前的配置
     */
    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settingsComponent.setAppSetting(settings.getAppSetting());
    }

    /**
     * 表单关闭的时候触发
     */
    @Override
    public void disposeUIResources() {
        settingsComponent = null;
    }
}
