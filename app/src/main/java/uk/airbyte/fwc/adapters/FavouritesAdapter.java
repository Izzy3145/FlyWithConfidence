package uk.airbyte.fwc.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.model.Module;

public class FavouritesAdapter extends RealmRecyclerViewAdapter<Module, FavouritesAdapter.ViewHolder> {

    private final static String TAG = FavouritesAdapter.class.getSimpleName();
    private Context mContext;
    private RealmResults<Module> mModules;
    private FavouritesAdapterListener mClickHandler;
    private int mEditing;
    private Realm realm;

    public FavouritesAdapter(RealmResults<Module> modules, Context c, FavouritesAdapterListener clickHandler, int editting) {
        super(modules, true, true);
        setHasStableIds(true);
        mModules = modules;
        mContext = c;
        realm = Realm.getDefaultInstance();
        mClickHandler = clickHandler;
        mEditing = editting;
    }


    @NonNull
    @Override
    public FavouritesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_thumbnail_item, parent,
                false);
        //pass the view to the ViewHolder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesAdapter.ViewHolder holder, int position) {

        //TODO: test when full API response available, set proper error image, sort out cropping
        //TODO: Ask - is there a cleaner/better way of doing this?
        try {
            final int adapterPosition = holder.getAdapterPosition();
            final Module module = getItem(position);
            //final Module module = mListOfModules.get(position);
            if(module != null) {
                holder.mVideoTitle.setText(module.getName());
                holder.mVideoThumbnail.setClipToOutline(true);
                Picasso.get()
                        .load(module.getMedia().getThumbnail())
                        .placeholder(R.drawable.captain_placeholder)
                        .error(R.drawable.captain)
                        .resize(160, 90)
                        .centerCrop()
                        .into(holder.mVideoThumbnail);

                if (mEditing == 0) {
                    holder.mDeleteFavBtn.setVisibility(View.GONE);
                } else {
                    holder.mDeleteFavBtn.setVisibility(View.VISIBLE);
                    holder.mDeleteFavBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mClickHandler.onClickFavDeleteMethod(module, adapterPosition);
                        }
                    });
                }
            } else {
                holder.mVideoTitle.setText("");
                holder.mVideoThumbnail.setClipToOutline(true);
                Picasso.get()
                        .load(R.drawable.artboard)
                        .placeholder(R.drawable.artboard)
                        .error(R.drawable.captain)
                        .resize(160, 90)
                        .centerCrop()
                        .into(holder.mVideoThumbnail);
            }
        } catch (IndexOutOfBoundsException e) {
            holder.mVideoTitle.setText("");
            Log.d(TAG, "Index Out Of Bounds Exception: " + e);
            Picasso.get()
                    .load(R.drawable.artboard)
                    .placeholder(R.drawable.artboard)
                    .error(R.drawable.captain)
                    .resize(160, 90)
                    .centerCrop()
                    .into(holder.mVideoThumbnail);
        }
    }

    //create onClickListener interface
    public interface FavouritesAdapterListener {
        void onClickFavMethod(Module module, int position);
        void onClickFavDeleteMethod(Module module, int position);
    }

    //create viewholder class
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.video_title_tv)
        TextView mVideoTitle;
        @BindView(R.id.videoThumbnail)
        ImageView mVideoThumbnail;
        @BindView(R.id.deleteFavBtn)
        ImageView mDeleteFavBtn;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Module module;
            int adapterPosition = getAdapterPosition();
            try {
                module = mModules.get(adapterPosition);
                mClickHandler.onClickFavMethod(module, adapterPosition);
            } catch (IndexOutOfBoundsException e){
                Log.d(TAG, "Index out of bounds exception: " + e);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mModules.size() < 5){
            return 5;
        } else {
            return mModules.size();
        }
    }
}
