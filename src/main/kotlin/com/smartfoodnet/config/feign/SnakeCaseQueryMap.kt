package com.smartfoodnet.config.feign

import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.nosnos.extenstion.camelToSnakeCase
import feign.Param
import feign.QueryMapEncoder
import feign.codec.EncodeException
import java.beans.IntrospectionException
import java.beans.Introspector
import java.beans.PropertyDescriptor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

/**
 * QueryMap - SnakeCase
 */
class SnakeCaseQueryMap: QueryMapEncoder, Log {
    private val classToMetadata: MutableMap<Class<*>, ObjectParamMetadata> = HashMap()

    override fun encode(`object`: Any): MutableMap<String, Any> {
        try {
            val metadata = this.getMetadata(`object`.javaClass)
            val propertyNameToValue: MutableMap<String, Any> = HashMap()

            for (pd in metadata.objectProperties) {
                val method: Method = pd.readMethod
                val value = method.invoke(`object`)
                if (value != null && value != `object`) {
                    val alias: Param? = method.getAnnotation(Param::class.java)
                    var name: String = alias?.value ?: pd.name
                    name = name.camelToSnakeCase()
                    propertyNameToValue[name] = value
                }
            }
            return propertyNameToValue
        } catch (e : IllegalAccessException) {
            throw EncodeException("Failure encoding object into query map", e);
        } catch (e : IntrospectionException) {
            throw EncodeException("Failure encoding object into query map", e);
        } catch (e : InvocationTargetException){
            throw EncodeException("Failure encoding object into query map", e);
        }
    }

    @Throws(IntrospectionException::class)
    private fun getMetadata(objectType: Class<*>): ObjectParamMetadata {
        var metadata: ObjectParamMetadata? = classToMetadata[objectType]
        if (metadata == null) {
            metadata = ObjectParamMetadata.parseObjectType(objectType)
            classToMetadata[objectType] = metadata
        }
        return metadata
    }



    private class ObjectParamMetadata private constructor(
        objectProperties: List<PropertyDescriptor>
    ) {
        val objectProperties: List<PropertyDescriptor>

        companion object {
            @Throws(IntrospectionException::class)
            fun parseObjectType(type: Class<*>): ObjectParamMetadata {
                val properties: MutableList<PropertyDescriptor> = ArrayList()
                for (pd in Introspector.getBeanInfo(type).propertyDescriptors) {
                    val isGetterMethod = pd.readMethod != null && "class" != pd.name
                    if (isGetterMethod) {
                        properties.add(pd)
                    }
                }
                return ObjectParamMetadata(properties)
            }
        }

        init {
            this.objectProperties = Collections.unmodifiableList(objectProperties)
        }
    }

}

