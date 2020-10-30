# QQ登录、分享插件

## 安装

```shell script
eco plugin install https://github.com/kjeco/qq
```

## 卸载

```shell script
eco plugin uninstall https://github.com/kjeco/qq
```

## 引用

```js
const qq = app.requireModule("eco/qq");
```

## 参数配置


[eco.config.js](https://eco.app/guide/config.html)文件添加以下参数：

```

tencent: {
    ios: {
        appid: "ios的appid",
    },
    android: {
        appid: "android的appid",
    }
}

```

* APPID请在 https://connect.qq.com/index.html


## QQ登录

> `qq.login` QQ登录

```
/**
 * @param callback  回调事件
 */
qq.login(callback(result))

```

##### callback 回调`result`说明

```
{
    status: "success",    //状态，success|error
    body: "登录成功",      //状态描述
    
    //以下参数仅：status=success有
    openid: "...",      
    access_token: "...",
    expires_in: 7200,           //过期时间
    create_at: 1520492050,      //创建时间戳
}

```

## 退出登录

> `qq.logout` 退出登录

```
qq.logout()

```

## 获取用户信息

> `qq.getUserInfo` 获取用户信息

```
/**
 * @param callback  回调事件
 */
qq.getUserInfo(callback(result))

```

##### callback 回调`result`说明

```
{
    status: "success",    //状态，success|error
    body: "获取成功",      //状态描述
    
    //..... status=success 会返回用户昵称头像等信息
}

```

## 分享给好友

> `qq.share`分享给好友

```
/**
 * @param params    详细参数
 * @param callback  回调事件
 */
qq.share({params}, callback(result))

```

##### params 参数说明

| 属性名 | 类型 | 必须 | 描述 | 默认值 |
| --- | --- | --- | --- | --- |
| title | `String` | √ | 分享标题 | - |
| url | `String` | √ | 分享地址 | - |
| desc | `String` | √ | 分享描述 | - |
| imgUrl | `String` | √ | 缩略图 | - |


##### callback 回调`result`说明

```
{
    status: "success",    //状态，success|error
    body: "",             //状态描述
}

```

## 分享到QQ空间

> `qq.shareToQzone` 分享到QQ空间

```
/**
 * @param params    详细参数
 * @param callback  回调事件
 */
qq.shareToQzone({params}, callback(result))

```

##### params 参数说明

| 属性名 | 类型 | 必须 | 描述 | 默认值 |
| --- | --- | --- | --- | --- |
| title | `String` | √ | 分享标题 | - |
| url | `String` | √ | 分享地址 | - |
| desc | `String` | √ | 分享描述 | - |


##### callback 回调`result`说明

```
{
    status: "success",    //状态，success|error
    body: "",             //状态描述
}

```
