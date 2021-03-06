package studios.aestheticapps.linker.persistence.link

import android.arch.persistence.db.SupportSQLiteQuery
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.arch.persistence.room.RawQuery
import android.arch.persistence.room.Update
import studios.aestheticapps.linker.model.Link

@Dao
interface LinkDao
{
    @Query("SELECT * from link_table ORDER BY title ASC")
    fun getAll(): List<Link>

    @Insert(onConflict = REPLACE)
    fun insert(link: Link)

    @Query("SELECT * FROM link_table WHERE title LIKE '%' || :phrase || '%' OR domain LIKE '%' || :phrase || '%' OR url LIKE '%' || :phrase || '%' OR tags LIKE '%' || :phrase || '%' ORDER BY title")
    fun search(phrase: String): List<Link>

    @RawQuery
    fun searchRawQuery(query: SupportSQLiteQuery): List<Link>

    @Query("SELECT * from link_table WHERE id = :id")
    fun getById(id: Int): Link

    @Query("SELECT * from link_table WHERE isFavorite = 1")
    fun getFavourites(): List<Link>

    @Query("SELECT * from link_table ORDER BY lastUsed DESC")
    fun getRecent(): List<Link>

    @Update
    fun update(link: Link)

    @Query("UPDATE link_table SET category = 'Undefined' WHERE category = :categoryName")
    fun updateDeletedCategoryEntries(categoryName: String)

    @Query("DELETE FROM link_table WHERE id = :id")
    fun delete(id: Int)

    @Query("DELETE FROM link_table")
    fun deleteAll()
}