package ae.oleapp.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleKeyValuePair;

public class OlePreviewFieldAdapter extends RecyclerView.Adapter<OlePreviewFieldAdapter.ViewHolder> {

    private final Context context;
    private List<OleKeyValuePair> list;
    private ItemClickListener itemClickListener;
    private boolean isField = false;

    public OlePreviewFieldAdapter(Context context, List<OleKeyValuePair> list, boolean isField) {
        this.context = context;
        this.list = list;
        this.isField = isField;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setDatasource(List<OleKeyValuePair> list, boolean isField) {
        this.list = list;
        this.isField = isField;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olepreview_field, parent, false);
        return new ViewHolder(v);
    }

    private void setItemWidth(View view, ViewGroup parent) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
//        params.width = (parent.getMeasuredWidth() / 2);
        params.height = (parent.getMeasuredHeight() / 2);
        view.setLayoutParams(params);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = list.get(position).getValue();
        if (isField) {
            holder.imgVuShirt.setVisibility(View.INVISIBLE);
            holder.imgVuField.setVisibility(View.VISIBLE);
            holder.imgVuFieldBg.setVisibility(View.VISIBLE);
            holder.imgVuFieldBg.setImageResource(getDrawable(name));
            holder.imgVuField.setImageResource(getDrawable(name+"_img"));
        }
        else {
            holder.imgVuShirt.setVisibility(View.VISIBLE);
            holder.imgVuField.setVisibility(View.INVISIBLE);
            holder.imgVuFieldBg.setVisibility(View.INVISIBLE);
            holder.imgVuShirt.setImageResource(getDrawable(name));
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getAdapterPosition());
            }
        });
    }

    public int getDrawable(String str) {
        Resources resources = context.getResources();
        return resources.getIdentifier(str, "drawable", context.getPackageName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgVuField, imgVuFieldBg, imgVuShirt;
        CardView layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgVuField = itemView.findViewById(R.id.img_vu_field);
            imgVuFieldBg = itemView.findViewById(R.id.img_vu_field_bg);
            imgVuShirt = itemView.findViewById(R.id.img_vu_shirt);
            layout = itemView.findViewById(R.id.rel_main);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}