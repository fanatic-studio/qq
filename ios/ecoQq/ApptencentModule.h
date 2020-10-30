//
//  ApptencentModule.h
//  Pods
//

#import <Foundation/Foundation.h>
#import "WeexSDK.h"
#import <TencentOpenAPI/QQApiInterface.h>
#import <TencentOpenAPI/TencentOAuth.h>

@interface ApptencentModule : NSObject <WXModuleProtocol,TencentSessionDelegate, QQApiInterfaceDelegate>


@end
