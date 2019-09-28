package bainat.pos.lib

import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader

data class Contact(
    var firstName: String = "none",
    var lastName: String = "none",
    var numbers: MutableList<String> = emptyList<String>() as MutableList<String>,
    var emails: MutableList<String> = emptyList<String>() as MutableList<String>
) {
    init {
        // ADD SELF TO MAP FOR ACCESS LATER
        loadedContacts["$firstName$lastName"] = this
    }
    companion object {
        // RECORD OF LOADED CONTACTS
        val loadedContacts: MutableMap<String, Contact> = HashMap()

        @Throws(FileNotFoundException::class)
        infix fun named(name: String): Contact {

            // NAVIGATE TO AND CHECK FOR FILE
            val file = File("data/contacts/$name")
            if (file.exists()) {

                // READ FILE CONTENTS TO LIST
                val reader = FileReader(file)
                val contents: MutableMap<String,String> = HashMap()
                reader.readLines().forEach {
                    val line = it.split(":")
                    contents[line[0]] = line[1]
                }

                // GET NAMES
                val firstName = contents["first"] ?: "none"
                val lastName = contents["last"] ?: "none"

                // GET PHONE NUMBERS AND EMAILS
                val numbersList = contents["numbers"]?.split(",") as MutableList<String>
                val emailsList = contents["emails"]?.split(",") as MutableList<String>

                // CREATE CONTACT OBJECT WITH ACQUIRED DETAILS
                return Contact(firstName, lastName, numbersList, emailsList)

                // IF NO CONTACT FILE FOUND, THROW ERROR.
            } else throw FileNotFoundException()
        }

        // SAVE CONTACT DETAILS TO FILE
        infix fun save(toSave: Contact) {
            val file = File("data/contacts/${toSave.firstName}${toSave.lastName}")
            if (file.exists()) file.delete()
            file.printWriter().use { out ->
                out.println("first:${toSave.firstName}")
                out.println("last:${toSave.lastName}")

                out.println("numbers:${toSave.numbers.joinToString(",")}")
                out.println("emails:${toSave.emails.joinToString(",")}")
            }
        }

        // DELETE CONTACT WITH EITHER CLASS OR NAME
        infix fun delete(toDelete: Contact) {
            val file = File("data/contacts/${toDelete.firstName}${toDelete.lastName}")
            if (file.exists()) file.delete()
            loadedContacts.remove("${toDelete.firstName}${toDelete.lastName}")
        }

        infix fun delete(toDeleteName: String) {
            if (loadedContacts.containsKey(toDeleteName)) {
                val file = File("data/contacts/${toDeleteName}")
                if (file.exists()) file.delete()
                loadedContacts.remove(toDeleteName)
            }
        }
    }
}
