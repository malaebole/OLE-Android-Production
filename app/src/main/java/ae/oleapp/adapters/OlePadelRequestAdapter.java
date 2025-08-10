package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerInfo;

public class OlePadelRequestAdapter extends RecyclerView.Adapter<OlePadelRequestAdapter.ViewHolder> {

    private final Context context;
    private OnItemClickListener onItemClickListener;
    private final List<OlePlayerInfo> list;

    public OlePadelRequestAdapter(Context context, List<OlePlayerInfo> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olepadel_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OlePlayerInfo info = list.get(position);
        holder.tvSkillsLevel.setText(String.format("%s: %s", context.getResources().getString(R.string.skills_level), info.getSkillLevel()));
        Glide.with(context).load(info.getPhotoUrl()).placeholder(R.drawable.player_active).into(holder.imgVu);
        holder.tvName.setText(String.format("%s, %s %s", info.getNickName(), context.getString(R.string.age), info.getAge()));
        Glide.with(context).load(info.getMyPartner().getPhotoUrl()).placeholder(R.drawable.player_active).into(holder.imgVuPartner);
        holder.tvPartnerName.setText(String.format("%s, %s %s", info.getMyPartner().getNickName(), context.getString(R.string.age), info.getMyPartner().getAge()));
        holder.tvPaymentStatus.setVisibility(View.VISIBLE);
        if (info.getPaymentMethod() == null || info.getPaymentMethod().isEmpty()) {
            holder.tvPaymentStatus.setVisibility(View.GONE);
        }
        else if (info.getPaymentMethod().equalsIgnoreCase("cash")){
            holder.tvPaymentStatus.setText(String.format("%s: %s", context.getResources().getString(R.string.payment), context.getString(R.string.cash)));
        }
        else {
            holder.tvPaymentStatus.setText(String.format("%s: %s", context.getResources().getString(R.string.payment), context.getString(R.string.paid)));
        }

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnAcceptClick(v, holder.getAdapterPosition());
            }
        });
        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnRejectClick(v, holder.getAdapterPosition());
            }
        });
        holder.imgVu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.openProfile(view, info.getId());
            }
        });
        holder.imgVuPartner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.openProfile(view, info.getMyPartner().getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgVu, imgVuPartner;
        TextView tvPartnerName, tvName, tvSkillsLevel, tvPaymentStatus;
        CardView btnAccept;
        MaterialCardView btnReject;

        ViewHolder(View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.img_vu);
            imgVuPartner = itemView.findViewById(R.id.img_vu_partner);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSkillsLevel = itemView.findViewById(R.id.tv_skill_level);
            tvPaymentStatus = itemView.findViewById(R.id.tv_payment_status);
            tvPartnerName = itemView.findViewById(R.id.tv_partner_name);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }

    public interface OnItemClickListener {
        void OnAcceptClick(View v, int pos);
        void OnRejectClick(View v, int pos);
        void openProfile(View v, String pId);
    }
}