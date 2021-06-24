package br.edu.infnet.anotations.util

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import java.io.*

class Crypto {
    companion object {
        fun cryptoReadText(name: String, context: Context): ArrayList<String>{
            val decrypt = getEncryptedFile(name, context).openFileInput()
            val reader = BufferedReader(InputStreamReader(decrypt))
            val result = ArrayList<String>()
            reader.lines().forEach {
                result.add(it)
            }

            decrypt.close()
            return result
        }

        fun cryptoReadImage(name: String, context: Context) : ByteArray {
            val decrypt = getEncryptedFile(name, context).openFileInput()
            val read = ByteArrayInputStream(decrypt.readBytes())
            return read.readBytes()
        }

        fun cryptoRecordImage(name: String, context: Context, image: ByteArray) {
            val encrypted = getEncryptedFile(name, context).openFileOutput()
            encrypted.write(image)
            encrypted.close()
        }

        fun cryptoRecordText(name: String, context: Context, txt: List<String>) {
            val encrypted = getEncryptedFile(name, context).openFileOutput()
            val pw = PrintWriter(encrypted)
            txt.forEach {
                pw.println(it)
            }
            pw.flush()
            encrypted.close()
        }

        private fun getEncryptedFile(name: String, context: Context): EncryptedFile {
            val alias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            val folder = context.filesDir
            val nFolder = File(folder, "archives")
            nFolder.mkdir()
            nFolder.path

            val file = File(nFolder, name)

            return EncryptedFile.Builder(
                file,
                context,
                alias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
        }
    }
}