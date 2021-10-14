package com.smartfoodnet.fnproduct.code

import com.smartfoodnet.fnproduct.code.entity.Code

interface CodeCustom {
    fun findByGroupNameAndKeyName(groupName: String?, keyName: String?): List<Code>
}
