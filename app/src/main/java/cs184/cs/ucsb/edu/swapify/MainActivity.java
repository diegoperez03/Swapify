package cs184.cs.ucsb.edu.swapify;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.UserPrivate;
import kaaes.spotify.webapi.android.models.UserPublic;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "10bc6410b98746ad835baaa88574134d";
    private static final String REDIRECT_URI = "com.yourdomain.yourapp://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    private static final int REQUEST_CODE = 1337;
    private SpotifyApi api;
    ImageView temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        api = new SpotifyApi();

        temp = findViewById(R.id.image);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // We will start writing our code here.

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    api.setAccessToken(response.getAccessToken());
                    SpotifyService spotify = api.getService();


                    final Map<String,Object> playlistObj = new HashMap<String,Object>();
                    spotify.getMyPlaylists(new Callback<Pager<PlaylistSimple>>() {
                        @Override
                        public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                            for(PlaylistSimple s : playlistSimplePager.items) {
                                if(s.images.size() > 1){
                                    String url = s.images.get(1).url;
                                    Picasso.get()
                                            .load(url)
                                            .resize(200,200)
                                            .into(temp);
                                    Log.d("user", s.name + " " + s.owner.display_name + " ");
                                }

                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    Toast.makeText(getApplicationContext(), response.getError(), Toast.LENGTH_SHORT).show();

                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
                    Toast.makeText(getApplicationContext(), "nada ", Toast.LENGTH_SHORT).show();

            }
        }
    }





    @Override
    protected void onStop() {
        super.onStop();
        // Aaand we will finish off here.
    }
}
