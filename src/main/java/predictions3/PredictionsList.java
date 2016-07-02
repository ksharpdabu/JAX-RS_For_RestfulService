package predictions3;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by AlexY on 2016/6/30.
 */

@XmlRootElement(name = "predictionList")
public class PredictionsList {

    private List<Prediction> preds;
    private AtomicInteger predId;


    public PredictionsList() {

//        为了线程安全
        preds = new CopyOnWriteArrayList<>();
        predId = new AtomicInteger();

    }

    //有个name属性，可以用来修改xml中的元素的名字，（默认值就是程序从getter方法获取的）
    @XmlElement
    //指定封装这个xml元素的元素，就是在外面在套一层标签
    @XmlElementWrapper(name = "predictions")
    public List<Prediction> getPredictions() {
        return preds;
    }

    public void setPredictions(List<Prediction> preds) {
        this.preds = preds;
    }



    @Override
    public String toString() {

//         TODO: 2016/6/30 可以用stringbuffer优化
        String s = "";
        for (Prediction p : preds){
            s += p.toString();


        }


        return s;
    }


    public Prediction find(int id){
        Prediction pred = null;

//        从list中搜索
//          注意：因为现在list还很短，所以可以使用线性查找，
//          如果当list变成一个很大的有序列表，则最好用 二分查找

        for ( Prediction p : preds){

            if (p.getId() == id){
                pred = p;

                break;
            }

        }


        return pred;

    }


    public int add(String who,String what){

        int id = predId.incrementAndGet();
        Prediction p = new Prediction();

        p.setWho(who);
        p.setWhat(what);
        p.setId(id);

        preds.add(p);

        return id;

    }









}
