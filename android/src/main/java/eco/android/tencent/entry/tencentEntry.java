package eco.android.tencent.entry;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.common.WXException;
import com.tencent.tauth.Tencent;

import app.eco.framework.extend.annotation.ModuleEntry;
import app.eco.framework.extend.module.ecoBase;
import app.eco.framework.extend.module.ecoJson;
import eco.android.tencent.module.ApptencentModule;

@ModuleEntry
public class tencentEntry {
    public static Tencent mTencent;

    /**
     * APP启动会运行此函数方法
     * @param content Application
     */
    public void init(Context content) {
        JSONObject tencent = ecoJson.parseObject(ecoBase.config.getObject("tencent").get("android"));
        mTencent = Tencent.createInstance(ecoJson.getString(tencent, "appid"), content);

        //1、注册weex模块
        try {
            WXSDKEngine.registerModule("ecoQq", ApptencentModule.class);
        } catch (WXException e) {
            e.printStackTrace();
        }
    }
}
