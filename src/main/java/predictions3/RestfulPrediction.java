package predictions3;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by AlexY on 2016/6/30.
 */



//指定资源的url路由
@ApplicationPath("/resourcesP")
public class RestfulPrediction extends Application {


    @Override
    public Set<Class<?>> getClasses() {

        Set<Class<?>> set = new HashSet<>();
//        将资源类的class文件添加进去
        set.add(PredictionsRS.class);
        return set;
    }
}
