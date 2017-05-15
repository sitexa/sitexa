/**
 * Created by open on 13/05/2017.
 */

import com.sitexa.ktor.dbConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.transaction


val datasource = HikariDataSource().apply {
    maximumPoolSize = dbConfig["pool"].toString().toInt()
    driverClassName = dbConfig["driver"].toString()
    jdbcUrl = dbConfig["url"].toString()
    isAutoCommit = dbConfig["autoCommit"].toString().toBoolean()
    addDataSourceProperty("user", dbConfig["user"].toString())
    addDataSourceProperty("password", dbConfig["password"].toString())
    addDataSourceProperty("dialect", dbConfig["dialect"].toString())
}

object MyUser : IntIdTable() {
    val name = varchar("name", 50).index()
    val city = reference("city", MyCity)
    val age = integer("age")
}

object MyCity : IntIdTable() {
    val name = varchar("name", 50)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(MyUser)

    var name by MyUser.name
    var city by City referencedOn MyUser.city
    var age by MyUser.age
}

class City(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<City>(MyCity)

    var name by MyCity.name
    val users by User referrersOn MyUser.city
}


fun main(args: Array<String>) {
    Database.connect(datasource)

    transaction {
        logger.addLogger(StdOutSqlLogger())

        create(MyCity, MyUser)

        val stPete = City.new {
            name = "St. Petersburg"
        }

        val munich = City.new {
            name = "Munich"
        }

        User.new {
            name = "a"
            city = stPete
            age = 5
        }

        User.new {
            name = "b"
            city = stPete
            age = 27
        }

        User.new {
            name = "c"
            city = munich
            age = 42
        }

        println("Cities: ${City.all().joinToString { it.name }}")
        println("Users in ${stPete.name}: ${stPete.users.joinToString { it.name }}")
        println("Adults: ${User.find { MyUser.age greaterEq 18 }.joinToString { it.name }}")
    }
}