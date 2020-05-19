package kibaan.android.ios

/**
 * Adds an element to the end of the collection.
 */
fun <T> MutableCollection<T>.append(newElement: T) {
    add(newElement)
}

/**
 * Adds the elements of a collection to the end of this collection.
 */
fun <T> MutableCollection<T>.append(contentsOf: Collection<T>) {
    addAll(contentsOf)
}

/**
 * Removes all elements from the collection.
 */
fun <T> MutableCollection<T>.removeAll() {
    clear()
}

/**
 * The number of elements in this collection.
 */
val <T> Collection<T>.count: Int get() = size

/**
 * Returns the elements of this collection of collections, concatenated.
 */
fun Collection<String>.joined(separator: String = ""): String {
    return joinToString(separator = separator)
}

/**
 * Returns the index where the specified value appears in the collection.
 */
fun <T> Collection<T>.index(of: T): Int {
    return indexOf(of)
}

/**
 * 指定された条件を満たす値がコレクション内で現れる位置のインデックスを返す.
 * ただし、見つからなかった場合は"null"を返す.
 */
fun <T> Collection<T>.indexOrNull(predicate: (T) -> Boolean): Int? {
    val index = indexOfFirst(predicate)
    if (index == -1) {
        return null
    }
    return index
}

/**
 * 指定された条件を満たす値がコレクション内で現れる位置のインデックスを返す.
 * ただし、見つからなかった場合は"null"を返す.
 */
fun <T> Collection<T>.firstIndex(predicate: (T) -> Boolean): Int? {
    return indexOrNull(predicate)
}

// UIImage(named: ""

/**
 * 指定した要素が含まれるか判定する
 */
fun <T> Collection<T>.contains(where: (T) -> Boolean): Boolean {
    return firstOrNull(where) != null
}

/**
 * Returns a sequence of pairs (*n*, *x*), where *n* represents a consecutive integer starting at zero and *x* represents an element of the sequence.
 */
fun <T> Collection<T>.enumerated(): List<CollectionEnumerationItem<T>> {
    return mapIndexed { index, element ->
        CollectionEnumerationItem(index, element)
    }
}

/**
 * Returns an array containing the non-`nil` results of calling the given transformation with each element of this sequence.
 */
fun <T, R : Any> Collection<T>.compactMap(transform: (T) -> R?): List<R> {
    firstOrNull()
    return mapNotNull(transform)
}

/**
 * 引数の関数で大小比較して並べ替える。
 * iOSは比較結果を Bool で返して並べ替えができるが、Kotlinのsort関数は大・小・同値で返さないと並べ替えられないため、
 * IFがiOSと少し異なっている。
 */
fun <T> Collection<T>.sorted(by: (T, T) -> ComparisonResult): List<T> {
    return sortedWith(Comparator { left, right ->
        when (by(left, right)) {
            ComparisonResult.orderedAscending -> -1
            ComparisonResult.orderedDescending -> 1
            else -> 0
        }
    })
}

fun <T> Collection<T>.sortedByInt(by: (T, T) -> Int): List<T> {
    return sortedWith(Comparator { left, right ->
        by(left, right)
    })
}

fun <T, R> zip(sequence1: Collection<T>, sequence2: Collection<R>): List<Pair<T, R>> {
    return sequence1.zip(sequence2)
}

/**
 * enumerated()でインデックスとエレメントのコレクションを返却する為のクラス
 */
data class CollectionEnumerationItem<T>(val offset: Int, val element: T)
