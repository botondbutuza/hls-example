package uk.co.botondbutuza.android.minglvision;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    public static final String VIDEO_1 = "http://europe.minglvision.com/hls/cam1/index.m3u8";
    public static final String VIDEO_2 = "http://europe.minglvision.com/hls/cam2/index.m3u8";

    @BindView(R.id.video_1)     SimpleExoPlayerView video1;
    @BindView(R.id.video_2)     SimpleExoPlayerView video2;
    @BindView(R.id.button_1)    View button1;
    @BindView(R.id.button_2)    View button2;

    private TrackSelector trackSelector;
    private LoadControl loadControl;
    private SimpleExoPlayer player1, player2;
    private HlsMediaSource videoSource1, videoSource2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        createControls();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer(player1);
        releasePlayer(player2);
    }

    @OnClick(R.id.button_1)
    protected void initPlayer1() {
        // Create the player
        player1 = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        player1.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_READY) {
                    releasePlayer(player2);
                    player1.setPlayWhenReady(true);

                    button1.setEnabled(false);
                    button2.setEnabled(true);
                }
            }

            @Override public void onLoadingChanged(boolean isLoading) {}
            @Override public void onTimelineChanged(Timeline timeline, Object manifest) {}
            @Override public void onPlayerError(ExoPlaybackException error) {}
            @Override public void onPositionDiscontinuity() {}
        });

        player1.prepare(videoSource1);
        video1.setPlayer(player1);
    }

    @OnClick(R.id.button_2)
    protected void initPlayer2() {
        player2 = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        player2.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_READY) {
                    releasePlayer(player1);
                    player2.setPlayWhenReady(true);

                    button1.setEnabled(true);
                    button2.setEnabled(false);
                }
            }

            @Override public void onLoadingChanged(boolean isLoading) {}
            @Override public void onTimelineChanged(Timeline timeline, Object manifest) {}
            @Override public void onPlayerError(ExoPlaybackException error) {}
            @Override public void onPositionDiscontinuity() {}
        });

        player2.prepare(videoSource2);
        video2.setPlayer(player2);
    }

    private void createControls() {
        Handler mainHandler = new Handler();
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(mainHandler, videoTrackSelectionFactory);

        loadControl = new DefaultLoadControl();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "minglvision"), bandwidthMeter);

        videoSource1 = new HlsMediaSource(Uri.parse(VIDEO_1), dataSourceFactory, null, null);
        videoSource2 = new HlsMediaSource(Uri.parse(VIDEO_2), dataSourceFactory, null, null);
    }

    private void releasePlayer(ExoPlayer player) {
        if (player != null) {
            player.release();
        }
    }
}
