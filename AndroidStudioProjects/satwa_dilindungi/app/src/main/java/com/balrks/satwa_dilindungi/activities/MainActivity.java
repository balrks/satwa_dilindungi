package com.balrks.satwa_dilindungi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import com.balrks.satwa_dilindungi.R;
import com.balrks.satwa_dilindungi.adapter.MainAdapter;
import com.balrks.satwa_dilindungi.model.ModelMain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<ModelMain> modelMain = new ArrayList<>();
    MainAdapter mainAdapter;
    RecyclerView rvListSatwa;
    SearchView searchSatwa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set transparent statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        rvListSatwa = findViewById(R.id.rvListSatwa);
        searchSatwa = findViewById(R.id.searchSatwa);

        //transparent background searchview
        int searchPlateId = searchSatwa.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchSatwa.findViewById(searchPlateId);
        if (searchPlate != null) {
            searchPlate.setBackgroundColor(Color.TRANSPARENT);
        }

        searchSatwa.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchSatwa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mainAdapter.getFilter().filter(newText);
                return true;
            }
        });

        rvListSatwa.setLayoutManager(new LinearLayoutManager(this));
        rvListSatwa.setHasFixedSize(true);

        //get data json
        getNamaTanaman();

    }

    private void getNamaTanaman() {
        try {
            InputStream stream = getAssets().open("Satwa_Dilindungi.json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String strContent = new String(buffer, StandardCharsets.UTF_8);
            try {
                JSONObject jsonObject = new JSONObject(strContent);
                JSONArray jsonArray = jsonObject.getJSONArray("Daftar_Satwa");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    ModelMain dataApi = new ModelMain();
                    dataApi.setNama(object.getString("nama"));
                    dataApi.setDeskripsi(object.getString("deskripsi"));
                    dataApi.setImage(object.getString("image_url"));
                    modelMain.add(dataApi);
                }
                mainAdapter = new MainAdapter(this, modelMain);
                rvListSatwa.setAdapter(mainAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException ignored) {
            Toast.makeText(MainActivity.this, "Ups, ada yang tidak beres. " +
                    "Coba ulangi beberapa saat lagi.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        if (on) {
            layoutParams.flags |= bits;
        } else {
            layoutParams.flags &= ~bits;
        }
        window.setAttributes(layoutParams);
    }

}
