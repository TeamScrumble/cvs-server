package product.common.client

import ApiResponse
import member.MemberApiClient
import member.MemberListApi

class MemberClient(
    private val memberClient: MemberApiClient
) {

    suspend fun getMemberMap(
        memberIds: Collection<Long>
    ): Map<Long, MemberListApi.Response.Member> {
        if (memberIds.isEmpty()) return emptyMap()
        val ids = memberIds.distinct()

        return when (val res = memberClient.getList(ids)) {
            is ApiResponse.Success -> res.body.members.associateBy { it.memberId }
            else -> emptyMap()
        }
    }

}