package studios.aestheticapps.linker.persistence

import android.os.AsyncTask

class DatabaseAsyncTask<T> internal constructor(private val task: DatabaseTask<T>) : AsyncTask<Void, Void, T>()
{
    override fun doInBackground(vararg params: Void?): T = task.performOperation()
}