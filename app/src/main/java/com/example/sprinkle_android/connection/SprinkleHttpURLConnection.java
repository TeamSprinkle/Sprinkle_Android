package com.example.sprinkle_android.connection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SprinkleHttpURLConnection extends AsyncTask<String, Void, String> {
    private static final String TAG = "InitHttpURLConnection";
    private static final String HOST = "http://awsbit.mynetgear.com:65032";
    public Context mContext;

    public SprinkleHttpURLConnection(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        Log.d(TAG, "POST response  - " + result);
    }

    @Override
    protected String doInBackground(String... params) {
        String serverURL = HOST.concat(params[0]); //url mapping
        String action = params[1]; // GET , POST 방식의 action 정보
        JSONObject parameters = new JSONObject(); // 전달인자로 보내는 정보
        String result = null;
        
        try {
            int i = 0;
            for (i = 2; i < params.length; i += 2) {
                parameters.put(params[i], params[i+1]);
            }
        }
        catch (Exception e){
            return "Error: " + e.getMessage();
        }

        System.out.println("doInBackground parameter : " + parameters);

        try {
            URL url = new URL(serverURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(100000); //10초동안 서버로부터 반응없으면 에러
            conn.setConnectTimeout(15000); //접속하는 커넥션 타임 15초동안 접속안되면 접속안되는 것으로 간주 (ms)
            conn.setRequestMethod(action); //송수신할때, post방식으로 선언(get방식,delete방식등도 있음)
            conn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset ����. //character set을 utf-8로 선언
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); //서버로 보내는 패킷이 어떤타입인지 선언
            //폼테그방식
            conn.setDoInput(true); //안드로이드가 서버로부터 받는거를 트루
            conn.setDoOutput(true); //안드로이드가 서버로 보내는거를 트루

            OutputStream outStream = conn.getOutputStream();
            outStream.write(parameters.toString().getBytes("utf-8"));//parameters는 서버로보낼 스트링값등을 설정하는 것

            int resCode = conn.getResponseCode(); // connect, send http reuqest, receive htttp request

            InputStream inputStream = null;
            if (resCode == HttpURLConnection.HTTP_OK) {
                // 정상적인 응답 데이터
                inputStream = conn.getInputStream();
                System.out.println("서버 요청 및 응답 성공");
            } else {
                // 에러 발생
                inputStream = conn.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder builder = new StringBuilder();//스트링을 만들어주는데 유용하게쓰이는 클래스
            String resultStr = ""; //저장할 공간

            while ((resultStr = bufferedReader.readLine()) != null) {//(중요)서버로부터 한줄씩 읽어서 문자가 없을때까지 넣어줌
                builder.append(resultStr + "\n"); //읽어준 스트링값을 더해준다.
            }

            result = builder.toString();

            bufferedReader.close();
        }
        catch (Exception e)
        {
            Log.d(TAG, "InsertData: Error ", e);
            return new String("Error: " + e.getMessage());
        }

        return result;
    }
}
