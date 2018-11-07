package studios.aestheticapps.linker.persistence

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import studios.aestheticapps.linker.model.Link

@Dao
interface LinkDao
{
    @Query("SELECT * from link_table ORDER BY title ASC")
    fun getAll(): List<Link>

    @Insert(onConflict = REPLACE)
    fun insert(link: Link)

    @Query("SELECT * from link_table " +
        "WHERE title LIKE '%' || :phrase || '%' " +
        "OR domain LIKE '%' || :phrase || '%' " +
        "OR url LIKE '%' || :phrase || '%' " +
        "ORDER BY title")
    fun search(phrase: String): List<Link>

    @Query("SELECT * from link_table WHERE id = :id")
    fun searchById(id: Int): Link

    @Update
    fun update(link: Link)

    @Query("DELETE FROM link_table WHERE id = :id")
    fun delete(id: Int)

    @Query("DELETE FROM link_table")
    fun deleteAll()
}