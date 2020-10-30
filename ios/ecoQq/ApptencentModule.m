//
//  ApptencentModule.m
//  Pods
//

#import "ApptencentModule.h"
#import "Config.h"
#import <WeexPluginLoader/WeexPluginLoader.h>

@interface ApptencentModule ()

@end

@implementation ApptencentModule

TencentOAuth *_oauth;
WXModuleCallback _mycall;

- (void)initOAuth {
    NSMutableDictionary *tencent = [[Config getObject:@"tencent"] objectForKey:@"ios"];
    _oauth = [[TencentOAuth alloc] initWithAppId:tencent[@"appid"] andDelegate:self];
}

WX_PlUGIN_EXPORT_MODULE(ecoQq, ApptencentModule)
WX_EXPORT_METHOD_SYNC(@selector(isQQInstalled))
WX_EXPORT_METHOD(@selector(login:))
WX_EXPORT_METHOD(@selector(logout))
WX_EXPORT_METHOD(@selector(getUserInfo:))
WX_EXPORT_METHOD(@selector(share:callback:))
WX_EXPORT_METHOD(@selector(shareToQzone:callback:))

//是否安装QQ
- (BOOL)isQQInstalled
{
    if ([QQApiInterface isSupportShareToQQ]) {
        return YES;
    } else {
        return NO;
    }
}

//登录
- (void)login:(WXModuleCallback)callback
{
    if (callback == nil) {
        return;
    }
    if (_oauth == nil) {
        [self initOAuth];
    }
    NSArray *permissions = [@"get_user_info,get_simple_userinfo,add_t" componentsSeparatedByString:@","];
    [_oauth authorize:permissions];
    _mycall = callback;
}

//登出
- (void)logout
{
    if (_oauth != nil) {
        [_oauth logout:self];
    }
    _oauth = nil;
}

//获取用户信息
- (void)getUserInfo:(WXModuleCallback)callback
{
    if (callback == nil) {
        return;
    }
    if (_oauth == nil) {
        callback(@{
                @"status": @"error",
                @"body": @"请先登录",
        });
        return;
    }
    [_oauth getUserInfo];
    _mycall = callback;
}

//分享给好友
- (void)share:(NSDictionary*)params callback:(WXModuleCallback)callback
{
    [self shareWebpage:@"qq" params:params callback:callback];
}

//分享到QQ空间
- (void)shareToQzone:(NSDictionary*)params callback:(WXModuleCallback)callback
{
    [self shareWebpage:@"qzone" params:params callback:callback];
}

#pragma mark - TencentSessionDelegate

- (void)tencentDidLogin {
    if (_mycall == nil) {
        return;
    }
    if (_oauth.accessToken != nil && _oauth.accessToken.length > 0) {
        // 登录成功
        NSString *openId = _oauth.openId;
        NSString *accessToken = _oauth.accessToken;
        long long expiresIn = (long long int) ceil(_oauth.expirationDate.timeIntervalSinceNow); // 向上取整
        long long createAt = (long long int) [[NSDate date] timeIntervalSince1970];
        _mycall(@{
                @"status": @"success",
                @"body": @"登录成功",
                @"openid": openId,
                @"access_token": accessToken,
                @"expires_in": @(expiresIn),
                @"create_at": @(createAt),
        });
    } else {
        // 登录失败
        _mycall(@{
                @"status": @"error",
                @"body": @"登录失败",
        });
    }
}

- (void)tencentDidNotLogin:(BOOL)cancelled {
    if (_mycall == nil) {
        return;
    }
    _mycall(@{
            @"status": @"error",
            @"body": cancelled ? @"登录取消" : @"登录失败",
    });
}

- (void)tencentDidNotNetWork {
    if (_mycall == nil) {
        return;
    }
    _mycall(@{
            @"status": @"error",
            @"body": @"登录失败",
    });
}

- (void)getUserInfoResponse:(APIResponse*) response {
    if (_mycall == nil) {
        return;
    }
    if (URLREQUEST_SUCCEED == response.retCode && kOpenSDKErrorSuccess == response.detailRetCode) {
        NSMutableDictionary *data = [response.jsonResponse mutableCopy];
        [data setValue:@"success" forKey:@"status"];
        [data setValue:@"获取成功" forKey:@"body"];
        _mycall(data);
    } else {
        _mycall(@{
                @"status": @"error",
                @"body": @"获取失败: 返回为空",
        });
    }
}

#pragma mark - QQApiInterfaceDelegate

//网页分享
- (void)shareWebpage:(NSString*)type params:(NSDictionary*)params callback:(WXModuleCallback)callback
{
    if (callback == nil) {
        callback = ^(id _Nullable result) { };
    }
    if (_oauth == nil) {
        [self initOAuth];
    }
    NSString *title = params[@"title"];
    NSString *summary = params[@"desc"];
    NSString *imageUri = [params[@"imgUrl"] stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet characterSetWithCharactersInString:@"`#%^{}\"[]|\\<> "].invertedSet];
    NSString *targetUrl = [params[@"url"] stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet characterSetWithCharactersInString:@"`#%^{}\"[]|\\<> "].invertedSet];
    QQApiNewsObject *object = [QQApiNewsObject objectWithURL:[NSURL URLWithString:targetUrl]
                                                       title:title
                                                 description:summary
                                             previewImageURL:[NSURL URLWithString:imageUri]];
    SendMessageToQQReq *req = [SendMessageToQQReq reqWithContent:object];
    if (type == @"qzone") {
        [QQApiInterface SendReqToQZone:req];
    } else {
        [QQApiInterface sendReq:req];
    }
    _mycall = callback;
}

- (void)onReq:(QQBaseReq *)req {
}

- (void)onResp:(QQBaseResp *)resp {
    if (_mycall == nil) {
        return;
    }
    if ([resp isKindOfClass:[SendMessageToQQResp class]]) {
        switch (resp.result.intValue) {
            case 0:
                // 分享成功
                _mycall(@{
                        @"status": @"success",
                        @"body": @"分享成功",
                });
                break;
            case -4:
                // 用户取消
                _mycall(@{
                        @"status": @"error",
                        @"body": @"分享取消",
                });
                break;
            default:
                // 分享错误
                _mycall(@{
                        @"status": @"error",
                        @"body": [NSString stringWithFormat:@"分享错误: %@.", resp.errorDescription],
                });
                break;
        }
    }
}

- (void)isOnlineResponse:(NSDictionary *)response {
}

@end
