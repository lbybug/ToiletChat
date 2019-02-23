package bean;

/**
 * Created by Lee on 2019/2/23.
 */


public class MsgBean {

    public static final int RECEIVED = 0x01;
    public static final int SEND = 0x02;

    private String content;
    private int type;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
