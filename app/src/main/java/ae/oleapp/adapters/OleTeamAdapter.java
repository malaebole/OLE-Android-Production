/*
 * Copyright 2014 Magnus Woxblom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OlePlayerInfo;

public class OleTeamAdapter extends DragItemAdapter<OlePlayerInfo, OleTeamAdapter.ViewHolder> {

    private final int mLayoutId;
    private final int mGrabHandleId;
    private final boolean mDragOnLongPress;
    private final Context context;
    private OnItemClickListener itemClickListener;
    private final int columnIndex;

    public OleTeamAdapter(Context context, List<OlePlayerInfo> list, int layoutId, int grabHandleId, boolean dragOnLongPress, int columnIndex) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        setItemList(list);
        this.context = context;
        this.columnIndex = columnIndex;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        OlePlayerInfo info = mItemList.get(position);
        if (info.getNickName() == null || info.getNickName().isEmpty()) {
            if (info.getFirstName() == null || info.getFirstName().isEmpty()) {
                holder.tvName.setText(info.getName());
            }
            else {
                holder.tvName.setText(info.getFirstName());
            }
        }
        else {
            holder.tvName.setText(info.getNickName());
        }
        if (info.getPlayerPosition() != null && !info.getPlayerPosition().isEmpty()) {
            holder.tvPosition.setText(info.getPlayerPosition().getName());
        }
        else {
            holder.tvPosition.setText("");
        }
        Glide.with(context).load(info.getPhotoUrl()).placeholder(R.drawable.player_active).into(holder.imgVu);

        holder.itemView.setTag(mItemList.get(position));
    }

    @Override
    public long getUniqueItemId(int position) {
        return Long.parseLong(mItemList.get(position).getId());
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView tvName, tvPosition;
        ImageView imgVu;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPosition = itemView.findViewById(R.id.tv_position);
            imgVu = itemView.findViewById(R.id.img_vu);
        }

        @Override
        public void onItemClicked(View view) {
            if (itemClickListener != null) {
                itemClickListener.itemClicked(view, getAdapterPosition(), columnIndex);
            }
        }

        @Override
        public boolean onItemLongClicked(View view) {
            return true;
        }
    }

    public interface OnItemClickListener {
        void itemClicked(View view, int pos, int columnIndex);
    }
}
