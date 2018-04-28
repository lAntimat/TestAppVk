package ru.lantimat.testappvk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKObject;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiUsers;
import com.vk.sdk.api.model.VKApiApplicationContent;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;
import com.vk.sdk.api.model.VKUsersArray;
import com.vk.sdk.util.VKUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.lantimat.testappvk.utils.DownloadImageTask;
import ru.lantimat.testappvk.utils.ImagesCache;

public class MainActivity extends AppCompatActivity {

    private TextView tvFirstName, tvLastName;
    private ImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());

        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        ivProfile = findViewById(R.id.imageView);

        VKParameters params = VKParameters.from(VKApiConst.FIELDS, "photo_50, photo_200");
        if(VKSdk.isLoggedIn()) {
            VKRequest request = new VKRequest(VKApi.users().get().methodName, params) ;
            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    //VKList<VKApiUser> users = (VKList<VKApiUser>) response.parsedModel;

                    VKApiUser user;

                    try {
                        JSONArray jsonArray = response.json.getJSONArray("response");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        user = new VKApiUser();
                        user.parse(jsonObject);
                        tvFirstName.setText(user.first_name);
                        tvLastName.setText(user.last_name);
                        ImagesCache cache = ImagesCache.getInstance();
                        Bitmap bm = cache.getImageFromWarehouse(user.photo_200);

                        if (bm != null) {
                            ivProfile.setImageBitmap(bm);
                        } else {
                            ivProfile.setImageBitmap(null);

                            DownloadImageTask imgTask = new DownloadImageTask(cache, ivProfile, 300, 300);//Since you are using it from `Activity` call second Constructor.

                            imgTask.execute(user.photo_200);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            } else {
            VKSdk.login(this, "what");
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // Пользователь успешно авторизовался
            }
            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
