package predictions3;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by AlexY on 2016/6/30.
 */


//指定生成的xml的根元素
@XmlRootElement(name = "prediction")
public class Prediction implements Comparable<Prediction> {


    private String who;  // 人
    private String what; //他的prediction
    private int id; //作为搜索的key

    @XmlElement
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    @XmlElement
    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    @Override
    public int compareTo(Prediction o) {
        return this.id - o.id;
    }

//    重写toString方法，这样可以直接以纯文本输出（text/plain）
    @Override
    public String toString() {
        return String.format("%2d:",id)+who+"==>"+what+ "\n";
    }
}
