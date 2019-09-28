package bainat.pos.lib

import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader

data class Account(
    var id: String = "none",
    var name: String = "unnamed",
    var contacts: MutableList<String>,
    var addresses: MutableMap<String,String> = HashMap(),
    var heldItems: MutableMap<String,Int> = HashMap(),
    var sales: MutableList<Int>
) {
    init {
        // ADD SELF TO MAP FOR ACCESS LATER
        loadedAccounts[name] = this
    }

    companion object {
        // LOADED ACCOUNT STORAGE
        val loadedAccounts: MutableMap<String,Account> = HashMap()

        @Throws(FileNotFoundException::class)
        infix fun named(name: String): Account {

            // NAVIGATE TO AND CHECK FOR FILE
            val file = File("data/accounts/$name")
            if (file.exists()) {

                // READ FILE CONTENTS TO LIST
                val reader = FileReader(file)
                val contents: MutableMap<String,String> = HashMap()
                reader.readLines().forEach {
                    val line = it.split(":")
                    contents[line[0]] = line[1]
                }

                // PROCESS ADDRESSES TO MAP
                val addressMap: MutableMap<String,String> = HashMap()
                contents["addresses"]?.split(",")?.forEach {
                    addressMap[it.substringBefore(":")] = it.substringAfter(":")
                }

                // PROCESS HELD ITEMS TO MAP
                val heldMap: MutableMap<String,Int> = HashMap()
                contents["helditems"]?.split(",")?.forEach {
                    heldMap[it.substringBefore(":")] = it.substringAfter(":").toInt()
                }

                // SALES LIST AS INTEGERS
                val salesInts: MutableList<Int> = emptyList<Int>().toMutableList()
                contents["sales"]?.split(",")?.forEach {
                    salesInts.add(it.toInt())
                }

                // LOAD LIST OF CONTACT NAMES TO REFER TO
                val contactsList = contents["contacts"]?.split(",") as MutableList<String>

                // CREATE ACCOUNT OBJECT WITH ACQUIRED DETAILS
                return Account(contents["id"] ?: "none", name, contactsList, addressMap, heldMap, salesInts)

            // IF NO ACCOUNT FILE FOUND, THROW ERROR.
            } else throw FileNotFoundException()
        }

        // SAVE ACCOUNT TO FILE
        infix fun save(toSave: Account) {
            val file = File("data/accounts/${toSave.name}")
            if (file.exists()) file.delete()
            file.printWriter().use { out ->
                out.println("id:${toSave.id}")
                out.println("contacts:${toSave.contacts.joinToString(",")}")

                val addressesCombined: MutableList<String> = emptyList<String>() as MutableList<String>
                toSave.addresses.forEach {
                    addressesCombined.add("${it.key}:${it.value}")
                }; out.println("addresses:${addressesCombined.joinToString(",")}")

                val heldCombined: MutableList<String> = emptyList<String>() as MutableList<String>
                toSave.heldItems.forEach {
                    addressesCombined.add("${it.key}:${it.value}")
                }; out.println("helditems:${heldCombined.joinToString(",")}")

                out.println("sales:${toSave.sales.joinToString(",")}")
            }
        }

        // DELETE ACCOUNT WITH EITHER CLASS OR NAME
        infix fun delete(toDelete: Account) {
            val file = File("data/accounts/${toDelete.name}")
            if (file.exists()) file.delete()
            loadedAccounts.remove(toDelete.name)
        }

        infix fun delete(toDeleteString: String) {
            if (loadedAccounts.containsKey(toDeleteString)) {
                val file = File("data/accounts/${toDeleteString}")
                if (file.exists()) file.delete()
                loadedAccounts.remove(toDeleteString)
            }
        }
    }
}