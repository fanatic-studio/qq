// 安装插件时会node运行此文件
const fs = require('fs');
const path = require('path');
const plist = require('plist');
const utils = require('./utils');

(function(){
    let workPath = process.cwd();
    let infoPath = path.join(workPath, 'platforms/ios/vdApp/vdApp/Info.plist');
    let configPath = path.join(workPath, 'vd.config.js');
    //
    if (!fs.existsSync(infoPath) || !fs.existsSync(configPath)) {
        return;
    }
    let configObject = require(configPath);
    let configAppid = utils.getObject(configObject,'tencent.ios.appid') || '';
    let infoContent = fs.readFileSync(infoPath, 'utf8');
    let infoObject = plist.parse(infoContent);
    let infoIn = false;
    infoObject['CFBundleURLTypes'].some((item) => {
        if (item['CFBundleURLName'] == "tencent") {
            item['CFBundleURLSchemes'] = [ "tencent" + configAppid ];
            return infoIn = true;
        }
    });
    if (!infoIn) {
        infoObject['CFBundleURLTypes'].push({
            "CFBundleTypeRole": "Editor",
            "CFBundleURLName": "tencent",
            "CFBundleURLSchemes": [ "tencent" + configAppid ]
        });
    }
    let schemes = [
        "tim",
        "mqq",
        "mqqapi",
        "mqqbrowser",
        "mttbrowser",
        "mqqOpensdkSSoLogin",
        "mqqopensdkapiV2",
        "mqqopensdkapiV4",
        "mqzone",
        "mqzoneopensdk",
        "mqzoneopensdkapi",
        "mqzoneopensdkapi19",
        "mqzoneopensdkapiV2",
        "mqqapiwallet",
        "mqqopensdkfriend",
        "mqqopensdkavatar",
        "mqqopensdkminiapp",
        "mqqopensdkdataline",
        "mqqgamebindinggroup",
        "mqqopensdkgrouptribeshare",
        "tencentapi.qq.reqContent",
        "tencentapi.qzone.reqContent",
        "mqqthirdappgroup",
        "mqqopensdklaunchminiapp"
    ];
    schemes.forEach((item) => {
        if (infoObject['LSApplicationQueriesSchemes'].indexOf(item) === -1) {
            infoObject['LSApplicationQueriesSchemes'].push(item);
        }
    });
    fs.writeFileSync(infoPath, plist.build(infoObject), 'utf8');
})();
