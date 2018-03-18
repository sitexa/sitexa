import org.jetbrains.ktor.auth.UserHashedTableAuth
import org.jetbrains.ktor.auth.UserPasswordCredential
import org.jetbrains.ktor.util.decodeBase64
import org.jetbrains.ktor.util.encodeBase64
import org.jetbrains.ktor.util.getDigest

/**
 * Created by open on 12/05/2017.
 */

fun main(vararg: Array<String>) {
    val username = "test"
    val password = "test1"

    val digest = getDigest(password, "SHA-256", "ktor") //[B@573fd745
    val enc = encodeBase64(digest) //VltM4nfheqcJSyH887H+4NEOm2tDuKCl83p5axYXlF0=

    val userHashTableAuth = UserHashedTableAuth(table = mapOf(username to decodeBase64(enc)))
    val credential = UserPasswordCredential(username, password)
    val userIdPrincipal = userHashTableAuth.authenticate(credential)
    println("\nuserIdPrincipal:$userIdPrincipal")
}