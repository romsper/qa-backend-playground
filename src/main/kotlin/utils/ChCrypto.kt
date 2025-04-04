package utils

import utils.ConfigHelper.Companion.appConfig
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object ChCrypto{
    fun aesEncrypt(v: String, secretKey: String = appConfig.jwt.secret) = AES256.encrypt(v, secretKey)
    fun aesDecrypt(v: String, secretKey: String = appConfig.jwt.secret) = AES256.decrypt(v, secretKey)
}

private object AES256{
    private val encoder = Base64.getEncoder()
    private val decoder = Base64.getDecoder()

    private fun cipher(opmode:Int, secretKey: String): Cipher {
        if(secretKey.length != 32) throw RuntimeException("SecretKey length is not 32 chars")
        val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val sk = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")
        val iv = IvParameterSpec(secretKey.substring(0, 16).toByteArray(Charsets.UTF_8))
        c.init(opmode, sk, iv)
        return c
    }

    fun encrypt(str:String, secretKey: String): String {
        val encrypted = cipher(Cipher.ENCRYPT_MODE, secretKey).doFinal(str.toByteArray(Charsets.UTF_8))
        return String(encoder.encode(encrypted))
    }

    fun decrypt(str:String, secretKey: String): String {
        val byteStr = decoder.decode(str.toByteArray(Charsets.UTF_8))
        return String(cipher(Cipher.DECRYPT_MODE, secretKey).doFinal(byteStr))
    }
}
