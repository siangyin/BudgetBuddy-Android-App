package mdad.networkdata.volleysqldatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String URL_USER_LOGIN = MainActivity.SERVER_URL+"/login.php";

    Button btn;
    TextView tvRegister;
    EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn = findViewById(R.id.button);
        tvRegister=findViewById(R.id.tvLogin);
        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);

        // register click event
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerUI = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerUI);
            }
        });

        // loginBtn click event
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Required fields cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject reqBodyData = new JSONObject();
                try {
                    reqBodyData.put("email",email);
                    reqBodyData.put("password",password);

                } catch (JSONException e) {}

                System.out.println(reqBodyData);
                postData(URL_USER_LOGIN,reqBodyData);
            }
        });

    } // onCreate end

    public void postData(String url, final JSONObject json){
        // create a RequestQueue for Volley
        RequestQueue reqQ = Volley.newRequestQueue(this);

        // define JSONObjectRequest for http post
        JsonObjectRequest json_obj_req = new JsonObjectRequest(
                Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("status") == 1) {
                        // save userId from response
                        System.out.println(response);
                        System.out.println(response.getInt("userId"));
                        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("userId", response.getInt("userId"));
                        editor.apply();
                        // success alert and directing to MainActivity
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                        finish();
                        Intent mainUI = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainUI);
                    } else {
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Request failed", Toast.LENGTH_LONG).show();
                }}
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Request failed", Toast.LENGTH_LONG).show();
            }
        });
        reqQ.add(json_obj_req);
    }

} // LoginActivity End