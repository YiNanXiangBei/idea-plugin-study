/*
  Copyright (C), 2018-2020, ZhangYuanSheng
  FileName: AppSettingsState
  Author:   ZhangYuanSheng
  Date:     2020/5/27 18:08
  Description: 
  History:
  <author>          <time>          <version>          <desc>
  作者姓名            修改时间           版本号              描述
 */
package org.yinan.core.configuration;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yinan.core.beans.AppSetting;

/**
 * @author ZhangYuanSheng
 * @version 1.0
 */
@State(
        name = "org.yinan.core.configuration.AppSettingsState",
        storages = {
                @Storage("SdkSettingsPlugin.xml")
        }
)

public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {
    //ide中保持状态的组件都需要实现PersistentStateComponent接口
    private final AppSetting setting;

    public AppSettingsState() {
        this.setting = new AppSetting();
        this.setting.initValue();
    }

    public static AppSettingsState getInstance() {
        return ServiceManager.getService(AppSettingsState.class);
    }

    public boolean isModified(AppSetting setting) {
        if (setting == null) {
            return false;
        }
        return this.setting.isModified(setting);
    }

    /**
     * 保存设置的时候会调用，如果当前设置和默认状态一样，（其实就是认为没有更改），
     * 那么就不会保留内容，否则返回状态将会以xml格式进行存储
     * @return
     */
    @Nullable
    @Override
    public AppSettingsState getState() {
        return this;
    }

    /**
     * 打开配置页面的时候会调用该方法
     * @param state
     */
    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    @NotNull
    public AppSetting getAppSetting() {
        return this.setting;
    }

    public void setAppSetting(AppSetting setting) {
        this.setting.applySetting(setting);
    }
}
