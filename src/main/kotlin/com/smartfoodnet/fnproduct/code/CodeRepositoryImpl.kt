package com.smartfoodnet.fnproduct.code

import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.code.entity.Code
import com.smartfoodnet.fnproduct.code.entity.QCode.code

class CodeRepositoryImpl : Querydsl4RepositorySupport(Code::class.java), CodeCustom {

    override fun findByGroupNameAndKeyName(groupName: String?, keyName: String?): List<Code> {
        return selectFrom(code)
            .where(eqGroupName(groupName), eqKeyName(keyName))
            .fetch()
    }

    private fun eqGroupName(groupName: String?) = if (groupName == null) null else code.groupName.eq(groupName)

    private fun eqKeyName(keyName: String?) = if (keyName == null) null else code.keyName.eq(keyName)
}
