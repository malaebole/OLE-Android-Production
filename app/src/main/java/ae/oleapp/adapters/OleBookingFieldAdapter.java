package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Field;

public class OleBookingFieldAdapter extends RecyclerView.Adapter<OleBookingFieldAdapter.ViewHolder> {

    private final Context context;
    private final List<Field> list;
    private String selectedFieldId = "";
    private OnItemClickListener onItemClickListener;
    private boolean isMultiSelection = false;
    public List<Field> selectedFields = new ArrayList<>();

    public OleBookingFieldAdapter(Context context, List<Field> list, String fieldId) {
        this.context = context;
        this.list = list;
        this.selectedFieldId = fieldId;
        isMultiSelection = false;
    }

    public OleBookingFieldAdapter(Context context, List<Field> list, boolean isMultiSelection) {
        this.context = context;
        this.list = list;
        this.isMultiSelection = isMultiSelection;
    }

    public void setSelectedFieldId(String selectedFieldId) {
        this.selectedFieldId = selectedFieldId;
        this.notifyDataSetChanged();
    }

    public String getSelectedFieldId() {
        return selectedFieldId;
    }

    public void setSelectedPosition(int pos) {
        int index = checkFieldExist(list.get(pos).getId());
        if (index == -1) {
            selectedFields.add(list.get(pos));
        }
        else {
            selectedFields.remove(index);
        }
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public int checkFieldExist(String fieldId) {
        int index = -1;
        for (int i = 0; i < selectedFields.size(); i++) {
            if (selectedFields.get(i).getId().equalsIgnoreCase(fieldId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olebooking_field, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Field field = list.get(position);
        if (field.getFieldSize() != null && !field.getFieldSize().isEmpty()) {
            holder.tvName.setText(String.format("%s (%s)", field.getName(), field.getFieldSize().getName()));
        }
        else {
            holder.tvName.setText(field.getName());
        }
        int index = checkFieldExist(field.getId());
        if (field.getId().equalsIgnoreCase(selectedFieldId) || index != -1) {
            holder.mainLayout.setCardBackgroundColor(context.getResources().getColor(R.color.blueColorNew));
            holder.tvName.setTextColor(context.getResources().getColor(R.color.whiteColor));
        }
        else {
            holder.mainLayout.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor));
            holder.tvName.setTextColor(context.getResources().getColor(R.color.darkTextColor));
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CardView mainLayout;
        TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);

            mainLayout = itemView.findViewById(R.id.rel_main);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}