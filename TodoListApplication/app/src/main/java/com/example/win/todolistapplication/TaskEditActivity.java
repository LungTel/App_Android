package com.example.win.todolistapplication;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class TaskEditActivity extends AppCompatActivity {

    EditText mDeadlineEdit;
    EditText mTitleEdit;
    EditText mDetailEdit;
    Button mDelete;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        mDeadlineEdit = (EditText) findViewById(R.id.DeadlineEdit);
        mTitleEdit = (EditText) findViewById(R.id.TitleEdit);
        mDetailEdit = (EditText) findViewById(R.id.DetailEdit);
        mDelete = (Button) findViewById(R.id.delete);

        // taskId:インテントで遷移時に渡されるtaks_idを格納, [-1]値を取得できない場合にセットする値（false値)
        long taskId = getIntent().getLongExtra("task_id", -1);
        // データがtrue（存在する場合）
        if (taskId != -1) {
            // RealmビルダーのビルドメソッドによりRealmの設定情報を変数に格納
            RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
            // DBのインスタンス取得
            Realm realm = Realm.getInstance(realmConfig);
            // DBに登録されている一致するデータを取得
            RealmResults<Task> results = realm.where(Task.class).equalTo("id", taskId).findAll();
            //resultsの中の先頭データをtask変数にセット > 値を取り出して画面にセット
            Task task = results.first();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String date = sdf.format(task.getDeadline());
            mDeadlineEdit.setText(date);
            mTitleEdit.setText(task.getTitle());
            mDetailEdit.setText(task.getDetail());
            // 削除ボタン　:setVisibility 可視性を設定するメソッド、Viewクラスのあらかじめ用意されてるVISIBLE
            mDelete.setVisibility(View.VISIBLE);
        } else {
            mDelete.setVisibility(View.INVISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N) //API 24以上
    public void onSaveTapped(View view) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date deadline = new Date();
        try {
            deadline = sdf.parse(mDeadlineEdit.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long taskId = getIntent().getLongExtra("task_id", -1);

        // 既にデータが存在する場合、データ更新処理
        if (taskId != -1) {
            // RealmビルダーのビルドメソッドによりRealmの設定情報を変数に格納
            RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
            // DBのインスタンス取得
            Realm realm = Realm.getInstance(realmConfig);
            // DBに登録されている一致するデータを取得
            RealmResults<Task> results = realm.where(Task.class).equalTo("id", taskId).findAll();

            // トランザクションの実行：DB更新処理の実行
            // トランザクションの開始
            realm.beginTransaction();
            Task task = results.first();
            task.setDeadline(deadline);
            task.setTitle(mTitleEdit.getText().toString());
            task.setDetail(mDetailEdit.getText().toString());

            //コミット
            realm.commitTransaction();

            // メッセージの表示　android.resouce.id.contentへ　メッセージ：”更新しました" 表示する時間：LENGTH_SHORT 短め
            // 次の動作：クリックの動作が走る場合：終了　ラベル付ける：戻る クリックしたときにページを閉じて戻る処理:public void・・・finish();
            Snackbar.make(findViewById(android.R.id.content),
                    "更新しました", Snackbar.LENGTH_SHORT).
                    setAction("戻る", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // トランザクションの終了
                            finish();
                        }
                    })
                    // 画面上のメッセージの配色を変更
                    .setActionTextColor(Color.YELLOW)
                    // そのメッセージを表示する
                    .show();

            // 新規登録処理
        } else {
            // RealmビルダーのビルドメソッドによりRealmの設定情報を変数に格納
            RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
            // DBのインスタンス取得
            Realm realm = Realm.getInstance(realmConfig);

            // トランザクションの実行：DB更新処理の実行
            // トランザクションの開始
            realm.beginTransaction();

            // DBに登録されているデータの最大のIDを取得
            Number maxId = realm.where(Task.class).max("id");
            // nextIdにこれから登録するデータのID（整数）をセット
            long nextId = 1;
            if (maxId != null) nextId = maxId.longValue() + 1;
            Task task = realm.createObject(Task.class);
            task.setId(nextId);
            task.setDeadline(deadline);
            task.setTitle(mTitleEdit.getText().toString());
            task.setDetail(mDetailEdit.getText().toString());

            //コミット
            realm.commitTransaction();

            // 成功したらメッセージを出力
            Toast.makeText(this, "保存しました", Toast.LENGTH_SHORT).show();

            // トランザクションの終了
            finish();
        }
    }

    public void onDeleteTapped(View view) {
        long taskId = getIntent().getLongExtra("task_id", -1);
        if(taskId != -1){
            RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
            Realm realm = Realm.getInstance(realmConfig);
            RealmResults<Task> results = realm.where(Task.class).equalTo("id", taskId).findAll();
            realm.beginTransaction();
            // https://realm.io/docs/java/latest/#deletion
            // results.remove(0);
            results.deleteFromRealm(0);
            realm.commitTransaction();
            Toast.makeText(this, "削除しました", Toast.LENGTH_SHORT).show();
        }
        finish();

    }
}