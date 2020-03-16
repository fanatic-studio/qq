package vd.android.tencent.module;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.tencent.connect.UnionInfo;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.vd.framework.activity.PageActivity;
import app.vd.framework.extend.module.vdCommon;
import app.vd.framework.extend.module.vdJson;
import app.vd.framework.extend.module.vdMap;
import app.vd.framework.extend.module.vdParse;

import static vd.android.tencent.entry.tencentEntry.mTencent;

public class ApptencentModule extends WXModule {

    /**
     * 是否安装QQ
     */
    @JSMethod(uiThread = false)
    public boolean isQQInstalled() {
        if (mTencent == null) {
            return false;
        }
        return mTencent.isQQInstalled(mWXSDKInstance.getContext());
    }

    /**
     * 登录
     * @param callback
     */
    @JSMethod
    public void login(final JSCallback callback) {
        if (callback == null) {
            return;
        }
        if (mTencent == null) {
            Map<String, Object> data = new HashMap<>();
            data.put("status", "error");
            data.put("body", "初始化失败");
            callback.invoke(data);
            return;
        }
        final IUiListener baseUiListener = new IUiListener() {
            @Override
            public void onComplete(Object response) {
                if (null == response) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("status", "error");
                    data.put("body", "登录失败: 返回为空");
                    callback.invoke(data);
                    return;
                }
                JSONObject jsonResponse = (JSONObject) response;
                if (jsonResponse.length() == 0) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("status", "error");
                    data.put("body", "登录失败: 返回为空");
                    callback.invoke(data);
                    return;
                }
                //
                Map<String, Object> data = new HashMap<>();
                data.put("status", "error");
                data.put("body", "登录失败");
                try {
                    String token = jsonResponse.getString(Constants.PARAM_ACCESS_TOKEN);
                    String expires = jsonResponse.getString(Constants.PARAM_EXPIRES_IN);
                    String openId = jsonResponse.getString(Constants.PARAM_OPEN_ID);
                    if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
                        mTencent.setAccessToken(token, expires);
                        mTencent.setOpenId(openId);
                        //
                        data.put("status", "success");
                        data.put("body", "登录成功");
                        data.put("openid", openId);
                        data.put("access_token", token);
                        data.put("expires_in", expires);
                        data.put("create_at", vdCommon.timeStamp());
                    }
                } catch (Exception ignored) {
                    //
                }
                callback.invoke(data);
            }

            @Override
            public void onError(UiError uiError) {
                Map<String, Object> data = new HashMap<>();
                data.put("status", "error");
                data.put("body", "登录错误: " + uiError.errorMessage);
                callback.invoke(data);
            }

            @Override
            public void onCancel() {
                Map<String, Object> data = new HashMap<>();
                data.put("status", "error");
                data.put("body", "登录取消");
                callback.invoke(data);
            }
        };
        //
        PageActivity mActivity = (PageActivity) mWXSDKInstance.getContext();
        mActivity.setPageStatusListener("__extendTencent", new JSCallback() {
            @Override
            public void invoke(Object o) {

            }

            @Override
            public void invokeAndKeepAlive(Object data) {
                Map<String, Object> retData = vdMap.objectToMap(data);
                if (retData != null && vdParse.parseStr(retData.get("status")).equals("activityResult")) {
                    int requestCode = vdParse.parseInt(retData.get("requestCode"));
                    int resultCode = vdParse.parseInt(retData.get("resultCode"));
                    Intent intent = (Intent) retData.get("resultData");
                    if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
                        Tencent.onActivityResultData(requestCode, resultCode, intent, baseUiListener);
                    }
                }
            }
        });
        mTencent.login(mActivity, "all", baseUiListener, true);
    }

    /**
     * 登出
     */
    @JSMethod
    public void logout() {
        if (mTencent == null) {
            return;
        }
        mTencent.logout(mWXSDKInstance.getContext());
    }

    /**
     * 获取用户信息
     * @param callback
     */
    @JSMethod
    public void getUserInfo(final JSCallback callback) {
        if (callback == null) {
            return;
        }
        if (mTencent == null) {
            Map<String, Object> data = new HashMap<>();
            data.put("status", "error");
            data.put("body", "初始化失败");
            callback.invoke(data);
            return;
        }
        boolean ready = mTencent.isSessionValid() && mTencent.getQQToken().getOpenId() != null;
        if (!ready) {
            Map<String, Object> data = new HashMap<>();
            data.put("status", "error");
            data.put("body", "请先登录并获取openId");
            callback.invoke(data);
            return;
        }
        PageActivity mActivity = (PageActivity) mWXSDKInstance.getContext();
        UserInfo mInfo = new UserInfo(mActivity, mTencent.getQQToken());
        mInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object response) {
                if (null == response) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("status", "error");
                    data.put("body", "获取失败: 返回为空");
                    callback.invoke(data);
                    return;
                }
                JSONObject jsonResponse = (JSONObject) response;
                if (jsonResponse.length() == 0) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("status", "error");
                    data.put("body", "获取失败: 返回为空");
                    callback.invoke(data);
                    return;
                }
                com.alibaba.fastjson.JSONObject data = vdJson.parseObject(response);
                data.put("status", "success");
                data.put("body", "获取成功");
                callback.invoke(data);
            }

            @Override
            public void onError(UiError uiError) {
                Map<String, Object> data = new HashMap<>();
                data.put("status", "error");
                data.put("body", "获取错误: " + uiError.errorMessage);
                callback.invoke(data);
            }

            @Override
            public void onCancel() {
                Map<String, Object> data = new HashMap<>();
                data.put("status", "error");
                data.put("body", "操作取消");
                callback.invoke(data);
            }
        });
    }

    /**
     * 分享给好友
     * @param json
     * @param callback
     */
    @JSMethod
    public void share(com.alibaba.fastjson.JSONObject json, JSCallback callback) {
        if (callback == null) {
            callback = new JSCallback() {
                @Override
                public void invoke(Object o) {

                }

                @Override
                public void invokeAndKeepAlive(Object o) {

                }
            };
        }
        if (mTencent == null) {
            Map<String, Object> data = new HashMap<>();
            data.put("status", "error");
            data.put("body", "初始化失败");
            callback.invoke(data);
            return;
        }
        final JSCallback finalCallback = callback;
        final IUiListener baseUiListener = new IUiListener() {
            @Override
            public void onComplete(Object response) {
                if (null == response) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("status", "error");
                    data.put("body", "分享失败: 返回为空");
                    finalCallback.invoke(data);
                    return;
                }
                JSONObject jsonResponse = (JSONObject) response;
                if (jsonResponse.length() == 0) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("status", "error");
                    data.put("body", "分享失败: 返回为空");
                    finalCallback.invoke(data);
                    return;
                }
                Map<String, Object> data = new HashMap<>();
                data.put("status", "success");
                data.put("body", vdJson.parseObject(response));
                finalCallback.invoke(data);
            }

            @Override
            public void onError(UiError uiError) {
                Map<String, Object> data = new HashMap<>();
                data.put("status", "error");
                data.put("body", "分享错误: " + uiError.errorMessage);
                finalCallback.invoke(data);
            }

            @Override
            public void onCancel() {
                Map<String, Object> data = new HashMap<>();
                data.put("status", "error");
                data.put("body", "分享取消");
                finalCallback.invoke(data);
            }
        };
        //
        PageActivity mActivity = (PageActivity) mWXSDKInstance.getContext();
        mActivity.setPageStatusListener("__extendTencent", new JSCallback() {
            @Override
            public void invoke(Object o) {

            }

            @Override
            public void invokeAndKeepAlive(Object data) {
                Map<String, Object> retData = vdMap.objectToMap(data);
                if (retData != null && vdParse.parseStr(retData.get("status")).equals("activityResult")) {
                    int requestCode = vdParse.parseInt(retData.get("requestCode"));
                    int resultCode = vdParse.parseInt(retData.get("resultCode"));
                    Intent intent = (Intent) retData.get("resultData");
                    if (requestCode == Constants.REQUEST_OLD_SHARE || requestCode == Constants.REQUEST_QQ_SHARE) {
                        Tencent.onActivityResultData(requestCode, resultCode, intent, baseUiListener);
                    }
                }
            }
        });
        //
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_TITLE, vdJson.getString(json, "title"));
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, vdJson.getString(json, "url"));
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, vdJson.getString(json, "desc"));
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, vdJson.getString(json, "imgUrl"));
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        mTencent.shareToQQ(mActivity, params, baseUiListener);
    }

    /**
     * 分享到QQ空间
     * @param json
     * @param callback
     */
    @JSMethod
    public void shareToQzone(com.alibaba.fastjson.JSONObject json, final JSCallback callback) {
        if (callback == null) {
            return;
        }
        if (mTencent == null) {
            Map<String, Object> data = new HashMap<>();
            data.put("status", "error");
            data.put("body", "初始化失败");
            callback.invoke(data);
            return;
        }
        final IUiListener baseUiListener = new IUiListener() {
            @Override
            public void onComplete(Object response) {
                Map<String, Object> data = new HashMap<>();
                data.put("status", "success");
                data.put("body", "分享成功");
                callback.invoke(data);
            }

            @Override
            public void onError(UiError uiError) {
                Map<String, Object> data = new HashMap<>();
                data.put("status", "error");
                data.put("body", "分享错误: " + uiError.errorMessage);
                callback.invoke(data);
            }

            @Override
            public void onCancel() {
                Map<String, Object> data = new HashMap<>();
                data.put("status", "error");
                data.put("body", "分享取消");
                callback.invoke(data);
            }
        };
        //
        PageActivity mActivity = (PageActivity) mWXSDKInstance.getContext();
        mActivity.setPageStatusListener("__extendTencent", new JSCallback() {
            @Override
            public void invoke(Object o) {

            }

            @Override
            public void invokeAndKeepAlive(Object data) {
                Map<String, Object> retData = vdMap.objectToMap(data);
                if (retData != null && vdParse.parseStr(retData.get("status")).equals("activityResult")) {
                    int requestCode = vdParse.parseInt(retData.get("requestCode"));
                    int resultCode = vdParse.parseInt(retData.get("resultCode"));
                    Intent intent = (Intent) retData.get("resultData");
                    if (requestCode == Constants.REQUEST_OLD_SHARE || requestCode == Constants.REQUEST_QQ_SHARE) {
                        Tencent.onActivityResultData(requestCode, resultCode, intent, baseUiListener);
                    }
                }
            }
        });
        //
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, vdJson.getString(json, "title"));
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, vdJson.getString(json, "url"));
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, vdJson.getString(json, "desc"));
        //
        JSONArray array = vdJson.parseArray(json.get("imgUrl"));
        if (array.size() == 0 && !TextUtils.isEmpty(vdJson.getString(json, "imgUrl"))) {
            array.add(vdJson.getString(json, "imgUrl"));
        }
        ArrayList<String> imageUrls = new ArrayList<String>();
        for (int i = 0; i < array.size(); i++) {
            imageUrls.add(vdParse.parseStr(array.get(i)));
        }
        params.putStringArrayList(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        mTencent.shareToQzone(mActivity, params, baseUiListener);
    }
}
