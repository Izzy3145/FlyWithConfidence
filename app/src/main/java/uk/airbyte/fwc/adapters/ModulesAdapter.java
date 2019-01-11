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

public class ModulesAdapter extends RealmRecyclerViewAdapter<Module, ModulesAdapter.ViewHolder> {

    private Context mContext;
    private RealmResults<Module> mModules;
    private ModulesAdapterListener mClickHandler;
    private int mEditting;
    private Realm realm;
    private static final String TAG = ModulesAdapter.class.getSimpleName();

    public ModulesAdapter(RealmResults<Module> modules, ModulesAdapterListener clickHandler){
        super(modules, true, true);
        //TODO: test setHasStableIds(true)
        //setHasStableIds(true);
       // mContext = c;
        realm = Realm.getDefaultInstance();
        mClickHandler = clickHandler;
       // mEditting = editting;
        mModules = modules;
    }

    public ModulesAdapter(RealmResults<Module> modules, Context c, ModulesAdapterListener clickHandler, int editting){
        super(modules, true, true);
        //setHasStableIds(true);
        mContext = c;
        realm = Realm.getDefaultInstance();
        mClickHandler = clickHandler;
        mEditting = editting;
        mModules = modules;
    }

    /*public void changeEditMode(){
        if(!mEditting){
            mEditting = true;
        } else {
            mEditting = false;
        }
    }*/

    /*public ModulesAdapter(Context c, ArrayList<Module> listOfModules, FavouritesAdapterListener clickHandler) {
        mContext = c;
        mListOfModules = listOfModules;
        mClickHandler = clickHandler;
    }

    public ModulesAdapter(Context c, ArrayList<Module> listOfModules, FavouritesAdapterListener clickHandler, int editting) {
        mContext = c;
        mListOfModules = listOfModules;
        mClickHandler = clickHandler;
        mEditting = editting;
    }*/

    @NonNull
    @Override
    public ModulesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_thumbnail_item, parent,
                false);
        //pass the view to the ViewHolder
        ModulesAdapter.ViewHolder viewHolder = new ModulesAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ModulesAdapter.ViewHolder holder, int position) {
        //TODO: test when full API response available & set proper error image
           final int adapterPosition = holder.getAdapterPosition();
            final Module module = getItem(position);
           // final Module module = mListOfModules.get(position);
            holder.mVideoTitle.setText(module.getName());
            holder.mVideoThumbnail.setClipToOutline(true);
            Picasso.get()
                    .load(module.getMedia().getThumbnail())
                    .placeholder(R.drawable.captain_placeholder)
                    .error(R.drawable.captain)
                    .resize(150, 120)
                    .centerCrop()
                    .fit()
                    .into(holder.mVideoThumbnail);

            if (mEditting == 0) {
                holder.mDeleteFavBtn.setVisibility(View.GONE);
            } else {
                holder.mDeleteFavBtn.setVisibility(View.VISIBLE);
                holder.mDeleteFavBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mClickHandler.onClickRecentsDelete(module, adapterPosition);
                    }
                });
            }
        }


    public void setData(RealmResults<Module> modules) {
        mModules = modules;
        Log.d(TAG, "Modules realm list size: " +mModules.size());
        //notifyDataSetChanged();
        updateData(modules);
    }

    //create onClickListener interface
    public interface ModulesAdapterListener {
        void onClickModule(Module module, int position);
        void onClickRecentsDelete(Module module, int position);
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
            module = getItem(adapterPosition);
            mClickHandler.onClickModule(module, adapterPosition);
        }
    }
}
