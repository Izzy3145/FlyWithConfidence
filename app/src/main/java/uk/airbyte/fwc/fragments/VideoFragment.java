package uk.airbyte.fwc.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import uk.airbyte.fwc.MainActivity;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.model.ShowPlay;
import uk.airbyte.fwc.utils.Const;
import uk.airbyte.fwc.viewmodels.ModuleViewModel;
import uk.airbyte.fwc.viewmodels.VideoViewModel;


public class VideoFragment extends Fragment {

    private static final String TAG = VideoFragment.class.getSimpleName();

    @BindView(R.id.exo_player_view)
    SimpleExoPlayerView simpleExoPlayerView;
    @BindView(R.id.placeholder_image_view)
    ImageView placeholderImageView;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.vid_full_screen)
    ImageView vidFullScreen;
    @BindView(R.id.exit_cross)
    ImageView exitCross;
    @BindView(R.id.intro_exit_cross)
    ImageView introExitCross;
    @BindView(R.id.vid_fav_off)
    ImageView favBtnOff;
    @BindView(R.id.vid_fav_on)
    ImageView favBtnOn;
    @BindView(R.id.playBtn)
    ImageView playBtn;
    @BindView(R.id.pauseBtn)
    ImageView pauseBtn;
    @BindView(R.id.intro_play_btn)
    Button introPlayBtn;
    @BindView(R.id.intro_pause_btn)
    Button introPauseBtn;
    @BindView(R.id.parentVideoView)
    ConstraintLayout parentVideoView;
    @BindView(R.id.module_vid_overlay)
    ConstraintLayout moduleVidOverlay;
    @BindView(R.id.intro_vid_overlay_land)
    ConstraintLayout introVidOverlayLand;
    @BindView(R.id.welcome_desc_land)
    TextView welcomeDescription;
    @BindView(R.id.welcome_title_land)
    TextView welcomeTitle;

    float currentPercent = 0f;
    private boolean playbackReady = true;
    private SimpleExoPlayer mSimpleExoPlayer;
    // Update video time
    Runnable updateBar = new Runnable() {
        @Override
        public void run() {
            //get percentage of the way through the video, current pos/total duration
            //use it to move the seekBar on UI
            if (mSimpleExoPlayer != null) {
                onVideoTick(((float) mSimpleExoPlayer.getCurrentPosition()) /
                        ((float) mSimpleExoPlayer.getDuration()), mSimpleExoPlayer.getCurrentPosition());
            }
        }
    };
    private long introVidPosition;
    private VideoViewModel mVideoViewModel;
    private ModuleViewModel mModuleViewModel;
    @Nullable
    private ShowPlay mShowPlay;
    private Boolean isFavourite;
    private Boolean isIntro;
    private Timer timer;
    private boolean mUserIsSeeking;
    private IntroVidListener mListener;
    // Handler on UI thread
    private Handler handler = new Handler(Looper.getMainLooper());

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideoViewModel = ViewModelProviders.of(getActivity()).get(VideoViewModel.class);
        mModuleViewModel = ViewModelProviders.of(getActivity()).get(ModuleViewModel.class);
        mVideoViewModel = ViewModelProviders.of(getActivity()).get(VideoViewModel.class);
        mVideoViewModel.getSelected().observe(this, new Observer<ShowPlay>() {
            @Override
            public void onChanged(@Nullable ShowPlay showPlay) {
                if (mShowPlay != null) {
                    saveState();
                }
                mShowPlay = showPlay;
                passShowPlayObj(mShowPlay);

                if (mShowPlay != null && !mShowPlay.isIntro()) {
                    isFavourite = mModuleViewModel.getFavouritedStatus(mShowPlay.getModuleID());
                    setFavBtn(isFavourite);
                }
                if (mShowPlay != null && mShowPlay.isIntro()) {
                    isIntro = true;
                    setUpIntroVidUI();
                }
            }
        });

        if(savedInstanceState != null){
            isIntro = savedInstanceState.getBoolean(Const.IS_INTRO_VID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, view);
        simpleExoPlayerView.setVisibility(View.GONE);
        placeholderImageView.setVisibility(View.VISIBLE);
        mVideoViewModel.clearVideo();

        Resources res = getActivity().getResources();
        final int whiteColor = res.getColor(R.color.colorWhite);
        playBtn.setColorFilter(whiteColor, PorterDuff.Mode.SRC_ATOP);
        pauseBtn.setColorFilter(whiteColor, PorterDuff.Mode.SRC_ATOP);

        Log.d(TAG, " passing through onCreateView()");

        if(isIntro != null && isIntro){
            introVidOverlayLand.setBackgroundColor(getResources().getColor(R.color.trans_grey));
            introVidOverlayLand.setVisibility(View.VISIBLE);
            togglePlayPause();

        } else {

            parentVideoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    moduleVidOverlay.setBackgroundColor(getResources().getColor(R.color.trans_grey));
                    moduleVidOverlay.setVisibility(View.VISIBLE);
                    togglePlayPause();
                    AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
                    anim.setDuration(3000);
                    anim.setRepeatCount(0);
                    anim.setFillAfter(true);
                    moduleVidOverlay.startAnimation(anim);
                    return false;
                }
            });
        }

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playbackReady = !playbackReady;
                if (mSimpleExoPlayer != null) {
                    mSimpleExoPlayer.setPlayWhenReady(playbackReady);
                }
                togglePlayPause();
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playbackReady = !playbackReady;
                if (mSimpleExoPlayer != null) {
                    mSimpleExoPlayer.setPlayWhenReady(playbackReady);
                }
                togglePlayPause();
            }
        });

        vidFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).hideNavBarAndLandscape();
            }
        });

        exitCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentOrientation = getResources().getConfiguration().orientation;
                if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    ((MainActivity) getActivity()).onBackPressed();
                } else {
                    Navigation.findNavController(getActivity(), R.id.my_nav_host_fragment)
                            .popBackStack();
                    ((MainActivity) getActivity()).showNavBar();

                }
            }
        });

        favBtnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavourite) {
                    isFavourite = false;
                } else {
                    isFavourite = true;
                }
                mModuleViewModel.setFavouriteStatus(isFavourite, mShowPlay.getModuleID());
                setFavBtn(isFavourite);
            }
        });

        favBtnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavourite) {
                    isFavourite = false;
                } else {
                    isFavourite = true;
                }
                mModuleViewModel.setFavouriteStatus(isFavourite, mShowPlay.getModuleID());
                setFavBtn(isFavourite);
            }
        });

        moveBar(0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int userSelectedPosition = 0;

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mUserIsSeeking = true;
                timer.cancel();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    userSelectedPosition = progress;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mUserIsSeeking = false;
                float seekBarWidth = seekBar.getWidth();
                float percent = userSelectedPosition / seekBarWidth;
                long duration = mSimpleExoPlayer.getDuration();
                float newPosition = percent * duration;
                mSimpleExoPlayer.seekTo((long) newPosition);

                seekBar.setProgress(seekBar.getProgress());
                TimerTask progress = new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(updateBar);
                    }
                };
                timer = new Timer();
                timer.scheduleAtFixedRate(progress, 0, 50);
            }
        });
        return view;
    }

    private void setUpIntroVidUI() {
        moduleVidOverlay.setVisibility(View.GONE);
        introVidOverlayLand.setBackgroundColor(getResources().getColor(R.color.trans_grey));
        togglePlayPause();
        introVidOverlayLand.setVisibility(View.VISIBLE);
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(3000);
        anim.setRepeatCount(0);
        anim.setFillAfter(true);
        introVidOverlayLand.startAnimation(anim);

        parentVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                introVidOverlayLand.setBackgroundColor(getResources().getColor(R.color.trans_grey));
                togglePlayPause();
                introVidOverlayLand.setVisibility(View.VISIBLE);
                AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
                anim.setDuration(3000);
                anim.setRepeatCount(0);
                anim.setFillAfter(true);
                introVidOverlayLand.startAnimation(anim);
                return false;
            }
        });

        introExitCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playbackReady = false;
                if (mSimpleExoPlayer != null) {
                    introVidPosition = mSimpleExoPlayer.getCurrentPosition();
                    mListener.setIntroVidPosition(introVidPosition);
                    mSimpleExoPlayer.setPlayWhenReady(playbackReady);
                }

                introVidOverlayLand.setVisibility(View.GONE);
                parentVideoView.setOnTouchListener(null);

                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        introPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playbackReady = !playbackReady;
                if (mSimpleExoPlayer != null) {
                    introVidPosition = mSimpleExoPlayer.getCurrentPosition();
                    mListener.setIntroVidPosition(introVidPosition);
                    mSimpleExoPlayer.setPlayWhenReady(playbackReady);
                }
                togglePlayPause();
            }
        });

        introPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playbackReady = !playbackReady;
                if (mSimpleExoPlayer != null) {
                    mSimpleExoPlayer.setPlayWhenReady(playbackReady);
                    mSimpleExoPlayer.seekTo(introVidPosition);
                }
                ((MainActivity) getActivity()).hideNavBarAndLandscape();

                togglePlayPause();
            }
        });
    }

    public void togglePlayPause() {
        if (isIntro) {
            if (!playbackReady) {
                introPauseBtn.setVisibility(View.GONE);
                introPlayBtn.setVisibility(View.VISIBLE);
                AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
                anim.setDuration(3000);
                anim.setRepeatCount(0);
                anim.setFillAfter(true);
                introPlayBtn.startAnimation(anim);
            } else {
                introPlayBtn.setVisibility(View.GONE);
                introPauseBtn.setVisibility(View.VISIBLE);
                AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
                anim.setDuration(3000);
                anim.setRepeatCount(0);
                anim.setFillAfter(true);
                introPauseBtn.startAnimation(anim);
            }
        } else {
            if (!playbackReady) {
                pauseBtn.setVisibility(View.GONE);
                playBtn.setVisibility(View.VISIBLE);
                AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
                anim.setDuration(3000);
                anim.setRepeatCount(0);
                anim.setFillAfter(true);
                playBtn.startAnimation(anim);
            } else {
                playBtn.setVisibility(View.GONE);
                pauseBtn.setVisibility(View.VISIBLE);
                AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
                anim.setDuration(3000);
                anim.setRepeatCount(0);
                anim.setFillAfter(true);
                pauseBtn.startAnimation(anim);
            }
        }
    }

    private void setFavBtn(Boolean isFavourite) {
        if (isFavourite) {
            favBtnOff.setVisibility(View.GONE);
            favBtnOn.setVisibility(View.VISIBLE);
        } else {
            favBtnOn.setVisibility(View.GONE);
            favBtnOff.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(isIntro != null){
            outState.putBoolean(Const.IS_INTRO_VID, isIntro);
        }
    }

    private void passShowPlayObj(ShowPlay showPlay) {
        if (showPlay != null) {
            isIntro = showPlay.isIntro();
            videoOrImageDisplay(showPlay.getImage(), showPlay.getThumbnail(), showPlay.getVideoUrl(),
                    showPlay.getCurrentWindow(), showPlay.getPlayerPosition());
        } else {
            isIntro = false;
            videoOrImageDisplay(null, null, null, 0, 0);
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
        timer = new Timer();
        // play bar update interval
        timer.scheduleAtFixedRate(progress, 0, 50);
        //every 50ms, call VideoTick to move the progress bar
    }

    private void moveBar(float percent) {
        float seekBarWidth = seekBar.getWidth();
        seekBar.setMax((int) seekBarWidth);
        seekBar.setProgress((int) (seekBarWidth * percent), true);

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
        if(isIntro){
            setUpIntroVidUI();
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            vidFullScreen.setVisibility(View.GONE);
        } else {
            vidFullScreen.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (IntroVidListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement IntroVidListener");
        }
    }

    public interface IntroVidListener{
        void setIntroVidPosition(long inPosition);
    }
}
