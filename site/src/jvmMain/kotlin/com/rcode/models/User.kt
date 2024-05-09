@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.rcode.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.ObjectIdGenerator
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
actual data class User(

    @BsonId
    @SerialName("_id")
    actual val id: String = ObjectIdGenerator().generate().toString(),
    actual val username: String = "",
    actual val password: String = ""
)

@Serializable
actual data class UserWithoutPassword(
    @BsonId
    @SerialName("_id")
    actual val id: String = ObjectIdGenerator().generate().toString(),
    actual val username: String = ""
)
