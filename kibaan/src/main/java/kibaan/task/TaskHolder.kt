package kibaan.task

import kibaan.ios.append
import kibaan.ios.first
import kibaan.ios.removeAll

class TaskHolder {

    private var taskList: MutableList<Task> = mutableListOf()
    private var taskMap: MutableMap<String, Task> = mutableMapOf()

    /**
     * タスクを追加する
     * キーを指定した場合は既存の同じキーのタスクをキャンセルする
     */
    @Suppress("NAME_SHADOWING")
    fun add(task: Task, key: String? = null) {
        taskList.append(task)

        val key = key ?: return

        taskMap[key]?.cancel()
        taskMap[key] = task
    }

    /**
     * タスクを削除する
     */
    fun remove(task: Task) {
        taskList.remove(element = task)

        val removeKey = taskMap.first { it.value === task }?.key
        if (removeKey != null) {
            taskMap.remove(key = removeKey)
        }
    }

    /**
     * 保持するタスクを全てクリアする
     */
    fun clearAll() {
        taskList.forEach { it.cancel() }
        taskMap.removeAll()
    }

}
