package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.database.Chat;
import ae.oleapp.database.Message;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class OleChatAdapter extends SectionedRecyclerViewAdapter{
    private final List<Chat> list;
    private final Context context;
    private ItemClickListener itemClickListener;

    private static final int TYPE_SENDER = 1;
    private static final int TYPE_RECEIVER = 2;

    public OleChatAdapter(Context context, @NonNull final List<Chat> list) {

        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getSectionCount() {
        return list.size();
    }


    public int getItemCount(int section) {
        return list.get(section).getMessages().size();
    }


    public int getItemViewType(int section, int relativePosition, int absolutePosition) {
        if (list.get(section).getMessages().get(relativePosition).getSenderId().equalsIgnoreCase(Functions.getPrefValue(context, Constants.kUserID))) {
            return TYPE_SENDER;
        }
        else {
            return TYPE_RECEIVER;
        }
    }

    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int section) {
        HeaderViewHolder holder = (HeaderViewHolder)viewHolder;
        holder.tvDate.setText(list.get(section).getDate());
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int section, int relativePosition, int absolutePosition) {
        Message message = list.get(section).getMessages().get(relativePosition);
        if (getItemViewType(section, relativePosition, absolutePosition) == TYPE_SENDER) {
            SenderViewHolder holder = (SenderViewHolder)viewHolder;
            holder.tvMsg.setText(message.getMessage());
            if (message.getMsgStatus() == 0) { //waiting
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.btnError.setVisibility(View.GONE);
            }
            else if (message.getMsgStatus() == 2) { //error
                holder.progressBar.setVisibility(View.GONE);
                holder.btnError.setVisibility(View.VISIBLE);
            }
            else { //sent
                holder.progressBar.setVisibility(View.GONE);
                holder.btnError.setVisibility(View.GONE);
            }

            holder.btnError.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.btnErrorClicked(v, section, relativePosition);
                }
            });
        }
        else {
            ReceiverViewHolder holder = (ReceiverViewHolder)viewHolder;
            holder.tvMsg.setText(message.getMessage());
            if (message.getTeamName().equalsIgnoreCase("")) {
                holder.tvName.setText(message.getSenderName());
            }
            else {
                holder.tvName.setText(String.format("%s (%s)", message.getSenderName(), message.getTeamName()));
            }
            Glide.with(context).load(message.getSenderImage()).placeholder(R.drawable.player_active).into(holder.imgVu);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olewallet_header, parent, false);
            return new HeaderViewHolder(v);
        }
        else {
            if (viewType == TYPE_SENDER) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olesender_msg, parent, false);
                return new SenderViewHolder(v);
            } else {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olereceiver_msg, parent, false);
                return new ReceiverViewHolder(v);
            }
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView tvMsg;
        ImageButton btnError;
        ProgressBar progressBar;

        SenderViewHolder(@NonNull View view) {
            super(view);

            tvMsg = itemView.findViewById(R.id.tv_msg);
            btnError = itemView.findViewById(R.id.btn_error);
            progressBar = itemView.findViewById(R.id.progressbar);
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView tvMsg, tvName;
        ImageView imgVu;

        ReceiverViewHolder(@NonNull View view) {
            super(view);

            tvMsg = itemView.findViewById(R.id.tv_msg);
            tvName = itemView.findViewById(R.id.tv_name);
            imgVu = itemView.findViewById(R.id.img_vu);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        final TextView tvDate;

        HeaderViewHolder(@NonNull View view) {
            super(view);

            tvDate = view.findViewById(R.id.tv_date);
            tvDate.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
    }

    public interface ItemClickListener {
        void btnErrorClicked(View view, int section, int pos);
    }
}
