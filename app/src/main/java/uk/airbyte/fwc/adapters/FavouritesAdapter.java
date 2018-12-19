package uk.airbyte.fwc.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.fragments.ModuleFragment;
import uk.airbyte.fwc.fragments.VideoFragment;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {

   // private ArrayList<RecipeItem> mListOfRecipes;
   private Context mContext;
   private FavouritesAdapterListener mClickHandler;

   // public FavouritesAdapter(Context c, ArrayList<RecipeItem> listOfRecipes, RecipeItemAdapterListener clickHandler) {
   public FavouritesAdapter(Context c, FavouritesAdapterListener clickHandler) {
        mContext = c;
        mClickHandler = clickHandler;
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
    public void onBindViewHolder(@NonNull FavouritesAdapter.ViewHolder holder, int i) {
       // RecipeItem recipe = mListOfRecipes.get(position);
       // String recipeName = recipe.getName();
        holder.mVideoTitle.setText("Introduction");
        holder.mVideoThumbnail.setImageResource(R.drawable.captain);

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    //create onClickListener interface
    public interface FavouritesAdapterListener {
        //void onClickMethod(RecipeItem recipeItem, int position);
        void onClickMethod(int position);

    }

    //create viewholder class
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.video_title_tv)
        TextView mVideoTitle;
        @BindView(R.id.videoThumbnail)
        ImageView mVideoThumbnail;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //RecipeItem recipeItem;
            int adapterPosition = getAdapterPosition();
            //recipeItem = mListOfRecipes.get(adapterPosition);
            mClickHandler.onClickMethod(adapterPosition);
        }
    }
}
