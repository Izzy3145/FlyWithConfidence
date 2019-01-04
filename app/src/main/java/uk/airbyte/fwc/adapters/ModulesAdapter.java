package uk.airbyte.fwc.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.model.Module;

public class ModulesAdapter extends RecyclerView.Adapter<ModulesAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Module> mListOfModules;
    private ModulesAdapterListener mClickHandler;
    private int mEditting;

    public ModulesAdapter(Context c, ArrayList<Module> listOfModules, ModulesAdapterListener clickHandler) {
        mContext = c;
        mListOfModules = listOfModules;
        mClickHandler = clickHandler;
    }

    public ModulesAdapter(Context c, ArrayList<Module> listOfModules, ModulesAdapterListener clickHandler, int editting) {
        mContext = c;
        mListOfModules = listOfModules;
        mClickHandler = clickHandler;
        mEditting = editting;
    }

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
    public void onBindViewHolder(@NonNull ModulesAdapter.ViewHolder holder, final int position) {
        //TODO: test when full API response available set proper error image

            final Module module = mListOfModules.get(position);
            holder.mVideoTitle.setText(module.getName());
            holder.mVideoThumbnail.setClipToOutline(true);
            Picasso.get()
                    .load(module.getMedia().getThumbnail())
                    .placeholder(R.drawable.captain)
                    .error(R.drawable.captain)
                    .resize(150, 120)
                    .centerCrop()
                    .fit()
                    .into(holder.mVideoThumbnail);

        if(mEditting == 0){
            holder.mDeleteFavBtn.setVisibility(View.GONE);
        } else {
            holder.mDeleteFavBtn.setVisibility(View.VISIBLE);
            holder.mDeleteFavBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickHandler.onClickRecentsDeleteMethod(module, position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mListOfModules.size();

    }

    public void setModulesToAdapter(ArrayList<Module> foundModuleList) {
        mListOfModules = foundModuleList;
        notifyDataSetChanged();
    }

    public void clearModulesList(){
        if(mListOfModules!=null) {
            mListOfModules.clear();
        }
    }


    //create onClickListener interface
    public interface ModulesAdapterListener {
        void onClickMethod(Module module, int position);
        void onClickRecentsDeleteMethod(Module module, int position);
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
            module = mListOfModules.get(adapterPosition);
            mClickHandler.onClickMethod(module, adapterPosition);
        }
    }
}
