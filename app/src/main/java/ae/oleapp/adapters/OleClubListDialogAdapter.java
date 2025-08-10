package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Club;
import ae.oleapp.models.Field;

public class OleClubListDialogAdapter extends RecyclerView.Adapter<OleClubListDialogAdapter.ViewHolder> {

    private final Context context;
    private final List<Club> list;
    private final List<Field> fieldList;
    private ItemClickListener itemClickListener;
    private boolean isField = false;

    public OleClubListDialogAdapter(Context context, List<Club> list, List<Field> fieldList, boolean isField) {
        this.context = context;
        this.list = list;
        this.isField = isField;
        this.fieldList = fieldList;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleclub_dialog_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (isField) {
            Field field = fieldList.get(position);
            holder.tvName.setText(field.getName());
            Glide.with(context).load(field.getImage().getPhotoPath()).into(holder.imgVu);
        }
        else {
            Club club = list.get(position);
            holder.tvName.setText(club.getName());
            Glide.with(context).load(club.getLogoPath()).into(holder.imgVu);
        }
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(v, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        if (isField) {
            return fieldList.size();
        }
        else {
            return list.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName;
        ImageView imgVu;
        RelativeLayout mainLayout;

        ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            imgVu = itemView.findViewById(R.id.img_vu);
            mainLayout = itemView.findViewById(R.id.main);
        }
    }

    public interface ItemClickListener {
        void onItemClicked(View view, int pos);
    }
}