package ua.rdev.facerecognition.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ua.rdev.facerecognition.R;

/**
 * Created by Руслан on 25.03.2018.
 */

class MainRecyclerViewAdapter extends RecyclerView.Adapter {
    int items = 0;
    public Model model;

    public void setData(Model model) {
        this.model = model;
        items = model.photos.get(0).tags.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item, parent, false);
        return new TagItem(v);
    }

    class TagItem extends RecyclerView.ViewHolder {
        TextView tagTv;
        TextView statusTv;
        TextView widthTv;
        TextView heightTv;
        TextView confirmTv;

        public TagItem(View itemView) {
            super(itemView);
            tagTv = itemView.findViewById(R.id.tag);
            statusTv = itemView.findViewById(R.id.status);
            widthTv = itemView.findViewById(R.id.width);
            heightTv = itemView.findViewById(R.id.height);
            confirmTv = itemView.findViewById(R.id.confirm);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Photo photo = model.photos.get(0);
        Photo.Tag tag = photo.tags.get(position);
        try {
            if (tag.uids.get(0).confidence > 35) {
                ((TagItem) holder).tagTv.setText("Label: " + tag.uids.get(0).uid);
            }
        } catch (IndexOutOfBoundsException e) {
            ((TagItem) holder).tagTv.setText("Tid: " + tag.tid);
        }
        ((TagItem) holder).statusTv.setText("status: " + model.status);
        ((TagItem) holder).widthTv.setText("width: " + tag.width);
        ((TagItem) holder).heightTv.setText("height: " + tag.height);
        ((TagItem) holder).confirmTv.setText("confirm: " + Boolean.toString(tag.confirmed));
    }

    @Override
    public int getItemCount() {
        return items;
    }
}
