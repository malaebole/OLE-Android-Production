package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleUserPermission;

public class OleUserPermissionAdapter extends RecyclerView.Adapter<OleUserPermissionAdapter.ViewHolder> {

    private final Context context;
    private final List<OleUserPermission> list;
    public List<OleUserPermission> selectedList = new ArrayList<>();
    private ItemClickListener itemClickListener;

    public OleUserPermissionAdapter(Context context, List<OleUserPermission> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelection(OleUserPermission data) {
        int index = checkIfExist(data.getId());
        if (index == -1) {
            selectedList.add(data);
        }
        else {
            selectedList.remove(index);
        }
        notifyDataSetChanged();
    }

    private int checkIfExist(String id) {
        int index = -1;
        for (int i = 0; i < selectedList.size(); i++) {
            if (selectedList.get(i).getId().equalsIgnoreCase(id)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleuser_role, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleUserPermission permission = list.get(position);
        holder.tvTitle.setText(permission.getPermName());
        int index = checkIfExist(permission.getId());
        holder.btnSwitch.setChecked(index != -1);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.itemClicked(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        SwitchCompat btnSwitch;
        RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            btnSwitch = itemView.findViewById(R.id.btn_switch);
            layout = itemView.findViewById(R.id.rel);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}