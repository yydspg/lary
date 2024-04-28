package cn.lary.api.id;

/**
 *  distribute id result
 * @author paul 2024/4/25
 */

public class Res {
    // distributed id
    private long id;
    // 0 means fail,1 means success
    private byte status;
    public Res(long id,byte status){
        this.id = id;
        this.status = status;
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
