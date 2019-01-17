package uk.airbyte.fwc.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.model.ShowPlay;
import uk.airbyte.fwc.viewmodels.VideoViewModel;

import static java.security.AccessController.getContext;


public class VideoFragment extends Fragment {

    private static final String TAG = VideoFragment.class.getSimpleName();

    @BindView(R.id.exo_player_view)
    SimpleExoPlayerView simpleExoPlayerView;
    @BindView(R.id.placeholder_image_view)
    ImageView placeholderImageView;
    @BindView(R.id.playbackBar)
    FrameLayout playbackBar;
//    @BindView(R.id.foregroundBar)
//    ImageView foregroundBar;
//    @BindView(R.id.backgroundBar)
//    ImageView backgroundBar;
//    @BindView(R.id.dotContainer)
//    FrameLayout dotContainer;
//    @BindView(R.id.playbackDot)
//    LinearLayout playbackDot;
    @BindView(R.id.seekBar)
    SeekBar seekBar;

    private boolean playbackReady = true;
    private SimpleExoPlayer mSimpleExoPlayer;
    private VideoViewModel mVideoViewModel;
    @Nullable
    private ShowPlay mShowPlay;

    float currentPercent = 0f;

    // Handler on UI thread
    private Handler handler = new Handler(Looper.getMainLooper());

    // Update video time
    Runnable updateBar = new Runnable() {
        @Override
        public void run() {
            onVideoTick(((float) mSimpleExoPlayer.getCurrentPosition()) / ((float) mSimpleExoPlayer.getDuration()), mSimpleExoPlayer.getCurrentPosition());
        }
    };

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideoViewModel = ViewModelProviders.of(getActivity()).get(VideoViewModel.class);
    }

    //TODO: change error image

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, view);
        simpleExoPlayerView.setVisibility(View.GONE);
        placeholderImageView.setVisibility(View.VISIBLE);
        mVideoViewModel.clearVideo();
        moveBar(0);
//        foregroundBar.setVisibility(View.VISIBLE);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // use seekBar getProgress

//                float seekBarWidth = seekBar.getWidth();
//                seekBar.setMax((int) seekBarWidth);
//
//                float seekBarProgress = seekBar.getProgress();
//
//                float percent =  seekBarProgress / seekBarWidth;
//                mSimpleExoPlayer.seekTo((long) percent);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // stopping videotick from being called or pause timer so moveBar doesn't get called
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // start videotick again so moveBar proceeds

            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mVideoViewModel = ViewModelProviders.of(getActivity()).get(VideoViewModel.class);
        mVideoViewModel.getSelected().observe(this, new Observer<ShowPlay>() {
            @Override
            public void onChanged(@Nullable ShowPlay showPlay) {
                if (mShowPlay != null) {
                    saveState();
                }
                mShowPlay = showPlay;
                passShowPlayObj(mShowPlay);
            }
        });
    }

    private void passShowPlayObj(ShowPlay showPlay) {
        if (showPlay == null) {
            videoOrImageDisplay(null, null, null, 0, 0);
        } else {
            videoOrImageDisplay(showPlay.getImage(), showPlay.getThumbnail(), showPlay.getVideoUrl(),
                    showPlay.getCurrentWindow(), showPlay.getPlayerPosition());
        }
    }

    public void videoOrImageDisplay(String image, String thumbnail, String videoUrl, int rmCurrentWindow, long rmPlayerPosition) {
        if (videoUrl != null && videoUrl.trim().length() != 0) {
            simpleExoPlayerView.setVisibility(View.VISIBLE);
            placeholderImageView.setVisibility(View.GONE);
            initializeExoPlayer(Uri.parse(videoUrl), rmCurrentWindow, rmPlayerPosition);
        } else if (image != null && image.trim().length() != 0) {
            simpleExoPlayerView.setVisibility(View.GONE);
            placeholderImageView.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.captain_placeholder)
                    .error(R.drawable.captain_placeholder)
                    .into(placeholderImageView);
        } else if (thumbnail != null && thumbnail.trim().length() != 0) {
            simpleExoPlayerView.setVisibility(View.GONE);
            placeholderImageView.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.captain_placeholder)
                    .error(R.drawable.captain_placeholder)
                    .into(placeholderImageView);
        } else {
            simpleExoPlayerView.setVisibility(View.GONE);
            placeholderImageView.setVisibility(View.VISIBLE);
            placeholderImageView.setImageResource(R.drawable.captain_placeholder);
        }
    }

    //initialise ExoPlayer
    public void initializeExoPlayer(Uri firstUri, int sCurrentWindow, long sPlaybackPosition) {
        if (mSimpleExoPlayer == null) {
            //create an instance of the ExoPlayer
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            simpleExoPlayerView.setPlayer(mSimpleExoPlayer);

            //prepare the mediasource
            String userAgent = Util.getUserAgent(getContext(), "fwc");
            MediaSource firstMediaSource = new ExtractorMediaSource(firstUri, new DefaultDataSourceFactory(getContext(),
                    userAgent), new DefaultExtractorsFactory(), null, null);
            mSimpleExoPlayer.prepare(firstMediaSource);
            mSimpleExoPlayer.setPlayWhenReady(playbackReady);
            mSimpleExoPlayer.seekTo(sCurrentWindow, sPlaybackPosition);
        }

        TimerTask progress = new TimerTask() {
            @Override
            public void run() {
                handler.post(updateBar);
            }
        };

        Timer timer = new Timer();

        // play bar update interval
        timer.scheduleAtFixedRate(progress, 0, 50);
    }

    private void moveBar(float percent) {
        float seekBarWidth = seekBar.getWidth();
        seekBar.setMax((int) seekBarWidth);
        seekBar.setProgress((int) (seekBarWidth * percent), true);


//        float backWidth = backgroundBar.getWidth();
//        foregroundBar.setTranslationX(-backWidth + ((int) (backWidth * percent)));
//        playbackDot.setTranslationX(((int) (backWidth * percent)));
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();

        if (mShowPlay != null) {
            saveState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        passShowPlayObj(mShowPlay);
    }


    //TODO: do i need this?
    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
        if (mShowPlay != null) {
            saveState();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
        mVideoViewModel.clearVideo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mVideoViewModel.closeRealm();
    }


    public void releasePlayer() {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.stop();
            mSimpleExoPlayer.release();
        }
    }

    private void saveState() {
        if (mSimpleExoPlayer != null) {
            playbackReady = mSimpleExoPlayer.getPlayWhenReady();
            mVideoViewModel.setVideoPosition(mShowPlay, mSimpleExoPlayer);
        }
        mSimpleExoPlayer = null;
    }

    void onVideoTick(float percent, long currentTimeMillis) {
        currentPercent = percent;
        moveBar(percent);
    }
}
