package studios.aestheticapps.linker.persistence.category

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import studios.aestheticapps.linker.model.Category

@Dao
interface CategoryDao
{
    @Query("SELECT * FROM category_table ORDER BY name")
    fun getAll(): List<Category>

    @Query("SELECT DISTINCT name FROM category_table ORDER BY name")
    fun getAllCategories(): List<String>

    @Query("SELECT * FROM category_table WHERE ruleDomain = :domain ORDER BY usages DESC, lastUsed DESC, id")
    fun getCategoriesByDomain(domain: String): List<Category>

    @Query("SELECT ruleDomain FROM category_table WHERE name = :categoryName")
    fun getDomainsByCategory(categoryName: String): List<String>

    @Query("SELECT * FROM category_table WHERE name = :categoryName AND ruleDomain = :domain")
    fun getCategoryWithDomain(categoryName: String, domain: String): Category

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category)

    @Query("UPDATE category_table SET usages = usages + 1, lastUsed = :lastUsed WHERE id = :id")
    fun update(id: Int, lastUsed: String)

    @Query("UPDATE category_table SET usages = usages + 1, lastUsed = :lastUsed WHERE name = :categoryName AND ruleDomain = :domain")
    fun update(categoryName: String, domain: String, lastUsed: String)

    @Query("UPDATE category_table SET ruleDomain = :newDomain, lastUsed = :lastUsed WHERE name = :categoryName AND ruleDomain = ''")
    fun updateEmptyCategory(categoryName: String, newDomain: String, lastUsed: String)

    @Update
    fun update(category: Category)

    @Query("DELETE FROM category_table WHERE id = :id")
    fun delete(id: Int)

    @Query("DELETE FROM category_table WHERE name = :categoryName")
    fun deleteCategory(categoryName: String)

    @Query("DELETE FROM category_table")
    fun deleteAll()
}