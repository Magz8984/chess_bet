package chessbet.app.com;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.adapter.ExternalPGNViewerAdapter;
import chessengine.PGNHolder;

public class PgnViewerActivity extends AppCompatActivity {
@BindView(R.id.recPGN) RecyclerView recPGNS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pgn_viewer);
        ButterKnife.bind(this);

        recPGNS.setHasFixedSize(true);
        recPGNS.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if(uri != null){
            String scheme = uri.getScheme();
            if(ContentResolver.SCHEME_CONTENT.equals(scheme)){
                try {
                    ContentResolver contentResolver = getContentResolver();
                    InputStream inputStream = contentResolver.openInputStream(uri);
                    List<PGNHolder> pgnHolders =  PGNHolder.getPGNHoldersFromFileInputStream(inputStream);

                    ExternalPGNViewerAdapter adapter = new ExternalPGNViewerAdapter();
                    adapter.setContext(this);
                    adapter.setPgnHolders(pgnHolders);
                    recPGNS.setAdapter(adapter);
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }
}
