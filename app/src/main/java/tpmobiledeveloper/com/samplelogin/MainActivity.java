package tpmobiledeveloper.com.samplelogin;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    /*https://developers.facebook.com/docs/facebook-login/android/
     *
     * Starting 12. Add the Facebook Login Button
      *
      *
      *
      *
      * */

    private LoginButton loginButton ;
    private Button btnAction;
    private CallbackManager callbackManager ;
    private TextView txtShow ;
    private AccessTokenTracker accessTokenTracker ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
            callbackManager = CallbackManager.Factory.create();
            loginButton = (LoginButton) findViewById(R.id.login_button);
            txtShow = (TextView) findViewById(R.id.txtShow);

            /* Custom Login button */

            btnAction = (Button) findViewById(R.id.btnAction);
            loginButton.setReadPermissions(Arrays.asList(
                    "public_profile","email","user_friends"));


            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code

                    graphRequest(loginResult.getAccessToken());

                }

                @Override
                public void onCancel() {
                    // App code
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                }
            });

        /* Implement to get data from Facebook API
         *
         * Custom Login
          * */

          LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code

                        graphRequest(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (AccessToken.getCurrentAccessToken() != null){
                    LoginManager.getInstance().logOut();
                    show(false,"Login By Facebook");
                }

                else{

                    LoginManager.getInstance().logInWithReadPermissions(
                            MainActivity.this,
                            Arrays.asList("public_profile","email","user_friends")
                    );

                }
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                if (currentAccessToken==null){

                    Toast.makeText(getApplicationContext(),"User log out",Toast.LENGTH_SHORT).show();
                    show(false,"Login By Facebook");

                }else{

                    Toast.makeText(getApplicationContext(),"User log in",Toast.LENGTH_SHORT).show();

                }
            }
        };

        if (AccessToken.getCurrentAccessToken() != null){

            graphRequest(AccessToken.getCurrentAccessToken());

        }
        else{
            show(false,"Login By Facebook");
        }
    }


    public void show(boolean active, String name){

        if (active){

            btnAction.setText("Log Out");
            txtShow.setText("Hi " + name);

        }
        else{

            btnAction.setText("Custom Login");
            txtShow.setText(name);
        }

    }

    public void parseJson(JSONObject response){

        try {

         JSONObject json = response ;

            String id = json.getString("id");
            String email = json.getString("email");
            String first_name = json.getString("first_name");
            String last_name = json.getString("last_name");
            show(true,first_name);
            Toast.makeText(getApplicationContext(),id + " " + email + " " + first_name + " " + last_name ,Toast.LENGTH_SHORT).show();

        }
        catch (JSONException e){

        }

    }

    public void graphRequest(AccessToken token){

        GraphRequest request = GraphRequest.newMeRequest(token,new GraphRequest.GraphJSONObjectCallback(){

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                parseJson(object);
                Log.d("successed",response.toString());

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","id,email,last_name,first_name,picture.type(large),updated_time");
        request.setParameters(parameters);
        request.executeAsync();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
