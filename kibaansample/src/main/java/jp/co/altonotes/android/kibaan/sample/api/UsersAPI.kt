package jp.co.altonotes.android.kibaan.sample.api

import jp.co.altonotes.android.kibaan.task.TaskHolder

class UsersAPI(var id: String, owner: TaskHolder, key: String?) : BaseAPI<UsersResponse>(owner, key) {

    override val resultClass = UsersResponse::class

    override val path: String get() = "users/$id"
}

