package uk.airbyte.fwc.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.model.Module;
import uk.airbyte.fwc.model.ShowPlay;
import uk.airbyte.fwc.viewmodels.HomeViewModel;


public class VideoFragment extends Fragment {
    private static final String TAG = VideoFragment.class.getSimpleName();

    private static final String PLAYER_POSITION = "playback_position";
    private static final String PLAYBACK_READY = "playback_ready";
    private long playbackPosition;
    private boolean playbackReady = true;
    private int currentWindow;
    @Nullable
    private String mVideoString;
    @Nullable
    private String mImageString;
    @Nullable
    private String mThumbnailString;
    private SimpleExoPlayer mSimpleExoPlayer;
    private HomeViewModel homeViewModel;
    private Realm realm;
    private Module mModule;
    private ShowPlay showPlayObj;

    @BindView(R.id.exo_player_view)
    SimpleExoPlayerView simpleExoPlayerView;
    @BindView(R.id.placeholder_image_view)
    ImageView placeholderImageView;


    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        homeViewModel.getSelected().observe(this, new Observer<ShowPlay>() {
            @Override
            public void onChanged(@Nullable ShowPlay showPlay) {
                if (showPlay != null) {
                    showPlayObj = showPlay;
                    videoOrImageDisplay(showPlayObj.getImage(), showPlayObj.getThumbnail(), showPlayObj.getVideoUrl());
                    Log.d(TAG, "Video string received: " + showPlayObj.getVideoUrl());
                }
            }
        });
        realm = Realm.getDefaultInstance();
    }

    //TODO: change error image

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, view);

        //if video has already been started, pick up from where it left off
        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong(PLAYER_POSITION);
            playbackReady = savedInstanceState.getBoolean(PLAYBACK_READY);
        }

        simpleExoPlayerView.setVisibility(View.GONE);
        placeholderImageView.setVisibility(View.VISIBLE);

        return view;
    }

    public void videoOrImageDisplay(String image, String thumbnail, String videoUrl) {
        if (videoUrl!= null && videoUrl.trim().length() != 0) {

            simpleExoPlayerView.setVisibility(View.VISIBLE);
            placeholderImageView.setVisibility(View.GONE);

            initializeExoPlayer(Uri.parse(videoUrl));

        } else if (image != null && image.trim().length() != 0) {

            simpleExoPlayerView.setVisibility(View.GONE);
            placeholderImageView.setVisibility(View.VISIBLE);

            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.captain)
                    .error(R.drawable.captain)
                    .into(placeholderImageView);


        } else if (thumbnail != null && thumbnail.trim().length() != 0) {

            simpleExoPlayerView.setVisibility(View.GONE);
            placeholderImageView.setVisibility(View.VISIBLE);

            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.captain)
                    .error(R.drawable.captain)
                    .into(placeholderImageView);

        } else {

            simpleExoPlayerView.setVisibility(View.GONE);
            placeholderImageView.setVisibility(View.VISIBLE);
            //TODO: set default placeholder image
            placeholderImageView.setImageResource(R.drawable.captain);
        }
    }

    //initialise ExoPlayer
    public void initializeExoPlayer(Uri firstUri) {
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
            mSimpleExoPlayer.seekTo(currentWindow, playbackPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save state so that upon rotation, the video doesn't restart
        if (mSimpleExoPlayer != null) {
            playbackPosition = mSimpleExoPlayer.getCurrentPosition();
            playbackReady = mSimpleExoPlayer.getPlayWhenReady();
            currentWindow = mSimpleExoPlayer.getCurrentWindowIndex();
        }
        outState.putLong(PLAYER_POSITION, playbackPosition);
        outState.putBoolean(PLAYBACK_READY, playbackReady);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveState();
        releasePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(showPlayObj!=null) {
            videoOrImageDisplay(showPlayObj.getImage(), showPlayObj.getThumbnail(), showPlayObj.getVideoUrl());
        }
        //TODO: start video from last saved position
    }

    @Override
    public void onStop() {
        super.onStop();
        saveState();
        releasePlayer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        showPlayObj = null;
        releasePlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        realm.close();
    }

    public void releasePlayer() {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.stop();
            mSimpleExoPlayer.release();
            mSimpleExoPlayer = null;
        }
    }

    private void saveState(){
        if (mSimpleExoPlayer != null) {
            playbackReady = false;

            playbackPosition = mSimpleExoPlayer.getCurrentPosition();
            Log.d(TAG, "Playback position: " + playbackPosition);
            currentWindow = mSimpleExoPlayer.getCurrentWindowIndex();
            Log.d(TAG, "Current window: " + currentWindow);

            mModule = realm.where(Module.class)
                    .equalTo("id", showPlayObj.getModuleID())
                    .findFirst();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    mModule.setCurrentWindow(currentWindow);
                    mModule.setPlayerPosition(playbackPosition);
                    realm.copyToRealmOrUpdate(mModule);
                }
            });
        }
    }
}
