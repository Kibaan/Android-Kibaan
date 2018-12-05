package kibaan.android.sample.api

class UsersResponse {
    var id: String? = null
    var name: String? = null
    var email: String? = null


    // region -> Computed Property

    val displayId: String? get() = "id = $id"
    val displayName: String? get() = "name = $name"
    val displayEmail: String? get() = "email = $email"

    // endregion

    override fun toString(): String {
        return "UsersResponse(id=$id, name=$name, email=$email)"
    }
}