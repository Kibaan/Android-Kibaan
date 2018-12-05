package kibaan.sample.api

import kibaan.task.TaskHolder

class UsersAPI(var id: String, owner: TaskHolder, key: String?) : BaseAPI<UsersResponse>(owner, key) {

    override val resultClass = UsersResponse::class

    override val path: String get() = "users/$id"
}

