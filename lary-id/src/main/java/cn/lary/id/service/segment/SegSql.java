package cn.lary.id.service.segment;

import cn.lary.id.core.segment.LaryCore;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author paul 2024/4/25
 */


public class SegSql {
    
    private final String[] sql = {
            "select tag,max_id,step,update_time from lary_core",
            "select tag,max_id,step from lary_core where tag = ",
            "update lary_core set max_id = max_id + step where tag = ",
            "select tag from lary_core"
    };
    public List<LaryCore> getAll()  {
        Connection connection = SegConnPool.get();
        ResultSet tem;
        List<LaryCore> res = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            tem = statement.executeQuery(sql[0]);
            connection.commit();
            if(tem == null) {
                return Collections.emptyList();
            }
            while(tem.next()){
                LaryCore t = new LaryCore();
                t.setTag(tem.getString(0));
                t.setMaxId(tem.getLong(1));
                t.setStep(tem.getInt(2));
                t.setUpdateTime(tem.getString(3));
                res.add(t);
            }
            tem.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            SegConnPool.close(connection);
        }
        return res;
    }
    public List<String> getAllTag()  {
        Connection connection = SegConnPool.get();
        ResultSet tem;
        ArrayList<String> res = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            tem = statement.executeQuery(sql[3]);
            connection.commit();
            if(tem == null) {
                return Collections.emptyList();
            }
            while(tem.next()){
                res.add(tem.getString(0));
            }
            tem.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            SegConnPool.close(connection);
        }
        return res;
    }

    public LaryCore update(String tag)  {
        Connection connection = SegConnPool.get();
        ResultSet tem;
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql[2] + tag);
            tem = statement.executeQuery(sql[1] + tag);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            SegConnPool.close(connection);
        }
        return build(tem);
    }

    public LaryCore updateCustom(String tag,int step)  {
        Connection connection = SegConnPool.get();
        ResultSet tem;
        StringBuilder sb = new StringBuilder("update lary_core set max_id = max_id + ");
        sb.append(step).append(" where tag = ");
        sb.append(tag);
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sb.toString());
            tem = statement.executeQuery(sql[1]+tag);
            connection.commit();
            tem.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            SegConnPool.close(connection);
        }
        return build(tem);
    }
    private LaryCore build(ResultSet tem) {
        if(tem == null) {
            return null;
        }
        LaryCore res = new LaryCore();
        try {
            res.setTag(tem.getString(0));
            res.setMaxId(tem.getLong(1));
            res.setStep(tem.getInt(tem.getInt(2)));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }
}
