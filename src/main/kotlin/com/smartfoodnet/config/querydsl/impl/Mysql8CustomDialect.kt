package com.smartfoodnet.config.querydsl.impl

import org.hibernate.dialect.MySQL8Dialect
import org.hibernate.dialect.function.StandardSQLFunction
import org.hibernate.type.StandardBasicTypes

class Mysql8CustomDialect : MySQL8Dialect() {
    init {
        registerFunction("GROUP_CONCAT", StandardSQLFunction("group_concat", StandardBasicTypes.STRING))
    }
}
