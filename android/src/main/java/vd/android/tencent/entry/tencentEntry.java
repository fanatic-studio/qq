package vd.android.tencent.entry;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.common.WXException;
import com.tencent.tauth.Tencent;

import app.vd.framework.extend.annotation.ModuleEntry;
import app.vd.framework.extend.module.vdBase;
import app.vd.framework.extend.module.vdJson;
import vd.android.tencent.module.ApptencentModule;

@ModuleEntry
public class tencentEntry {
    public static Tencent mTencent;

    /**
     * APP启动会运行此函数方法
     * @param content Application
     */
    public void init(Context content) {
        JSONObject tencent = vdJson.parseObject(vdBase.config.getObject("tencent").get("android"));
        mTencent = Tencent.createInstance(vdJson.getString(tencent, "appid"), content);

        //1、注册weex模块
        try {
            WXSDKEngine.registerModule("vdQq", ApptencentModule.class);
        } catch (WXException e) {
            e.printStackTrace();
        }
    }
}
