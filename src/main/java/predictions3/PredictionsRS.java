package predictions3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by AlexY on 2016/6/30.
 */


@Path("/")
public class PredictionsRS {

    @Context
    private ServletContext sctx;  //依赖注入

    private static PredictionsList plist; // 在populate()方法中设置


    public PredictionsRS() {



    }


    @GET
    @Path("/xml")
    @Produces({MediaType.APPLICATION_XML})
    public Response getXml() {
        checkContext();


        return Response.ok(plist, "application/xml").build();
    }



    @GET
    @Path("/xml/{id:\\d+}")
    @Produces({MediaType.APPLICATION_XML}) //也可以直接用"application/xml"
    public Response getXml(@PathParam("id") int id) {

        checkContext();

        return toRequestedType(id, "application/xml");

    }


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/json")
    public Response getJson() {

        checkContext();
        return Response.ok(toJson(plist), "application/json").build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/json/{id:\\d+}")
    public Response getJson(@PathParam("id") int id) {

        checkContext();
        return toRequestedType(id, "application/json");
    }


    @GET
    @Path("/plain")
    @Produces({MediaType.TEXT_PLAIN})
    public String getPlain() {

        checkContext();
        return plist.toString();
    }

//    创建新的记录
    @POST
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/create")
    public Response create(@FormParam("who") String who, @FormParam("what") String what) {

        checkContext();

        String msg = null;


//        who和what两个字段都必须有
        if (null == who || null == what) {
            msg = "Property 'who' or 'what' is missing.\n";

            return Response.status(Response.Status.BAD_REQUEST).
                    entity(msg).
                    type(MediaType.TEXT_PLAIN)
                    .build();
        }


//        创建新的Prediction，并返回它的id
        int id = addPrediction(who,what);

        msg = "Prediction " + id + " created: (who = " + who + " what = " + what + ").\n";
        return Response.ok(msg, "text/plain").build();

    }


    @PUT
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/update")
    public Response update(@FormParam("id") int id, @FormParam("who") String who, @FormParam("what") String what){

        checkContext();

        String msg = null;

        if ( null == who && null == what){
            msg = "Neither who nor what is given: nothing to edit.\n";
        }

        // 这里id为int ，所以默认肯定是0，我们PredictionsList中没有0的索引
        Prediction p = plist.find(id);


        if ( null == p){
            msg = "There is no prediction with ID " + id + "\n";
        }

//        如果有错误消息，则返回
        if ( null != msg){
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();

        }


//        更新记录
        if ( null != who){
            p.setWho(who);
        }

        if ( null != what){
            p.setWhat(what);
        }

        msg = "Prediction " + id + " has been updated.\n";

        return Response.ok(msg,"text/plain").build();

    }


    @DELETE
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/delete/{id:\\d+}")
    public Response delete(@PathParam("id") int id){
        checkContext();

        String msg = null;

        Prediction p = plist.find(id);

        if (null == p){

            msg = "There is no prediction with ID " + id + ". Cannot delete.\n";

            return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
        }


//        查找到记录，并删除
        plist.getPredictions().remove(p);


        msg = "Prediction " + id + " deleted.\n";

        return Response.ok(msg, "text/plain").build();

    }






//    生成Http error 响应 或  指定type的 Ok响应（即 http code 200）
    private Response toRequestedType(int id, String type) {

        Prediction pred = plist.find(id);

        if (null == pred) {
            String msg = id + " is a bad ID.\n";

            return Response.status(Response.Status.BAD_REQUEST).
                    entity(msg).
                    type(MediaType.TEXT_PLAIN).
                    build();
        } else if (type.contains("json")) {

            return Response.ok(toJson(pred), type).build();


        } else {


            return Response.ok(pred, type).build();
        }

    }


//  工具方法，检查plist是否为null，如果为null，则调用populate()方法，
// 从predictions.db中读取记录
    private void checkContext() {
        if (plist == null) {

            populate();
        }

    }


    //    从文件中读取记录到PredictionsList中
    private void populate() {

        plist = new PredictionsList();

        String filename = "WEB-INF/data/predictions.db";

        InputStream in = sctx.getResourceAsStream(filename);


        if ( null != in){
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));



            String record = null;

            try {
                while (null != (record = reader.readLine())) {
                    System.out.println("record:"+record);
                    String[] parts = record.split("!");
                    addPrediction(parts[0], parts[1]);

                }
            } catch (IOException e) {
                throw new RuntimeException("I/O failed!");
            }
        }


    }

//    添加一个新的prediction到list中
    private int addPrediction(String who, String what) {

        int id = plist.add(who, what);

        return id;

    }

//    将Prediction转换为 json文档
    private String toJson(Prediction prediction){
        String json = "If you see this, there's a problem.";


        if ( null != prediction){

//             使用Gson库
//            Gson gson = new Gson();
//            json =  gson.toJson(prediction).toString();

            try {

//                使用jackson库
                json = new ObjectMapper().writeValueAsString(prediction);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }


        return json;

    }


//    将PredictionsList转换为 json文档
    private String toJson(PredictionsList plist) {
        String json = "If you see this, there's a problem.";
        try {
            json = new ObjectMapper().writeValueAsString(plist);
        }
        catch(Exception e) { }
        return json;
    }



}
