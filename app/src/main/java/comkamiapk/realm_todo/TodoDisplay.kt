package comkamiapk.realm_todo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_todo_display.*

class TodoDisplay : AppCompatActivity() {

    private lateinit var realm : Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_display)

        //インスタンスの取得。
        realm = Realm.getDefaultInstance()


        //id関連の変数の初期化
        var id_min : Long  = -1
        var id_max : Long = -2
        var id_now : Long = 0
        var id_delete : Long = -1


        //クエリと初期化
        //これはidの値を取得しているが型がNumberというものなのでキャストが必要
        var Now = realm.where<TodoModel>().min("id")
        var Max = realm.where<TodoModel>().max("id")

        var Todo = realm.where<TodoModel>().equalTo("id",Now?.toLong() ?: -1).findFirst()



        if(Todo != null){
            id_min = Todo.id
            //一番小さいidのものを一番最初に持ってくる
            id_now = id_min
            //Todoがnullでない時点でMaxは存在するはず。ただ安全呼び出しを用いる
            id_max = Max?.toLong() ?: 0L
        }

        //初期化　表示
        todo_dis.setText((Todo?.todo ?:"何もやることはないよ！").toString()   + (Todo?.id ?:"").toString())

        //次のやることを表示

        next_todo.setOnClickListener {

            //最後までは表示するものが切り替わる。
            if(id_now < id_max) {
                // Todoの更新
                Now = realm.where<TodoModel>().greaterThan("id",id_now).min("id")
                id_now = Now?.toLong() ?: 0L
                Todo = realm.where<TodoModel>().equalTo("id",id_now).findFirst()
                todo_dis.setText((Todo?.todo ?:"何もやることはないよ！").toString()   + (Todo?.id ?:"").toString())
            }else{
                //id_now == id_max またはTodoがnullのとき
                Toast.makeText(this, "やることを追加してね!", Toast.LENGTH_LONG).show()
            }
        }

        //ここはok
        back.setOnClickListener {
            if(id_min < id_now){
                Now = realm.where<TodoModel>().lessThan("id",id_now).max("id")
                id_now = Now?.toLong() ?: 0L
                Todo = realm.where<TodoModel>().equalTo("id",id_now).findFirst()
                todo_dis.setText((Todo?.todo ?:"何もやることはないよ！").toString()   + (Todo?.id ?:"").toString())
            }else{
                Toast.makeText(this, "これを最初にやってしまおう!", Toast.LENGTH_LONG).show()
            }
        }


        //データの削除



        /*流れ

        ・id_nowの値を保持しておく。
        ・id_nowの値を次に表示するものに変える
            ・id_now == id_max　だったらid_maxの値を更新する
            ・id_now == id_min だったらid_minの値を更新する


        ・表示されているデータを消す。

        ・更新されたid_nowの値に応じてテキストの更新をする。



         */
        delete.setOnClickListener {

            if(Todo != null){
                //削除用のid保持
                id_delete = id_now

                //表示用のid更新など
                //データが一つしかないとき
                if(id_min == id_max){
                    //next内の処理に合わせる
                    id_min = -1
                    id_max = -100
                    Todo = null
                } else if(id_now == id_max){

                    Now = realm.where<TodoModel>().lessThan("id",id_now).max("id")
                    id_max = Now?.toLong() ?: 0L
                    id_now = id_max

                }else if(id_now == id_min){

                    Now = realm.where<TodoModel>().greaterThan("id",id_now).min("id")
                    id_min = Now?.toLong() ?: 0L
                    id_now = id_min

                }else{

                    Now = realm.where<TodoModel>().lessThan("id",id_now).max("id")
                    id_now = Now?.toLong() ?: 0L

                }

                if(id_max != -100L){
                    Toast.makeText(this, "お疲れさま!", Toast.LENGTH_LONG).show()
                    Todo = realm.where<TodoModel>().equalTo("id",id_now).findFirst()
                    todo_dis.setText((Todo?.todo ?:"何もやることはないよ！").toString()   + (Todo?.id ?:"").toString())
                }else{
                    todo_dis.setText("何もやることはないよ！")
                }


                //データ削除
                realm.executeTransaction { db: Realm ->
                    db.where<TodoModel>().equalTo("id",id_delete)
                        ?.findFirst()
                        ?.deleteFromRealm()
                }


            }else {//データが何もない時
                Toast.makeText(this, "やることを追加してね!", Toast.LENGTH_LONG).show()
            }
        }

    }




}
