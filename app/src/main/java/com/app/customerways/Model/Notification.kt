package com.app.customerways.Model

class Notification {

    var id: Int? = null
    var user_id: Int? = null
    var notify_user_id: Int? = null
    var name: String? = null
    var profile: String? = null
    var cover_img: String? = null
    var message: String? = null
    var datetime: String? = null
    var time: String? = null
    var updated_at: String? = null
    var created_at: String? = null
//    var friend: String? = null

    constructor(
        id: Int?,
        userId: Int?,
        notifyUserId: Int?,
        name: String?,
        profile: String?,
        coverImg: String?,
        message: String?,
        datetime: String?,
        time: String?,
        updatedAt: String?,
        createdAt: String?,
//        friend: String?,
    ) {
        this.id = id
        this.user_id = userId
        this.notify_user_id = notifyUserId
        this.name = name
        this.profile = profile
        this.cover_img = coverImg
        this.message = message
        this.datetime = datetime
        this.time = time
        this.updated_at = updatedAt
        this.created_at = createdAt
//        this.friend = friend
    }



}