package comkamiapk.realm_todo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class TodoModel : RealmObject(){
    @PrimaryKey
    var id : Long = 0
    var todo : String = ""
}