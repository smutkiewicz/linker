package studios.aestheticapps.linker.persistence

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import studios.aestheticapps.linker.model.Category

@Dao
interface CategoryDao
{
    @Query("SELECT * from category_table ORDER BY title ASC")
    fun getAll(): List<Category>

    // TODO join
    /*@Query("SELECT * from category_table ORDER BY title ASC")
    fun getCategoryByDomain(domain: String): Category*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(link: Category)

    @Query("SELECT * FROM category_table " +
        "WHERE title LIKE '%' || :phrase || '%' " +
        "ORDER BY title")
    fun search(phrase: String): List<Category>

    @Update
    fun update(link: Category)

    @Query("DELETE FROM category_table WHERE id = :id")
    fun delete(id: Int)

    @Query("DELETE FROM category_table")
    fun deleteAll()
}