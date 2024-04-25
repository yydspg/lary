package cn.lary.id.core;

/**
 * @author paul 2024/4/25
 */

public class Res {
    private long id;
    private byte status;

    public Res(){}

    public Res(long id,byte status){
        this.id = id;
        this.status = status;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Result{");
        sb.append("id=").append(id);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
