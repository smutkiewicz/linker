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
    @Query("SELECT * from category_table ORDER BY title")
    fun getAll(): List<Category>

    @Query("SELECT * FROM category_table WHERE ruleDomain = :domain ORDER BY title")
    fun getCategoryByDomain(domain: String): Category

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(link: Category)

    @Update
    fun update(link: Category)

    @Query("DELETE FROM category_table WHERE id = :id")
    fun delete(id: Int)

    @Query("DELETE FROM category_table")
    fun deleteAll()
}