package com.smartfoodnet.fnproduct.code

import com.smartfoodnet.fnproduct.code.entity.Code
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CodeService(
    private val codeRepository: CodeRepository,
) {
    fun getCodeByGroupNameKeyName(groupName: String? = null, keyName: String? = null): List<Code> {
        return codeRepository.findByGroupNameAndKeyName(groupName, keyName)
    }

    @Transactional
    fun createCodes(codes: List<Code>) {
        codeRepository.saveAll(codes)
    }
}
