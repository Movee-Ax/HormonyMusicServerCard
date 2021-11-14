package com.mingyang.servicecard1;

import ohos.app.Context;
import ohos.global.resource.RawFileEntry;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.player.Player;

import java.io.IOException;

public class MusicPlayerManager {
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.LOG_APP, 0x111, "MusicPlayerManager");

    private int mCurrentPlayingIndex = 0;
    private final Player mPlayer;
    private String[] mUris = null;
    private boolean mCurrentMusicPrepared = false;

    public MusicPlayerManager(Context context) {
        mPlayer = new Player(context);
    }

    public void initMusicList(Context context, String[] uris) {
        HiLog.info(TAG, "init music list!");
        mUris = uris;
        if (uris == null || mCurrentPlayingIndex < 0 || mCurrentPlayingIndex >= uris.length) {
            return;
        }
        String uri = mUris[mCurrentPlayingIndex];
        mCurrentMusicPrepared = prepareMusic(context, uri);
    }

    public String playMusic() {
        if (!mCurrentMusicPrepared) {
            HiLog.error(TAG, "Current play list has not prepared!");
            return null;
        }
        boolean playResult = mPlayer.play();
        HiLog.info(TAG, "play music is OK? " + playResult);
        return getFileName(mUris[mCurrentPlayingIndex]);
    }

    private String getFileName(String filePath) {
        int firstIndex = filePath.lastIndexOf("/");
        int lastIndex = filePath.lastIndexOf(".");
        return filePath.substring(firstIndex + 1, lastIndex);
    }

    public void pauseMusic() {
        if (!mPlayer.isNowPlaying()) {
            HiLog.error(TAG, "Current play list has not prepared!");
        }
        mPlayer.pause();
    }

    public String playNextMusic(Context context) {
        if (!mCurrentMusicPrepared) {
            HiLog.error(TAG, "play next music but not prepared!");
            return null;
        }
        if (mUris == null || mCurrentPlayingIndex < 0 || mCurrentPlayingIndex + 1 >= mUris.length) {
            HiLog.warn(TAG, "it is end");
            return null;
        }

        mCurrentPlayingIndex++;
        String uri = mUris[mCurrentPlayingIndex];
        boolean preparedResult = prepareMusic(context, uri);
        HiLog.info(TAG, "prepare result is " + preparedResult);
        return playMusic();
    }

    public String playPreviousMusic(Context context) {
        if (!mCurrentMusicPrepared) {
            HiLog.error(TAG, "play next music but not prepared!");
            return null;
        }
        if (mUris == null || mCurrentPlayingIndex >= mUris.length || mCurrentPlayingIndex <= 0) {
            HiLog.warn(TAG, "it is first");
            return null;
        }

        mCurrentPlayingIndex--;
        String uri = mUris[mCurrentPlayingIndex];
        boolean preparedResult = prepareMusic(context, uri);
        HiLog.info(TAG, "prepare result is " + preparedResult);
        return playMusic();
    }

    private boolean prepareMusic(Context context, String uri) {
        RawFileEntry entry = context.getResourceManager().getRawFileEntry(uri);
        if (entry == null) {
            HiLog.error(TAG, "prepare music but entry is null");
            return false;
        }
        try {
            mPlayer.setSource(entry.openRawFileDescriptor());
            return mPlayer.prepare();
        } catch (IOException e) {
            HiLog.error(TAG, "open raw file descriptor fail!");
        }
        return false;
    }
}
