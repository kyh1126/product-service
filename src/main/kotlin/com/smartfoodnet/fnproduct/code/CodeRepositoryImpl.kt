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

    private fun eqGroupName(groupName: String?) = groupName?.let { code.groupName.eq(it) }

    private fun eqKeyName(keyName: String?) = keyName?.let { code.keyName.eq(it) }
}
