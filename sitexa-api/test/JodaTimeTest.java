import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by open on 06/05/2017.
 */

public class JodaTimeTest {
    private static Gson gson = new GsonBuilder()
            //.registerTypeAdapter(DateTime.class, new JodaTimeAdapter())
            .create();

    public static void main(String[] args) {
        APIResult apiResult = new APIResult(1, "success", "3466671c885b03a9366efa41b9ef5687b5f5d966");
        System.out.println("apiResult = " + apiResult);

        String apiResult_json = gson.toJson(apiResult);
        System.out.println("apiResult_json = " + apiResult_json);

        String apiResult_data = apiResult.data(String.class);
        System.out.println("apiResult_data = " + apiResult_data);

        String apiResult_data_json = gson.toJson(apiResult_data);
        System.out.println("apiResult_data_json = " + apiResult_data_json);

        RESP resp_apiResult = new RESP(apiResult);
        String resp_apiResult_json = gson.toJson(resp_apiResult);
        System.out.println("resp_apiResult_json = " + resp_apiResult_json);

        RESP resp_apiResult_data = new RESP(apiResult_data);
        String resp_apiResult_data_json = gson.toJson(resp_apiResult_data);
        System.out.println("resp_apiResult_data_json = " + resp_apiResult_data_json);

    }


}

class APIResult implements Serializable {

    private int code;
    private String desc;
    private String data;


    APIResult(int code, String desc, String data) {
        this.code = code;
        this.desc = desc;
        this.data = data;
    }

    <T> T data(Class<T> aClass) {
        Gson gson = new GsonBuilder()
                //.registerTypeAdapter(DateTime.class, new JodaTimeAdapter())
                .create();
        try {
            T value = gson.fromJson(data, aClass);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> List<T> dataList(Type type) {
        Gson gson = new GsonBuilder()
                //.registerTypeAdapter(DateTime.class, new JodaTimeAdapter())
                .create();
        try {
            return gson.fromJson(data, type);
        } catch (Exception e) {
            return null;
        }
    }

}

class RESP {
    private Object data;

    RESP(Object data) {
        this.data = data;
    }
}