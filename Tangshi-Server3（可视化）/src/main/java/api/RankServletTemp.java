package api;

import DB.DBUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author yolo
 * @date 2020/4/3-21:22
 */
@WebServlet("/ranktemp.json")
public class RankServletTemp extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        JSONArray jsonArray=new JSONArray();
        {
            JSONArray item=new JSONArray();
            item.add("李白");
            item.add(5);
            jsonArray.add(item);
        }

        {
            JSONArray item=new JSONArray();
            item.add("杜甫");
            item.add(7);
            jsonArray.add(item);
        }

        {
            JSONArray item=new JSONArray();
            item.add("王维");
            item.add(3);
            jsonArray.add(item);
        }

        resp.setContentType("application/json ;charset=utf-8");
    }
}
