package com.example.win.todolistapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;



public class MainActivity extends AppCompatActivity {

    //リストビュー変数定義
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 画面と関連づける
        mListView = (ListView)findViewById(R.id.listView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // Realm（DB)を設定を読み込む変数を定義 自身をビルド
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        // Realm(DB)を取得して、realm変数に格納
        Realm realm = Realm.getInstance(realmConfig);

        // Realm(DB)よりタスクclassの情報をすべて取得する
        RealmResults<Task> tasks = realm.where(Task.class).findAll();
        // 2つの引数、this(自身のクラス）、データ（Realmから取得したデータ
        TaskAdapter adapter = new TaskAdapter(this, tasks);
        // リストビューとアダプターを関連づける
        mListView.setAdapter(adapter);

        // ボタンにイベントリスナーを設置
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {

            //クリックされたら、StartActivityメソッドでインテントに指定されたアクティビティに遷移する
            // MainActivityからTaskEditActivityを呼び出す
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TaskEditActivity.class));
            }
        });

        // ListViewクラスのメソッド クリックされた時
        mListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                @Override
                // position 何番目のデータ　id データベース内のID番号
                public  void onItemClick(AdapterView<?> parent,View view, int position, long id){
                    //getItemAtPosition 指定した位置のデータを取得
                    Task task = (Task)parent.getItemAtPosition(position);
                            //putExtra 追加　task_idをidとして出す
                            startActivity(new Intent(MainActivity.this, TaskEditActivity.class).putExtra("task_id",task.getId()));
                }
                }
        );
    }

}
