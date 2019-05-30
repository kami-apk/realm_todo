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
        //TodoModelにデータがないなら、id_now > id_maxが成り立つように初期化する。
        var id_min : Long  = -1 //idの最小値
        var id_max : Long = -2 //idの最大値
        var id_now : Long = 0 //表示するもののid
        var id_delete : Long = -1 //消したいid



        //これはidの値を取得しているが型がNumberというものなのでキャストが必要
        var Now = realm.where<TodoModel>().min("id")
        var Max = realm.where<TodoModel>().max("id")
        //どのデータを取得するのかはidを用いる。
        //id_nowの一個前のidを取得したい操作を考えるだけならリスト型配列を用いたほうがよい。
        //ただTodoModelをリスト型配列で取得してもいいが、データの削除に対応したいので・・・
        var Todo = realm.where<TodoModel>().equalTo("id",Now?.toLong() ?: -1).findFirst()


        //Todoがnullとなるときは、データが一つもない時。
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
                // 表示されているid_nowより一つ大きいidを取得
                Now = realm.where<TodoModel>().greaterThan("id",id_now).min("id")
                //id_nowの更新
                id_now = Now?.toLong() ?: 0L
                //Todoの更新
                Todo = realm.where<TodoModel>().equalTo("id",id_now).findFirst()
                todo_dis.setText((Todo?.todo ?:"何もやることはないよ！").toString()   + (Todo?.id ?:"").toString())
            }else{ //id_max <= id_now
                Toast.makeText(this, "やることを追加してね!", Toast.LENGTH_LONG).show()
            }
        }
        //一つ前のやることを表示
        back.setOnClickListener {
            if(id_min < id_now){
                //lessThanを用いてfindFisrstなどでidを取得すると、一個前のidではなくid_minを取得してしまう
                //そのため、現在のid_nowより小さいものを対象にして(lessThan)、その中から最大のidを取得すれば
                //一つ前のidが取得できる
                Now = realm.where<TodoModel>().lessThan("id",id_now).max("id")
                id_now = Now?.toLong() ?: 0L
                Todo = realm.where<TodoModel>().equalTo("id",id_now).findFirst()
                todo_dis.setText((Todo?.todo ?:"何もやることはないよ！").toString()   + (Todo?.id ?:"").toString())
            }else{
                //id_nowがid_minと同じ場合、トースト表示
                Toast.makeText(this, "これを最初にやってしまおう!", Toast.LENGTH_LONG).show()
            }
        }


        //データの削除
        //基本的には表示されているデータを消して、一つ前のやることを表示する。
        delete.setOnClickListener {
            if(Todo != null){ //データがある時、idの更新をする。
                //削除用のid保持
                id_delete = id_now

                if(id_min == id_max){ //データが一つしかないとき
                    //next内の処理に合わせる
                    id_min = -1
                    id_max = -100
                    Todo = null //ここでTodoをnullにする。
                } else if(id_now == id_max){ //この場合、id_maxの値も更新する必要がある。

                    Now = realm.where<TodoModel>().lessThan("id",id_now).max("id")
                    id_max = Now?.toLong() ?: 0L
                    id_now = id_max

                }else if(id_now == id_min){ //

                    Now = realm.where<TodoModel>().greaterThan("id",id_now).min("id")
                    id_min = Now?.toLong() ?: 0L
                    id_now = id_min

                }else{ //id_min < id_now < id_max　大半はここで処理がされる。

                    Now = realm.where<TodoModel>().lessThan("id",id_now).max("id")
                    id_now = Now?.toLong() ?: 0L

                }

                //id更新が終わった後、トースト表示
                Toast.makeText(this, "お疲れさま!", Toast.LENGTH_LONG).show()

                //id_max == -100となるときは、データが一つしかない場合。
                //ここでテキストの更新をする。
                if(id_max != -100L){

                    Todo = realm.where<TodoModel>().equalTo("id",id_now).findFirst()
                    todo_dis.setText((Todo?.todo ?:"何もやることはないよ！").toString()   + (Todo?.id ?:"").toString())
                }else{
                    todo_dis.setText("何もやることはないよ！")
                }


                //最後にデータを削除する
                realm.executeTransaction { db: Realm ->
                    db.where<TodoModel>().equalTo("id",id_delete).findFirst()?.deleteFromRealm()
                }


            }else {//データが何もない時
                Toast.makeText(this, "やることを追加してね!", Toast.LENGTH_LONG).show()
            }
        }

    }
}
