package studios.aestheticapps.linker.persistence

interface DatabaseTask<T>
{
    fun performOperation(): T
}