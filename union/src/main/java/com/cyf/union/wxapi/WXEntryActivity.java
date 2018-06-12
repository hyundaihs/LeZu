package com.cyf.union.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cyf.lezu.utils.ToastUtil;
import com.cyf.union.AppUnion;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/5/6/006.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AppUnion.Companion.getInstance().getApi().handleIntent(getIntent(), this)) {
            finish();
        }
    }

    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (!AppUnion.Companion.getInstance().getApi().handleIntent(getIntent(), this)) {
            finish();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp resp) {
        int errorCode = resp.errCode;
        switch (errorCode) {
            case BaseResp.ErrCode.ERR_OK:
                //用户同意
                ToastUtil.show(this, "分享成功");
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //用户拒绝
                ToastUtil.show(this, "用户拒绝");
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //用户取消
                ToastUtil.show(this, "用户取消");
                finish();
                break;
            default:
                ToastUtil.show(this, "分享失败,请稍后再试");
                finish();
                break;
        }
    }
}
