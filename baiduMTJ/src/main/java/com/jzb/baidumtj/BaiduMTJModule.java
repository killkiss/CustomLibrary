package com.jzb.baidumtj;

import com.baidu.mobstat.StatService;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

/**
 * create：2022/5/9 10:42
 *
 * @author ykx
 * @version 1.0
 * @Description 百度统计初始化
 */
public class BaiduMTJModule extends UZModule {

    public BaiduMTJModule(UZWebView webView) {
        super(webView);
    }

    /**
     * 初始化百度统计SDK
     * appKey: String 应用key
     * appVersion: String 应用版本
     * enableExceptionLog: boolean 是否开启错误日志
     * enableDebugOn: boolean 是否开启调试报告
     * enableDeviceMac: boolean 是否获取 mac id
     * setSendLogStrategy已经@deprecated，建议使用新的start接口
     * 如果没有页面和自定义事件统计埋点，此代码一定要设置，否则无法完成统计
     * 进程第一次执行此代码，会导致发送上次缓存的统计数据；若无上次缓存数据，则发送空启动日志
     * 由于多进程等可能造成Application多次执行，建议此代码不要埋点在Application中，否则可能造成启动次数偏高
     * 建议此代码埋点在统计路径触发的第一个页面中，若可能存在多个则建议都埋点
     */
    public void jsmethod_startWithAppKey(UZModuleContext moduleContext) {
        StatService.setAppKey(moduleContext.optString("appKey"));
        StatService.setAppVersionName(moduleContext.getContext(), moduleContext.optString("appVersion"));
//        StatService.setAppChannel(moduleContext.optString("channelId"));
        StatService.crashEnableSendLog(moduleContext.optBoolean("enableExceptionLog"));
        if (moduleContext.optBoolean("enableExceptionLog"))
            StatService.setOn(moduleContext.getContext(), StatService.EXCEPTION_LOG);
        StatService.setDebugOn(moduleContext.optBoolean("enableDebugOn"));
        StatService.enableDeviceMac(moduleContext.getContext(), moduleContext.optBoolean("enableDeviceMac"));
        // 开始初始化
        StatService.start(moduleContext.getContext());
    }

    /**
     * 设置百度统计是否采集隐私数据
     * authorizedState: boolean
     * 通过该接口可以控制敏感数据采集，true表示可以采集，false表示不可以采集，
     * 该方法一定要最优先调用，请在StatService.start(this)之前调用，采集这些数据可以帮助App运营人员更好的监控App的使用情况，
     * 建议有用户隐私策略弹窗的App，用户未同意前设置false,同意之后设置true
     */
    public void jsmethod_setAuthorizedState(UZModuleContext moduleContext) {
        StatService.setAuthorizedState(moduleContext.getContext(), moduleContext.optBoolean("authorizedState"));
    }

    public void jsmethod_onPageStart(UZModuleContext e) {
        StatService.onPageStart(e.getContext(), e.optString("pageName"));
    }

    public void jsmethod_onPageEnd(UZModuleContext e) {
        StatService.onPageEnd(e.getContext(), e.optString("pageName"));
    }
}
