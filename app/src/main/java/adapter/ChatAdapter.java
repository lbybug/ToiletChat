package adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lee.toiletchat.R;

import java.util.List;

import bean.MsgBean;

/**
 * Created by Lee on 2019/2/23.
 */


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{

    public List<MsgBean> msgList;

    public ChatAdapter(List<MsgBean> msgList) {
        this.msgList = msgList;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,parent,false);
        ChatViewHolder holder = new ChatViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{

        public ChatViewHolder(View itemView) {
            super(itemView);
        }
    }

}
