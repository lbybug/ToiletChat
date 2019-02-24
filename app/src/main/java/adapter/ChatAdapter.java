package adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lee.toiletchat.R;

import java.util.List;

import bean.MsgBean;

/**
 * Created by Lee on 2019/2/23.
 */


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public List<MsgBean> msgList;

    public ChatAdapter(List<MsgBean> msgList) {
        this.msgList = msgList;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        ChatViewHolder holder = new ChatViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        int type = msgList.get(position).getType();
        String msg = msgList.get(position).getContent();
        if (type == MsgBean.RECEIVED){
            holder.receivedLayout.setVisibility(View.VISIBLE);
            holder.sendLayout.setVisibility(View.GONE);
            holder.receivedMsg.setText(msg);
        }else if (type == MsgBean.SEND){
            holder.receivedLayout.setVisibility(View.GONE);
            holder.sendLayout.setVisibility(View.VISIBLE);
            holder.sendMsg.setText(msg);
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        LinearLayout receivedLayout,sendLayout;

        TextView receivedMsg,sendMsg;

        public ChatViewHolder(View itemView) {
            super(itemView);
            receivedLayout = itemView.findViewById(R.id.itemReceiverLayout);
            sendLayout = itemView.findViewById(R.id.itemSendLayout);
            receivedMsg = itemView.findViewById(R.id.itemReceiver);
            sendMsg = itemView.findViewById(R.id.itemSend);
        }
    }

}
