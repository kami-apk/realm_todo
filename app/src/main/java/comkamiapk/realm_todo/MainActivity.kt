package comkamiapk.realm_todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private lateinit var realm :Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //インスタンスの取得。
        realm = Realm.getDefaultInstance()

        //保存する。
        save.setOnClickListener {

            if(editText.getText().toString() != ""){ //editTextに文字が何か入力されているとき

                //データベースへの追加
                realm.executeTransaction { realm: Realm ->
                    val maxId = realm.where<TodoModel>().max("id")
                    val nextId = (maxId?.toLong() ?: 0L) + 1
                    //引数はプライマリーキーがつけられているidを取る
                    //今回はnextIdがidに相当
                    val db = realm.createObject<TodoModel>(nextId) //データを新しく追加するためにオブジェクトを作る。
                    db.todo = editText.getText().toString()
                }

                editText.setText("")
                Toast.makeText(this, "保存したよ。", Toast.LENGTH_LONG).show()
            }else {
                Toast.makeText(this, "何かを記入してね!", Toast.LENGTH_LONG).show()
            }


        }

        //アクティビティの遷移
        dis.setOnClickListener {
            val intent = Intent(this,TodoDisplay::class.java)
            startActivity(intent)
        }

    }
}
