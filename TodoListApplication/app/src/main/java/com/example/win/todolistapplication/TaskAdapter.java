package com.example.win.todolistapplication;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;


/**
 * Created by win on 2018/08/04.
 */

public class TaskAdapter extends RealmBaseAdapter<Task> {

    // 表示データを格納するクラス変数を定義
    private static  class ViewHolder{
        TextView deadline; // 締め切り
        TextView title; // タスクのタイトル

    }
    // RealmBaseAdapterに必要な継承コンストラクタ
    public TaskAdapter(Context context, OrderedRealmCollection<Task> data) {
        super(context, data);
    }

    // RealmBaseAdapterに必要な継承メソッド
    // ListViewに表示するビューのデータを返す処理
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //ListView（複数あり）からadapterの「getView」が呼ばれて、Real(DM)に接続して、そのデータをセットして返す
        ViewHolder viewHolder;
        if(convertView == null){ //convertViewの意味：画面上に見える範囲 :既存ビューにはタグがつき、再利用する
            // タグがない場合

            // inflate ：画面パーツをプログラムから生成する命令
            convertView = inflater.inflate(android.R.layout.simple_list_item_2,parent,false); //2行からなるビュー（データセット）
            viewHolder = new ViewHolder();
            viewHolder.deadline = (TextView)convertView.findViewById(android.R.id.text1);
            viewHolder.title = (TextView)convertView.findViewById(android.R.id.text2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        // position　： ListViewの何番目のデータなのか
        // 各データセットを、taskという変数に格納
        Task task = adapterData.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String formatDate = sdf.format(task.getDeadline());
        viewHolder.deadline.setText(formatDate);
        viewHolder.title.setText(task.getTitle());
        return convertView;
    }
}
