package com.mingyang.servicecard1.widget.widget;

import com.mingyang.servicecard1.MusicPlayerManager;
import com.mingyang.servicecard1.ResourceTable;
import com.mingyang.servicecard1.widget.controller.FormController;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.FormBindingData;
import ohos.aafwk.ability.FormException;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.zson.ZSONObject;

/**
 * The WidgetImpl
 */
public class WidgetImpl extends FormController {
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, WidgetImpl.class.getName());

    private static final String PLAY = "play";
    private static final String PAUSE = "pause";
    private static final String PLAY_PRE = "play_pre";
    private static final String PLAY_NEXT = "play_next";
    private static final String GET_FAVORITE = "get_favorite";
    private static final int DIMENSION_1x2 = 1;
    private static final int DIMENSION_2x2 = 2;
    private static final int DIMENSION_2x4 = 3;

    private final Context mContext;
    private final MusicPlayerManager mPlayerManager;

    public WidgetImpl(Context context, String formName, Integer dimension) {
        super(context, formName, dimension);
        mContext = context;
        mPlayerManager = new MusicPlayerManager(context);
        mPlayerManager.initMusicList(context,
                new String[]{"resources/rawfile/demo.wav", "resources/rawfile/dynamic.wav"});
                // new String[]{"resources/rawfile/sb.wav"});
    }

    @Override
    public ProviderFormInfo bindFormData() {
        ZSONObject data = new ZSONObject();
        data.put("songName", context.getString(ResourceTable.String_song_name_default));
        switch (dimension) {
            case DIMENSION_1x2:
                data.put("isMiniWidget", true);
                break;
            case DIMENSION_2x2:
                data.put("isSmallWidget", true);
                break;
            case DIMENSION_2x4:
                data.put("isLargeWidget", true);
                break;
            default:
                break;
        }
        FormBindingData bindingData = new FormBindingData(data);
        ProviderFormInfo info = new ProviderFormInfo();
        info.setJsBindingData(bindingData);
        return info;
    }

    @Override
    public void updateFormData(long formId, Object... vars) {
    }

    @Override
    public void onTriggerFormEvent(long formId, String message) {
        ZSONObject zsonObject = ZSONObject.stringToZSON(message);

        // Do something here after receive the message from js card
        ZSONObject result = new ZSONObject();
        String songName = null;
        switch (zsonObject.getString("mAction")) {
            case PLAY:
                result.put("itemContent", "you clicked play");
                result.put("isPause", false);
                songName = mPlayerManager.playMusic();
                result.put("songName", songName);
                break;
            case PAUSE:
                result.put("isPause", true);
                result.put("songName", context.getString(ResourceTable.String_song_name_default));
                mPlayerManager.pauseMusic();
                break;
            case PLAY_PRE:
                result.put("isPause", false);
                songName = mPlayerManager.playPreviousMusic(context);
                if (songName == null) {
                    makeToast();
                    return;
                }
                result.put("songName", songName);
                break;
            case PLAY_NEXT:
                result.put("isPause", false);
                songName = mPlayerManager.playNextMusic(mContext);
                if (songName == null) {
                    makeToast();
                    return;
                }
                result.put("songName", songName);
                break;
            default:
                break;
        }

        // Update js card
        try {
            if (mContext instanceof Ability) {
                ((Ability) mContext).updateForm(formId, new FormBindingData(result));
            }
        } catch (FormException e) {
            HiLog.error(TAG, e.getMessage());
        }
    }

    @Override
    public Class<? extends AbilitySlice> getRoutePageSlice(Intent intent) {
        return null;
    }

    private void makeToast() {
        ToastDialog toastDialog = new ToastDialog(mContext);
        toastDialog.setText("沒有歌曲了");
        toastDialog.show();
    }
}