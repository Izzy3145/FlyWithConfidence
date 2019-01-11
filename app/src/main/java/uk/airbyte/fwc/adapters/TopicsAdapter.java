package uk.airbyte.fwc.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.RealmResults;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.fragments.TopicsFragment;
import uk.airbyte.fwc.model.Module;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.ViewHolder> {

    private static final String TAG = TopicsFragment.class.getSimpleName();

    private final Context mContext;
    private static ArrayList<RealmResults<Module>> mData;
    private static RecyclerView horizontalList;
    private static TopicsAdapterListener mClickHandler;


    public static class ViewHolder extends RecyclerView.ViewHolder implements ModulesAdapter.ModulesAdapterListener {
        public final TextView title;
        private ModulesAdapter modulesAdapter;

        public ViewHolder(View view) {
            super(view);
            Context context = itemView.getContext();
            title = (TextView) view.findViewById(R.id.horizTopicTitle);
            horizontalList = (RecyclerView) itemView.findViewById(R.id.horizTopicsRv);
            horizontalList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            modulesAdapter = new ModulesAdapter(null, this);
            horizontalList.setAdapter(modulesAdapter);
        }

        @Override
        public void onClickModule(Module module, int position) {
            mClickHandler.onClickModuleInTopic(module,position);
        }

        @Override
        public void onClickRecentsDelete(Module module, int position) {

        }
    }

    public interface TopicsAdapterListener {
        void onClickModuleInTopic(Module module, int position);
    }

    public TopicsAdapter(ArrayList<RealmResults<Module>> modulesTopics, Context context, TopicsAdapterListener clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        if (modulesTopics != null)
            mData = new ArrayList<>(modulesTopics);
        else mData = new ArrayList<>();
    }

    public TopicsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.topic_modules_rv, parent, false);
        TopicsAdapter.ViewHolder viewholder = new TopicsAdapter.ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
            if(mData != null && mData.get(position).where().findFirst() != null) {
            holder.title.setText(mData.get(position).where().findFirst().getTopic().getName());
            holder.modulesAdapter.setData(mData.get(position));
            //holder.modulesAdapter.setRowIndex(position);
        }
    }

    public void setData(ArrayList<RealmResults<Module>> data){
        mData = data;
        notifyDataSetChanged();
    }

    public void clearData(){
        mData = new ArrayList<>();
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        if(mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }
}
