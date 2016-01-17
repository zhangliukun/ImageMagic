package zalezone.imagemagic.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import zalezone.imagemagic.R;
import zalezone.imagemagic.opencv.cameraprew.CameraPrewActivity;
import zalezone.imagemagic.opencv.grayprocess.GrayProcessActivity;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    ListView functionListView;
    MainListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }


    private void initView() {
        functionListView = (ListView) findViewById(R.id.function_listview);
        adapter = new MainListAdapter(this, R.layout.adapter_listview_main);
        adapter.add("灰度处理");
        adapter.add("照相机预览");
        functionListView.setAdapter(adapter);
        functionListView.setOnItemClickListener(this);
    }

    private void initData() {
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                GrayProcessActivity.startActivity(this);
                break;
            case 1:
                CameraPrewActivity.startActivity(this);
                break;
            default:
                return;
        }
    }
}
