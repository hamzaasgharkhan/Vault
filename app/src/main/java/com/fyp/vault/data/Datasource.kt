package com.fyp.vault.data

import androidx.annotation.DrawableRes
import com.fyp.vault.R

val vaults: MutableList<String> = mutableListOf(
    "Vault 1",
    "Vault 2",
    "Vault 3"
)

open class Node(
    open var name: String,
    open val index: Long
)

data class File(
    override var name: String,
    override val index: Long,    // DirectoryStore Index
    val size: Long,     // Bytes
    @DrawableRes val thumbnail: Int?      // TO BE DELETED IN FINAL PRODUCT
//    val thumbnail: InputStream?,      // TO BE UNCOMMENTED IN FINAL PRODUCT
//    val stream: InputStream           // TO BE UNCOMMENTED IN FINAL PRODUCT
): Node(name, index)

data class Directory(
    override var name: String,
    override val index: Long,     // DirectoryStore Index
    val childrenDirectories: MutableList<Directory>,
    val childrenFiles: MutableList<File>
): Node(name, index)

val file1 = File(
    name = "hamza.jpg",
    index = 1,
    size = 198767,
    thumbnail = R.drawable.hamza,
)

val file2 = File(
    name = "whatsapp.backup.long.extralong.data.bin",
    index = 2,
    size = 89919,
    thumbnail = null,
)

val file3 = File(
    name = "test.java",
    index = 3,
    size = 2399102,
    thumbnail = null,
)

val file4 = File(
    name = "mochi.jpg",
    index = 4,
    size = 35346,
    thumbnail = R.drawable.mochi,
)

val file5 = File(
    name = "chibi.jpg",
    index = 5,
    size = 271197,
    thumbnail = R.drawable.chibi,
)

val directory3 = Directory(
    name = "Videos",
    childrenDirectories = mutableListOf(),
    childrenFiles = mutableListOf(file4),
    index = 8
)
val directory2 = Directory(
    name = "Downloads",
    childrenDirectories = mutableListOf(),
    childrenFiles = mutableListOf(file3),
    index = 7
)
val directory1: Directory = Directory(
    name = "Documents",
    childrenDirectories = mutableListOf(directory2, directory3),
    childrenFiles = mutableListOf(file1),
    index = 6
)

val root = Directory(
    name = "/",
    childrenDirectories = mutableListOf(directory1),
    childrenFiles = mutableListOf(file5, file2),
    index = 0
)

fun getDirectory(index: Long): Directory{
    if (index == 6L){
        return directory1
    } else if (index == 7L){
        return directory2
    } else if (index == 8L){
        return directory3
    } else {
        return root
    }
}