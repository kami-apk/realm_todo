package comkamiapk.realm_todo

import android.app.Application
import io.realm.Realm

open class OpenRealm  : Application() {
    override fun onCreate(){
        super.onCreate()
        //Realmの初期化
        Realm.init(this)
    }
}